package com.example.adventure_management_api.service;

import com.example.adventure_management_api.dto.*;
import com.example.adventure_management_api.entity.Adventurer;
import com.example.adventure_management_api.entity.Mission;
import com.example.adventure_management_api.entity.MissionParticipation;
import com.example.adventure_management_api.entity.Organization;
import com.example.adventure_management_api.entity.enums.MissionDangerLevel;
import com.example.adventure_management_api.entity.enums.MissionStatus;
import com.example.adventure_management_api.exception.BusinessRuleException;
import com.example.adventure_management_api.exception.ResourceNotFoundException;
import com.example.adventure_management_api.repository.AdventurerRepository;
import com.example.adventure_management_api.repository.MissionParticipationRepository;
import com.example.adventure_management_api.repository.MissionRepository;
import com.example.adventure_management_api.repository.OrganizationRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@SuppressWarnings("null")
public class MissionService {

    private final MissionRepository missionRepository;
    private final MissionParticipationRepository participationRepository;
    private final OrganizationRepository organizationRepository;
    private final AdventurerRepository adventurerRepository;

    public MissionService(MissionRepository missionRepository,
                          MissionParticipationRepository participationRepository,
                          OrganizationRepository organizationRepository,
                          AdventurerRepository adventurerRepository) {
        this.missionRepository = missionRepository;
        this.participationRepository = participationRepository;
        this.organizationRepository = organizationRepository;
        this.adventurerRepository = adventurerRepository;
    }

    @Transactional(readOnly = true)
    public Page<MissionSummaryDto> search(Long orgId, MissionFullDto filter, Pageable pageable) {
        return missionRepository.findById(
                orgId,
                filter.status(),
                filter.nivelPerigo(),
                filter.dataInicio(),
                filter.dataTermino(),
                pageable
        );
    }

    @Transactional(readOnly = true)
    public MissionFullDto getDetails(Long orgId, Long id) {
        Mission m = missionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Missão não encontrada"));

        if (!m.getOrganization().getId().equals(orgId)) {
            throw new ResourceNotFoundException("Organização incorreta");
        }

        Specification<MissionParticipation> specPart = (root, query, cb) -> 
            cb.equal(root.get("mission").get("id"), id);
            
        List<MissionParticipation> participacoes = participationRepository.findAll(specPart);

        List<MissionParticipationDto> partDtos = participacoes.stream()
                .map(p -> new MissionParticipationDto(
                        p.getAdventurer().getId(), 
                        p.getAdventurer().getNome(), 
                        p.getPapel(), 
                        p.getRecompensaOuro(), 
                        p.getDestaque()))
                .collect(Collectors.toList());

        return new MissionFullDto(
                m.getId(), m.getTitulo(), m.getNivelPerigo(), m.getStatus(),
                m.getDataInicio(), m.getDataTermino(), partDtos
        );
    }

    @Transactional
    public MissionSummaryDto createMission(Long orgId, CreateMissionDto dto) {
        Organization org = organizationRepository.findById(orgId)
                .orElseThrow(() -> new ResourceNotFoundException("Organização não encontrada"));

        Mission mission = new Mission();
        mission.setOrganization(org);
        mission.setTitulo(dto.titulo());
        mission.setNivelPerigo(dto.nivelPerigo());
        if (dto.status() != null) {
            mission.setStatus(dto.status());
        }
        if (dto.dataInicio() != null) {
            mission.setDataInicio(dto.dataInicio());
        }
        if (dto.dataTermino() != null) {
            mission.setDataTermino(dto.dataTermino());
        }

        missionRepository.save(mission);
        return toSummaryDto(mission);
    }

    @Transactional
    public MissionParticipationDto addParticipation(Long orgId, Long missionId, CreateParticipationDto dto) {

        Mission mission = missionRepository.findById(missionId)
                .orElseThrow(() -> new ResourceNotFoundException("Missão não encontrada"));
        if (!mission.getOrganization().getId().equals(orgId)) {
            throw new ResourceNotFoundException("Missão pertence a outra organização");
        }

        if (mission.getStatus() == MissionStatus.CONCLUIDA || mission.getStatus() == MissionStatus.CANCELADA) {
            throw new BusinessRuleException("Não é possível adicionar participantes a uma missão com status " + mission.getStatus() + ".");
        }

        Adventurer adventurer = adventurerRepository.findById(dto.aventureiroId())
                .orElseThrow(() -> new ResourceNotFoundException("Aventureiro não encontrado"));

        if (!adventurer.getOrganization().getId().equals(orgId)) {
            throw new BusinessRuleException("O aventureiro não pertence à mesma organização da missão.");
        }

        if (!adventurer.getAtivo()) {
            throw new BusinessRuleException("Aventureiro inativo não pode ser associado a novas missões.");
        }

        if (participationRepository.existsByMissionIdAndAdventurerId(missionId, dto.aventureiroId())) {
            throw new BusinessRuleException("Este aventureiro já participa desta missão.");
        }

        MissionParticipation participation = new MissionParticipation();
        participation.setMission(mission);
        participation.setAdventurer(adventurer);
        participation.setPapel(dto.papel());
        participation.setRecompensaOuro(dto.recompensaOuro());
        participation.setDestaque(dto.destaque());

        participationRepository.save(participation);

        return new MissionParticipationDto(
                adventurer.getId(), adventurer.getNome(),
                participation.getPapel(), participation.getRecompensaOuro(),
                participation.getDestaque()
        );
    }

    @Transactional
    public void deleteMission(Long orgId, Long id) {
        Mission m = missionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Missão não encontrada"));
        if (!m.getOrganization().getId().equals(orgId)) {
            throw new ResourceNotFoundException("Organização inválida");
        }
        
        Specification<MissionParticipation> specPart = (root, query, cb) -> 
            cb.equal(root.get("mission").get("id"), id);
        
        if (participationRepository.count(specPart) > 0) {
            throw new BusinessRuleException("Não é possível apagar uma missão que já possui participantes.");
        }

        missionRepository.delete(m);
    }

    @Transactional
    public MissionSummaryDto updateMission(Long orgId, Long id, MissionSummaryDto dto) {
        Mission m = missionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Missão não encontrada"));
        if (!m.getOrganization().getId().equals(orgId)) {
            throw new ResourceNotFoundException("Organização inválida");
        }
        
        if (dto.titulo() != null) m.setTitulo(dto.titulo());
        if (dto.nivelPerigo() != null) m.setNivelPerigo(dto.nivelPerigo());
        if (dto.status() != null) m.setStatus(dto.status());
        if (dto.dataInicio() != null) m.setDataInicio(dto.dataInicio());
        if (dto.dataTermino() != null) m.setDataTermino(dto.dataTermino());
        missionRepository.save(m);
        
        return toSummaryDto(m);
    }

    private MissionSummaryDto toSummaryDto(Mission m) {
        return new MissionSummaryDto(m.getId(), m.getTitulo(), m.getNivelPerigo(), m.getStatus(), m.getDataInicio(), m.getDataTermino());
    }

    public Object search(Long orgId, MissionFilterDto filter, Pageable pageable) {
        return null;
    }
}
