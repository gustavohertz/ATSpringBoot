package com.example.adventure_management_api.service;

import com.example.adventure_management_api.dto.*;
import com.example.adventure_management_api.entity.Adventurer;
import com.example.adventure_management_api.entity.Companion;
import com.example.adventure_management_api.entity.Organization;
import com.example.adventure_management_api.entity.User;
import com.example.adventure_management_api.entity.MissionParticipation;
import com.example.adventure_management_api.entity.enums.AdventurerClass;
import com.example.adventure_management_api.exception.BusinessRuleException;
import com.example.adventure_management_api.exception.ResourceNotFoundException;
import com.example.adventure_management_api.repository.AdventurerRepository;
import com.example.adventure_management_api.repository.MissionParticipationRepository;
import com.example.adventure_management_api.repository.OrganizationRepository;
import com.example.adventure_management_api.repository.UserRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@SuppressWarnings("null")
public class AdventurerService {

    private final AdventurerRepository adventurerRepository;
    private final MissionParticipationRepository participationRepository;
    private final OrganizationRepository organizationRepository;
    private final UserRepository userRepository;

    public AdventurerService(AdventurerRepository adventurerRepository,
                             MissionParticipationRepository participationRepository,
                             OrganizationRepository organizationRepository,
                             UserRepository userRepository) {
        this.adventurerRepository = adventurerRepository;
        this.participationRepository = participationRepository;
        this.organizationRepository = organizationRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public Page<AdventurerSummaryDto> search(Long orgId, Boolean ativo, AdventurerClass classe, Integer nivelMinimo, String nomeParcial, Pageable pageable) {
        Specification<Adventurer> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("organization").get("id"), orgId));

            if (ativo != null) {
                predicates.add(cb.equal(root.get("ativo"), ativo));
            }
            if (classe != null) {
                predicates.add(cb.equal(root.get("classeAventureiro"), classe));
            }
            if (nivelMinimo != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("nivel"), nivelMinimo));
            }
            if (nomeParcial != null && !nomeParcial.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("nome")), "%" + nomeParcial.toLowerCase() + "%"));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return adventurerRepository.findAll(spec, pageable).map(this::toSummaryDto);
    }

    @Transactional(readOnly = true)
    public AdventurerFullDto getFullProfile(Long orgId, Long id) {
        Adventurer ad = adventurerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Aventureiro não encontrado"));

        if (!ad.getOrganization().getId().equals(orgId)) {
            throw new ResourceNotFoundException("Aventureiro pertence a outra organização");
        }

        CompanionDto compDto = null;
        if (ad.getCompanion() != null) {
            compDto = new CompanionDto(ad.getCompanion().getId(), ad.getCompanion().getNome(), ad.getCompanion().getEspecie(), ad.getCompanion().getIndiceLealdade());
        }

        Specification<MissionParticipation> specPart = (root, query, cb) -> 
            cb.equal(root.get("adventurer").get("id"), id);

        Page<MissionParticipation> participacoes = participationRepository.findAll(
            specPart, PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "dataRegistro")));
            
        long totalParticipacoes = participationRepository.count(specPart);

        MissionSummaryDto ultimaMissao = null;
        if (!participacoes.isEmpty()) {
            var m = participacoes.getContent().get(0).getMission();
            ultimaMissao = new MissionSummaryDto(m.getId(), m.getTitulo(), m.getNivelPerigo(), m.getStatus(), m.getDataInicio(), m.getDataTermino());
        }

        return new AdventurerFullDto(
                ad.getId(), ad.getNome(), ad.getClasseAventureiro(), ad.getNivel(), ad.getAtivo(),
                compDto, totalParticipacoes, ultimaMissao
        );
    }

    @Transactional
    public AdventurerSummaryDto createAdventurer(Long orgId, CreateAdventurerDto dto) {

        Organization org = organizationRepository.findById(orgId)
                .orElseThrow(() -> new ResourceNotFoundException("Organização não encontrada"));

        User user = userRepository.findById(dto.usuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        if (!user.getOrganization().getId().equals(orgId)) {
            throw new BusinessRuleException("O usuário não pertence à organização informada.");
        }

        Adventurer adventurer = new Adventurer();
        adventurer.setOrganization(org);
        adventurer.setUserCadastro(user);
        adventurer.setNome(dto.nome());
        adventurer.setClasseAventureiro(dto.classe());
        adventurer.setNivel(dto.nivel());
        adventurer.setAtivo(true);

        adventurerRepository.save(adventurer);
        return toSummaryDto(adventurer);
    }

    @Transactional
    public CompanionDto addCompanion(Long orgId, Long adventurerId, CreateCompanionDto dto) {
        Adventurer adventurer = adventurerRepository.findById(adventurerId)
                .orElseThrow(() -> new ResourceNotFoundException("Aventureiro não encontrado"));

        if (!adventurer.getOrganization().getId().equals(orgId)) {
            throw new ResourceNotFoundException("Aventureiro pertence a outra organização");
        }

        if (adventurer.getCompanion() != null) {
            throw new BusinessRuleException("Este aventureiro já possui um companheiro.");
        }

        Companion companion = new Companion();
        companion.setNome(dto.nome());
        companion.setEspecie(dto.especie());
        companion.setIndiceLealdade(dto.indiceLealdade());
        adventurer.setCompanion(companion);

        adventurerRepository.save(adventurer);

        return new CompanionDto(companion.getId(), companion.getNome(), companion.getEspecie(), companion.getIndiceLealdade());
    }

    @Transactional
    public void deleteAdventurer(Long orgId, Long id) {
        Adventurer ad = adventurerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Aventureiro não encontrado"));
        if (!ad.getOrganization().getId().equals(orgId)) {
            throw new ResourceNotFoundException("Organização inválida");
        }

        adventurerRepository.delete(ad);
    }

    @Transactional
    public AdventurerSummaryDto updateAdventurer(Long orgId, Long id, AdventurerSummaryDto dto) {
        Adventurer ad = adventurerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Aventureiro não encontrado"));
        if (!ad.getOrganization().getId().equals(orgId)) {
            throw new ResourceNotFoundException("Organização inválida");
        }
        
        if (dto.nome() != null) ad.setNome(dto.nome());
        if (dto.classe() != null) ad.setClasseAventureiro(dto.classe());
        if (dto.nivel() != null) {
            if (dto.nivel() < 1) {
                throw new BusinessRuleException("O nível mínimo permitido é 1.");
            }
            ad.setNivel(dto.nivel());
        }
        if (dto.ativo() != null) ad.setAtivo(dto.ativo());
        adventurerRepository.save(ad);
        
        return toSummaryDto(ad);
    }

    private AdventurerSummaryDto toSummaryDto(Adventurer a) {
        return new AdventurerSummaryDto(a.getId(), a.getNome(), a.getClasseAventureiro(), a.getNivel(), a.getAtivo());
    }
}
