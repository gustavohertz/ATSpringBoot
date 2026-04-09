package com.example.adventure_management_api.repository;

import com.example.adventure_management_api.dto.reports.AdventurerRankingDto;
import com.example.adventure_management_api.dto.reports.MissionMetricsDto;
import com.example.adventure_management_api.entity.enums.MissionStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ReportQueryTest {

    @Autowired
    private MissionParticipationRepository participationRepository;

    @Autowired
    private MissionRepository missionRepository;


    @Test
    public void deveGerarRankingSemFiltros() {
        List<AdventurerRankingDto> ranking = participationRepository.getAdventurerRanking(1L, null, null, null);
        assertThat(ranking).isNotEmpty();

        assertThat(ranking).hasSizeGreaterThanOrEqualTo(3);

        AdventurerRankingDto primeiro = ranking.get(0);
        assertThat(primeiro.totalParticipacoes()).isGreaterThanOrEqualTo(2);

        System.out.println("=== RANKING DE AVENTUREIROS ===");
        ranking.forEach(r -> System.out.println(
                r.nomeAventureiro() + " | Participações: " + r.totalParticipacoes()
                + " | Recompensas: " + r.somaRecompensas()
                + " | Destaques: " + r.quantidadeDestaques()));
    }

    @Test
    public void deveGerarRankingFiltroPorMissaoConcluida() {
        List<AdventurerRankingDto> ranking = participationRepository.getAdventurerRanking(1L, null, null, MissionStatus.CONCLUIDA);
        assertThat(ranking).isNotEmpty();

        assertThat(ranking).hasSize(3);

        System.out.println("=== RANKING (CONCLUÍDA) ===");
        ranking.forEach(r -> System.out.println(
                r.nomeAventureiro() + " | Recompensas: " + r.somaRecompensas()));
    }

    @Test
    public void deveGerarRankingFiltroPorPeriodo() {
        Instant inicio = Instant.parse("2025-06-01T00:00:00Z");
        Instant fim = Instant.parse("2025-12-31T23:59:59Z");

        List<AdventurerRankingDto> ranking = participationRepository.getAdventurerRanking(1L, inicio, fim, null);
        assertThat(ranking).isNotEmpty();

        assertThat(ranking).hasSize(2);

        System.out.println("=== RANKING (JUN-DEZ 2025) ===");
        ranking.forEach(r -> System.out.println(
                r.nomeAventureiro() + " | Participações: " + r.totalParticipacoes()));
    }

    @Test
    public void rankingDeveRetornarVazioParaOrganizacaoSemDados() {

        List<AdventurerRankingDto> ranking = participationRepository.getAdventurerRanking(999L, null, null, null);
        assertThat(ranking).isEmpty();
    }

    @Test
    public void rankingDeveValidarTotaisCorretos() {
        List<AdventurerRankingDto> ranking = participationRepository.getAdventurerRanking(1L, null, null, null);

        AdventurerRankingDto aragorn = ranking.stream()
                .filter(r -> "Aragorn".equals(r.nomeAventureiro()))
                .findFirst().orElseThrow();

        assertThat(aragorn.totalParticipacoes()).isEqualTo(2);
        assertThat(aragorn.somaRecompensas()).isEqualByComparingTo(new BigDecimal("700.00"));
        assertThat(aragorn.quantidadeDestaques()).isEqualTo(1);

        AdventurerRankingDto legolas = ranking.stream()
                .filter(r -> "Legolas".equals(r.nomeAventureiro()))
                .findFirst().orElseThrow();

        assertThat(legolas.totalParticipacoes()).isEqualTo(2);
        assertThat(legolas.somaRecompensas()).isEqualByComparingTo(new BigDecimal("550.00"));
        assertThat(legolas.quantidadeDestaques()).isEqualTo(2);
    }

    @Test
    public void deveGerarRelatorioMissoesSemFiltros() {
        List<MissionMetricsDto> metrics = missionRepository.getMissionMetrics(1L, null, null);
        assertThat(metrics).isNotEmpty();
        assertThat(metrics).hasSize(4);

        System.out.println("=== RELATÓRIO DE MISSÕES ===");
        metrics.forEach(m -> System.out.println(
                m.titulo() + " | Status: " + m.status()
                + " | Participantes: " + m.quantidadeParticipantes()
                + " | Recompensas: " + m.totalRecompensas()));
    }

    @Test
    public void deveGerarRelatorioMissoesComPeriodo() {
        Instant inicio = Instant.parse("2025-01-01T00:00:00Z");
        Instant fim = Instant.parse("2025-04-01T00:00:00Z");

        List<MissionMetricsDto> metrics = missionRepository.getMissionMetrics(1L, inicio, fim);

        assertThat(metrics).hasSize(2);
    }

    @Test
    public void relatorioDeveValidarMetricasCorretas() {
        List<MissionMetricsDto> metrics = missionRepository.getMissionMetrics(1L, null, null);

        MissionMetricsDto sociedade = metrics.stream()
                .filter(m -> "A Sociedade do Anel".equals(m.titulo()))
                .findFirst().orElseThrow();

        assertThat(sociedade.quantidadeParticipantes()).isEqualTo(3);
        assertThat(sociedade.totalRecompensas()).isEqualByComparingTo(new BigDecimal("1200.00"));
        assertThat(sociedade.status()).isEqualTo(MissionStatus.CONCLUIDA);

        MissionMetricsDto retorno = metrics.stream()
                .filter(m -> "O Retorno do Rei".equals(m.titulo()))
                .findFirst().orElseThrow();

        assertThat(retorno.quantidadeParticipantes()).isEqualTo(0);
        assertThat(retorno.totalRecompensas()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    public void relatorioNaoDeveConterMissoesDeOutraOrganizacao() {
        List<MissionMetricsDto> metrics = missionRepository.getMissionMetrics(1L, null, null);
        assertThat(metrics).noneMatch(m -> "Busca pelo Palantir".equals(m.titulo()));
    }
}
