package com.warehouse.management.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.warehouse.management.model.StorageLocation;
import com.warehouse.management.repository.ElectronicPartRepository;
import com.warehouse.management.repository.StorageLocationRepository;

@Service
public class StorageLocationService {

    private final StorageLocationRepository storageLocationRepository;
    private final ElectronicPartRepository electronicPartRepository;

    @Autowired
    public StorageLocationService(StorageLocationRepository storageLocationRepository, ElectronicPartRepository electronicPartRepository) {
        this.storageLocationRepository = storageLocationRepository;
        this.electronicPartRepository = electronicPartRepository;
    }

    public List<StorageLocation> getAllStorageLocations() {
        return storageLocationRepository.findAll();
    }

    public StorageLocation getStorageLocationById(Long id) {
        return storageLocationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Storage location not found with id: " + id));
    }

    public StorageLocation createStorageLocation(StorageLocation storageLocation) {
        // Check if a storage location with the same name already exists
        StorageLocation existingLocation = storageLocationRepository.findByNameIgnoreCase(storageLocation.getName());
        if (existingLocation != null) {
            throw new RuntimeException("A storage location with the name '" + storageLocation.getName() + "' already exists");
        }
        return storageLocationRepository.save(storageLocation);
    }

    public StorageLocation updateStorageLocation(Long id, StorageLocation storageLocationDetails) {
        StorageLocation storageLocation = getStorageLocationById(id);
        
        // Check if the new name is already in use by another storage location
        StorageLocation existingLocation = storageLocationRepository.findByNameIgnoreCase(storageLocationDetails.getName());
        if (existingLocation != null && !existingLocation.getId().equals(id)) {
            throw new RuntimeException("A storage location with the name '" + storageLocationDetails.getName() + "' already exists");
        }
        
        storageLocation.setName(storageLocationDetails.getName());
        return storageLocationRepository.save(storageLocation);
    }

    public void deleteStorageLocation(Long id) {
        StorageLocation storageLocation = getStorageLocationById(id);
        
        // Check if the storage location is in use by any electronic parts
        if (electronicPartRepository.existsByStorageLocation(storageLocation)) {
            throw new RuntimeException("Cannot delete storage location '" + storageLocation.getName() + "' because it is in use by one or more electronic parts");
        }
        
        storageLocationRepository.delete(storageLocation);
    }
    
    public boolean isStorageLocationInUse(Long id) {
        StorageLocation storageLocation = getStorageLocationById(id);
        return electronicPartRepository.existsByStorageLocation(storageLocation);
    }
    
    public long getStorageLocationUsageCount(Long id) {
        StorageLocation storageLocation = getStorageLocationById(id);
        return electronicPartRepository.countByStorageLocation(storageLocation);
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
