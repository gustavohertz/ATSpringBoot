package com.example.adventure_management_api.dto;

import com.example.adventure_management_api.entity.enums.MissionDangerLevel;
import com.example.adventure_management_api.entity.enums.MissionStatus;
import java.time.Instant;
import java.util.List;

public record MissionFullDto(
    Long id,
    String titulo,
    MissionDangerLevel nivelPerigo,
    MissionStatus status,
    Instant dataInicio,
    Instant dataTermino,
    List<MissionParticipationDto> participantes
) {
}
