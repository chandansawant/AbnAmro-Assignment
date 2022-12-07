package com.challenge.abnamro.controller;

import com.challenge.abnamro.exception.ApplicationException;
import com.challenge.abnamro.exception.ExceptionWrapper;
import com.challenge.abnamro.model.dto.IngredientDTO;
import com.challenge.abnamro.model.dto.RecipeDTO;
import com.challenge.abnamro.service.RecipeService;
import com.challenge.abnamro.util.TestUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.util.Strings;
import org.assertj.core.util.Lists;
import org.assertj.core.util.Sets;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.endsWith;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RecipeController.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@ActiveProfiles("test")
class RecipeControllerTest {

	private static final String RECIPES_API_URL = ApiConstants.Version.V_1_0 + ApiConstants.Endpoints.RECIPES;
	private static final ObjectMapper OBJECT_MAPPER;
	private static final String INTERNAL_SERVER_ERROR_JSON;

	static {
		OBJECT_MAPPER = new ObjectMapper();

		try {
			INTERNAL_SERVER_ERROR_JSON = OBJECT_MAPPER.writeValueAsString(ExceptionWrapper.INTERNAL_SERVER_ERROR);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private RecipeService mockRecipeService;

	/*
	 * Tests for POST method
	 */
	@Test
	void test_POST_when_correct_input_then_create_recipe() throws Exception {

		//given
		RecipeDTO inputRecipeDTO = TestUtils.RECIPE_DTO.toBuilder().build();

		IngredientDTO ingredientDTO1
				= TestUtils.INGREDIENT_DTO_1.toBuilder()
				.id(1L)
				.build();

		IngredientDTO ingredientDTO2
				= TestUtils.INGREDIENT_DTO_2.toBuilder()
				.id(2L)
				.build();

		RecipeDTO createdRecipeDTO
				= TestUtils.RECIPE_DTO.toBuilder()
				.id(1L)
				.ingredients(Sets.set(ingredientDTO1, ingredientDTO2))
				.build();

		when(this.mockRecipeService.create(inputRecipeDTO))
				.thenReturn(createdRecipeDTO);

		//when
		ResultActions resultActions
				= this.mockMvc.perform(
						post(RECIPES_API_URL)
								.contentType(MediaType.APPLICATION_JSON_VALUE)
								.content(OBJECT_MAPPER.writeValueAsString(inputRecipeDTO)));

		//then
		resultActions
				.andExpect(status().isCreated())
				.andExpect(header()
						.string(HttpHeaders.LOCATION,
								endsWith(RECIPES_API_URL + "/" + createdRecipeDTO.getId())))
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(content().string(OBJECT_MAPPER.writeValueAsString(createdRecipeDTO)));
	}

	@ParameterizedTest(name = "POST " + RECIPES_API_URL + " - when {3}")
	@MethodSource
	void test_POST_when_incorrect_input_then_return_error(
			final RecipeDTO invalidRecipe,
			final boolean validateResponseBody,
			final String expectedErrorMessage,
			final String testDescription
	) throws Exception {

		//given
		ExceptionWrapper expectedApiError
				= ExceptionWrapper.builder()
					.httpStatus(HttpStatus.BAD_REQUEST)
					.error("Request validation failed.")
					.description(expectedErrorMessage)
					.build();

		//when
		ResultActions resultActions
				= this.mockMvc.perform(
						post(RECIPES_API_URL)
								.contentType(MediaType.APPLICATION_JSON_VALUE)
								.content(OBJECT_MAPPER.writeValueAsString(invalidRecipe)));

		//then
		resultActions
				.andExpect(status().isBadRequest())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE));

		if (validateResponseBody)
			resultActions
					.andExpect(content().string(OBJECT_MAPPER.writeValueAsString(expectedApiError)));
	}

	private static Stream<Arguments> test_POST_when_incorrect_input_then_return_error() {

		return getStreamOfIncorrectRecipeDTO();
	}

