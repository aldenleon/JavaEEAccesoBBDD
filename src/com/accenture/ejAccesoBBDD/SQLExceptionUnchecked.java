package com.accenture.ejAccesoBBDD;

public class SQLExceptionUnchecked extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public SQLExceptionUnchecked(Throwable cause) {
		super("Unchecked wrapper for SQLException - ", cause);
	}

	public SQLExceptionUnchecked(String message, Throwable cause) {
		super(message, cause);
	}
	
	public SQLExceptionUnchecked(String message) {
		super(message);
	}
	
}
