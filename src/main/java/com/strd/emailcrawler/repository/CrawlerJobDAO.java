package com.strd.emailcrawler.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.strd.emailcrawler.model.CrawlerJob;

/**
 * DAO interface that performs CRUD operations crawler job domain  
 * @author psilveira
 *
 */
@Repository
public interface CrawlerJobDAO extends CrudRepository<CrawlerJob, Integer> {

}
