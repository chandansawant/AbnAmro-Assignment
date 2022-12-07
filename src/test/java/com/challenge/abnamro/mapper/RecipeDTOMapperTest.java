package com.challenge.abnamro.mapper;

import com.challenge.abnamro.model.dto.IngredientDTO;
import com.challenge.abnamro.model.dto.RecipeDTO;
import com.challenge.abnamro.model.entity.Recipe;
import com.challenge.abnamro.util.TestUtils;
import org.assertj.core.util.Lists;
import org.assertj.core.util.Sets;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@ActiveProfiles("test")
class RecipeDTOMapperTest {

	private final RecipeDTOMapper cut = new RecipeDTOMapper();

	@Test
	void test_fromDTO() {

		//given
		RecipeDTO inputRecipeDTO
				= TestUtils.RECIPE_DTO.toBuilder()
				.id(1L)
				.build();
		Recipe expectedResult = TestUtils.RECIPE;

		//when
		Recipe actualResult = this.cut.fromDTO(inputRecipeDTO);

		//then
		assertEquals(expectedResult, actualResult);
	}

	@Test
	void test_toDTO_when_input_is_single_entity_then_return_single_DTO() {

		//given
		Recipe inputRecipe = TestUtils.RECIPE;

		IngredientDTO ingredientDTO1
				= TestUtils.INGREDIENT_DTO_1.toBuilder()
				.id(1L)
				.build();

		IngredientDTO ingredientDTO2
				= TestUtils.INGREDIENT_DTO_2.toBuilder()
				.id(2L)
				.build();

		RecipeDTO expectedResult
				= TestUtils.RECIPE_DTO.toBuilder()
				.id(1L)
				.ingredients(Sets.set(ingredientDTO1, ingredientDTO2))
				.build();

		//when
		RecipeDTO actualResult = this.cut.toDTO(inputRecipe);

		//then
		assertEquals(expectedResult, actualResult);
	}

	@Test
	void test_toDTO_when_input_is_list_of_entity_then_return_list_of_DTO() {

		//given
		Recipe recipe1 = TestUtils.getRecipe(1L);
		Recipe recipe2 = TestUtils.getRecipe(2L);
		List<Recipe> inputRecipes = Lists.list(recipe1, recipe2);

		IngredientDTO ingredientDTO1
				= TestUtils.INGREDIENT_DTO_1.toBuilder()
				.id(1L)
				.build();

		IngredientDTO ingredientDTO2
				= TestUtils.INGREDIENT_DTO_2.toBuilder()
				.id(2L)
				.build();

		Set<IngredientDTO> ingredientDTOs = Sets.set(ingredientDTO1, ingredientDTO2);

		RecipeDTO recipeDTO1
				= TestUtils.getRecipeDTO(1L).toBuilder()
				.id(1L)
				.ingredients(ingredientDTOs)
				.build();

		RecipeDTO recipeDTO2
				= TestUtils.getRecipeDTO(2L).toBuilder()
				.id(2L)
				.ingredients(ingredientDTOs)
				.build();

		List<RecipeDTO> expectedResult = Lists.list(recipeDTO1, recipeDTO2);

		//when
		List<RecipeDTO> actualResult = this.cut.toDTO(inputRecipes);

		//then
		assertEquals(expectedResult, actualResult);
	}
}
