package com.strd.emailcrawler.service;

import com.strd.emailcrawler.exception.APIException;
import com.strd.emailcrawler.model.CrawlerJob;
import com.strd.emailcrawler.model.EmailCrawler;

/**
 * Service API layer that creates job crawlers and retrieves job results
 * @author psilveira
 *
 */
public interface CrawlerServiceAPI {

	/**
	 * Creates a job based on URL received
	 * @param job
	 * @return CrawlerJob
	 */
	CrawlerJob createJob(CrawlerJob job);
	
	/**
	 * Returns job status for a given id
	 * @param jobId
	 * @return CrawlerJob
	 * @throws APIException
	 */
	CrawlerJob getJobStatus(int jobId) throws APIException;
	
	/**
	 * Returns job's result for a given id
	 * @param jobId
	 * @return EmailCrawler
	 * @throws APIException
	 */
	EmailCrawler getJobResult(int jobId) throws APIException;
}
