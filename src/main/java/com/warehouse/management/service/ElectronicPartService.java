package com.warehouse.management.service;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.warehouse.management.model.ElectronicPart;
import com.warehouse.management.repository.ElectronicPartRepository;

@Service
public class ElectronicPartService {

    private final ElectronicPartRepository electronicPartRepository;

    @Autowired
    public ElectronicPartService(ElectronicPartRepository electronicPartRepository) {
        this.electronicPartRepository = electronicPartRepository;
    }

    public List<ElectronicPart> getAllElectronicParts() {
        return electronicPartRepository.findAll();
    }

    public ElectronicPart getElectronicPartById(Long id) {
        return electronicPartRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Electronic part not found with id: " + id));
    }

    public ElectronicPart createElectronicPart(ElectronicPart electronicPart) {
        System.out.println("ID : " + electronicPart.getId());
        return electronicPartRepository.save(electronicPart);
    }

    public ElectronicPart updateElectronicPart(Long id, ElectronicPart electronicPartDetails) {
        ElectronicPart electronicPart = getElectronicPartById(id);
        
        // Update fields
        electronicPart.setPartType(electronicPartDetails.getPartType());
        electronicPart.setName(electronicPartDetails.getName());
        electronicPart.setProperties(electronicPartDetails.getProperties());
        electronicPart.setQuantity(electronicPartDetails.getQuantity());
        electronicPart.setStorageLocation(electronicPartDetails.getStorageLocation());
        
        // Only update image if a new one is provided
        if (electronicPartDetails.getImageData() != null && electronicPartDetails.getImageData().length > 0) {
            electronicPart.setImageData(electronicPartDetails.getImageData());
            electronicPart.setImageContentType(electronicPartDetails.getImageContentType());
        }
        
        return electronicPartRepository.save(electronicPart);
    }

    public void deleteElectronicPart(Long id) {
        ElectronicPart electronicPart = getElectronicPartById(id);
        electronicPartRepository.delete(electronicPart);
    }

    public List<ElectronicPart> searchElectronicParts(String type, String name, String properties, String location) {
        // Convert empty strings to null for the query
        type = (type != null && !type.trim().isEmpty()) ? type.trim() : null;
        name = (name != null && !name.trim().isEmpty()) ? name.trim() : null;
        properties = (properties != null && !properties.trim().isEmpty()) ? properties.trim() : null;
        location = (location != null && !location.trim().isEmpty()) ? location.trim() : null;
        
        // If all search parameters are null, return an empty list
        if (type == null && name == null && properties == null && location == null) {
            return List.of();
        }
        
        return electronicPartRepository.searchParts(type, name, properties, location);
    }

    public ElectronicPart saveElectronicPartWithImage(ElectronicPart electronicPart, MultipartFile imageFile) throws IOException {
        if (imageFile != null && !imageFile.isEmpty()) {
            // Validate file size (1MB max)
            if (imageFile.getSize() > 1048576) { // 1MB in bytes
                throw new RuntimeException("Image file size exceeds the maximum limit of 1MB");
            }
            
            electronicPart.setImageData(imageFile.getBytes());
            electronicPart.setImageContentType(imageFile.getContentType());
        }
        
        return electronicPartRepository.save(electronicPart);
    }
}
