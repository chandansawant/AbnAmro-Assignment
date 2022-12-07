package com.challenge.abnamro.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.util.Objects;

/**
 * JPA Entity class for ingredient.

 * Uniqueness of ingredient is simply unique case-sensitive name of ingredient.
 * Hence, "salt", "Salt" and "SALT" will be considered as separate entities.
 */
@Entity
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Table(uniqueConstraints = {
		@UniqueConstraint(name = "UniqueIngredient", columnNames = "name")})
public class Ingredient {

	@Id
	@GeneratedValue(
			strategy = GenerationType.SEQUENCE,
			generator = "INGREDIENT_ID_SEQUENCE_GENERATOR")
	@SequenceGenerator(
			name = "INGREDIENT_ID_SEQUENCE_GENERATOR",
			sequenceName = "INGREDIENT_ID_SEQUENCE",
			allocationSize = 1)
	private Long id;

	@Column(length = 200)
	private String name;

	@Override
	public boolean equals(final Object other) {

		if (this == other)
			return true;

		if (other == null || getClass() != other.getClass())
			return false;

		Ingredient that = (Ingredient) other;
		return this.name.equals(that.name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.name);
	}
}
