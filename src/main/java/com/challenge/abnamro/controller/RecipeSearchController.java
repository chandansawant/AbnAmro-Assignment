package com.challenge.abnamro.controller;

import com.challenge.abnamro.model.dto.RecipeDTO;
import com.challenge.abnamro.model.searchcriteria.RecipeSearchCriteria;
import com.challenge.abnamro.service.RecipeSearchService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping(
		path = ApiConstants.Version.V_1_0
				+ ApiConstants.Endpoints.RECIPES
				+ ApiConstants.Endpoints.SEARCH,
		consumes = MediaType.APPLICATION_JSON_VALUE,
		produces = MediaType.APPLICATION_JSON_VALUE)
@AllArgsConstructor
public class RecipeSearchController {

	private final RecipeSearchService recipeSearchService;

	/**
	 * Search is handled using POST method as it allows to accept search criteria in request body.
	 *
	 * It's possible to have a long list of criteria specially for
	 * {@link RecipeSearchCriteria#includedIngredients} and {@link RecipeSearchCriteria#excludedIngredients}.
	 * If we accept them as a part of {@link PathVariable} then request url will become too long.
	 *
	 * @param recipeSearchCriteria in request body
	 * @return  if matched records found then returns {@link HttpStatus#OK} with matched records in response body
	 *          else returns {@link HttpStatus#NOT_FOUND} without response body
	 */
	@PostMapping
	public List<RecipeDTO> search(@NotNull @Valid @RequestBody final RecipeSearchCriteria recipeSearchCriteria) {
		return this.recipeSearchService.search(recipeSearchCriteria);
	}
}