	@ParameterizedTest(name = "POST " + RECIPES_API_URL + " - when service throws {1}")
	@MethodSource
	void test_POST_when_application_exception_in_service_layer_then_return_error(
			final RuntimeException runtimeException,
			final String testDescription
	) throws Exception {

		//given
		RecipeDTO inputRecipeDTO = TestUtils.RECIPE_DTO.toBuilder().build();

		ExceptionWrapper expectedApiError
				= ExceptionWrapper.builder()
				.httpStatus(HttpStatus.UNPROCESSABLE_ENTITY)
				.error("Recipe creation failed")
				.description(runtimeException.getMessage())
				.build();

		when(this.mockRecipeService.create(any(RecipeDTO.class)))
				.thenThrow(runtimeException);

		//when
		ResultActions resultActions
				= this.mockMvc.perform(
						post(RECIPES_API_URL)
								.contentType(MediaType.APPLICATION_JSON_VALUE)
								.content(OBJECT_MAPPER.writeValueAsString(inputRecipeDTO)));

		//then
		resultActions
				.andExpect(status().isUnprocessableEntity())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(content().string(OBJECT_MAPPER.writeValueAsString(expectedApiError)));
	}

	private static Stream<Arguments> test_POST_when_application_exception_in_service_layer_then_return_error() {

		return Stream.of(
				arguments(
						new ApplicationException.RecipeCreationFailedException(
								"Failed to create and save recipe to database"),
						ApplicationException.RecipeCreationFailedException.class.getSimpleName())
		);
	}

	@ParameterizedTest(name = "POST " + RECIPES_API_URL + " - when service throws {1}")
	@MethodSource
	void test_POST_when_non_application_exception_in_service_layer_then_return_error(
			final RuntimeException runtimeException,
			final String testDescription
	) throws Exception {

		//given
		RecipeDTO inputRecipeDTO = TestUtils.RECIPE_DTO.toBuilder().build();

		when(this.mockRecipeService.create(any(RecipeDTO.class)))
				.thenThrow(runtimeException);

		//when
		ResultActions resultActions
				= this.mockMvc.perform(
						post(RECIPES_API_URL)
								.contentType(MediaType.APPLICATION_JSON_VALUE)
								.content(OBJECT_MAPPER.writeValueAsString(inputRecipeDTO)));

		//then
		resultActions
				.andExpect(status().isInternalServerError())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(content().string(INTERNAL_SERVER_ERROR_JSON));
	}

	private static Stream<Arguments> test_POST_when_non_application_exception_in_service_layer_then_return_error() {

		return getNonApplicationExceptionsForServiceLayer();
	}

	/*
	 * Tests for unsupported method
	 */
	@Test
	void test_when_method_not_allowed_then_return_error() throws Exception {
		//given
		RecipeDTO inputRecipeDTO
				= RecipeDTO.builder()
					.name("test recipe")
					.vegetarian(true)
					.build();

		String supportedMethods
				= String.join(", ",
				HttpMethod.POST.name(), HttpMethod.PUT.name(), HttpMethod.GET.name(), HttpMethod.DELETE.name());

		ExceptionWrapper expectedApiError
				= ExceptionWrapper.builder()
				.httpStatus(HttpStatus.METHOD_NOT_ALLOWED)
				.error("Method not supported")
				.description("Supported methods are - " + supportedMethods)
				.build();

		//when
		ResultActions resultActions
				= this.mockMvc.perform(
						patch(RECIPES_API_URL)
								.contentType(MediaType.APPLICATION_JSON_VALUE)
								.content(OBJECT_MAPPER.writeValueAsString(inputRecipeDTO)));

		//then
		resultActions
				.andExpect(status().isMethodNotAllowed())
				.andExpect(header().string(HttpHeaders.ALLOW,supportedMethods))
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(content().string(OBJECT_MAPPER.writeValueAsString(expectedApiError)));;
	}

