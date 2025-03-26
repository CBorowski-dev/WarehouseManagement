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
}
