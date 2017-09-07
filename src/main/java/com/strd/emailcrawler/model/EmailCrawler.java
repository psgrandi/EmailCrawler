package com.strd.emailcrawler.model;

import java.util.Set;

/**
 * Entity with the result from page crawling
 * @author psilveira
 *
 */
public class EmailCrawler {

	private CrawlerJob job;
	private Set<Page> pages;

	public EmailCrawler() {
	}

	public CrawlerJob getJob() {
		return job;
	}

	public void setJob(CrawlerJob job) {
		this.job = job;
	}

	public Set<Page> getPages() {
		return pages;
	}

	public void setPages(Set<Page> pages) {
		this.pages = pages;
	}
}
