package com.example.adventure_management_api.dto;

import com.example.adventure_management_api.entity.enums.MissionRole;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record CreateParticipationDto(
    @NotNull(message = "O ID do aventureiro é obrigatório")
    Long aventureiroId,

    @NotNull(message = "O papel na missão é obrigatório")
    MissionRole papel,

    @DecimalMin(value = "0", message = "A recompensa em ouro não pode ser negativa")
    BigDecimal recompensaOuro,

    Boolean destaque
) {
}
