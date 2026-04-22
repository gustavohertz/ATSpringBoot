package com.example.adventure_management_api.service;

import com.example.adventure_management_api.entity.Organization;
import com.example.adventure_management_api.repository.OrganizationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class OrganizacaoService {

    @Autowired
    private OrganizationRepository organizationRepository;

    @Transactional
    public Organization create(Organization organization) {
        if (organization.getCreatedAt() == null) {
            organization.setCreatedAt(Instant.now());
        }
        return organizationRepository.save(organization);
    }

    public List<Organization> findAll() {
        return organizationRepository.findAll();
    }

    public Optional<Organization> findById(Long id) {
        return organizationRepository.findById(id);
    }
}