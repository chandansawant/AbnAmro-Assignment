package com.challenge.abnamro.controller;

import com.challenge.abnamro.model.dto.RecipeDTO;
import com.challenge.abnamro.service.RecipeService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(
		path = ApiConstants.Version.V_1_0 + ApiConstants.Endpoints.RECIPES,
		produces = MediaType.APPLICATION_JSON_VALUE)
@AllArgsConstructor
class RecipeController {

	static final String PATH_VARIABLE_ID = "/{id}";

	private final RecipeService recipeService;

	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<RecipeDTO> post(@Valid @RequestBody final RecipeDTO recipeDTO) {

		RecipeDTO createdRecipeDTO = this.recipeService.create(recipeDTO);

		URI createdRecipeUri
				= ServletUriComponentsBuilder.fromCurrentRequest()
					.path(PATH_VARIABLE_ID)
					.buildAndExpand(createdRecipeDTO.getId())
					.toUri();

		return ResponseEntity
				.status(HttpStatus.CREATED)
				.header(HttpHeaders.LOCATION, createdRecipeUri.toString())
				.body(createdRecipeDTO);
	}

	@GetMapping
	public List<RecipeDTO> getAll() {
		return this.recipeService.getAll();
	}

	@GetMapping(PATH_VARIABLE_ID)
	public RecipeDTO get(@PathVariable final long id) {
		return this.recipeService.get(id);
	}

	@PutMapping(
			value = PATH_VARIABLE_ID,
			consumes = MediaType.APPLICATION_JSON_VALUE
	)
	public RecipeDTO update(@PathVariable final long id,
							@Valid @RequestBody final RecipeDTO recipeDTO) {
		return this.recipeService.update(id, recipeDTO);
	}

	@DeleteMapping(PATH_VARIABLE_ID)
	public RecipeDTO delete(@PathVariable final long id) {
		return this.recipeService.delete(id);
	}
}
