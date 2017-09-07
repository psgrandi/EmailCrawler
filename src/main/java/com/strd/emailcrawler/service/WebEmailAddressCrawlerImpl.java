package com.strd.emailcrawler.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.security.cert.CertificateException;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.UnsupportedMimeTypeException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

import com.strd.emailcrawler.model.CrawlerJob;
import com.strd.emailcrawler.model.Page;
import com.strd.emailcrawler.repository.CrawlerJobDAO;
import com.strd.emailcrawler.util.UrlFormatter;

/**
 * Component that runs asynchronous web crawling jobs
 * @author psilveira
 *
 */
@Scope("prototype")
@Component
@EnableAsync
public class WebEmailAddressCrawlerImpl implements WebCrawler {
	
	private static Logger logger = Logger.getLogger(WebEmailAddressCrawlerImpl.class);
	
	private final Integer MAX_DEPTH = 2;
	private final Pattern EMAIL_PATTERN = Pattern.compile("[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+");
	private final Pattern FILTERS = Pattern.compile(".*(\\.(css|js|gif|jpg|png|mp3|mp4|zip|gz|pdf|jsp|jsf))");

	private CrawlerJob crawlerJob;
	private int crawlerId;
	private Set<String> pageLinks = new HashSet<>();
	
	@Autowired
	private CrawlerJobDAO crawlerJobDAO;
	
	@Async
	@Override
	public void execute(CrawlerJob job) {		
		
		this.crawlerJob = job;
		crawlerId = crawlerJob.getJobId();
		try {
			logger.info(crawlerId + ": Crawl started!");
			crawlerJob.startJob();
			crawlerJobDAO.save(crawlerJob);
			
			connectOriginalUrl(crawlerJob.getUrl());
			
			logger.info(crawlerId + ": Getting links!");			
			getPageLinks(crawlerJob.getUrl(), 0);
			
			logger.info(crawlerId + ": Getting emails!");
			getEmails();
			
			crawlerJob.finishJob();
			crawlerJobDAO.save(crawlerJob);
			
		} catch (CertificateException e) {
			errorHandler(e);
		} catch (HttpStatusException e) {
			errorHandler(e);
		} catch (UnsupportedMimeTypeException e) {
			errorHandler(e);
		} catch (MalformedURLException e) {
			errorHandler(e);
		} catch (IOException e) {
			errorHandler(e);
		} catch (Exception e) {
			errorHandler(e);
		}
	}
	
	private Document connect(String URL) throws IOException, MalformedURLException, HttpStatusException, CertificateException {
		logger.info(crawlerId + ": Accessing page: " + URL);
		
		Document document = null;
		
		if (!pageLinks.contains(URL) && !FILTERS.matcher(URL.toLowerCase()).matches()) {
			document = Jsoup.connect(URL).get();
		}
		
		return document;
	}
	
	private void connectOriginalUrl(String URL) throws IOException, MalformedURLException, HttpStatusException, CertificateException {
		connect(URL);
		
		pageLinks.add(URL);
	}
	
	private void getPageLinks(String URL, int depth) {		
		if (!pageLinks.contains(URL) && !FILTERS.matcher(URL.toLowerCase()).matches() && (depth < MAX_DEPTH)) {			
			try {
				Document document = connect(URL);
				
				Elements children = document.select("a[href~=(((http://)|(www.))(" + UrlFormatter.formatUrl(crawlerJob.getUrl()) + "))]");
	
				depth++;
				for(Element child : children) {
					pageLinks.add(URL);
					
					getPageLinks(child.attr("abs:href"), depth);			
				};
				
			} catch (CertificateException e) {
				logger.debug(crawlerId + ": " + e.getMessage());
			} catch (HttpStatusException e) {
				logger.debug(crawlerId + ": " + e.getMessage());
			} catch (UnsupportedMimeTypeException e) {
				logger.debug(crawlerId + ": " + e.getMessage());
			} catch (MalformedURLException e) {
				logger.debug(crawlerId + ": " + e.getMessage());
			} catch (IOException e) {
				logger.debug(crawlerId + ": " + e.getMessage());
			} 	
		}
	}

	private void getEmails() {
		pageLinks.stream()
				.forEach(page -> {
					try {
						Document document = connect(page);
						
						getEmailsFromLinkTag(document, page);
						getEmailsFromPlainText(document, page);
					
					} catch (CertificateException e) {
						logger.debug(crawlerId + ": " + e.getMessage());
					} catch (HttpStatusException e) {
						logger.debug(crawlerId + ": " + e.getMessage());
					} catch (UnsupportedMimeTypeException e) {
						logger.debug(crawlerId + ": " + e.getMessage());
					} catch (MalformedURLException e) {
						logger.debug(crawlerId + ": " + e.getMessage());
					} catch (IOException e) {
						logger.debug(crawlerId + ": " + e.getMessage());
					} 	
				});
	}

	private void getEmailsFromLinkTag(Document document, String URL) {
		Elements links = document.select("a[href]");

		links.stream().forEach(link -> {
			Matcher matcher = EMAIL_PATTERN.matcher(link.text());

			getEmailPatternMatchers(matcher, URL);
		});

	}

	private void getEmailsFromPlainText(Document document, String URL) {
		Matcher matcher = EMAIL_PATTERN.matcher(document.text());

		getEmailPatternMatchers(matcher, URL);
	}
	
	private void getEmailPatternMatchers(Matcher matcher, String URL) {
		while (matcher.find()) {
			String email = matcher.group();
			
			logger.debug(crawlerId + ": Email [" + email + "] found at: " + URL);
			
			Page page = crawlerJob.addPage(URL);
			page.addEmail(email);
		}
	}
	
	private void errorHandler(Exception e) {
		crawlerJob.errorJob(e.getMessage());
		crawlerJobDAO.save(crawlerJob);
		
		logger.error(crawlerId + ": " + e.getMessage(), e);
	}
}
