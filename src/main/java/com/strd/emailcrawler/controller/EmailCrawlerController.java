package com.strd.emailcrawler.controller;

import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.strd.emailcrawler.error.ResponseError;
import com.strd.emailcrawler.error.builder.ValidationErrorBuilder;
import com.strd.emailcrawler.exception.APIException;
import com.strd.emailcrawler.model.CrawlerJob;
import com.strd.emailcrawler.model.EmailCrawler;
import com.strd.emailcrawler.service.CrawlerServiceAPI;

/**
 * REST Controller that exposes endpoints to create web crawler jobs, job's status and results.
 * 
 * @author psilveira
 *
 */
@Scope("request")
@RestController
@RequestMapping("/api")
public class EmailCrawlerController {
	
	@Autowired
	private CrawlerServiceAPI crawlerService;

	@RequestMapping(value = "/test", method = RequestMethod.GET)
	public ResponseEntity<?> test() {
		return new ResponseEntity<String>("Email Crawler Rest Service", HttpStatus.OK);
	}

	/**
	 * Endpoint that receives an URL and creates a job to crawl the website and extract email addresses.
	 * 
	 * @param job URL to be crawl
	 * @param errors Validations errors
	 * @return CrawlerJob
	 */
	@RequestMapping(value = "/crawler", 
			method = RequestMethod.POST,
			consumes = {MediaType.APPLICATION_JSON_VALUE},
			produces = {MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<?> createEmailCrawler(@Valid @RequestBody CrawlerJob job, Errors errors) {
		
		if (errors.hasErrors()) {
			
			String errorMessage = ValidationErrorBuilder.fromBindingErrors(errors)
					.getErrors()
					.stream()
					.map(String::toUpperCase)
                    .collect(Collectors.joining(";"));
					
			job.errorJob(errorMessage);
			
			return new ResponseEntity<CrawlerJob>(job, HttpStatus.BAD_REQUEST);
		}
		
		crawlerService.createJob(job);
		
		return new ResponseEntity<Object>(job, HttpStatus.OK);
	}

	/**
	 * Endpoint that receives a jobId and returns the status.
	 * @param jobId
	 * @return CrawlerJob
	 */
	@RequestMapping(value = "/crawler/checkStatus/{jobId}", 
			method = RequestMethod.GET,
			produces = {MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<?> getCrawlerStatus(@PathVariable(value="jobId") Integer jobId) {
		CrawlerJob job;

		try {
			job = crawlerService.getJobStatus(jobId);
		} catch(APIException e) {
			return new ResponseEntity<ResponseError>(new ResponseError(e.getMessage()), HttpStatus.BAD_REQUEST);
		}
		
		return new ResponseEntity<CrawlerJob>(job, HttpStatus.OK);
	}
	
	/**
	 * Endpoint that receives a jobId and if the job is completed returns the emails found on each page. 
	 * @param jobId
	 * @return EmailCrawler
	 */
	@RequestMapping(value = "/crawler/getResult/{jobId}",
			method = RequestMethod.GET,
			produces = {MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<?> getCrawlerResult(@PathVariable(value="jobId") Integer jobId) {
		EmailCrawler result;		

		try {
			result = crawlerService.getJobResult(jobId);
		} catch(APIException e) {
			return new ResponseEntity<ResponseError>(new ResponseError(e.getMessage()), HttpStatus.BAD_REQUEST);
		}

		return new ResponseEntity<EmailCrawler>(result, HttpStatus.OK);
	}
}
