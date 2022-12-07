package com.challenge.abnamro.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.http.HttpStatus;

import java.io.Serializable;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
public class ExceptionWrapper implements Serializable {

	public static final ExceptionWrapper INTERNAL_SERVER_ERROR
			= ExceptionWrapper.builder()
			.httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
			.error("Unexpected error")
			.description("System has encountered unexpected error. Kindly contact support.")
			.build();

	private HttpStatus httpStatus;
	private String error;
	private String description;
}
