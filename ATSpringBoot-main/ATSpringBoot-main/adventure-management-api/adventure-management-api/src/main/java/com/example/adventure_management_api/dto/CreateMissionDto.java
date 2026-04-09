package com.example.adventure_management_api.dto;

import com.example.adventure_management_api.entity.enums.MissionDangerLevel;
import com.example.adventure_management_api.entity.enums.MissionStatus;
import jakarta.validation.constraints.*;
import java.time.Instant;

public record CreateMissionDto(
    @NotNull(message = "O ID da organização é obrigatório")
    Long organizacaoId,

    @NotBlank(message = "O título é obrigatório")
    @Size(max = 150, message = "O título deve ter no máximo 150 caracteres")
    String titulo,

    @NotNull(message = "O nível de perigo é obrigatório")
    MissionDangerLevel nivelPerigo,

    MissionStatus status,

    Instant dataInicio,

    Instant dataTermino
) {
}
