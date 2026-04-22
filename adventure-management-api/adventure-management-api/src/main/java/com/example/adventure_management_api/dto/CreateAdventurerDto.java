package com.example.adventure_management_api.dto;

import com.example.adventure_management_api.entity.enums.AdventurerClass;
import jakarta.validation.constraints.*;

public record CreateAdventurerDto(
    @NotNull(message = "O ID da organização é obrigatório")
    Long organizacaoId,

    @NotNull(message = "O ID do usuário responsável é obrigatório")
    Long usuarioId,

    @NotBlank(message = "O nome é obrigatório")
    @Size(max = 120, message = "O nome deve ter no máximo 120 caracteres")
    String nome,

    @NotNull(message = "A classe do aventureiro é obrigatória")
    AdventurerClass classe,

    @NotNull(message = "O nível é obrigatório")
    @Min(value = 1, message = "O nível mínimo permitido é 1")
    Integer nivel
) {
}
