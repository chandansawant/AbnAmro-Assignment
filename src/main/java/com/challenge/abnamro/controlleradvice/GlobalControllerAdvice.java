package com.challenge.abnamro.controlleradvice;

import com.challenge.abnamro.exception.ExceptionWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Import({
		ApiExceptionControllerAdvice.class,
		ApplicationExceptionControllerAdvice.class})
@RestControllerAdvice
@Slf4j
public class GlobalControllerAdvice {

	@ExceptionHandler(Throwable.class)
	public ResponseEntity<ExceptionWrapper> handleException(final Throwable ex) {

		log.error("Unexpected error - {}", ex.getMessage());

		return ResponseEntity
				.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(ExceptionWrapper.INTERNAL_SERVER_ERROR);
	}
}
