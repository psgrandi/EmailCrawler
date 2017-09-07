package com.strd.emailcrawler.error;

/**
 * Class that wrap error messages to be returned as JSON
 * @author psilveira
 *
 */
public class ResponseError {

	private final String error;

	public ResponseError(String error) {
		this.error = error;
	}
	
	public String getError() {
		return error;
	}	
}
