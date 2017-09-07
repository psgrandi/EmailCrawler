package com.strd.emailcrawler.test.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.strd.emailcrawler")	
public class ApplicationTest {
	public static void main(String[] args) {
		SpringApplication.run(ApplicationTest.class, args);
	}
}
