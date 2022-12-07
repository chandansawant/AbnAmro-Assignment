package com.challenge.abnamro.model.dto;

import com.challenge.abnamro.model.entity.Ingredient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * This class is used as DTO for {@link Ingredient}
 */
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class IngredientDTO {

	private Long id;

	@NotBlank(message = "Ingredient name can not be blank.")
	@Size(max = 200, message = "Ingredient name can not be more than 200 characters long.")
	private String name;
}
