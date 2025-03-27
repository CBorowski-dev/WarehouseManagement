package com.warehouse.management.controller;

import java.io.IOException;
import java.util.Base64;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.warehouse.management.dto.SearchDTO;
import com.warehouse.management.model.ElectronicPart;
import com.warehouse.management.model.PartType;
import com.warehouse.management.model.StorageLocation;
import com.warehouse.management.service.ElectronicPartService;
import com.warehouse.management.service.PartTypeService;
import com.warehouse.management.service.StorageLocationService;

import jakarta.validation.Valid;

@Controller
public class WarehouseController {

    private final ElectronicPartService electronicPartService;
    private final PartTypeService partTypeService;
    private final StorageLocationService storageLocationService;

    @Autowired
    public WarehouseController(ElectronicPartService electronicPartService, PartTypeService partTypeService,
            StorageLocationService storageLocationService) {
        this.electronicPartService = electronicPartService;
        this.partTypeService = partTypeService;
        this.storageLocationService = storageLocationService;
    }

    // Add common model attributes for all requests
    @ModelAttribute
    public void addCommonAttributes(Model model) {
        model.addAttribute("partTypes", partTypeService.getAllPartTypes());
        model.addAttribute("storageLocations", storageLocationService.getAllStorageLocations());
    }

    // Home page - redirects to search page
    @GetMapping("/")
    public String home() {
        return "redirect:/search";
    }

    // Input form for new electronic part
    @GetMapping("/parts/new")
    public String showNewPartForm(Model model) {
        model.addAttribute("part", new ElectronicPart());
        model.addAttribute("isNew", true);
        return "part-form";
    }

    // Save new electronic part
    @PostMapping("/parts/save")
    public String savePart(@Valid @ModelAttribute("part") ElectronicPart part, BindingResult result,
            @RequestParam(value = "image", required = false) MultipartFile imageFile,
            @RequestParam(value = "partTypeId", required = false) Long partTypeId,
            @RequestParam(value = "storageLocationId", required = false) Long storageLocationId,
            Model model, RedirectAttributes redirectAttributes) {
        System.out.println("==> in /parts/save");
        // Set part type and storage location
        if (partTypeId != null) {
            part.setPartType(partTypeService.getPartTypeById(partTypeId));
        }
        if (storageLocationId != null) {
            part.setStorageLocation(storageLocationService.getStorageLocationById(storageLocationId));
        }

        System.out.println("==> in /parts/save (1)");
        // Validate the part
        /*if (result.hasErrors()) {
            model.addAttribute("isNew", part.getId() == null);
            return "part-form";
        }*/

        System.out.println("==> in /parts/save (2)");
        try {
            // Save the part with image if provided
            System.out.println("ID2 : " + part.getId());
            electronicPartService.saveElectronicPartWithImage(part, imageFile);
            redirectAttributes.addFlashAttribute("successMessage", "Electronic part saved successfully!");
            return "redirect:/parts/" + part.getId();
        } catch (IOException e) {
            model.addAttribute("errorMessage", "Error saving image: " + e.getMessage());
            model.addAttribute("isNew", part.getId() == null);
            return "part-form";
        }
    }

    // View/edit electronic part details
    @GetMapping("/parts/{id}")
    public String showPartDetails(@PathVariable Long id, Model model) {
        ElectronicPart part = electronicPartService.getElectronicPartById(id);
        model.addAttribute("part", part);
        model.addAttribute("isNew", false);

        // Add image data as Base64 if available
        if (part.hasImage()) {
            String base64Image = Base64.getEncoder().encodeToString(part.getImageData());
            model.addAttribute("base64Image", base64Image);
            model.addAttribute("imageContentType", part.getImageContentType());
        }

        return "part-form";
    }

    // Delete electronic part
    @PostMapping("/parts/{id}/delete")
    public String deletePart(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        electronicPartService.deleteElectronicPart(id);
        redirectAttributes.addFlashAttribute("successMessage", "Electronic part deleted successfully!");
        return "redirect:/search";
    }

    // Search page
    @GetMapping("/search")
    public String showSearchPage(Model model) {
        if (!model.containsAttribute("searchDTO")) {
            model.addAttribute("searchDTO", new SearchDTO());
        }
        return "search";
    }

    // Perform search
    @Transactional 
    @PostMapping("/search")
    public String searchParts(@ModelAttribute SearchDTO searchDTO, Model model) {
        if (searchDTO.hasSearchCriteria()) {
            List<ElectronicPart> searchResults = electronicPartService.searchElectronicParts(
                    searchDTO.getType(), searchDTO.getName(), searchDTO.getProperties(), searchDTO.getLocation());
            model.addAttribute("searchResults", searchResults);

            // Add image data as Base64 for each part with an image
            for (ElectronicPart part : searchResults) {
                if (part.hasImage()) {
                    String base64Image = Base64.getEncoder().encodeToString(part.getImageData());
                    // System.out.println("==> " + base64Image.length());
                    // System.out.println("==> " + part.getImageContentType());
                    // part.getImageContentType(); // This is used in the template
                    model.addAttribute("base64Image", part.getImageData());
                    model.addAttribute("imageContentType", part.getImageContentType());
                }
            }
        }
        return "search";
    }

