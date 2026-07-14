package com.sgordon.promevo_gmail.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.method.ParameterValidationResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
	private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	@ExceptionHandler(LabelNotFoundException.class)
	public ProblemDetail handleLabelNotFound(LabelNotFoundException e) {
		ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
		problem.setTitle("Label not found");
		problem.setDetail(e.getMessage());

		return problem;
	}

	@ExceptionHandler(DuplicateLabelException.class)
	public ProblemDetail handleDuplicate(DuplicateLabelException e) {
		ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.CONFLICT);
		problem.setTitle("A label with this name already exists!");
		problem.setDetail(e.getMessage());

		return problem;
	}

	@ExceptionHandler(GMailAPIException.class)
	public ProblemDetail handleGoogleFailure(GMailAPIException e) {
		log.error("Gmail failure", e);

		ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_GATEWAY);
		problem.setTitle("An issue occurred with GMail!");
		problem.setDetail(e.getMessage());

		return problem;
	}

	@ExceptionHandler(HandlerMethodValidationException.class)
	public ProblemDetail handleValidation(HandlerMethodValidationException e) {
		//Grab the validator errors
		ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
		problem.setTitle("Validation failed");

		//Grab the validator errors
		List<String> errors = e.getParameterValidationResults()
				.stream()
				.map(ParameterValidationResult::getResolvableErrors)
				.flatMap(List::stream)
				.map(MessageSourceResolvable::getDefaultMessage)
				.toList();
		problem.setProperty("errors", errors);

		return problem;
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ProblemDetail handleValidation(MethodArgumentNotValidException ex) {
		ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
		problem.setTitle("Validation failed");

		//Grab the validator errors
		List<Map<String, String>> errors = ex.getBindingResult()
				.getFieldErrors()
				.stream()
				.map(error -> Map.of(
						"field", error.getField(),
						"message", error.getDefaultMessage()))
				.toList();
		problem.setProperty("errors", errors);

		return problem;
	}

	@ExceptionHandler(Exception.class)
	public ProblemDetail handleUnexpected(Exception e) {
		log.error("Unexpected exception", e);

		ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
		problem.setTitle("An unexpected error occurred.");

		return problem;
	}
}