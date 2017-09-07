package com.strd.emailcrawler.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.Valid;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.strd.emailcrawler.util.JobStatus;
import com.strd.emailcrawler.util.UrlFormatter;

/**
 * CrawlerJob entity
 * @author psilveira
 *
 */
@Entity
@Table
public class CrawlerJob implements Serializable {

	private static final long serialVersionUID = 1L;

	private int jobId;
	private String url;
	private Set<Page> pages;
	private Enum<JobStatus> status;
	private LocalDateTime createdDate;
	private LocalDateTime finishedDate;
	private String error;

	public CrawlerJob() {
		this.status = JobStatus.CREATED;
		this.createdDate = LocalDateTime.now();
		this.pages = new HashSet<>();
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public int getJobId() {
		return jobId;
	}

	public void setJobId(int jobId) {
		this.jobId = jobId;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@Column(name = "createdDate")
	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(LocalDateTime createdDate) {
		this.createdDate = createdDate;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@Column(name = "finishedDate")
	public LocalDateTime getFinishedDate() {
		return finishedDate;
	}

	public void setFinishedDate(LocalDateTime finishedDate) {
		this.finishedDate = finishedDate;
	}
	
	@NotEmpty(message = "A website URL is required { url : website }")
	@Valid
	@Column(name = "url")
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		if (url.startsWith("www")) {
			url = "http://" + url;
		}
		
		this.url = url;
	}

	@Column(name = "status")
	public Enum<JobStatus> getStatus() {
		return status;
	}

	public void setStatus(Enum<JobStatus> status) {
		this.status = status;
	}

	@JsonIgnore
	@OneToMany(mappedBy = "job", cascade = CascadeType.ALL, orphanRemoval = true)
	public Set<Page> getPages() {
		return pages;
	}

	public void setPages(Set<Page> pages) {
		this.pages = pages;
	}
	
	@Column(name = "error")
	public String getError() {
		return error;
	}
	
	public void setError(String error) {
		this.error = error;
	}

	public void addPage(Page page) {
		page.setJob(this);
		this.pages.add(page);		
	}

	public Page addPage(String url) {
		Page page = new Page(url, this);
		
		this.pages.add(page);
		
		return page;
	}	
	
	public boolean hasPage(String url) {
		return this.pages.stream()
				.map(page -> page.getUrl())
				.anyMatch(pageUrl -> UrlFormatter.formatUrl(url).equals(UrlFormatter.formatUrl(pageUrl)));
	}

	public void startJob() {
		this.status = JobStatus.PROCESSING;
	}

	public void finishJob() {
		this.status = JobStatus.COMPLETED;
		this.finishedDate = LocalDateTime.now();
	}

	public void errorJob(String error) {
		this.status = JobStatus.ERROR;
		this.finishedDate = LocalDateTime.now();
		this.error = error;
	}

	@Override
	public String toString() {
		return "CrawlerJob [jobId=" + jobId + ", url=" + url + ", status=" + status
				+ ", createdDate=" + createdDate + ", finishedDate=" + finishedDate + ", error=" + error + "]";
	}
	
	
}
