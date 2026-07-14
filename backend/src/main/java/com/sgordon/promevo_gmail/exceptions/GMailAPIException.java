package com.sgordon.promevo_gmail.exceptions;

public class GMailAPIException extends RuntimeException {
	private final Integer status;

	public GMailAPIException(Integer status, String message, Throwable cause) {
		super(message, cause);
		this.status = status;
	}

	public Integer getStatus() {
		return status;
	}
}
