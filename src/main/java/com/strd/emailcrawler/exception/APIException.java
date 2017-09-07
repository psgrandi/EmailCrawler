package com.strd.emailcrawler.exception;

/**
 * Custom exception that can be thrown by API services
 * @author psilveira
 *
 */
public class APIException extends Exception {
	
	private static final long serialVersionUID = 1L;

	public APIException(String message) {
		super(message);
	}
	
	public APIException(String message, Throwable cause) {
		super(message, cause);
	}


}
