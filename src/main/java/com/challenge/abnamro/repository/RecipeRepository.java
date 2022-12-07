package com.challenge.abnamro.repository;

import com.challenge.abnamro.model.entity.Recipe;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long>, JpaSpecificationExecutor<Recipe> {

	/*
	 * @EntityGraph helps to avoid N+1 problem by enabling joins for eager fetch of ingredients.
	 */
	@EntityGraph(
			type = EntityGraph.EntityGraphType.FETCH,
			attributePaths = "ingredients"
	)
	List<Recipe> findAll();

	@EntityGraph(
			type = EntityGraph.EntityGraphType.FETCH,
			attributePaths = "ingredients"
	)
	List<Recipe> findAll(@Nullable Specification<Recipe> spec);
}
