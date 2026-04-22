package com.example.adventure_management_api.dto;

import com.example.adventure_management_api.entity.enums.CompanionSpecies;
import jakarta.validation.constraints.*;

public record CreateCompanionDto(
    @NotBlank(message = "O nome do companheiro é obrigatório")
    @Size(max = 120, message = "O nome deve ter no máximo 120 caracteres")
    String nome,

    @NotNull(message = "A espécie é obrigatória")
    CompanionSpecies especie,

    @NotNull(message = "O índice de lealdade é obrigatório")
    @Min(value = 0, message = "O índice de lealdade mínimo é 0")
    @Max(value = 100, message = "O índice de lealdade máximo é 100")
    Integer indiceLealdade
) {
}
