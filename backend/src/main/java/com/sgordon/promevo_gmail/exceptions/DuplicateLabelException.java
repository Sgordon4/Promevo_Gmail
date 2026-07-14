package com.sgordon.promevo_gmail.exceptions;

public class DuplicateLabelException extends RuntimeException {
	public DuplicateLabelException(String name) {
		super("A label named '%s' already exists.".formatted(name));
	}
}