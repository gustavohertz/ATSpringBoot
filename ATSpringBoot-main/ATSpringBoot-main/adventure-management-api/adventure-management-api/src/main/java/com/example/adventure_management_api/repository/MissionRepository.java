package com.example.adventure_management_api.repository;

import com.example.adventure_management_api.dto.MissionSummaryDto;
import com.example.adventure_management_api.entity.Mission;
import com.example.adventure_management_api.entity.enums.MissionDangerLevel;
import com.example.adventure_management_api.entity.enums.MissionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.adventure_management_api.dto.reports.MissionMetricsDto;
import java.time.Instant;
import java.util.List;

@Repository
public interface MissionRepository extends JpaRepository<Mission, Long>, JpaSpecificationExecutor<Mission> {
    
    @Query("SELECT new com.example.adventure_management_api.dto.reports.MissionMetricsDto(" +
           "m.id, m.titulo, m.status, m.nivelPerigo, COUNT(p.id), COALESCE(SUM(p.recompensaOuro), 0)) " +
           "FROM Mission m " +
           "LEFT JOIN MissionParticipation p ON p.mission.id = m.id " +
           "WHERE m.organization.id = :organizationId " +
           "AND (cast(:dataInicio as java.time.Instant) IS NULL OR m.createdAt >= :dataInicio) " +
           "AND (cast(:dataTermino as java.time.Instant) IS NULL OR m.createdAt <= :dataTermino) " +
           "GROUP BY m.id, m.titulo, m.status, m.nivelPerigo " +
           "ORDER BY m.createdAt DESC")
    List<MissionMetricsDto> getMissionMetrics(
           @Param("organizationId") Long organizationId,
           @Param("dataInicio") Instant dataInicio,
           @Param("dataTermino") Instant dataTermino
    );

    Page<MissionSummaryDto> findById(Long orgId, MissionStatus status, MissionDangerLevel missionDangerLevel, Instant instant, Instant instant1, Pageable pageable);
}
