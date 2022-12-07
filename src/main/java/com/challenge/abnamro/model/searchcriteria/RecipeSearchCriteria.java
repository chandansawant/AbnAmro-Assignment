package com.challenge.abnamro.model.searchcriteria;

import com.challenge.abnamro.model.entity.Ingredient;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.apache.commons.lang3.ObjectUtils;

import javax.validation.constraints.AssertTrue;
import java.util.Objects;
import java.util.Set;

/**
 * This class represents search criteria body.
 * All search criteria will be combined using AND.
 * To perform OR operation, separate requests need to be made by client.
 *
 * Validation rules for search criteria are as follows.
 *
 * At least one of the criteria field must be not-null.
 *
 * {@link RecipeSearchCriteria#includedIngredients} must be either null i.e. not provided
 * or with at lease one {@link Ingredient}.
 * Empty list will be considered as request for recipe without any ingredient.
 *
 * If bothe, {@link RecipeSearchCriteria#includedIngredients} and {@link RecipeSearchCriteria#excludedIngredients}
 * are provided then they should not contain a common {@link Ingredient}.
 */
@Builder
@Getter
@EqualsAndHashCode
@ToString
public class RecipeSearchCriteria {

	@Getter(AccessLevel.NONE)
	private Boolean vegetarian;

	private Integer numberOfServings;
	private Set<Ingredient> includedIngredients;
	private Set<Ingredient> excludedIngredients;
	private String textInInstructions;

	public Boolean isVegetarian() {
		return this.vegetarian;
	}

	@JsonIgnore
	@AssertTrue(message = "Search criteria must be valid.")
	public boolean isValid() {

		if (!ObjectUtils.anyNotNull(
				this.vegetarian,
				this.numberOfServings,
				this.includedIngredients,
				this.excludedIngredients,
				this.textInInstructions))
			return false;

		if (Objects.nonNull(this.includedIngredients)
				&& this.includedIngredients.isEmpty())
			return false;

		if (Objects.nonNull(this.includedIngredients)
				&& Objects.nonNull(this.excludedIngredients)) {

			return this.includedIngredients.stream()
						.noneMatch(this.excludedIngredients::contains);
		}

		return true;
	}
}
