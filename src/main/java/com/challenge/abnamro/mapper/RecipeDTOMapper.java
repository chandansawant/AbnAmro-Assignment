package com.challenge.abnamro.mapper;

import com.challenge.abnamro.model.dto.RecipeDTO;
import com.challenge.abnamro.model.entity.Recipe;
import org.springframework.stereotype.Component;

@Component
public class RecipeDTOMapper extends DTOMapper<Recipe, RecipeDTO>{

	public RecipeDTOMapper() {
		super(Recipe.class, RecipeDTO.class);
	}
}
