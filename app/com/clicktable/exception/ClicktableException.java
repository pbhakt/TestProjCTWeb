package com.clicktable.exception;

/**
 * Created by a.raina on 10-05-2016.
 */
public class ClicktableException extends RuntimeException {

	public ClicktableException() {
		super();
	}

	public ClicktableException(String message) {
		super(message);
	}

	public ClicktableException(String message, Throwable cause) {
		super(message, cause);
	}
}
