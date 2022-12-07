package com.challenge.abnamro.mapper;

import com.challenge.abnamro.model.dto.IngredientDTO;
import com.challenge.abnamro.model.entity.Ingredient;
import org.springframework.stereotype.Component;

@Component
public class IngredientDTOMapper extends DTOMapper<Ingredient, IngredientDTO>{

	public IngredientDTOMapper() {
		super(Ingredient.class, IngredientDTO.class);
	}
}
