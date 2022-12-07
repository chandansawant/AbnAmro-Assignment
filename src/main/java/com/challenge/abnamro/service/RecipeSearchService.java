package com.challenge.abnamro.service;

import com.challenge.abnamro.exception.ApplicationException;
import com.challenge.abnamro.mapper.RecipeDTOMapper;
import com.challenge.abnamro.model.dto.RecipeDTO;
import com.challenge.abnamro.model.entity.Ingredient;
import com.challenge.abnamro.model.entity.Recipe;
import com.challenge.abnamro.model.searchcriteria.RecipeSearchCriteria;
import com.challenge.abnamro.repository.RecipeRepository;
import com.challenge.abnamro.repository.filter.RecipeSearchSpecification;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.data.jpa.domain.Specification.where;

@Service
@AllArgsConstructor
@Slf4j
public class RecipeSearchService {

	private final RecipeRepository recipeRepository;
	private final RecipeDTOMapper recipeDTOMapper;

	public List<RecipeDTO> search(final RecipeSearchCriteria recipeSearchCriteria) {

		log.info("Search recipe(s) using criteria - {}", recipeSearchCriteria);

		//get search specifications as per provided criteria
		List<Specification<Recipe>> recipeSearchSpecifications
				= RecipeSearchSpecification.getRecipeSearchSpecifications(recipeSearchCriteria);

		//combine search specifications
		Specification<Recipe> combinedRecipeSearchSpecifications
				= combineRecipeSearchSpecifications(recipeSearchSpecifications);

		//perform search in database as per combined search specification
		List<Recipe> matchedRecipesFromDatabase
				= this.recipeRepository.findAll(where(combinedRecipeSearchSpecifications));

		log.info("Found {} recipes in database.", matchedRecipesFromDatabase.size());

		//TODO - research on how to use JPA Specification for filtering on collections
		//perform filtering based on inclusion and/or exclusion of ingredients
		List<Recipe> matchedRecipes = applyIngredientsBasedFilter(recipeSearchCriteria, matchedRecipesFromDatabase);

		log.info("Found {} recipes.", matchedRecipes.size());

		//raise exception if nothing is found
		if (CollectionUtils.isEmpty(matchedRecipes))
			throw new ApplicationException.RecipeNotFoundException("No recipe fond.");

		//return DTO and not entity
		return this.recipeDTOMapper.toDTO(matchedRecipes);
	}

	private Specification<Recipe> combineRecipeSearchSpecifications(
			final List<Specification<Recipe>> recipeSearchSpecifications) {

		//combine search specifications with AND operation
		return recipeSearchSpecifications.stream()
				.reduce(RecipeSearchSpecification.defaultSpecification(), Specification::and);
	}

	private List<Recipe> applyIngredientsBasedFilter(final RecipeSearchCriteria recipeSearchCriteria,
													 final List<Recipe> recipes) {

		Set<Ingredient> includedIngredients = recipeSearchCriteria.getIncludedIngredients();
		Set<Ingredient> excludedIngredients = recipeSearchCriteria.getExcludedIngredients();

		if (CollectionUtils.isEmpty(recipes)
				|| (Objects.isNull(includedIngredients)
					&& Objects.isNull(excludedIngredients)))
			return recipes;

		return recipes.stream()
				.filter(recipe -> isIngredientCriteriaMatched(recipeSearchCriteria, recipe))
				.collect(Collectors.toList());
	}

	private boolean isIngredientCriteriaMatched(final RecipeSearchCriteria recipeSearchCriteria,
												final Recipe recipe) {

		Set<Ingredient> recipeIngredients = recipe.getIngredients();
		Set<Ingredient> includedIngredients = recipeSearchCriteria.getIncludedIngredients();
		Set<Ingredient> excludedIngredients = recipeSearchCriteria.getExcludedIngredients();

		if (Objects.nonNull(includedIngredients)
				&& (includedIngredients.isEmpty()
						|| !recipeIngredients.containsAll(includedIngredients)))
			return false;

		if (Objects.isNull(excludedIngredients))
			return true;

		return recipeSearchCriteria.getExcludedIngredients()
				.stream()
				.noneMatch(recipeIngredients::contains);
	}
}