	/*
	 * Tests for GET method
	 */
	@Test
	void test_GET_when_no_id_provided_then_return_all_available_recipes() throws Exception {

		//given
		RecipeDTO recipeDTO1
				= RecipeDTO.builder()
				.id(1L)
				.name("Recipe 1")
				.vegetarian(true)
				.build();

		RecipeDTO recipeDTO2
				= RecipeDTO.builder()
				.id(2L)
				.name("Recipe 2")
				.vegetarian(false)
				.build();

		List<RecipeDTO> expectedResult = Lists.list(recipeDTO1, recipeDTO2);

		when(this.mockRecipeService.getAll())
				.thenReturn(expectedResult);

		//when
		ResultActions resultActions = this.mockMvc.perform(get(RECIPES_API_URL));

		//then
		resultActions
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(content().string(OBJECT_MAPPER.writeValueAsString(expectedResult)));
	}

	@Test
	void test_GET_when_no_id_provided_and_no_data_is_available_then_return_no_data_found_error() throws Exception {

		//given
		when(this.mockRecipeService.getAll())
				.thenThrow(new ApplicationException.RecipeNotFoundException("No recipe fond."));

		//when
		ResultActions resultActions = this.mockMvc.perform(get(RECIPES_API_URL));

		//then
		resultActions
				.andExpect(status().isNotFound())
				.andExpect(content().string(emptyOrNullString()));
	}

	@ParameterizedTest(name = "POST " + RECIPES_API_URL + " - when service throws {1}")
	@MethodSource
	void test_GET_when_when_no_id_provided_and_non_application_exception_in_service_layer_then_return_error(
			final RuntimeException runtimeException,
			final String testDescription
	) throws Exception {

		//given
		when(this.mockRecipeService.getAll())
				.thenThrow(runtimeException);

		//when
		ResultActions resultActions = this.mockMvc.perform(get(RECIPES_API_URL));

		//then
		resultActions
				.andExpect(status().isInternalServerError())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(content().string(INTERNAL_SERVER_ERROR_JSON));
	}

	private static Stream<Arguments> test_GET_when_when_no_id_provided_and_non_application_exception_in_service_layer_then_return_error() {

		return getNonApplicationExceptionsForServiceLayer();
	}

	@Test
	void test_GET_when_id_is_provided_and_data_is_available_then_return_data() throws Exception {

		//given
		long recipeId = 1L;

		RecipeDTO expectedResult
				= RecipeDTO.builder()
				.id(recipeId)
				.name("Recipe " + recipeId)
				.vegetarian(true)
				.build();

		when(this.mockRecipeService.get(recipeId))
				.thenReturn(expectedResult);

		//when
		ResultActions resultActions = this.mockMvc.perform(get(RECIPES_API_URL + "/" + recipeId));

		//then
		resultActions
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(content().string(OBJECT_MAPPER.writeValueAsString(expectedResult)));
	}

	@Test
	void test_GET_when_id_is_provided_and_no_data_is_available_then_return_no_data_found_error() throws Exception {

		//given
		long recipeId = 1L;

		when(this.mockRecipeService.get(recipeId))
				.thenThrow(new ApplicationException.RecipeNotFoundException("Recipe not found"));

		//when
		ResultActions resultActions = this.mockMvc.perform(get(RECIPES_API_URL + "/" + recipeId));

		//then
		resultActions
				.andExpect(status().isNotFound())
				.andExpect(content().string(emptyOrNullString()));
	}

	@ParameterizedTest(name = "GET " + RECIPES_API_URL + " - when service throws {1}")
	@MethodSource
	void test_GET_when_when_id_is_provided_and_non_application_exception_in_service_layer_then_return_error(
			final RuntimeException runtimeException,
			final String testDescription
	) throws Exception {

		//given
		long recipeId = 1L;

		when(this.mockRecipeService.get(recipeId))
				.thenThrow(runtimeException);

		//when
		ResultActions resultActions = this.mockMvc.perform(get(RECIPES_API_URL + "/" + recipeId));

		//then
		resultActions
				.andExpect(status().isInternalServerError())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(content().string(INTERNAL_SERVER_ERROR_JSON));
	}

