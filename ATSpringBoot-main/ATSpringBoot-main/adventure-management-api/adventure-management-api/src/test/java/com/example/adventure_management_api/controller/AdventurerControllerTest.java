package com.example.adventure_management_api.controller;

import com.example.adventure_management_api.config.TestSecurityConfig;
import com.example.adventure_management_api.dto.AdventurerSummaryDto;
import com.example.adventure_management_api.dto.CompanionDto;
import com.example.adventure_management_api.dto.AdventurerFullDto;
import com.example.adventure_management_api.entity.enums.AdventurerClass;
import com.example.adventure_management_api.entity.enums.CompanionSpecies;
import com.example.adventure_management_api.service.AdventurerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Testes de slice da camada web utilizando @WebMvcTest.
 *
 * Isola o AdventurerController e testa os endpoints REST
 * mockando a camada de serviço (AdventurerService).
 *
 * Verifica:
 * - Status HTTP corretos (200, 201, 204)
 * - Conteúdo da resposta JSON
 * - Chamada correta dos métodos do serviço
 */
@WebMvcTest(AdventurerController.class)
@Import(TestSecurityConfig.class)
class AdventurerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdventurerService adventurerService;

    @Test
    void deveListarAventureirosComSucesso() throws Exception {
        AdventurerSummaryDto dto = new AdventurerSummaryDto(1L, "Aragorn", AdventurerClass.GUERREIRO, 10, true);
        Page<AdventurerSummaryDto> page = new PageImpl<>(List.of(dto));

        when(adventurerService.search(eq(1L), isNull(), isNull(), isNull(), isNull(), any(Pageable.class)))
                .thenReturn(page);

        mockMvc.perform(get("/api/adventurers")
                        .header("X-Organization-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].nome").value("Aragorn"))
                .andExpect(jsonPath("$.content[0].classe").value("GUERREIRO"))
                .andExpect(jsonPath("$.content[0].nivel").value(10));
    }

    @Test
    void deveRetornarPerfilCompletoDoAventureiro() throws Exception {
        CompanionDto companheiro = new CompanionDto(1L, "Brego", CompanionSpecies.LOBO, 95);
        AdventurerFullDto fullDto = new AdventurerFullDto(
                1L, "Aragorn", AdventurerClass.GUERREIRO, 10, true,
                companheiro, 5L, null
        );

        when(adventurerService.getFullProfile(1L, 1L)).thenReturn(fullDto);

        mockMvc.perform(get("/api/adventurers/1")
                        .header("X-Organization-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Aragorn"))
                .andExpect(jsonPath("$.companheiro.nome").value("Brego"))
                .andExpect(jsonPath("$.totalParticipacoes").value(5));
    }

    @Test
    void deveCriarAventureiroComSucesso() throws Exception {
        AdventurerSummaryDto created = new AdventurerSummaryDto(10L, "Frodo", AdventurerClass.LADINO, 5, true);

        when(adventurerService.createAdventurer(eq(1L), any())).thenReturn(created);

        String json = """
                {
                    "organizacaoId": 1,
                    "usuarioId": 1,
                    "nome": "Frodo",
                    "classe": "LADINO",
                    "nivel": 5
                }
                """;

        mockMvc.perform(post("/api/adventurers")
                        .header("X-Organization-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("Frodo"))
                .andExpect(jsonPath("$.nivel").value(5));
    }

    @Test
    void deveAtualizarAventureiroComSucesso() throws Exception {
        AdventurerSummaryDto updated = new AdventurerSummaryDto(1L, "Aragorn II", AdventurerClass.GUERREIRO, 15, true);

        when(adventurerService.updateAdventurer(eq(1L), eq(1L), any())).thenReturn(updated);

        String json = """
                {
                    "nome": "Aragorn II",
                    "nivel": 15
                }
                """;

        mockMvc.perform(put("/api/adventurers/1")
                        .header("X-Organization-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Aragorn II"))
                .andExpect(jsonPath("$.nivel").value(15));
    }

    @Test
    void deveDeletarAventureiroComSucesso() throws Exception {
        mockMvc.perform(delete("/api/adventurers/1")
                        .header("X-Organization-Id", "1"))
                .andExpect(status().isNoContent());

        verify(adventurerService).deleteAdventurer(1L, 1L);
    }
}
