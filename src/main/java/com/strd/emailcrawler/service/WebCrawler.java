package com.strd.emailcrawler.service;

import com.strd.emailcrawler.model.CrawlerJob;

/**
 * Component that perform web crawling activities
 * @author psilveira
 *
 */
public interface WebCrawler {
	
	/**
	 * Execute web crawling on an URL from a CrawlerJob
	 * @param job
	 */
	void execute(CrawlerJob job);

}
