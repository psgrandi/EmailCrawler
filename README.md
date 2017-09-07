# Email Crawler Project #

	REST project that creates web crawler's job
		
	This project includes:
	- Java 8
	- Maven
	- Spring Boot
	- Derby DB

# How to build #

	mvn clean install

# How to run #

	mvn spring-boot:run

# End points #

https://swaggerhub.com/apis/pedrograndi/EmailCrawler/1.0.0

## Create new job ##
	```
	/api/crawler
	```
	```
	HTTP Method: POST
	```
	
	```
	Parameters:
	```
	```
	#!json
	
	{
		"url" : "www.testurl.com"
	}
	```
	
## Check job status ##
	```
	/api/crawler/checkStatus/{jobId}
	```
	```
	HTTP Method: GET
	```
	```
	#!json
	
	{
		"url" : "www.testurl.com"
	}
	```
	

## Get job result ##
		```
	/api/crawler/getResult/{jobId}
	```
	```
	HTTP Method: GET
	```