package com.challenge.abnamro.controlleradvice;

import com.challenge.abnamro.exception.ExceptionWrapper;
import org.apache.logging.log4j.util.Strings;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
@ResponseBody
public class ApiExceptionControllerAdvice {

	private static final String REQUEST_VALIDATION_FAILED = "Request validation failed.";

	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public ResponseEntity<ExceptionWrapper> handleException(final HttpRequestMethodNotSupportedException ex) {

		String supportedMethods
				= String.join(", ",
				HttpMethod.POST.name(), HttpMethod.PUT.name(), HttpMethod.GET.name(), HttpMethod.DELETE.name());

		ExceptionWrapper exceptionWrapper
				= ExceptionWrapper.builder()
				.httpStatus(HttpStatus.METHOD_NOT_ALLOWED)
				.error("Method not supported")
				.description("Supported methods are - " + supportedMethods)
				.build();

		return ResponseEntity
				.status(HttpStatus.METHOD_NOT_ALLOWED)
				.header(HttpHeaders.ALLOW, supportedMethods)
				.body(exceptionWrapper);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ExceptionWrapper> handleException(final MethodArgumentNotValidException ex) {

		String errorDescription
				= ex.getBindingResult().getAllErrors().stream()
				.map(DefaultMessageSourceResolvable::getDefaultMessage)
				.reduce(Strings.EMPTY, Strings::concat);

		ExceptionWrapper exceptionWrapper
				= ExceptionWrapper.builder()
				.httpStatus(HttpStatus.BAD_REQUEST)
				.error(REQUEST_VALIDATION_FAILED)
				.description(errorDescription)
				.build();

		return ResponseEntity
				.status(HttpStatus.BAD_REQUEST)
				.body(exceptionWrapper);
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<ExceptionWrapper> handleException(final MethodArgumentTypeMismatchException ex) {

		ExceptionWrapper exceptionWrapper
				= ExceptionWrapper.builder()
				.httpStatus(HttpStatus.BAD_REQUEST)
				.error(REQUEST_VALIDATION_FAILED)
				.description("Data validation failed.")
				.build();

		return ResponseEntity
				.status(HttpStatus.BAD_REQUEST)
				.body(exceptionWrapper);
	}

	@ExceptionHandler(HttpMessageConversionException.class)
	public ResponseEntity<ExceptionWrapper> handleException(final HttpMessageConversionException ex) {

		ExceptionWrapper exceptionWrapper
				= ExceptionWrapper.builder()
				.httpStatus(HttpStatus.BAD_REQUEST)
				.error(REQUEST_VALIDATION_FAILED)
				.description("Request is not valid. Please verify request contents.")
				.build();

		return ResponseEntity
				.status(HttpStatus.BAD_REQUEST)
				.body(exceptionWrapper);
	}
}
