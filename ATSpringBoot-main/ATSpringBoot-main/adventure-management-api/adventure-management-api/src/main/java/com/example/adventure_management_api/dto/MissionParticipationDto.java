package com.example.adventure_management_api.dto;

import com.example.adventure_management_api.entity.enums.MissionRole;
import java.math.BigDecimal;

public record MissionParticipationDto(
    Long aventureiroId,
    String aventureiroNome,
    MissionRole papel,
    BigDecimal recompensaOuro,
    Boolean destaque
) {
}
