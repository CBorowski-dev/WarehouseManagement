package com.warehouse.management.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.warehouse.management.model.PartType;
import com.warehouse.management.repository.ElectronicPartRepository;
import com.warehouse.management.repository.PartTypeRepository;

@Service
public class PartTypeService {

    private final PartTypeRepository partTypeRepository;
    private final ElectronicPartRepository electronicPartRepository;

    @Autowired
    public PartTypeService(PartTypeRepository partTypeRepository, ElectronicPartRepository electronicPartRepository) {
        this.partTypeRepository = partTypeRepository;
        this.electronicPartRepository = electronicPartRepository;
    }

    public List<PartType> getAllPartTypes() {
        return partTypeRepository.findAll();
    }

    public PartType getPartTypeById(Long id) {
        return partTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Part type not found with id: " + id));
    }

    public PartType createPartType(PartType partType) {
        // Check if a part type with the same name already exists
        PartType existingType = partTypeRepository.findByNameIgnoreCase(partType.getName());
        if (existingType != null) {
            throw new RuntimeException("A part type with the name '" + partType.getName() + "' already exists");
        }
        return partTypeRepository.save(partType);
    }

    public PartType updatePartType(Long id, PartType partTypeDetails) {
        PartType partType = getPartTypeById(id);
        
        // Check if the new name is already in use by another part type
        PartType existingType = partTypeRepository.findByNameIgnoreCase(partTypeDetails.getName());
        if (existingType != null && !existingType.getId().equals(id)) {
            throw new RuntimeException("A part type with the name '" + partTypeDetails.getName() + "' already exists");
        }
        
        partType.setName(partTypeDetails.getName());
        return partTypeRepository.save(partType);
    }

    public void deletePartType(Long id) {
        PartType partType = getPartTypeById(id);
        
        // Check if the part type is in use by any electronic parts
        if (electronicPartRepository.existsByPartType(partType)) {
            throw new RuntimeException("Cannot delete part type '" + partType.getName() + "' because it is in use by one or more electronic parts");
        }
        
        partTypeRepository.delete(partType);
    }
    
    public boolean isPartTypeInUse(Long id) {
        PartType partType = getPartTypeById(id);
        return electronicPartRepository.existsByPartType(partType);
    }
    
    public long getPartTypeUsageCount(Long id) {
        PartType partType = getPartTypeById(id);
        return electronicPartRepository.countByPartType(partType);
    }

    // Initialize default part types if none exist
    public void initializeDefaultPartTypes() {
        if (partTypeRepository.count() == 0) {
            String[] defaultTypes = {"Capacitor", "Power Supply", "Resistor", "Microcontroller", "Transistor", "Diode", "LED", "Sensor", "Switch", "Connector"};
            for (String typeName : defaultTypes) {
                partTypeRepository.save(new PartType(typeName));
            }
        }
    }
}
