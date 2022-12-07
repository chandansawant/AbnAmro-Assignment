package com.challenge.abnamro.controlleradvice;

import com.challenge.abnamro.exception.ApplicationException;
import com.challenge.abnamro.exception.ExceptionWrapper;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
@ResponseBody
public class ApplicationExceptionControllerAdvice {

	@ExceptionHandler(ApplicationException.RecipeCreationFailedException.class)
	public ResponseEntity<ExceptionWrapper> handleException(final ApplicationException.RecipeCreationFailedException ex) {

		ExceptionWrapper exceptionWrapper
				= ExceptionWrapper.builder()
				.httpStatus(HttpStatus.UNPROCESSABLE_ENTITY)
				.error("Recipe creation failed")
				.description(ex.getMessage())
				.build();

		return ResponseEntity
				.status(HttpStatus.UNPROCESSABLE_ENTITY)
				.body(exceptionWrapper);
	}

	@ExceptionHandler(ApplicationException.RecipeUpdateFailedException.class)
	public ResponseEntity<ExceptionWrapper> handleException(final ApplicationException.RecipeUpdateFailedException ex) {

		ExceptionWrapper exceptionWrapper
				= ExceptionWrapper.builder()
				.httpStatus(HttpStatus.UNPROCESSABLE_ENTITY)
				.error("Recipe update failed")
				.description(ex.getMessage())
				.build();

		return ResponseEntity
				.status(HttpStatus.UNPROCESSABLE_ENTITY)
				.body(exceptionWrapper);
	}

	@ExceptionHandler(ApplicationException.RecipeNotFoundException.class)
	public ResponseEntity<Void> handleException(final ApplicationException.RecipeNotFoundException ex) {

		return ResponseEntity.notFound().build();
	}

	@ExceptionHandler(ApplicationException.RecipeSearchCriteriaNotFoundException.class)
	public ResponseEntity<ExceptionWrapper> handleException(
			final ApplicationException.RecipeSearchCriteriaNotFoundException ex) {

		ExceptionWrapper exceptionWrapper
				= ExceptionWrapper.builder()
				.httpStatus(HttpStatus.BAD_REQUEST)
				.error("No search criteria provided.")
				.description("No search criteria provided.")
				.build();

		return ResponseEntity
				.status(HttpStatus.BAD_REQUEST)
				.body(exceptionWrapper);
	}
}
