package com.example.adventure_management_api.dto;

import com.example.adventure_management_api.entity.enums.MissionDangerLevel;
import com.example.adventure_management_api.entity.enums.MissionStatus;

import java.time.Instant;

public record MissionFilterDto(
            String titulo,
            MissionDangerLevel nivelPerigo,
            MissionStatus status,
            Instant dataInicio,
            Instant dataTermino
    ) {}
