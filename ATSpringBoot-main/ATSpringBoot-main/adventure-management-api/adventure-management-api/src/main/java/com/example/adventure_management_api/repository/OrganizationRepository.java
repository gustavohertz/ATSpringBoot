package com.example.adventure_management_api.repository;

import com.example.adventure_management_api.entity.Organization;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrganizationRepository extends JpaRepository<Organization, Long> {

}
