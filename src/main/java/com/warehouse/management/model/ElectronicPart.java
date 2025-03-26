package com.warehouse.management.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "electronic_parts")
public class ElectronicPart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Part type is required")
    @ManyToOne
    @JoinColumn(name = "part_type_id")
    private PartType partType;

    @NotBlank(message = "Name is required")
    @Column(length = 256)
    private String name;

    @NotBlank(message = "Properties are required")
    @Column(length = 256)
    private String properties;

    @NotNull(message = "Quantity is required")
    @Min(value = 0, message = "Quantity must be greater than or equal to 0")
    private Integer quantity;

    @NotNull(message = "Storage location is required")
    @ManyToOne
    @JoinColumn(name = "storage_location_id")
    private StorageLocation storageLocation;

    @Lob
    @Column(name = "image_data")
    private byte[] imageData;

    @Column(name = "image_content_type")
    private String imageContentType;

    // Constructors
    public ElectronicPart() {
    }

    public ElectronicPart(PartType partType, String name, String properties, Integer quantity, StorageLocation storageLocation) {
        this.partType = partType;
        this.name = name;
        this.properties = properties;
        this.quantity = quantity;
        this.storageLocation = storageLocation;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PartType getPartType() {
        return partType;
    }

    public void setPartType(PartType partType) {
        this.partType = partType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProperties() {
        return properties;
    }

    public void setProperties(String properties) {
        this.properties = properties;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public StorageLocation getStorageLocation() {
        return storageLocation;
    }

    public void setStorageLocation(StorageLocation storageLocation) {
        this.storageLocation = storageLocation;
    }

    public byte[] getImageData() {
        return imageData;
    }

    public void setImageData(byte[] imageData) {
        this.imageData = imageData;
    }

    public String getImageContentType() {
        return imageContentType;
    }

    public void setImageContentType(String imageContentType) {
        this.imageContentType = imageContentType;
    }

    public boolean hasImage() {
        return imageData != null && imageData.length > 0;
    }

    @Override
    public String toString() {
        return "ElectronicPart{" +
                "id=" + id +
                ", partType=" + partType +
                ", name='" + name + '\'' +
                ", properties='" + properties + '\'' +
                ", quantity=" + quantity +
                ", storageLocation=" + storageLocation +
                ", hasImage=" + hasImage() +
                '}';
    }
}
