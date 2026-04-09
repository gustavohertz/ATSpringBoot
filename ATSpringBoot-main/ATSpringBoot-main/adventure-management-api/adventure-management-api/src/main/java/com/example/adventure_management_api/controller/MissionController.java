package com.example.adventure_management_api.controller;

import com.example.adventure_management_api.dto.*;
import com.example.adventure_management_api.entity.enums.MissionDangerLevel;
import com.example.adventure_management_api.entity.enums.MissionStatus;
import com.example.adventure_management_api.service.MissionService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
@RequestMapping("/api/missions")
public class MissionController {

    private final MissionService missionService;

    public MissionController(MissionService missionService) {
        this.missionService = missionService;
    }

    @GetMapping
    public ResponseEntity<Page<MissionSummaryDto>> list(
            @RequestHeader("X-Organization-Id") Long orgId,
            @RequestParam(required = false) MissionStatus status,
            @RequestParam(required = false) MissionDangerLevel nivel,
            @RequestParam(required = false) Instant dataInicio,
            @RequestParam(required = false) Instant dataFim,
            Pageable pageable) {

        return ResponseEntity.ok(missionService.search(orgId, status, nivel, dataInicio, dataFim, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MissionFullDto> getDetails(
            @RequestHeader("X-Organization-Id") Long orgId,
            @PathVariable Long id) {
        
        return ResponseEntity.ok(missionService.getDetails(orgId, id));
    }

    @PostMapping
    public ResponseEntity<MissionSummaryDto> create(
            @RequestHeader("X-Organization-Id") Long orgId,
            @Valid @RequestBody CreateMissionDto dto) {

        return ResponseEntity.status(HttpStatus.CREATED).body(missionService.createMission(orgId, dto));
    }

    @PostMapping("/{id}/participants")
    public ResponseEntity<MissionParticipationDto> addParticipation(
            @RequestHeader("X-Organization-Id") Long orgId,
            @PathVariable Long id,
            @Valid @RequestBody CreateParticipationDto dto) {

        return ResponseEntity.status(HttpStatus.CREATED).body(missionService.addParticipation(orgId, id, dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MissionSummaryDto> update(
            @RequestHeader("X-Organization-Id") Long orgId,
            @PathVariable Long id,
            @RequestBody MissionSummaryDto dto) {
        
        return ResponseEntity.ok(missionService.updateMission(orgId, id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @RequestHeader("X-Organization-Id") Long orgId,
            @PathVariable Long id) {
        
        missionService.deleteMission(orgId, id);
        return ResponseEntity.noContent().build();
    }
}
