package com.example.adventure_management_api.controller;

import com.example.adventure_management_api.dto.*;
import com.example.adventure_management_api.entity.enums.AdventurerClass;
import com.example.adventure_management_api.service.AdventurerService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/adventurers")
public class AdventurerController {

    private final AdventurerService adventurerService;

    public AdventurerController(AdventurerService adventurerService) {
        this.adventurerService = adventurerService;
    }

    @GetMapping
    public ResponseEntity<Page<AdventurerSummaryDto>> list(
            @RequestHeader("X-Organization-Id") Long orgId,
            @RequestParam(required = false) Boolean ativo,
            @RequestParam(required = false) AdventurerClass classe,
            @RequestParam(required = false) Integer nivelMinimo,
            @RequestParam(required = false) String nome,
            Pageable pageable) {

        return ResponseEntity.ok(adventurerService.search(orgId, ativo, classe, nivelMinimo, nome, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdventurerFullDto> getFullProfile(
            @RequestHeader("X-Organization-Id") Long orgId,
            @PathVariable Long id) {
        
        return ResponseEntity.ok(adventurerService.getFullProfile(orgId, id));
    }

    @PostMapping
    public ResponseEntity<AdventurerSummaryDto> create(
            @RequestHeader("X-Organization-Id") Long orgId,
            @Valid @RequestBody CreateAdventurerDto dto) {

        return ResponseEntity.status(HttpStatus.CREATED).body(adventurerService.createAdventurer(orgId, dto));
    }

    @PostMapping("/{id}/companion")
    public ResponseEntity<CompanionDto> addCompanion(
            @RequestHeader("X-Organization-Id") Long orgId,
            @PathVariable Long id,
            @Valid @RequestBody CreateCompanionDto dto) {

        return ResponseEntity.status(HttpStatus.CREATED).body(adventurerService.addCompanion(orgId, id, dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AdventurerSummaryDto> update(
            @RequestHeader("X-Organization-Id") Long orgId,
            @PathVariable Long id,
            @RequestBody AdventurerSummaryDto dto) {
        
        return ResponseEntity.ok(adventurerService.updateAdventurer(orgId, id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @RequestHeader("X-Organization-Id") Long orgId,
            @PathVariable Long id) {
        
        adventurerService.deleteAdventurer(orgId, id);
        return ResponseEntity.noContent().build();
    }
}
