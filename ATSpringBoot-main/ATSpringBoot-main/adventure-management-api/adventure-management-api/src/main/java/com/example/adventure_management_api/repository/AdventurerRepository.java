package com.example.adventure_management_api.repository;

import com.example.adventure_management_api.entity.Adventurer;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdventurerRepository extends JpaRepository<Adventurer, Long>, JpaSpecificationExecutor<Adventurer> {
}
