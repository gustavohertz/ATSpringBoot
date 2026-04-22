package com.example.adventure_management_api.dto.reports;

import java.math.BigDecimal;

public record AdventurerRankingDto(
    Long aventureiroId,
    String nomeAventureiro,
    Long totalParticipacoes,
    BigDecimal somaRecompensas,
    Long quantidadeDestaques
) {
}
