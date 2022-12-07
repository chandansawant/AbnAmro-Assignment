package com.challenge.abnamro.repository;

import com.challenge.abnamro.model.entity.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IngredientRepository extends JpaRepository<Ingredient, Long> {

	Optional<Ingredient> findByName(final String name);
}
