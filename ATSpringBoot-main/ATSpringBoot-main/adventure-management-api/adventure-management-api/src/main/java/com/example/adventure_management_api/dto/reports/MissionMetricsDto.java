package com.example.adventure_management_api.dto.reports;

import com.example.adventure_management_api.entity.enums.MissionDangerLevel;
import com.example.adventure_management_api.entity.enums.MissionStatus;
import java.math.BigDecimal;

public record MissionMetricsDto(
    Long missaoId,
    String titulo,
    MissionStatus status,
    MissionDangerLevel nivelPerigo,
    Long quantidadeParticipantes,
    BigDecimal totalRecompensas
) {
}
