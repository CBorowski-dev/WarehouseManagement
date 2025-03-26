package com.warehouse.management.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.warehouse.management.service.PartTypeService;
import com.warehouse.management.service.StorageLocationService;

@Component
public class DataInitializer implements CommandLineRunner {

    private final PartTypeService partTypeService;
    private final StorageLocationService storageLocationService;

    @Autowired
    public DataInitializer(PartTypeService partTypeService, StorageLocationService storageLocationService) {
        this.partTypeService = partTypeService;
        this.storageLocationService = storageLocationService;
    }

    @Override
    public void run(String... args) {
        // Initialize default part types and storage locations
        partTypeService.initializeDefaultPartTypes();
        storageLocationService.initializeDefaultStorageLocations();
    }
}
