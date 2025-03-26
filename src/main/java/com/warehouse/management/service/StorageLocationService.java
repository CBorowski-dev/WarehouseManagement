package com.warehouse.management.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.warehouse.management.model.StorageLocation;
import com.warehouse.management.repository.StorageLocationRepository;

@Service
public class StorageLocationService {

    private final StorageLocationRepository storageLocationRepository;

    @Autowired
    public StorageLocationService(StorageLocationRepository storageLocationRepository) {
        this.storageLocationRepository = storageLocationRepository;
    }

    public List<StorageLocation> getAllStorageLocations() {
        return storageLocationRepository.findAll();
    }

    public StorageLocation getStorageLocationById(Long id) {
        return storageLocationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Storage location not found with id: " + id));
    }

    public StorageLocation createStorageLocation(StorageLocation storageLocation) {
        return storageLocationRepository.save(storageLocation);
    }

    public StorageLocation updateStorageLocation(Long id, StorageLocation storageLocationDetails) {
        StorageLocation storageLocation = getStorageLocationById(id);
        storageLocation.setName(storageLocationDetails.getName());
        return storageLocationRepository.save(storageLocation);
    }

    public void deleteStorageLocation(Long id) {
        StorageLocation storageLocation = getStorageLocationById(id);
        storageLocationRepository.delete(storageLocation);
    }

    // Initialize default storage locations if none exist
    public void initializeDefaultStorageLocations() {
        if (storageLocationRepository.count() == 0) {
            String[] defaultLocations = {"Carton 1", "Carton 2", "Shelf Top", "Shelf Middle", "Shelf Bottom", "Drawer Top", "Drawer Bottom", "Cabinet A", "Cabinet B", "Workbench"};
            for (String locationName : defaultLocations) {
                storageLocationRepository.save(new StorageLocation(locationName));
            }
        }
    }
}
