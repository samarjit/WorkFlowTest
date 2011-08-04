package com.ycs.wfl.exception;

public class WflException extends Exception {
	
	private static final long serialVersionUID = 1L;
	
	private String exceptionString;
	
	public WflException() {
		super();
		exceptionString = "WflException";
	}

	public WflException(String message) {
		super(message);
		exceptionString = message;
	}

}
