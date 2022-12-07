package com.challenge.abnamro.mapper;

import com.challenge.abnamro.model.dto.IngredientDTO;
import com.challenge.abnamro.model.entity.Ingredient;
import com.challenge.abnamro.util.TestUtils;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test")
class IngredientDTOMapperTest {

	private final IngredientDTOMapper cut = new IngredientDTOMapper();

	@Test
	void test_fromDTO() {

		//given
		IngredientDTO inputIngredientDTO = TestUtils.INGREDIENT_DTO_1;
		Ingredient expectedResult = TestUtils.INGREDIENT_1;

		//when
		Ingredient actualResult = this.cut.fromDTO(inputIngredientDTO);

		//then
		assertEquals(expectedResult, actualResult);
	}

	@Test
	void test_toDTO_when_input_is_single_entity_then_return_single_DTO() {

		//given
		Ingredient inputIngredient = TestUtils.INGREDIENT_1;
		IngredientDTO expectedResult
				= TestUtils.INGREDIENT_DTO_1.toBuilder()
				.id(1L)
				.build();

		//when
		IngredientDTO actualResult = this.cut.toDTO(inputIngredient);

		//then
		assertEquals(expectedResult, actualResult);
	}

	@Test
	void test_toDTO_when_input_is_list_of_entity_then_return_list_of_DTO() {

		//given
		List<Ingredient> inputIngredients = Lists.list(TestUtils.INGREDIENT_1, TestUtils.INGREDIENT_2);

		IngredientDTO ingredientDTO1
				= TestUtils.INGREDIENT_DTO_1.toBuilder()
				.id(1L)
				.build();

		IngredientDTO ingredientDTO2
				= TestUtils.INGREDIENT_DTO_2.toBuilder()
				.id(2L)
				.build();

		List<IngredientDTO> expectedResult = Lists.list(ingredientDTO1, ingredientDTO2);

		//when
		List<IngredientDTO> actualResult = this.cut.toDTO(inputIngredients);

		//then
		assertEquals(expectedResult, actualResult);
	}
}