package com.warehouse.management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.warehouse.management.model.StorageLocation;

@Repository
public interface StorageLocationRepository extends JpaRepository<StorageLocation, Long> {
    // Find by name (case-insensitive)
    StorageLocation findByNameIgnoreCase(String name);
}