	private static Stream<Arguments> test_GET_when_when_id_is_provided_and_non_application_exception_in_service_layer_then_return_error() {

		return getNonApplicationExceptionsForServiceLayer();
	}

	/*
	 * Tests for PUT method
	 */
	@Test
	void test_PUT_when_correct_input_but_recipe_do_not_exists_then_create_recipe() throws Exception {

		//given
		long recipeId = 1;

		RecipeDTO inputRecipeDTO
				= TestUtils.RECIPE_DTO.toBuilder()
				.id(recipeId)
				.build();

		when(this.mockRecipeService.update(recipeId, inputRecipeDTO))
				.thenReturn(inputRecipeDTO);

		//when
		ResultActions resultActions
				= this.mockMvc.perform(
						put(RECIPES_API_URL + "/" + recipeId)
								.contentType(MediaType.APPLICATION_JSON_VALUE)
								.content(OBJECT_MAPPER.writeValueAsString(inputRecipeDTO)));

		//then
		resultActions
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(content().string(OBJECT_MAPPER.writeValueAsString(inputRecipeDTO)));
	}

	@Test
	void test_PUT_when_correct_input_and_recipe_exists_then_update_recipe() throws Exception {

		//given
		long recipeId = 1;

		RecipeDTO inputRecipeDTO
				= TestUtils.RECIPE_DTO.toBuilder()
				.id(recipeId)
				.build();

		when(this.mockRecipeService.update(recipeId, inputRecipeDTO))
				.thenReturn(inputRecipeDTO);

		//when
		ResultActions resultActions
				= this.mockMvc.perform(
						put(RECIPES_API_URL + "/" + recipeId)
								.contentType(MediaType.APPLICATION_JSON_VALUE)
								.content(OBJECT_MAPPER.writeValueAsString(inputRecipeDTO)));

		//then
		resultActions
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(content().string(OBJECT_MAPPER.writeValueAsString(inputRecipeDTO)));
	}

	@Test
	void test_PUT_for_idempotency_when_correct_input_and_recipe_exists_then_update_recipe() throws Exception {

		//given
		int totalRequestsToTest = 10;
		long recipeId = 1;

		RecipeDTO inputRecipeDTO
				= TestUtils.RECIPE_DTO.toBuilder()
				.id(recipeId)
				.build();

		when(this.mockRecipeService.update(recipeId, inputRecipeDTO))
				.thenReturn(inputRecipeDTO);

		//when
		List<ResultActions> resultActionsList = new ArrayList<>(totalRequestsToTest);

		for (int i = 0; i < totalRequestsToTest; ++i) {

			ResultActions resultActions
					= this.mockMvc.perform(
							put(RECIPES_API_URL + "/" + recipeId)
									.contentType(MediaType.APPLICATION_JSON_VALUE)
									.content(OBJECT_MAPPER.writeValueAsString(inputRecipeDTO)));

			resultActionsList.add(resultActions);
		}

		//then
		for (int i = 0; i < totalRequestsToTest; ++i) {

			resultActionsList.get(i)
					.andExpect(status().isOk())
					.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
					.andExpect(content().string(OBJECT_MAPPER.writeValueAsString(inputRecipeDTO)));
		}
	}

