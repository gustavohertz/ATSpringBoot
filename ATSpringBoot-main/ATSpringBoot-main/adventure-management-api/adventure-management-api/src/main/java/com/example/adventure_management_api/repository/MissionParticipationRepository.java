package com.example.adventure_management_api.repository;

import com.example.adventure_management_api.entity.MissionParticipation;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.adventure_management_api.dto.reports.AdventurerRankingDto;
import com.example.adventure_management_api.entity.enums.MissionStatus;
import java.time.Instant;
import java.util.List;


@Repository
public interface MissionParticipationRepository extends JpaRepository<MissionParticipation, Long>, JpaSpecificationExecutor<MissionParticipation> {
    
    boolean existsByMissionIdAndAdventurerId(Long missionId, Long adventurerId);

    @Query("SELECT new com.example.adventure_management_api.dto.reports.AdventurerRankingDto(" +
           "a.id, a.nome, COUNT(p.id), COALESCE(SUM(p.recompensaOuro), 0), " +
           "SUM(CASE WHEN p.destaque = true THEN 1L ELSE 0L END)) " +
           "FROM MissionParticipation p " +
           "JOIN p.adventurer a " +
           "JOIN p.mission m " +
           "WHERE a.organization.id = :organizationId " +
           "AND (cast(:dataInicio as java.time.Instant) IS NULL OR m.createdAt >= :dataInicio) " +
           "AND (cast(:dataTermino as java.time.Instant) IS NULL OR m.createdAt <= :dataTermino) " +
           "AND (:status IS NULL OR m.status = :status) " +
           "GROUP BY a.id, a.nome " +
           "ORDER BY COUNT(p.id) DESC, COALESCE(SUM(p.recompensaOuro), 0) DESC")
    List<AdventurerRankingDto> getAdventurerRanking(
           @Param("organizationId") Long organizationId,
           @Param("dataInicio") Instant dataInicio,
           @Param("dataTermino") Instant dataTermino,
           @Param("status") MissionStatus status
    );
}
