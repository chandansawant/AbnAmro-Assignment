package com.challenge.abnamro.service;

import com.challenge.abnamro.exception.ApplicationException;
import com.challenge.abnamro.mapper.IngredientDTOMapper;
import com.challenge.abnamro.mapper.RecipeDTOMapper;
import com.challenge.abnamro.model.dto.IngredientDTO;
import com.challenge.abnamro.model.dto.RecipeDTO;
import com.challenge.abnamro.model.entity.Recipe;
import com.challenge.abnamro.repository.IngredientRepository;
import com.challenge.abnamro.repository.RecipeRepository;
import com.challenge.abnamro.util.TestUtils;
import org.assertj.core.util.Sets;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.test.context.ActiveProfiles;

import javax.persistence.EntityExistsException;
import javax.persistence.QueryTimeoutException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@ActiveProfiles("test")
class RecipeServiceTest {

	private static final RecipeDTOMapper RECIPE_DTO_MAPPER = new RecipeDTOMapper();
	private static final IngredientDTOMapper INGREDIENT_DTO_MAPPER = new IngredientDTOMapper();

	private RecipeRepository mockRecipeRepository = mock(RecipeRepository.class);
	private IngredientRepository mockIngredientRepository = mock(IngredientRepository.class);
	private RecipeService cut
			= new RecipeService(
					this.mockRecipeRepository, this.mockIngredientRepository, RECIPE_DTO_MAPPER, INGREDIENT_DTO_MAPPER);

	//@Test
	void test_create_when_successful_then_return_saved_instance() {

		//given
		RecipeDTO inputRecipeDTO = TestUtils.RECIPE_DTO;
		Recipe createdRecipe = TestUtils.RECIPE;

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

		when(this.mockIngredientRepository.findByName(TestUtils.INGREDIENT_1.getName()))
				.thenReturn(Optional.of(TestUtils.INGREDIENT_1));

		when(this.mockIngredientRepository.findByName(TestUtils.INGREDIENT_2.getName()))
				.thenReturn(Optional.of(TestUtils.INGREDIENT_2));

		when(this.mockRecipeRepository.save(any(Recipe.class)))
				.thenReturn(createdRecipe);

		//when
		RecipeDTO actualResult = this.cut.create(inputRecipeDTO);

		//then
		assertEquals(expectedResult, actualResult);
	}

	@Test
	void test_create_when_fails_then_throw_exception() {

		//given
		when(this.mockIngredientRepository.findByName(TestUtils.INGREDIENT_1.getName()))
				.thenReturn(Optional.of(TestUtils.INGREDIENT_1));

		when(this.mockIngredientRepository.findByName(TestUtils.INGREDIENT_2.getName()))
				.thenReturn(Optional.of(TestUtils.INGREDIENT_2));

		when(this.mockRecipeRepository.save(any(Recipe.class)))
				.thenThrow(EntityExistsException.class);

		//when
		Executable testExecutable = () -> this.cut.create(TestUtils.RECIPE_DTO);

		//then
		assertThrows(EntityExistsException.class, testExecutable);
	}

	@Test
	void test_getAll_when_successful_then_return_all_available_instances() {

		//given
		int totalRecipeToTest = 10;
		List<Recipe> recipes
				= LongStream.range(0, totalRecipeToTest)
					.mapToObj(recipeId ->
							Recipe.builder()
									.id(recipeId)
									.name("Recipe" + recipeId)
									.vegetarian(true)
									.build())
					.collect(Collectors.toList());

		List<RecipeDTO> expectedResult
				= LongStream.range(0, totalRecipeToTest)
					.mapToObj(recipeId ->
							RecipeDTO.builder()
									.id(recipeId)
									.name("Recipe" + recipeId)
									.vegetarian(true)
									.build())
					.collect(Collectors.toList());

		when(this.mockRecipeRepository.findAll())
				.thenReturn(recipes);

		//when
		List<RecipeDTO> actualResult = this.cut.getAll();

		//then
		assertEquals(expectedResult, actualResult);
	}

	@Test
	void test_getAll_when_successful_but_no_recipe_available_then_throw_no_data_found_error() {

		//given
		List<Recipe> recipes = Collections.emptyList();

		when(this.mockRecipeRepository.findAll())
				.thenReturn(recipes);

		//when
		Executable testExecutable = this.cut::getAll;

		//then
		assertThrows(ApplicationException.RecipeNotFoundException.class, testExecutable);
	}

	@Test
	void test_getAll_when_fails_then_throw_exception() {

		//given
		when(this.mockRecipeRepository.findAll())
				.thenThrow(QueryTimeoutException.class);

		//when
		Executable testExecutable = this.cut::getAll;

		//then
		assertThrows(QueryTimeoutException.class, testExecutable);
	}

	@Test
	void test_get_when_successful_then_return_available_instances() {

		//given
		long recipeId = 1L;

		Optional<Recipe> optionalRecipe
				= Optional.of(
						Recipe.builder()
								.id(recipeId)
								.name("Recipe" + recipeId)
								.vegetarian(true)
								.build());

		RecipeDTO expectedResult
				= RecipeDTO.builder()
				.id(recipeId)
				.name("Recipe" + recipeId)
				.vegetarian(true)
				.build();

		when(this.mockRecipeRepository.findById(recipeId))
				.thenReturn(optionalRecipe);

		//when
		RecipeDTO actualResult = this.cut.get(recipeId);

		//then
		assertEquals(expectedResult, actualResult);
	}

