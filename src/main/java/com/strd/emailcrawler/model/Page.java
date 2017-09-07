package com.strd.emailcrawler.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.strd.emailcrawler.util.UrlFormatter;

/**
 * Entity with pages that was crawled
 * @author psilveira
 *
 */
@Entity
@Table
public class Page implements Serializable {

	private static final long serialVersionUID = 1L;

	@JsonIgnore
	private Integer pageId;
	private String url;
	private Set<EmailAddress> emails;
	@JsonIgnore
	private CrawlerJob job;

	public Page() {
		this.emails = new HashSet<>();
	}
	
	public Page(String url, CrawlerJob job) {
		this.emails = new HashSet<>();
		this.url = url;
		this.job = job;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getPageId() {
		return pageId;
	}

	public void setPageId(Integer pageId) {
		this.pageId = pageId;
	}

	@Column(name = "url")
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@ManyToMany(mappedBy = "pages", cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	public Set<EmailAddress> getEmails() {
		return emails;
	}

	public void setEmails(Set<EmailAddress> emails) {
		this.emails = emails;
	}

	public void addEmail(EmailAddress email) {
		email.addPage(this);
		this.emails.add(email);		
	}

	public void addEmail(String address) {
		EmailAddress email = new EmailAddress();
		email.setEmailAddress(address);
		email.addPage(this);
		
		this.emails.add(email);		
	}

	@ManyToOne
	public CrawlerJob getJob() {
		return job;
	}

	public void setJob(CrawlerJob job) {
		this.job = job;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((pageId == null) ? 0 : pageId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Page other = (Page) obj;		
		if (url == null) {
			if (other.url != null)
				return false;
		} else if (!UrlFormatter.formatUrl(url).equals(UrlFormatter.formatUrl(other.url))) {
			return false;
		}
		return true;
	}
}
