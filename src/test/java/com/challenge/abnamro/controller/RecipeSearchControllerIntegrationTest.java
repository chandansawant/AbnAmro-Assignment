package com.challenge.abnamro.controller;

import com.challenge.abnamro.RecipeManagerApplication;
import com.challenge.abnamro.exception.ExceptionWrapper;
import com.challenge.abnamro.model.dto.RecipeDTO;
import com.challenge.abnamro.model.searchcriteria.RecipeSearchCriteria;
import com.challenge.abnamro.util.IntegrationTestUtils;
import com.challenge.abnamro.util.TestUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.assertj.core.util.Sets;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import static com.challenge.abnamro.util.IntegrationTestUtils.OBJECT_MAPPER;
import static com.challenge.abnamro.util.IntegrationTestUtils.RECIPES_API_URL;
import static com.challenge.abnamro.util.IntegrationTestUtils.RECIPE_SEARCH_API_URL;
import static com.challenge.abnamro.util.IntegrationTestUtils.REST_TEMPLATE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@ExtendWith(SpringExtension.class)
@SpringBootTest(
		classes = RecipeManagerApplication.class,
		webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@ActiveProfiles("test")
class RecipeSearchControllerIntegrationTest {

	@LocalServerPort
	private int port;

	@ParameterizedTest(name = "invalid search criteria - {2}")
	@MethodSource
	@DirtiesContext
	void test_search_when_search_criteria_is_invalid_then_return_error(
			final RecipeSearchCriteria recipeSearchCriteria,
			final ExceptionWrapper expectedApiError,
			final String testDescription) throws JsonProcessingException {

		//given
		String url = getUrl(RECIPE_SEARCH_API_URL);
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<RecipeSearchCriteria> httpEntityRequest = new HttpEntity<>(recipeSearchCriteria, httpHeaders);

		//when
		ResponseEntity<String> actualResponse
				= REST_TEMPLATE.exchange(url, HttpMethod.POST, httpEntityRequest, String.class);

		//then
		assertEquals(HttpStatus.BAD_REQUEST, actualResponse.getStatusCode());
		assertEquals(MediaType.APPLICATION_JSON, actualResponse.getHeaders().getContentType());
		assertEquals(expectedApiError, OBJECT_MAPPER.readValue(actualResponse.getBody(), ExceptionWrapper.class));
	}

	private static Stream<Arguments> test_search_when_search_criteria_is_invalid_then_return_error() {

		ExceptionWrapper INVALID_REQUEST_CONTENT
				= ExceptionWrapper.builder()
				.httpStatus(HttpStatus.BAD_REQUEST)
				.error("Request validation failed.")
				.description("Request is not valid. Please verify request contents.")
				.build();

		ExceptionWrapper INVALID_SEARCH_RITERIA
				= ExceptionWrapper.builder()
				.httpStatus(HttpStatus.BAD_REQUEST)
				.error("Request validation failed.")
				.description("Search criteria must be valid.")
				.build();

		return Stream.of(
				arguments(null, INVALID_REQUEST_CONTENT, "null criteria"),
				arguments(RecipeSearchCriteria.builder().build(), INVALID_SEARCH_RITERIA, "without any criteria"),
				arguments(
						RecipeSearchCriteria.builder()
								.vegetarian(true)
								.includedIngredients(Collections.emptySet())
								.build(),
						INVALID_SEARCH_RITERIA, "included ingredients set is empty"),
				arguments(
						RecipeSearchCriteria.builder()
								.includedIngredients(Sets.set(TestUtils.INGREDIENT_1))
								.excludedIngredients(Sets.set(TestUtils.INGREDIENT_1))
								.build(),
						INVALID_SEARCH_RITERIA, "common ingredient(s) in included and excluded sets")
		);
	}

	@ParameterizedTest(name = "search criteria = {3}")
	@MethodSource
	@DirtiesContext
	void test_search_by_when_recipes_found_matching_to_search_criteria_then_return_matched_recipes(
			final RecipeSearchCriteria recipeSearchCriteria,
			final Set<RecipeDTO> inputRecipeDTOs,
			final Set<RecipeDTO> expectedResultRecipeDTOs,
			final String testDescription) {

		//given
		Set<RecipeDTO> expectedResult = new HashSet<>();

		for (RecipeDTO inputRecipeDTO : inputRecipeDTOs) {

			ResponseEntity<RecipeDTO> beforeTestResponse = postRecipeDTO(inputRecipeDTO);
			assertEquals(HttpStatus.CREATED, beforeTestResponse.getStatusCode());
			RecipeDTO savedRecipeDTO = beforeTestResponse.getBody();

			if (expectedResultRecipeDTOs.contains(inputRecipeDTO))
				expectedResult.add(savedRecipeDTO);
		}

		//when
		ResponseEntity<RecipeDTO[]> actualResponse = searchForRecipeDTOs(recipeSearchCriteria);

		//then
		if (expectedResultRecipeDTOs.isEmpty())
			assertEquals(HttpStatus.NOT_FOUND, actualResponse.getStatusCode());
		else {
			assertEquals(HttpStatus.OK, actualResponse.getStatusCode());
			assertEquals(MediaType.APPLICATION_JSON, actualResponse.getHeaders().getContentType());
			assertEquals(expectedResult, Sets.set(actualResponse.getBody()));
		}
	}

	private static Stream<Arguments>
	test_search_by_when_recipes_found_matching_to_search_criteria_then_return_matched_recipes() {

		String AND = " and ";
		String stepA = "step A";
		String stepB = "step B";
		String stepC = "step C";
		String stepD = "step D";

		RecipeDTO recipeDTO1
				= TestUtils.getRecipeDTO(1L).toBuilder()
				.vegetarian(true)
				.numberOfServings(1)
				.ingredients(Sets.set(TestUtils.INGREDIENT_DTO_1.toBuilder().build()))
				.instructions(stepA + AND + stepB)
				.build();

		RecipeDTO recipeDTO2
				= TestUtils.getRecipeDTO(2L).toBuilder()
				.vegetarian(false)
				.numberOfServings(2)
				.ingredients(Sets.set(TestUtils.INGREDIENT_DTO_2.toBuilder().build()))
				.instructions(stepB + AND + stepC)
				.build();

		RecipeDTO recipeDTO3
				= TestUtils.getRecipeDTO(3L).toBuilder()
				.vegetarian(true)
				.numberOfServings(1)
				.ingredients(Sets.set(TestUtils.INGREDIENT_DTO_1.toBuilder().build()))
				.instructions(stepC + AND + stepD)
				.build();

		RecipeDTO recipeDTO4
				= TestUtils.getRecipeDTO(4L).toBuilder()
				.vegetarian(false)
				.numberOfServings(2)
				.ingredients(Sets.set(TestUtils.INGREDIENT_DTO_2.toBuilder().build()))
				.instructions(stepD + AND + stepA)
				.build();

		Set<RecipeDTO> inputRecipeDTOs = Sets.set(recipeDTO1, recipeDTO2, recipeDTO3, recipeDTO4);

		return Stream.of(
				arguments(
						RecipeSearchCriteria.builder()
								.numberOfServings(100)
								.build(),
						inputRecipeDTOs,
						Collections.emptySet(),
						"vegetarian (no match)"),
				arguments(
						RecipeSearchCriteria.builder()
								.vegetarian(true)
								.build(),
						inputRecipeDTOs,
						Sets.set(recipeDTO1, recipeDTO3),
						"vegetarian"),
				arguments(
						RecipeSearchCriteria.builder()
								.numberOfServings(2)
								.build(),
						inputRecipeDTOs,
						Sets.set(recipeDTO2, recipeDTO4),
						"number of servings"),
				arguments(
						RecipeSearchCriteria.builder()
								.includedIngredients(Sets.set(TestUtils.INGREDIENT_1.toBuilder().build()))
								.build(),
						inputRecipeDTOs,
						Sets.set(recipeDTO1, recipeDTO3),
						"included ingredients"),
				arguments(
						RecipeSearchCriteria.builder()
								.excludedIngredients(Sets.set(TestUtils.INGREDIENT_1.toBuilder().build()))
								.build(),
						inputRecipeDTOs,
						Sets.set(recipeDTO2, recipeDTO4),
						"excluded ingredients"),
				arguments(
						RecipeSearchCriteria.builder()
								.textInInstructions("step A")
								.build(),
						inputRecipeDTOs,
						Sets.set(recipeDTO1, recipeDTO4),
						"text in instructions"),
				arguments(
						RecipeSearchCriteria.builder()
								.vegetarian(true)
								.textInInstructions("step A")
								.build(),
						inputRecipeDTOs,
						Sets.set(recipeDTO1),
						"vegetarian and text in instructions")
		);
	}

	private ResponseEntity<RecipeDTO[]> searchForRecipeDTOs(final RecipeSearchCriteria recipeSearchCriteria) {

		String url = getUrl(RECIPE_SEARCH_API_URL);
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<RecipeSearchCriteria> httpEntityRequest = new HttpEntity<>(recipeSearchCriteria, httpHeaders);

		return REST_TEMPLATE.exchange(url, HttpMethod.POST, httpEntityRequest, RecipeDTO[].class);
	}

	private ResponseEntity<RecipeDTO> postRecipeDTO(final RecipeDTO recipeDTO) {

		String url = getUrl(RECIPES_API_URL);
		return IntegrationTestUtils.postRecipeDTO(url, recipeDTO);
	}

	private String getUrl(String uri) {
		return IntegrationTestUtils.getLocalUrlForPort(this.port, uri);
	}
}
