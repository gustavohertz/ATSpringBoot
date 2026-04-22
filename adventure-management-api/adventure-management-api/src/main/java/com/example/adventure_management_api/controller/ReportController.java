package com.example.adventure_management_api.controller;

import com.example.adventure_management_api.dto.reports.AdventurerRankingDto;
import com.example.adventure_management_api.dto.reports.MissionMetricsDto;
import com.example.adventure_management_api.entity.enums.MissionStatus;
import com.example.adventure_management_api.service.ReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/ranking")
    public ResponseEntity<List<AdventurerRankingDto>> getRanking(
            @RequestHeader("X-Organization-Id") Long orgId,
            @RequestParam(required = false) Instant dataInicio,
            @RequestParam(required = false) Instant dataTermino,
            @RequestParam(required = false) MissionStatus status) {

        return ResponseEntity.ok(reportService.getAdventurerRanking(orgId, dataInicio, dataTermino, status));
    }

    @GetMapping("/missions-metrics")
    public ResponseEntity<List<MissionMetricsDto>> getMissionMetrics(
            @RequestHeader("X-Organization-Id") Long orgId,
            @RequestParam(required = false) Instant dataInicio,
            @RequestParam(required = false) Instant dataTermino) {

        return ResponseEntity.ok(reportService.getMissionMetrics(orgId, dataInicio, dataTermino));
    }
}
