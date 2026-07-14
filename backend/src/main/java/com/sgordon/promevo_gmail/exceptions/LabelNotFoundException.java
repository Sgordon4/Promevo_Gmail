package com.sgordon.promevo_gmail.exceptions;

public class LabelNotFoundException extends RuntimeException {
	public LabelNotFoundException(String id) {
		super("Label '%s' was not found.".formatted(id));
	}
}