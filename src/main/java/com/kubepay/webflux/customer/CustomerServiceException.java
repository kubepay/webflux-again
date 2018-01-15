package com.kubepay.webflux.customer;

import org.springframework.http.HttpStatus;

public class CustomerServiceException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3356987764618886267L;
	private final HttpStatus httpStatus;
	private final String message;

	public CustomerServiceException(HttpStatus httpStatus, String message) {
		this.httpStatus = httpStatus;
		this.message = message;
	}

	public HttpStatus getHttpStatus() {
		return httpStatus;
	}
	
	public String getMessage() {
		return message;
	}

}
