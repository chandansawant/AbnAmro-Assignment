package com.challenge.abnamro.mapper;

import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
public class DTOMapper<E, D> {

	private final ModelMapper MODEL_MAPPER = new ModelMapper();

	private final Class<E> entityClass;
	private final Class<D> dtoClass;

	public E fromDTO(final D dto) {
		return this.MODEL_MAPPER.map(dto, this.entityClass);
	}

	public D toDTO(final E entity) {
		return this.MODEL_MAPPER.map(entity, this.dtoClass);
	}

	public List<D> toDTO(final List<E> entities) {
		return entities.stream()
				.map(this::toDTO)
				.collect(Collectors.toList());
	}
}
