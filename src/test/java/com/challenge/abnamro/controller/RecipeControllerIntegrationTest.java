package com.challenge.abnamro.controller;

import com.challenge.abnamro.RecipeManagerApplication;
import com.challenge.abnamro.model.dto.IngredientDTO;
import com.challenge.abnamro.model.dto.RecipeDTO;
import com.challenge.abnamro.util.IntegrationTestUtils;
import com.challenge.abnamro.util.TestUtils;
import org.assertj.core.util.Lists;
import org.assertj.core.util.Sets;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

import java.util.ArrayList;
import java.util.List;

import static com.challenge.abnamro.util.IntegrationTestUtils.RECIPES_API_URL;
import static com.challenge.abnamro.util.IntegrationTestUtils.REST_TEMPLATE;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest(
		classes = RecipeManagerApplication.class,
		webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@ActiveProfiles("test")
public class RecipeControllerIntegrationTest {

	@LocalServerPort
	private int port;

	/*
	 * Tests for POST method
	 */
	@Test
	@DirtiesContext
	void test_POST_when_correct_input_then_create_recipe() {

		//given
		RecipeDTO inputRecipeDTO = TestUtils.RECIPE_DTO;

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

		//when
		ResponseEntity<RecipeDTO> actualResponse = postRecipeDTO(inputRecipeDTO);

		//then
		assertEquals(HttpStatus.CREATED, actualResponse.getStatusCode());
		assertEquals(MediaType.APPLICATION_JSON, actualResponse.getHeaders().getContentType());
		assertEquals(getUrl(RECIPES_API_URL + "/" + createdRecipeDTO.getId()), actualResponse.getHeaders().getLocation().toString());
		assertEquals(createdRecipeDTO, actualResponse.getBody());
	}

	/*
	 * Tests for GET method
	 */
	@Test
	@DirtiesContext
	void test_GET_when_no_id_provided_then_return_all_available_recipes() {

		//given
		RecipeDTO inputRecipeDTO1 = TestUtils.getRecipeDTO(1L);
		RecipeDTO inputRecipeDTO2 = TestUtils.getRecipeDTO(2L);

		postRecipeDTO(inputRecipeDTO1);
		postRecipeDTO(inputRecipeDTO2);

		IngredientDTO expectedIngredientDTO1
				= TestUtils.INGREDIENT_DTO_1.toBuilder()
				.id(1L)
				.build();

		IngredientDTO expectedIngredientDTO2
				= TestUtils.INGREDIENT_DTO_2.toBuilder()
				.id(2L)
				.build();

		RecipeDTO expectedRecipeDTO1
				= inputRecipeDTO1.toBuilder()
				.id(1L)
				.ingredients(Sets.set(expectedIngredientDTO1, expectedIngredientDTO2))
				.build();

		RecipeDTO expectedRecipeDTO2
				= inputRecipeDTO2.toBuilder()
				.id(2L)
				.ingredients(Sets.set(expectedIngredientDTO1, expectedIngredientDTO2))
				.build();

		List<RecipeDTO> expectedResult = Lists.list(expectedRecipeDTO1, expectedRecipeDTO2);

		String url = getUrl(RECIPES_API_URL);
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<Void> httpEntityRequest = new HttpEntity<>(null, httpHeaders);

		//when
		ResponseEntity<RecipeDTO[]> actualResponse
				= REST_TEMPLATE.exchange(url, HttpMethod.GET, httpEntityRequest, RecipeDTO[].class);

		//then
		assertEquals(HttpStatus.OK, actualResponse.getStatusCode());
		assertEquals(MediaType.APPLICATION_JSON, actualResponse.getHeaders().getContentType());
		assertEquals(expectedResult, Lists.list(actualResponse.getBody()));
	}

	@Test
	@DirtiesContext
	void test_GET_when_id_is_provided_and_data_is_available_then_return_data() {

		//given
		long recipeId = 1L;
		RecipeDTO inputRecipeDTO1 = TestUtils.getRecipeDTO(recipeId);

		postRecipeDTO(inputRecipeDTO1);

		IngredientDTO expectedIngredientDTO1
				= TestUtils.INGREDIENT_DTO_1.toBuilder()
				.id(1L)
				.build();

		IngredientDTO expectedIngredientDTO2
				= TestUtils.INGREDIENT_DTO_2.toBuilder()
				.id(2L)
				.build();

		RecipeDTO expectedResult
				= inputRecipeDTO1.toBuilder()
				.id(1L)
				.ingredients(Sets.set(expectedIngredientDTO1, expectedIngredientDTO2))
				.build();

		//when
		ResponseEntity<RecipeDTO> actualResponse = getRecipeDTO(recipeId);

		//then
		assertEquals(HttpStatus.OK, actualResponse.getStatusCode());
		assertEquals(MediaType.APPLICATION_JSON, actualResponse.getHeaders().getContentType());
		assertEquals(expectedResult, actualResponse.getBody());

//		resultActions
//				.andExpect(status().isOk())
//				.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
//				.andExpect(content().string(OBJECT_MAPPER.writeValueAsString(expectedResult)));
	}

	/*
	 * Tests for PUT method
	 */
	@Test
	@DirtiesContext
	void test_PUT_when_correct_input_but_recipe_do_not_exists_then_create_recipe() {

		//given
		long recipeId = 1;

		ResponseEntity<RecipeDTO> beforeTestResponse = getRecipeDTO(recipeId);
		assertEquals(HttpStatus.NOT_FOUND, beforeTestResponse.getStatusCode());

		RecipeDTO inputRecipeDTO = TestUtils.getRecipeDTO(recipeId);

		IngredientDTO expectedIngredientDTO1
				= TestUtils.INGREDIENT_DTO_1.toBuilder()
				.id(1L)
				.build();

		IngredientDTO expectedIngredientDTO2
				= TestUtils.INGREDIENT_DTO_2.toBuilder()
				.id(2L)
				.build();

		RecipeDTO expectedResult
				= inputRecipeDTO.toBuilder()
				.id(1L)
				.ingredients(Sets.set(expectedIngredientDTO1, expectedIngredientDTO2))
				.build();

		//when
		ResponseEntity<RecipeDTO> actualResponse = putRecipeDTO(recipeId, inputRecipeDTO);

		//then
		assertEquals(HttpStatus.OK, actualResponse.getStatusCode());
		assertEquals(MediaType.APPLICATION_JSON, actualResponse.getHeaders().getContentType());
		assertEquals(expectedResult, actualResponse.getBody());
	}

	@Test
	@DirtiesContext
	void test_PUT_when_correct_input_and_recipe_exists_then_update_recipe() {

		//given
		long recipeId = 1;

		RecipeDTO recipeDTO = TestUtils.getRecipeDTO(recipeId);
		ResponseEntity<RecipeDTO> beforeTestResponse = postRecipeDTO(recipeDTO);
		assertEquals(HttpStatus.CREATED, beforeTestResponse.getStatusCode());

		RecipeDTO existingRecipeDTO = beforeTestResponse.getBody();
		int changedNumberOfServings = existingRecipeDTO.getNumberOfServings() + 1;

		RecipeDTO changedRecipeDTO
				= existingRecipeDTO.toBuilder()
				.numberOfServings(changedNumberOfServings)
				.build();

		RecipeDTO expectedResult
				= existingRecipeDTO.toBuilder()
				.numberOfServings(changedNumberOfServings)
				.build();

		//when
		ResponseEntity<RecipeDTO> actualResponse = putRecipeDTO(recipeId, changedRecipeDTO);

		//then
		assertEquals(HttpStatus.OK, actualResponse.getStatusCode());
		assertEquals(MediaType.APPLICATION_JSON, actualResponse.getHeaders().getContentType());
		assertEquals(expectedResult, actualResponse.getBody());
	}

	@Test
	@DirtiesContext
	void test_PUT_for_idempotency_when_correct_input_and_recipe_exists_then_update_recipe() {

		//given
		int totalRequestsToTest = 10;
		long recipeId = 1;

		RecipeDTO recipeDTO = TestUtils.getRecipeDTO(recipeId);
		ResponseEntity<RecipeDTO> beforeTestResponse = postRecipeDTO(recipeDTO);
		assertEquals(HttpStatus.CREATED, beforeTestResponse.getStatusCode());

		RecipeDTO existingRecipeDTO = beforeTestResponse.getBody();
		int changedNumberOfServings = existingRecipeDTO.getNumberOfServings() + 1;

		RecipeDTO changedRecipeDTO
				= existingRecipeDTO.toBuilder()
				.numberOfServings(changedNumberOfServings)
				.build();

		RecipeDTO expectedResult
				= existingRecipeDTO.toBuilder()
				.numberOfServings(changedNumberOfServings)
				.build();

		//when
		List<ResponseEntity<RecipeDTO>> actualResponses = new ArrayList<>(totalRequestsToTest);

		for (int i = 0; i < totalRequestsToTest; ++i) {
			ResponseEntity<RecipeDTO> actualResponse = putRecipeDTO(recipeId, changedRecipeDTO);
			actualResponses.add(actualResponse);
		}

		//then
		for (int i = 0; i < totalRequestsToTest; ++i) {

			ResponseEntity<RecipeDTO> actualResponse = actualResponses.get(i);

			assertEquals(HttpStatus.OK, actualResponse.getStatusCode());
			assertEquals(MediaType.APPLICATION_JSON, actualResponse.getHeaders().getContentType());
			assertEquals(expectedResult, actualResponse.getBody());
		}
	}

	/*
	 * Tests for DELETE method
	 */
	@Test
	@DirtiesContext
	void test_DELETE_when_recipe_is_available_then_return_deleted_recipe() {

		//given
		long recipeId = 1L;

		RecipeDTO recipeDTO = TestUtils.getRecipeDTO(recipeId);
		ResponseEntity<RecipeDTO> beforeTestResponse = postRecipeDTO(recipeDTO);
		assertEquals(HttpStatus.CREATED, beforeTestResponse.getStatusCode());

		RecipeDTO existingRecipeDTO = beforeTestResponse.getBody();

		//when
		ResponseEntity<RecipeDTO> actualResponse = deleteRecipeDTO(recipeId);

		//then
		assertEquals(HttpStatus.OK, actualResponse.getStatusCode());
		assertEquals(MediaType.APPLICATION_JSON, actualResponse.getHeaders().getContentType());
		assertEquals(existingRecipeDTO, actualResponse.getBody());
	}

	private ResponseEntity<RecipeDTO> postRecipeDTO(final RecipeDTO recipeDTO) {

		String url = getUrl(RECIPES_API_URL);
		return IntegrationTestUtils.postRecipeDTO(url, recipeDTO);
	}

	private ResponseEntity<RecipeDTO> putRecipeDTO(final long recipeId, final RecipeDTO recipeDTO) {

		String url = getUrl(RECIPES_API_URL + "/" + recipeId);
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<RecipeDTO> httpEntityRequest = new HttpEntity<>(recipeDTO, httpHeaders);

		return REST_TEMPLATE.exchange(url, HttpMethod.PUT, httpEntityRequest, RecipeDTO.class);
	}

	private ResponseEntity<RecipeDTO> getRecipeDTO(final long recipeId) {
		return rquestById(HttpMethod.GET, recipeId);
	}

	private ResponseEntity<RecipeDTO> deleteRecipeDTO(final long recipeId) {
		return rquestById(HttpMethod.DELETE, recipeId);
	}

	private ResponseEntity<RecipeDTO> rquestById(final HttpMethod httpMethod, final long recipeId) {

		String url = getUrl(RECIPES_API_URL + "/" + recipeId);
		HttpHeaders httpHeaders = new HttpHeaders();
		HttpEntity<Void> httpEntityRequest = new HttpEntity<>(null, httpHeaders);

		return REST_TEMPLATE.exchange(url, httpMethod, httpEntityRequest, RecipeDTO.class);
	}

	private String getUrl(String uri) {
		return IntegrationTestUtils.getLocalUrlForPort(this.port, uri);
	}
}