	@ParameterizedTest(name = "PUT " + RECIPES_API_URL + "/1 - when {3}")
	@MethodSource
	void test_PUT_when_incorrect_input_then_return_error(final RecipeDTO invalidRecipe,
														 final boolean validateResponseBody,
														 final String expectedErrorMessage,
														 final String testDescription) throws Exception {

		//given
		ExceptionWrapper expectedApiError
				= ExceptionWrapper.builder()
				.httpStatus(HttpStatus.BAD_REQUEST)
				.error("Request validation failed.")
				.description(expectedErrorMessage)
				.build();

		//when
		ResultActions resultActions
				= this.mockMvc.perform(
						put(RECIPES_API_URL + "/1")
								.contentType(MediaType.APPLICATION_JSON_VALUE)
								.content(OBJECT_MAPPER.writeValueAsString(invalidRecipe)));

		//then
		resultActions
				.andExpect(status().isBadRequest())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE));

		if (validateResponseBody)
			resultActions
					.andExpect(content().string(OBJECT_MAPPER.writeValueAsString(expectedApiError)));
	}

	private static Stream<Arguments> test_PUT_when_incorrect_input_then_return_error() {

		return getStreamOfIncorrectRecipeDTO();
	}

	@ParameterizedTest(name = "PUT " + RECIPES_API_URL + "/1 - when service throws {1}")
	@MethodSource
	void test_PUT_when_application_exception_in_service_layer_then_return_error(
			final RuntimeException runtimeException,
			final String testDescription
	) throws Exception {

		//given
		long recipeId = 1;

		RecipeDTO inputRecipeDTO
				= TestUtils.RECIPE_DTO.toBuilder()
				.id(recipeId)
				.build();

		ExceptionWrapper expectedApiError
				= ExceptionWrapper.builder()
				.httpStatus(HttpStatus.UNPROCESSABLE_ENTITY)
				.error("Recipe update failed")
				.description(runtimeException.getMessage())
				.build();

		when(this.mockRecipeService.update(recipeId, inputRecipeDTO))
				.thenThrow(runtimeException);

		//when
		ResultActions resultActions
				= this.mockMvc.perform(
						put(RECIPES_API_URL + "/1")
								.contentType(MediaType.APPLICATION_JSON_VALUE)
								.content(OBJECT_MAPPER.writeValueAsString(inputRecipeDTO)));

		//then
		resultActions
				.andExpect(status().isUnprocessableEntity())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(content().string(OBJECT_MAPPER.writeValueAsString(expectedApiError)));
	}

	private static Stream<Arguments> test_PUT_when_application_exception_in_service_layer_then_return_error() {

		return Stream.of(
				arguments(
						new ApplicationException.RecipeUpdateFailedException(
								"Failed to update recipe to database"),
						ApplicationException.RecipeUpdateFailedException.class.getSimpleName())
		);
	}

	@ParameterizedTest(name = "PUT " + RECIPES_API_URL + "/1 - when service throws {1}")
	@MethodSource
	void test_PUT_when_non_application_exception_in_service_layer_then_return_error(
			final RuntimeException runtimeException,
			final String testDescription
	) throws Exception {

		//given
		long recipeId = 1L;

		RecipeDTO inputRecipeDTO
				= TestUtils.RECIPE_DTO.toBuilder()
				.id(recipeId)
				.build();

		when(this.mockRecipeService.update(recipeId, inputRecipeDTO))
				.thenThrow(runtimeException);

		//when
		ResultActions resultActions
				= this.mockMvc.perform(
						put(RECIPES_API_URL + "/" + recipeId)
								.contentType(MediaType.APPLICATION_JSON_VALUE)
								.content(OBJECT_MAPPER.writeValueAsString(inputRecipeDTO)));

		//then
		resultActions
				.andExpect(status().isInternalServerError())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(content().string(INTERNAL_SERVER_ERROR_JSON));
	}

	private static Stream<Arguments> test_PUT_when_non_application_exception_in_service_layer_then_return_error() {

		return getNonApplicationExceptionsForServiceLayer();
	}

	/*
	 * Tests for DELETE method
	 */
	@Test
	void test_DELETE_when_recipe_is_available_then_return_deleted_recipe() throws Exception {

		//given
		long recipeId = 1L;

		RecipeDTO expectedResult
				= RecipeDTO.builder()
				.id(recipeId)
				.name("Recipe " + recipeId)
				.vegetarian(true)
				.build();

		when(this.mockRecipeService.delete(recipeId))
				.thenReturn(expectedResult);

		//when
		ResultActions resultActions = this.mockMvc.perform(delete(RECIPES_API_URL + "/" + recipeId));

		//then
		resultActions
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(content().string(OBJECT_MAPPER.writeValueAsString(expectedResult)));
	}

	@Test
	void test_DELETE_when_recipe_is_not_available_then_return_error() throws Exception {

		//given
		long recipeId = 1L;

		when(this.mockRecipeService.delete(recipeId))
				.thenThrow(new ApplicationException.RecipeNotFoundException("Recipe not found"));

		//when
		ResultActions resultActions = this.mockMvc.perform(delete(RECIPES_API_URL + "/" + recipeId));

		//then
		resultActions
				.andExpect(status().isNotFound())
				.andExpect(content().string(emptyOrNullString()));
	}

	/*
	 * Common util functions
	 */
	private static Stream<Arguments> getStreamOfIncorrectRecipeDTO() {

		String INVALID_NAME_ERROR = "Recipe name can not be blank.";
		String INVALID_NUMBER_OF_SERVINGS_ERROR = "Number of servings must be at least 1";
		String INVALID_INGREDIENTS_ERROR = "Ingredients can not be empty.";
		String INVALID_INSTRUCTIONS_ERROR = "Instructions can not be blank.";

		return Stream.of(
				arguments(RecipeDTO.builder().build(), false, Strings.EMPTY, "All fields are null"),
				arguments(TestUtils.RECIPE_DTO.toBuilder().name(null).build(), true, INVALID_NAME_ERROR, "Name is null"),
				arguments(TestUtils.RECIPE_DTO.toBuilder().name(Strings.EMPTY).build(), true, INVALID_NAME_ERROR, "Name is blank"),
				arguments(TestUtils.RECIPE_DTO.toBuilder().name("    ").build(), true, INVALID_NAME_ERROR, "Name is white spaces"),
				arguments(TestUtils.RECIPE_DTO.toBuilder().name(getTextOfLength(201)).build(), true, "Recipe name is too long.", "Name is too long"),
				arguments(TestUtils.RECIPE_DTO.toBuilder().vegetarian(null).build(), true, "Vegetarian/Non-vegetarian flag must be provided.", "Vegetarian/non-vegetarian flag is null"),
				arguments(TestUtils.RECIPE_DTO.toBuilder().numberOfServings(null).build(), true, "Number of servings must be provided.", "Number of servings is null"),
				arguments(TestUtils.RECIPE_DTO.toBuilder().numberOfServings(-1).build(), true, INVALID_NUMBER_OF_SERVINGS_ERROR, "Number of servings is -ve"),
				arguments(TestUtils.RECIPE_DTO.toBuilder().numberOfServings(0).build(), true, INVALID_NUMBER_OF_SERVINGS_ERROR, "Number of servings is 0"),
				arguments(TestUtils.RECIPE_DTO.toBuilder().ingredients(null).build(), true, INVALID_INGREDIENTS_ERROR, "Ingredients are null"),
				arguments(TestUtils.RECIPE_DTO.toBuilder().ingredients(Collections.EMPTY_SET).build(), true, INVALID_INGREDIENTS_ERROR, "Ingredients are empty"),
				arguments(TestUtils.RECIPE_DTO.toBuilder().instructions(null).build(), true, INVALID_INSTRUCTIONS_ERROR, "Instructions are null"),
				arguments(TestUtils.RECIPE_DTO.toBuilder().instructions(Strings.EMPTY).build(), true, INVALID_INSTRUCTIONS_ERROR, "Instructions are blank"),
				arguments(TestUtils.RECIPE_DTO.toBuilder().instructions("    ").build(), true, INVALID_INSTRUCTIONS_ERROR, "Instructions are white spaces"),
				arguments(TestUtils.RECIPE_DTO.toBuilder().instructions(getTextOfLength(4001)).build(), true, "Instructions are too long.", "Instructions are too long")
		);
	}

	private static Stream<Arguments> getNonApplicationExceptionsForServiceLayer() {

		return Stream.of(
				arguments(
						new NullPointerException("Exception in service layer"),
						NullPointerException.class.getSimpleName())
		);
	}

	private static String getTextOfLength(final int length) {

		char[] bytes = new char[length];
		Arrays.fill(bytes, 'A');
		return new String(bytes);
	}
}
