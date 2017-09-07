package com.strd.emailcrawler.test.controller;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashMap;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.google.gson.Gson;
import com.strd.emailcrawler.model.CrawlerJob;
import com.strd.emailcrawler.model.EmailCrawler;
import com.strd.emailcrawler.model.Page;
import com.strd.emailcrawler.service.CrawlerServiceAPI;
import com.strd.emailcrawler.test.main.ApplicationTest;
import com.strd.emailcrawler.util.JobStatus;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = ApplicationTest.class)
@AutoConfigureMockMvc
public class EmailCrawlerControllerTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
    private CrawlerServiceAPI crawlerService;
	
	@Test
	public void shouldReturnCrawlerJobCreated() throws Exception {
		
		CrawlerJob crawlerJob = new CrawlerJob();
		crawlerJob.setUrl("http://www.testpage.com/");
		
		HashMap<String,String> objectToSend = new HashMap<>();
		objectToSend.put("url", crawlerJob.getUrl());
				
		Mockito.when(
				crawlerService.createJob(crawlerJob)).thenReturn(crawlerJob);
		
		mockMvc.perform(post("/api/crawler")
			      .contentType(MediaType.APPLICATION_JSON_VALUE)
				  .content(new Gson().toJson(objectToSend)))
			      .andExpect(status().isOk())
			      .andExpect(jsonPath("$.status", is(JobStatus.CREATED.toString())))
			      .andDo(print());
	}
	
	@Test
	public void shouldReturnErrorUrlIsRequired() throws Exception {
		
		CrawlerJob crawlerJob = new CrawlerJob();
		crawlerJob.setUrl("http://www.testpage.com/");
		
		HashMap<String,String> objectToSend = new HashMap<>();
		objectToSend.put("url", null);
				
		Mockito.when(
				crawlerService.createJob(crawlerJob)).thenReturn(crawlerJob);
		
		mockMvc.perform(post("/api/crawler")
			      .contentType(MediaType.APPLICATION_JSON_VALUE)
				  .content(new Gson().toJson(objectToSend)))
			      .andExpect(status().is(400))
			      .andExpect(jsonPath("$.status", is(JobStatus.ERROR.toString())))
			      .andExpect(jsonPath("$.error", is("A WEBSITE URL IS REQUIRED { URL : WEBSITE }")))
			      .andDo(print());
	}
	
	@Test
	public void shouldReturnStatusProcessing() throws Exception {
		
		CrawlerJob crawlerJob = new CrawlerJob();
		crawlerJob.setUrl("http://www.testpage.com/");
		crawlerJob.startJob();
		
		Integer jobId = 1;
				
		Mockito.when(
				crawlerService.getJobStatus(jobId)).thenReturn(crawlerJob);
		
		mockMvc.perform(get("/api/crawler/checkStatus/" + jobId)
			      .contentType(MediaType.APPLICATION_JSON_VALUE))
			      .andExpect(status().is(200))
			      .andExpect(jsonPath("$.status", is(JobStatus.PROCESSING.toString())))
			      .andDo(print());
	}
	
	@Test
	public void shouldReturnJobResult() throws Exception {
		
		CrawlerJob crawlerJob = new CrawlerJob();
		crawlerJob.setUrl("http://www.testpage.com/");
		
		Page page = new Page("http://www.testpage.com/", crawlerJob);
		page.addEmail("pedro.silveira.grandi@gmail.com");
		
		crawlerJob.addPage(page);
		
		crawlerJob.finishJob();
		
		EmailCrawler result = new EmailCrawler();
		result.setJob(crawlerJob);
		result.setPages(crawlerJob.getPages());
		
		Integer jobId = 1;
				
		Mockito.when(
				crawlerService.getJobResult(jobId)).thenReturn(result);
		
		mockMvc.perform(get("/api/crawler/getResult/" + jobId)
			      .contentType(MediaType.APPLICATION_JSON_VALUE))
			      .andExpect(status().is(200))
			      .andExpect(jsonPath("$.job.status", is(JobStatus.COMPLETED.toString())))
			      .andExpect(jsonPath("$.pages[0].url", is("http://www.testpage.com/")))
			      .andExpect(jsonPath("$.pages[0].emails[0].emailAddress", is("pedro.silveira.grandi@gmail.com")))
			      .andDo(print());
	}
}
