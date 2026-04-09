package com.example.adventure_management_api.dto;

import com.example.adventure_management_api.entity.enums.CompanionSpecies;

public record CompanionDto(
    Long id,
    String nome,
    CompanionSpecies especie,
    Integer indiceLealdade
) {
}
