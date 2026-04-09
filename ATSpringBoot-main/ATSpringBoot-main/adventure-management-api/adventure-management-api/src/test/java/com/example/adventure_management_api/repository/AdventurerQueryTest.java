package com.example.adventure_management_api.repository;

import com.example.adventure_management_api.entity.Adventurer;
import com.example.adventure_management_api.entity.MissionParticipation;
import com.example.adventure_management_api.entity.enums.AdventurerClass;
import jakarta.persistence.criteria.Predicate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Testes da Parte 3 - Consultas operacionais de Aventureiros (H2).
 */
@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class AdventurerQueryTest {

    @Autowired
    private AdventurerRepository adventurerRepository;

    @Autowired
    private MissionParticipationRepository participationRepository;

    // ==========================================
    // Consulta 1: Listagem com filtros
    // ==========================================

    @Test
    void deveListarAventureirosAtivosDaOrganizacao() {
        Specification<Adventurer> spec = (root, query, cb) -> {
            List<Predicate> p = new ArrayList<>();
            p.add(cb.equal(root.get("organization").get("id"), 1L));
            p.add(cb.equal(root.get("ativo"), true));
            return cb.and(p.toArray(new Predicate[0]));
        };

        Page<Adventurer> result = adventurerRepository.findAll(spec, PageRequest.of(0, 10));
        assertThat(result.getContent()).isNotEmpty();
        assertThat(result.getContent()).allMatch(a -> a.getAtivo());
        assertThat(result.getTotalElements()).isEqualTo(3);
    }

    @Test
    void deveListarAventureirosInativosDaOrganizacao() {
        Specification<Adventurer> spec = (root, query, cb) -> {
            List<Predicate> p = new ArrayList<>();
            p.add(cb.equal(root.get("organization").get("id"), 1L));
            p.add(cb.equal(root.get("ativo"), false));
            return cb.and(p.toArray(new Predicate[0]));
        };

        Page<Adventurer> result = adventurerRepository.findAll(spec, PageRequest.of(0, 10));
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getNome()).isEqualTo("Gimli");
    }

    @Test
    void deveFiltrarPorClasse() {
        Specification<Adventurer> spec = (root, query, cb) -> {
            List<Predicate> p = new ArrayList<>();
            p.add(cb.equal(root.get("organization").get("id"), 1L));
            p.add(cb.equal(root.get("classeAventureiro"), AdventurerClass.GUERREIRO));
            return cb.and(p.toArray(new Predicate[0]));
        };

        Page<Adventurer> result = adventurerRepository.findAll(spec, PageRequest.of(0, 10));
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent()).allMatch(a -> a.getClasseAventureiro() == AdventurerClass.GUERREIRO);
    }

    @Test
    void deveFiltrarPorNivelMinimo() {
        Specification<Adventurer> spec = (root, query, cb) -> {
            List<Predicate> p = new ArrayList<>();
            p.add(cb.equal(root.get("organization").get("id"), 1L));
            p.add(cb.greaterThanOrEqualTo(root.get("nivel"), 15));
            return cb.and(p.toArray(new Predicate[0]));
        };

        Page<Adventurer> result = adventurerRepository.findAll(spec, PageRequest.of(0, 10));
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent()).allMatch(a -> a.getNivel() >= 15);
    }

    @Test
    void deveOrdenarPorNome() {
        Specification<Adventurer> spec = (root, query, cb) ->
                cb.equal(root.get("organization").get("id"), 1L);

        Page<Adventurer> result = adventurerRepository.findAll(spec, PageRequest.of(0, 10, Sort.by("nome")));
        assertThat(result.getContent()).isNotEmpty();
        assertThat(result.getContent().get(0).getNome()).isEqualTo("Aragorn");
    }

    @Test
    void deveOrdenarPorNivel() {
        Specification<Adventurer> spec = (root, query, cb) ->
                cb.equal(root.get("organization").get("id"), 1L);

        Page<Adventurer> result = adventurerRepository.findAll(spec, PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "nivel")));
        assertThat(result.getContent()).isNotEmpty();
        assertThat(result.getContent().get(0).getNome()).isEqualTo("Gandalf");
    }

    // ==========================================
    // Consulta 2: Busca por nome parcial
    // ==========================================

    @Test
    void deveBuscarPorNomeParcial() {
        Specification<Adventurer> spec = (root, query, cb) -> {
            List<Predicate> p = new ArrayList<>();
            p.add(cb.equal(root.get("organization").get("id"), 1L));
            p.add(cb.like(cb.lower(root.get("nome")), "%ara%"));
            return cb.and(p.toArray(new Predicate[0]));
        };

        Page<Adventurer> result = adventurerRepository.findAll(spec, PageRequest.of(0, 10));
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getNome()).isEqualTo("Aragorn");
    }

    @Test
    void deveBuscarPorNomeParcialCaseInsensitive() {
        Specification<Adventurer> spec = (root, query, cb) -> {
            List<Predicate> p = new ArrayList<>();
            p.add(cb.equal(root.get("organization").get("id"), 1L));
            p.add(cb.like(cb.lower(root.get("nome")), "%gandalf%"));
            return cb.and(p.toArray(new Predicate[0]));
        };

        Page<Adventurer> result = adventurerRepository.findAll(spec, PageRequest.of(0, 10));
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getNome()).isEqualTo("Gandalf");
    }

    @Test
    void deveBuscarPorNomeSemResultados() {
        Specification<Adventurer> spec = (root, query, cb) -> {
            List<Predicate> p = new ArrayList<>();
            p.add(cb.equal(root.get("organization").get("id"), 1L));
            p.add(cb.like(cb.lower(root.get("nome")), "%inexistente%"));
            return cb.and(p.toArray(new Predicate[0]));
        };

        Page<Adventurer> result = adventurerRepository.findAll(spec, PageRequest.of(0, 10));
        assertThat(result.getTotalElements()).isEqualTo(0);
    }

    // ==========================================
    // Consulta 3: Perfil completo do aventureiro
    // ==========================================

    @Test
    void deveCarregarPerfilCompletoComCompanheiro() {
        Optional<Adventurer> advOpt = adventurerRepository.findById(1L);
        assertThat(advOpt).isPresent();

        Adventurer ad = advOpt.get();
        assertThat(ad.getNome()).isEqualTo("Aragorn");
        assertThat(ad.getCompanion()).isNotNull();
        assertThat(ad.getCompanion().getNome()).isEqualTo("Brego");

        Specification<MissionParticipation> specPart = (root, query, cb) ->
                cb.equal(root.get("adventurer").get("id"), 1L);
        long totalPart = participationRepository.count(specPart);
        assertThat(totalPart).isEqualTo(2);
    }

    @Test
    void deveCarregarPerfilCompletoSemCompanheiro() {
        Optional<Adventurer> advOpt = adventurerRepository.findById(2L);
        assertThat(advOpt).isPresent();

        Adventurer ad = advOpt.get();
        assertThat(ad.getNome()).isEqualTo("Gandalf");
        assertThat(ad.getCompanion()).isNull();
    }

    @Test
    void deveCarregarUltimaMissaoDoAventureiro() {
        Specification<MissionParticipation> specPart = (root, query, cb) ->
                cb.equal(root.get("adventurer").get("id"), 1L);

        Page<MissionParticipation> participacoes = participationRepository.findAll(
                specPart, PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "dataRegistro")));

        assertThat(participacoes.getContent()).hasSize(1);
        assertThat(participacoes.getContent().get(0).getMission().getTitulo()).isEqualTo("As Duas Torres");
    }

    @Test
    void deveRetornarVazioParaAventureiroSemParticipacoes() {
        Specification<MissionParticipation> specPart = (root, query, cb) ->
                cb.equal(root.get("adventurer").get("id"), 4L);

        long count = participationRepository.count(specPart);
        assertThat(count).isEqualTo(0);
    }
}
