package com.challenge.abnamro.util;

import com.challenge.abnamro.controller.ApiConstants;
import com.challenge.abnamro.model.dto.RecipeDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

public class IntegrationTestUtils {

	public static final String LOCAL_HOST_URL_PATTERN = "http://localhost:";

	public static final String CONTEXT_PATH = "/recipesApp"; //TODO - remove hardcoded context path

	public static final String RECIPES_API_URL = ApiConstants.Version.V_1_0 + ApiConstants.Endpoints.RECIPES;

	public static final String RECIPE_SEARCH_API_URL
			= ApiConstants.Version.V_1_0
			+ ApiConstants.Endpoints.RECIPES
			+ ApiConstants.Endpoints.SEARCH;

	public static final TestRestTemplate REST_TEMPLATE = new TestRestTemplate();
	public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	public static String getLocalUrlForPort(int port, String uri) {
		return LOCAL_HOST_URL_PATTERN + port + CONTEXT_PATH + uri;
	}

	public static ResponseEntity<RecipeDTO> postRecipeDTO(final String url, final RecipeDTO recipeDTO) {

		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<RecipeDTO> httpEntityRequest = new HttpEntity<>(recipeDTO, httpHeaders);

		return REST_TEMPLATE.exchange(url, HttpMethod.POST, httpEntityRequest, RecipeDTO.class);
	}
}
