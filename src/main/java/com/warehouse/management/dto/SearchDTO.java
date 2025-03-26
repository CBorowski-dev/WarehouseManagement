package com.warehouse.management.dto;

public class SearchDTO {
    private String type;
    private String name;
    private String properties;
    private String location;

    // Constructors
    public SearchDTO() {
    }

    public SearchDTO(String type, String name, String properties, String location) {
        this.type = type;
        this.name = name;
        this.properties = properties;
        this.location = location;
    }

    // Getters and Setters
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    // Helper method to check if at least one search field is filled
    public boolean hasSearchCriteria() {
        return (type != null && !type.trim().isEmpty()) ||
               (name != null && !name.trim().isEmpty()) ||
               (properties != null && !properties.trim().isEmpty()) ||
               (location != null && !location.trim().isEmpty());
    }

    @Override
    public String toString() {
        return "SearchDTO{" +
                "type='" + type + '\'' +
                ", name='" + name + '\'' +
                ", properties='" + properties + '\'' +
                ", location='" + location + '\'' +
                '}';
    }
}
