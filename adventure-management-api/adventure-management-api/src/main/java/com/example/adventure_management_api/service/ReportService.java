package com.example.adventure_management_api.service;

import com.example.adventure_management_api.dto.reports.AdventurerRankingDto;
import com.example.adventure_management_api.dto.reports.MissionMetricsDto;
import com.example.adventure_management_api.entity.enums.MissionStatus;
import com.example.adventure_management_api.repository.MissionParticipationRepository;
import com.example.adventure_management_api.repository.MissionRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class ReportService {

    private final MissionParticipationRepository participationRepository;
    private final MissionRepository missionRepository;

    public ReportService(MissionParticipationRepository participationRepository, MissionRepository missionRepository) {
        this.participationRepository = participationRepository;
        this.missionRepository = missionRepository;
    }

    public List<AdventurerRankingDto> getAdventurerRanking(Long orgId, Instant dataInicio, Instant dataTermino, MissionStatus status) {
        return participationRepository.getAdventurerRanking(orgId, dataInicio, dataTermino, status);
    }

    public List<MissionMetricsDto> getMissionMetrics(Long orgId, Instant dataInicio, Instant dataTermino) {
        return missionRepository.getMissionMetrics(orgId, dataInicio, dataTermino);
    }
}
