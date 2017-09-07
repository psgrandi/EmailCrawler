package com.strd.emailcrawler.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.strd.emailcrawler.main.Application;

@SpringBootApplication
@ComponentScan(basePackages = "com.strd.emailcrawler")	
@EnableAutoConfiguration
@EnableJpaRepositories(basePackages = "com.strd.emailcrawler.repository")
@EntityScan(basePackages = "com.strd.emailcrawler.model")
public class Application {

	public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
	
}
