package com.challenge.abnamro.exception;

public class ApplicationException {

	public static class RecipeCreationFailedException extends RuntimeException {

		public RecipeCreationFailedException(String message) {
			super(message);
		}
	}

	public static class RecipeUpdateFailedException extends RuntimeException {

		public RecipeUpdateFailedException(String message) {
			super(message);
		}
	}

	public static class RecipeNotFoundException extends RuntimeException {

		public RecipeNotFoundException(String message) {
			super(message);
		}
	}

	public static class RecipeSearchCriteriaNotFoundException extends RuntimeException {

		public RecipeSearchCriteriaNotFoundException(String message) {
			super(message);
		}
	}
}
