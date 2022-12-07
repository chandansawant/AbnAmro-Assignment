package com.challenge.abnamro.util;

import com.challenge.abnamro.model.dto.IngredientDTO;
import com.challenge.abnamro.model.dto.RecipeDTO;
import com.challenge.abnamro.model.entity.Ingredient;
import com.challenge.abnamro.model.entity.Recipe;
import org.assertj.core.util.Sets;

import java.util.Set;

public class TestUtils {

	//common attributes
	public static final Long TEST_ID = 1L;

	//recipe attributes
	public static final String RECIPE_NAME = "test recipe";
	public static final Boolean VEGETARIAN = true;
	public static final Integer NUMBER_OF_SERVINGS = 1;
	public static final String INSTRUCTIONS = "recipe instructions";

	//ingredient attributes
	public static final String INGREDIENT_NAME = "test name";

	//test objects
	public static final Ingredient INGREDIENT_1;
	public static final Ingredient INGREDIENT_2;
	public static final Set<Ingredient> INGREDIENTS;

	public static final IngredientDTO INGREDIENT_DTO_1;
	public static final IngredientDTO INGREDIENT_DTO_2;
	public static final Set<IngredientDTO> INGREDIENT_DTOs;

	public static final Recipe RECIPE;
	public static final RecipeDTO RECIPE_DTO;

	static {
		INGREDIENT_1 = getIngredient(1L);
		INGREDIENT_2 = getIngredient(2L);
		INGREDIENTS = Sets.set(INGREDIENT_1, INGREDIENT_2);

		INGREDIENT_DTO_1 = getIngredientDTO(1L);
		INGREDIENT_DTO_2 = getIngredientDTO(2L);
		INGREDIENT_DTOs = Sets.set(INGREDIENT_DTO_1, INGREDIENT_DTO_2);

		RECIPE = getRecipe();
		RECIPE_DTO = getRecipeDTO();
	}

	private static Recipe getRecipe() {
		return Recipe.builder()
				.id(TEST_ID)
				.name(RECIPE_NAME + TEST_ID)
				.vegetarian(VEGETARIAN)
				.numberOfServings(NUMBER_OF_SERVINGS)
				.ingredients(INGREDIENTS)
				.instructions(INSTRUCTIONS)
				.build();
	}

	public static Recipe getRecipe(final Long id) {
		return RECIPE.toBuilder()
				.id(id)
				.name(RECIPE_NAME + id)
				.build();
	}

	private static RecipeDTO getRecipeDTO() {
		return RecipeDTO.builder()
				.name(RECIPE_NAME + TEST_ID)
				.vegetarian(VEGETARIAN)
				.numberOfServings(NUMBER_OF_SERVINGS)
				.ingredients(INGREDIENT_DTOs)
				.instructions(INSTRUCTIONS)
				.build();
	}

	public static RecipeDTO getRecipeDTO(final Long id) {
		return RECIPE_DTO.toBuilder()
				.name(RECIPE_NAME + id)
				.build();
	}

	private static Ingredient getIngredient(final Long id) {
		return Ingredient.builder()
				.id(id)
				.name(INGREDIENT_NAME + id)
				.build();
	}

	private static IngredientDTO getIngredientDTO(final Long id) {
		return IngredientDTO.builder()
				.name(INGREDIENT_NAME + id)
				.build();
	}
}
