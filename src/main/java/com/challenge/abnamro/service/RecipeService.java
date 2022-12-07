package com.challenge.abnamro.service;

import com.challenge.abnamro.exception.ApplicationException;
import com.challenge.abnamro.mapper.IngredientDTOMapper;
import com.challenge.abnamro.mapper.RecipeDTOMapper;
import com.challenge.abnamro.model.dto.RecipeDTO;
import com.challenge.abnamro.model.entity.Ingredient;
import com.challenge.abnamro.model.entity.Recipe;
import com.challenge.abnamro.repository.IngredientRepository;
import com.challenge.abnamro.repository.RecipeRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class RecipeService {

	private final RecipeRepository recipeRepository;
	private final IngredientRepository ingredientRepository;
	private final RecipeDTOMapper recipeDTOMapper;
	private final IngredientDTOMapper ingredientDTOMapper;

	@Transactional
	public RecipeDTO create(final RecipeDTO recipeDTO) {

		log.info("Preparing to create {}", recipeDTO);

		try {
			//transform DTO to entity
			Recipe recipe = this.recipeDTOMapper.fromDTO(recipeDTO);

			/*
			 * Check if any ingredient is already persisted.
			 * If yes then use it, otherwise creates new.
			 */
			Set<Ingredient> ingredients
					= recipeDTO.getIngredients().stream()
					.map(ingredientDTO ->
							this.ingredientRepository.findByName(ingredientDTO.getName())
									.orElseGet(() -> this.ingredientDTOMapper.fromDTO(ingredientDTO)))
					.collect(Collectors.toSet());

			//update ingredients list
			recipe.setIngredients(ingredients);

			//save entity
			Recipe savedRecipe = this.recipeRepository.save(recipe);

			log.info("Created {}", savedRecipe);

			//return DTO and not entity
			return this.recipeDTOMapper.toDTO(savedRecipe);
		} catch (DataIntegrityViolationException ex) {
			throw new ApplicationException.RecipeCreationFailedException("Requested changes violates Recipe data");
		}
	}

	public List<RecipeDTO> getAll() {

		log.info("Preparing to get all recipes");

		List<Recipe> recipes = this.recipeRepository.findAll();

		if (CollectionUtils.isEmpty(recipes))
			throw new ApplicationException.RecipeNotFoundException("No recipe fond.");

		log.info("Fetched {} recipes", recipes.size());

		//return DTO and not entity
		return this.recipeDTOMapper.toDTO(recipes);
	}

	public RecipeDTO get(final long id) {

		log.info("Get recipe with id = [{}]", id);

		Optional<Recipe> optionalRecipe = this.recipeRepository.findById(id);
		return optionalRecipe
				.map(this.recipeDTOMapper::toDTO) //return DTO and not entity
				.orElseThrow(() ->
						new ApplicationException.RecipeNotFoundException("Recipe with id = [" + id + "] not found."));
	}

	@Transactional
	public RecipeDTO update(final long id, final RecipeDTO recipeDTO) {

		log.info("Preparing to update recipe with id = [{}] with update {}", id, recipeDTO);

		try {
			//find already persisted entity, create new if not found.
			Recipe existingRecipe
					= this.recipeRepository.findById(id)
						.orElseGet(() -> this.recipeDTOMapper.fromDTO(recipeDTO));

			/*
			 * Check if any ingredient is already persisted.
			 * If yes then use it, otherwise creates new.
			 */
			Set<Ingredient> ingredients
					= recipeDTO.getIngredients().stream()
					.map(ingredientDTO ->
							this.ingredientRepository.findByName(ingredientDTO.getName())
									.orElseGet(() -> this.ingredientDTOMapper.fromDTO(ingredientDTO)))
					.collect(Collectors.toSet());

			//transform DTO to entity
			Recipe newRecipe = this.recipeDTOMapper.fromDTO(recipeDTO);

			//update ingredients list
			newRecipe.setIngredients(ingredients);

			//merge new updates to persisted entity
			existingRecipe.merge(newRecipe);

			//save entity
			Recipe savedRecipe = this.recipeRepository.save(existingRecipe);

			log.info("Updated {}", savedRecipe);

			//return DTO and not entity
			return this.recipeDTOMapper.toDTO(savedRecipe);
		} catch (DataIntegrityViolationException ex) {
			throw new ApplicationException.RecipeUpdateFailedException("Requested changes violates Recipe data");
		}
	}

	@Transactional
	public RecipeDTO delete(final long id) {

		//find already persisted entity, throw exception if not found.
		Recipe recipe
				= this.recipeRepository.findById(id)
				.orElseThrow(() ->
						new ApplicationException.RecipeNotFoundException("Recipe with id = [" + id + "] not found."));

		//recommended in Spring documentation, hence flushing the EntityManager before calling deleteById()
		this.recipeRepository.flush();

		//delete persisted entity
		this.recipeRepository.deleteById(id);

		log.info("Deleted {}", recipe);

		//return DTO and not entity
		return this.recipeDTOMapper.toDTO(recipe);
	}
}
