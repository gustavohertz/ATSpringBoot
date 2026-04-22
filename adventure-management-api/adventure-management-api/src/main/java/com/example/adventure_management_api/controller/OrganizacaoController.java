package com.example.adventure_management_api.controller;

import com.example.adventure_management_api.entity.Organization;
import com.example.adventure_management_api.repository.OrganizationRepository;
import com.example.adventure_management_api.service.OrganizacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/organizacoes")
public class OrganizacaoController {

    @Autowired
    private OrganizationRepository organizationRepository;
    private OrganizacaoService organizacaoService;

    @PostMapping
    public ResponseEntity<Organization> criarOrganizacao(@RequestBody Organization organization) {
        Organization savedOrganization = organizacaoService.create(organization);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedOrganization);
    }

    @GetMapping
    public ResponseEntity<List<Organization>> listarTodas() {
        List<Organization> organizacoes = organizationRepository.findAll();
        return ResponseEntity.ok(organizacoes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Organization> buscarPorId(@PathVariable Long id) {
        return organizationRepository.findById(id)
                .map(organizacao -> ResponseEntity.ok().body(organizacao))
                .orElse(ResponseEntity.notFound().build()); // Retorna erro 404 se o ID não existir
    }
}