package com.example.adventure_management_api.repository;

import com.example.adventure_management_api.entity.Mission;
import com.example.adventure_management_api.entity.MissionParticipation;
import com.example.adventure_management_api.entity.enums.MissionStatus;
import jakarta.persistence.criteria.Predicate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class MissionQueryTest {

    @Autowired
    private MissionRepository missionRepository;

    @Autowired
    private MissionParticipationRepository participationRepository;

    @Test
    void deveListarMissoesDaOrganizacao() {
        Specification<Mission> spec = (root, query, cb) ->
                cb.equal(root.get("organization").get("id"), 1L);

        Page<Mission> result = missionRepository.findAll(spec, PageRequest.of(0, 10));
        assertThat(result.getTotalElements()).isEqualTo(4);
    }

    @Test
    void deveFiltrarMissoesPorStatus() {
        Specification<Mission> spec = (root, query, cb) -> {
            List<Predicate> p = new ArrayList<>();
            p.add(cb.equal(root.get("organization").get("id"), 1L));
            p.add(cb.equal(root.get("status"), MissionStatus.CONCLUIDA));
            return cb.and(p.toArray(new Predicate[0]));
        };

        Page<Mission> result = missionRepository.findAll(spec, PageRequest.of(0, 10));
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getTitulo()).isEqualTo("A Sociedade do Anel");
    }

    @Test
    void deveFiltrarMissoesPorIntervaloDeDatas() {
        Instant inicio = Instant.parse("2025-01-01T00:00:00Z");
        Instant fim = Instant.parse("2025-04-01T00:00:00Z");

        Specification<Mission> spec = (root, query, cb) -> {
            List<Predicate> p = new ArrayList<>();
            p.add(cb.equal(root.get("organization").get("id"), 1L));
            p.add(cb.greaterThanOrEqualTo(root.get("createdAt"), inicio));
            p.add(cb.lessThanOrEqualTo(root.get("createdAt"), fim));
            return cb.and(p.toArray(new Predicate[0]));
        };

        Page<Mission> result = missionRepository.findAll(spec, PageRequest.of(0, 10));
        assertThat(result.getTotalElements()).isEqualTo(2);
    }

    @Test
    void naoDeveRetornarMissoesDeOutraOrganizacao() {
        Specification<Mission> spec = (root, query, cb) ->
                cb.equal(root.get("organization").get("id"), 1L);

        Page<Mission> result = missionRepository.findAll(spec, PageRequest.of(0, 10));
        assertThat(result.getContent())
                .noneMatch(m -> "Busca pelo Palantir".equals(m.getTitulo()));
    }

    @Test
    void deveDetalharMissaoComParticipantes() {
        Optional<Mission> missionOpt = missionRepository.findById(1L);
        assertThat(missionOpt).isPresent();
        assertThat(missionOpt.get().getTitulo()).isEqualTo("A Sociedade do Anel");

        Specification<MissionParticipation> specPart = (root, query, cb) ->
                cb.equal(root.get("mission").get("id"), 1L);

        List<MissionParticipation> participacoes = participationRepository.findAll(specPart);
        assertThat(participacoes).hasSize(3);

        assertThat(participacoes).extracting(p -> p.getAdventurer().getNome())
                .containsExactlyInAnyOrder("Aragorn", "Gandalf", "Legolas");

        MissionParticipation aragornPart = participacoes.stream()
                .filter(p -> "Aragorn".equals(p.getAdventurer().getNome()))
                .findFirst().orElseThrow();
        assertThat(aragornPart.getPapel().name()).isEqualTo("LIDER");
        assertThat(aragornPart.getDestaque()).isTrue();
        assertThat(aragornPart.getRecompensaOuro()).isNotNull();
    }

    @Test
    void deveDetalharMissaoSemParticipantes() {
        Optional<Mission> missionOpt = missionRepository.findById(3L);
        assertThat(missionOpt).isPresent();

        Specification<MissionParticipation> specPart = (root, query, cb) ->
                cb.equal(root.get("mission").get("id"), 3L);

        List<MissionParticipation> participacoes = participationRepository.findAll(specPart);
        assertThat(participacoes).isEmpty();
    }

    @Test
    void deveDetalharMissaoCancelada() {
        Optional<Mission> missionOpt = missionRepository.findById(4L);
        assertThat(missionOpt).isPresent();
        assertThat(missionOpt.get().getStatus()).isEqualTo(MissionStatus.CANCELADA);
    }
}
