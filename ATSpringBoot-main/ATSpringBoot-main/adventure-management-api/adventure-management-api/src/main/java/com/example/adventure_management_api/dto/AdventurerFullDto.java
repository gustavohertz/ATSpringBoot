package com.example.adventure_management_api.dto;

import com.example.adventure_management_api.entity.enums.AdventurerClass;

public record AdventurerFullDto(
    Long id,
    String nome,
    AdventurerClass classe,
    Integer nivel,
    Boolean ativo,
    CompanionDto companheiro,
    Long totalParticipacoes,
    MissionSummaryDto ultimaMissao
) {
}
