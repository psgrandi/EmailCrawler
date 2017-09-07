package com.strd.emailcrawler.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.strd.emailcrawler.exception.APIException;
import com.strd.emailcrawler.model.CrawlerJob;
import com.strd.emailcrawler.model.EmailCrawler;
import com.strd.emailcrawler.repository.CrawlerJobDAO;
import com.strd.emailcrawler.util.JobStatus;

/**
 * Implementation of CrawlerServiceAPI
 * @author psilveira
 *
 */
@Scope("prototype")
@Service
public class CrawlerServiceAPIImpl implements CrawlerServiceAPI {
	
	@Autowired
	private CrawlerJobDAO crawlerJobDAO;
	
	@Autowired
	private WebCrawler webCrawler;
		
	@Override
	public CrawlerJob createJob(CrawlerJob job) {
		
		try {
			crawlerJobDAO.save(job);
		} catch(Exception e) {
			job.setError(e.getMessage());
		}
		
		webCrawler.execute(job);
		
		return job;
	}
	
	@Override
	public CrawlerJob getJobStatus(int jobId) throws APIException {
		CrawlerJob job = findJobById(jobId);
				
		return job;
	}

	@Override
	public EmailCrawler getJobResult(int jobId) throws APIException {
		CrawlerJob job = findJobById(jobId);
		
		checkIfJobIsFinished(job);
		
		EmailCrawler emailCrawler = new EmailCrawler();
		emailCrawler.setJob(job);
		emailCrawler.setPages(job.getPages());
				
		return emailCrawler;
	}
	
	private CrawlerJob findJobById(Integer jobId) throws APIException {
		CrawlerJob job = crawlerJobDAO.findOne(jobId);
		
		if (job == null) {
			throw new APIException("There is no job with ID: " + jobId);
		}
		
		return job;
	}
	
	private void checkIfJobIsFinished(CrawlerJob job) throws APIException {
		if ((!job.getStatus().equals(JobStatus.COMPLETED)) && (!job.getStatus().equals(JobStatus.ERROR))) {
			throw new APIException("Job still not finshed. Please use /checkStatus service");
		}		
	}
}