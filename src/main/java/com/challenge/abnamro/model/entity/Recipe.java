package com.challenge.abnamro.model.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.util.Objects;
import java.util.Set;

/**
 * JPA Entity class for recipe.

 * Uniqueness of recipe is unique combination of
 * case-sensitive name of recipe and whether recipe is vegetarian or not.
 *
 * Hence, Recipe(name = "Pasta", vegetarian = "true") and Recipe(name = "Pasta", vegetarian = "false")
 * will be considered as separate entities.
 *
 * But Recipe(name = "Pasta", vegetarian = "true", number of servings = 2)
 * and Recipe(name = "Pasta", vegetarian = "true", number of servings = 5) will be considered as same entities.
 */
@Entity
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Table(uniqueConstraints = {
		@UniqueConstraint(name = "UniqueRecipe", columnNames = {"name", "vegetarian"})})
public class Recipe {

	@Id
	@GeneratedValue(
			strategy = GenerationType.SEQUENCE,
			generator = "RECIPE_ID_SEQUENCE_GENERATOR")
	@SequenceGenerator(
			name = "RECIPE_ID_SEQUENCE_GENERATOR",
			sequenceName = "RECIPE_ID_SEQUENCE",
			allocationSize = 1)
	private Long id;

	@Column(length = 200)
	private String name;

	@Getter(AccessLevel.NONE)
	private Boolean vegetarian;

	private Integer numberOfServings;

	@ManyToMany(
			cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH},
			fetch = FetchType.EAGER)
	@JoinTable(
			name = "RECIPE_INGREDIENTS",
			joinColumns = @JoinColumn(name = "recipes_id"),
			inverseJoinColumns = @JoinColumn(name = "ingredients_id"))
	private Set<Ingredient> ingredients;

	@Column(length = 4000)
	private String instructions;

	public void merge(final Recipe otherRecipe) {

		this.numberOfServings = otherRecipe.numberOfServings;
		this.ingredients = otherRecipe.ingredients;
		this.instructions = otherRecipe.instructions;
	}

	public Boolean isVegetarian() {
		return this.vegetarian;
	}

	@Override
	public boolean equals(final Object other) {

		if (this == other)
			return true;

		if (other == null || getClass() != other.getClass())
			return false;

		Recipe recipe = (Recipe) other;
		return Objects.equals(this.name, recipe.name)
				&& Objects.equals(this.vegetarian, recipe.vegetarian);
	}

	@Override
	public int hashCode() {
		return Objects.hash(
				this.name,
				this.vegetarian);
	}
}