    // Reset search
    @GetMapping("/search/reset")
    public String resetSearch() {
        return "redirect:/search";
    }
    
    // ===== Part Type Management =====
    
    // List all part types
    @GetMapping("/part-types")
    public String listPartTypes(Model model) {
        List<PartType> partTypes = partTypeService.getAllPartTypes();
        model.addAttribute("partTypes", partTypes);
        
        // Check which part types are in use
        partTypes.forEach(type -> {
            boolean inUse = partTypeService.isPartTypeInUse(type.getId());
            model.addAttribute("inUse_" + type.getId(), inUse);
            
            if (inUse) {
                long usageCount = partTypeService.getPartTypeUsageCount(type.getId());
                model.addAttribute("usageCount_" + type.getId(), usageCount);
            }
        });
        
        // Add empty PartType for the create form
        model.addAttribute("newPartType", new PartType());
        
        return "part-type-management";
    }
    
    // Create new part type
    @PostMapping("/part-types/create")
    public String createPartType(@Valid @ModelAttribute("newPartType") PartType partType, 
                                BindingResult result, 
                                Model model, 
                                RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            // If there are validation errors, return to the form
            return listPartTypes(model);
        }
        
        try {
            partTypeService.createPartType(partType);
            redirectAttributes.addFlashAttribute("successMessage", "Part type created successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        
        return "redirect:/part-types";
    }
    
    // Update part type
    @PostMapping("/part-types/{id}/update")
    public String updatePartType(@PathVariable Long id,
                                @RequestParam("name") String name,
                                RedirectAttributes redirectAttributes) {
        try {
            PartType partType = new PartType();
            partType.setName(name);
            partTypeService.updatePartType(id, partType);
            redirectAttributes.addFlashAttribute("successMessage", "Part type updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        
        return "redirect:/part-types";
    }
    
    // Delete part type
    @PostMapping("/part-types/{id}/delete")
    public String deletePartType(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            partTypeService.deletePartType(id);
            redirectAttributes.addFlashAttribute("successMessage", "Part type deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        
        return "redirect:/part-types";
    }
    
    // ===== Storage Location Management =====
    
    // List all storage locations
    @GetMapping("/storage-locations")
    public String listStorageLocations(Model model) {
        List<StorageLocation> storageLocations = storageLocationService.getAllStorageLocations();
        model.addAttribute("storageLocations", storageLocations);
        
        // Check which storage locations are in use
        storageLocations.forEach(location -> {
            boolean inUse = storageLocationService.isStorageLocationInUse(location.getId());
            model.addAttribute("inUse_" + location.getId(), inUse);
            
            if (inUse) {
                long usageCount = storageLocationService.getStorageLocationUsageCount(location.getId());
                model.addAttribute("usageCount_" + location.getId(), usageCount);
            }
        });
        
        // Add empty StorageLocation for the create form
        model.addAttribute("newStorageLocation", new StorageLocation());
        
        return "storage-location-management";
    }
    
    // Create new storage location
    @PostMapping("/storage-locations/create")
    public String createStorageLocation(@Valid @ModelAttribute("newStorageLocation") StorageLocation storageLocation, 
                                      BindingResult result, 
                                      Model model, 
                                      RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            // If there are validation errors, return to the form
            return listStorageLocations(model);
        }
        
        try {
            storageLocationService.createStorageLocation(storageLocation);
            redirectAttributes.addFlashAttribute("successMessage", "Storage location created successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        
        return "redirect:/storage-locations";
    }
    
    // Update storage location
    @PostMapping("/storage-locations/{id}/update")
    public String updateStorageLocation(@PathVariable Long id,
                                      @RequestParam("name") String name,
                                      RedirectAttributes redirectAttributes) {
        try {
            StorageLocation storageLocation = new StorageLocation();
            storageLocation.setName(name);
            storageLocationService.updateStorageLocation(id, storageLocation);
            redirectAttributes.addFlashAttribute("successMessage", "Storage location updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        
        return "redirect:/storage-locations";
    }
    
    // Delete storage location
    @PostMapping("/storage-locations/{id}/delete")
    public String deleteStorageLocation(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            storageLocationService.deleteStorageLocation(id);
            redirectAttributes.addFlashAttribute("successMessage", "Storage location deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        
        return "redirect:/storage-locations";
    }
}
