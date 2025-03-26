package com.warehouse.management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.warehouse.management.model.PartType;

@Repository
public interface PartTypeRepository extends JpaRepository<PartType, Long> {
    // Find by name (case-insensitive)
    PartType findByNameIgnoreCase(String name);
}
