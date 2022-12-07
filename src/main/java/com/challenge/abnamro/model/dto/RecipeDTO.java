package com.challenge.abnamro.model.dto;

import com.challenge.abnamro.model.entity.Recipe;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;

/**
 * This class is used as DTO for {@link Recipe}
 */
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class RecipeDTO {

	private Long id;

	@NotBlank(message = "Recipe name can not be blank.")
	@Size(max = 200, message = "Recipe name is too long.")
	private String name;

	@NotNull(message = "Vegetarian/Non-vegetarian flag must be provided.")
	@Getter(AccessLevel.NONE)
	private Boolean vegetarian;

	@NotNull(message = "Number of servings must be provided.")
	@Min(value = 1, message = "Number of servings must be at least 1")
	private Integer numberOfServings;

	@NotEmpty(message = "Ingredients can not be empty.")
	private Set<IngredientDTO> ingredients;

	@NotBlank(message = "Instructions can not be blank.")
	@Size(max = 4000, message = "Instructions are too long.")
	private String instructions;

	public Boolean isVegetarian() {
		return this.vegetarian;
	}
}
