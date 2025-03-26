package com.warehouse.management.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.warehouse.management.model.PartType;
import com.warehouse.management.repository.PartTypeRepository;

@Service
public class PartTypeService {

    private final PartTypeRepository partTypeRepository;

    @Autowired
    public PartTypeService(PartTypeRepository partTypeRepository) {
        this.partTypeRepository = partTypeRepository;
    }

    public List<PartType> getAllPartTypes() {
        return partTypeRepository.findAll();
    }

    public PartType getPartTypeById(Long id) {
        return partTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Part type not found with id: " + id));
    }

    public PartType createPartType(PartType partType) {
        return partTypeRepository.save(partType);
    }

    public PartType updatePartType(Long id, PartType partTypeDetails) {
        PartType partType = getPartTypeById(id);
        partType.setName(partTypeDetails.getName());
        return partTypeRepository.save(partType);
    }

    public void deletePartType(Long id) {
        PartType partType = getPartTypeById(id);
        partTypeRepository.delete(partType);
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
