package com.challenge.abnamro.repository.filter;

import com.challenge.abnamro.model.entity.Recipe;
import com.challenge.abnamro.model.searchcriteria.RecipeSearchCriteria;
import org.apache.logging.log4j.util.Strings;
import org.springframework.data.jpa.domain.Specification;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class RecipeSearchSpecification {

	public static List<Specification<Recipe>> getRecipeSearchSpecifications(
			final RecipeSearchCriteria recipeSearchCriteria) {

		List<Specification<Recipe>> recipeSearchSpecifications = new LinkedList<>();

		if (Objects.nonNull(recipeSearchCriteria.isVegetarian())) {
			recipeSearchSpecifications.add(isVegetarian(recipeSearchCriteria.isVegetarian()));
		}

		if (Objects.nonNull(recipeSearchCriteria.getNumberOfServings())) {
			recipeSearchSpecifications.add(numberOfServings(recipeSearchCriteria.getNumberOfServings()));
		}

		if (Strings.isNotBlank(recipeSearchCriteria.getTextInInstructions())) {
			recipeSearchSpecifications.add(textInInstructions(recipeSearchCriteria.getTextInInstructions()));
		}

		return recipeSearchSpecifications;
	}

	public static Specification<Recipe> defaultSpecification() {
		return (root, query, criteriaBuilder) ->
				criteriaBuilder.conjunction();
	}

	private static Specification<Recipe> isVegetarian(final boolean vegetarian) {
		return (root, query, criteriaBuilder) ->
				criteriaBuilder.equal(root.get("vegetarian"), vegetarian);
	}

	private static Specification<Recipe> numberOfServings(final int numberOfServings) {
		return (root, query, criteriaBuilder) ->
				criteriaBuilder.equal(root.get("numberOfServings"), numberOfServings);
	}

	private static Specification<Recipe> textInInstructions(final String textInInstructions) {

		return (root, query, criteriaBuilder) ->
				criteriaBuilder.like(
						root.get("instructions"), "%" + textInInstructions + "%");
	}
}
