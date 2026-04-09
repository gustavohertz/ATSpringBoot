package com.example.adventure_management_api.controller;

import com.example.adventure_management_api.entity.MvPainelTaticoMissao;
import com.example.adventure_management_api.service.PainelTaticoMissaoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/missoes")
public class PainelTaticoMissaoController {

    private final PainelTaticoMissaoService service;

    public PainelTaticoMissaoController(PainelTaticoMissaoService service) {
        this.service = service;
    }

    @GetMapping("/top15dias")
    @PostMapping("/top15dias")
    public ResponseEntity<List<MvPainelTaticoMissao>> getTop15Dias() {
        return ResponseEntity.ok(service.getTop10MissoesTaticas());
    }
}