	@Test
	void test_get_when_successful_but_no_recipe_available_then_throw_no_data_found_error() {

		//given
		long recipeID = 1L;
		Optional<Recipe> optionalRecipe = Optional.empty();

		when(this.mockRecipeRepository.findById(recipeID))
				.thenReturn(optionalRecipe);

		//when
		Executable testExecutable = () -> this.cut.get(recipeID);

		//then
		assertThrows(ApplicationException.RecipeNotFoundException.class, testExecutable);
	}

	@Test
	void test_get_when_fails_then_throw_exception() {

		//given
		long recipeID = 1L;
		when(this.mockRecipeRepository.findById(anyLong()))
				.thenThrow(QueryTimeoutException.class);

		//when
		Executable testExecutable = () -> this.cut.get(recipeID);

		//then
		assertThrows(QueryTimeoutException.class, testExecutable);
	}

	@Test
	void test_update_when_recipe_is_available_then_update_and_return_saved_instance() {

		//given
		Recipe recipeInDatabase = TestUtils.RECIPE;
		int changedNumberOfServings = recipeInDatabase.getNumberOfServings() + 1;

		RecipeDTO inputRecipeDTO
				= TestUtils.RECIPE_DTO.toBuilder()
				.numberOfServings(changedNumberOfServings)
				.build();

		RecipeDTO expectedResult
				= TestUtils.RECIPE_DTO.toBuilder()
				.id(TestUtils.TEST_ID)
				.numberOfServings(changedNumberOfServings)
				.build();

		long recipeId = recipeInDatabase.getId();

		//make sure that field to update is not same
		assertNotEquals(inputRecipeDTO.getNumberOfServings(), recipeInDatabase.getNumberOfServings());

		when(this.mockRecipeRepository.findById(recipeId))
				.thenReturn(Optional.of(recipeInDatabase));

		when(this.mockRecipeRepository.save(any(Recipe.class)))
				.then(returnsFirstArg());

		//when
		RecipeDTO actualResult = this.cut.update(recipeId, inputRecipeDTO);

		//then
		assertEquals(expectedResult, actualResult);
	}

	@Test
	void test_update_when_no_recipe_available_then_create_and_return_saved_instance() {

		//given
		long recipeId = 1L;

		RecipeDTO inputRecipeDTO = TestUtils.RECIPE_DTO.toBuilder().build();
		RecipeDTO expectedResult = TestUtils.RECIPE_DTO.toBuilder().build();

		when(this.mockRecipeRepository.findById(recipeId))
				.thenReturn(Optional.empty());

		when(this.mockRecipeRepository.save(any(Recipe.class)))
				.then(returnsFirstArg());

		//when
		RecipeDTO actualResult = this.cut.update(recipeId, inputRecipeDTO);

		//then
		assertEquals(expectedResult, actualResult);
	}

	@Test
	void test_update_when_fails_then_throw_exception() {

		//given
		long recipeId = 1L;

		RecipeDTO inputRecipeDTO
				= RecipeDTO.builder()
				.id(recipeId)
				.name("Recipe" + recipeId)
				.vegetarian(false)
				.build();

		when(this.mockRecipeRepository.findById(recipeId))
				.thenThrow(QueryTimeoutException.class);

		//when
		Executable testExecutable = () -> this.cut.update(recipeId, inputRecipeDTO);

		//then
		assertThrows(QueryTimeoutException.class, testExecutable);
	}

	//
	@Test
	void test_delete_when_successful_then_return_deleted_instances() {

		//given
		long recipeId = 1L;

		Optional<Recipe> optionalRecipe
				= Optional.of(
				Recipe.builder()
						.id(recipeId)
						.name("Recipe" + recipeId)
						.vegetarian(true)
						.build());

		RecipeDTO expectedResult
				= RecipeDTO.builder()
				.id(recipeId)
				.name("Recipe" + recipeId)
				.vegetarian(true)
				.build();

		when(this.mockRecipeRepository.findById(recipeId))
				.thenReturn(optionalRecipe);

		//when
		RecipeDTO actualResult = this.cut.delete(recipeId);

		//then
		assertEquals(expectedResult, actualResult);
	}

	@Test
	void test_delete_when_no_recipe_available_then_throw_no_data_found_error() {

		//given
		long recipeId = 1L;
		Optional<Recipe> optionalRecipe = Optional.empty();

		when(this.mockRecipeRepository.findById(recipeId))
				.thenReturn(optionalRecipe);

		//when
		Executable testExecutable = () -> this.cut.delete(recipeId);

		//then
		assertThrows(ApplicationException.RecipeNotFoundException.class, testExecutable);
	}

	@Test
	void test_delete_when_fails_then_throw_exception() {

		//given
		long recipeId = 1L;

		when(this.mockRecipeRepository.findById(recipeId))
				.thenThrow(QueryTimeoutException.class);

		//when
		Executable testExecutable = () -> this.cut.delete(recipeId);

		//then
		assertThrows(QueryTimeoutException.class, testExecutable);
	}
}
