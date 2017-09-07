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
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * EmailAddress that contains emails found during page crawling
 * @author psilveira
 *
 */
@Entity
@Table
public class EmailAddress implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@JsonIgnore
	private Integer emailId;
	private String emailAddress;
	@JsonIgnore
	private Set<Page> pages;

	public EmailAddress() {
		this.pages = new HashSet<>();
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getEmailId() {
		return emailId;
	}
	
	public void setEmailId(Integer emailId) {
		this.emailId = emailId;
	}

	@Column(name = "emailAddress")
	public String getEmailAddress() {
		return emailAddress;
	}
	
	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}
	
	@ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	@JoinTable(name = "page_l_email", 
				joinColumns = @JoinColumn(name = "emailId", referencedColumnName = "emailId"),
				inverseJoinColumns = @JoinColumn(name = "pageId", referencedColumnName = "pageId"))
	public Set<Page> getPages() {
		return pages;
	}

	public void setPages(Set<Page> pages) {
		this.pages = pages;
	}
	
	public void addPage(Page page) {
		this.getPages().add(page);
	}

	@Override
	public String toString() {
		return "EmailAddress [emailId=" + emailId + ", emailAddress=" + emailAddress + "]";
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((emailId == null) ? 0 : emailId.hashCode());
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
		EmailAddress other = (EmailAddress) obj;
		if (emailAddress == null) {
			if (other.emailAddress != null)
				return false;
		} else if (!emailAddress.toLowerCase().equals(other.emailAddress.toLowerCase()))
			return false;
		return true;
	}
}
