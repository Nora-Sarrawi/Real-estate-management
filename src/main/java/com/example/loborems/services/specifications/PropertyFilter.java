package com.example.loborems.services.specifications;


import com.example.loborems.models.Property;

import java.util.List;
import java.util.stream.Collectors;

public class PropertyFilter {

    private String title;
    private String location;
    private Double minPrice;
    private Double maxPrice;
    private String propertyType;

    // Getters and setters for filter fields
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Double getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(Double minPrice) {
        this.minPrice = minPrice;
    }

    public Double getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(Double maxPrice) {
        this.maxPrice = maxPrice;
    }

    public String getPropertyType() {
        return propertyType;
    }

    public void setPropertyType(String propertyType) {
        this.propertyType = propertyType;
    }

    // Filter method
    public List<Property> applyFilters(List<Property> properties) {
        return properties.stream()
                .filter(property -> title == null || property.getTitle().toLowerCase().contains(title.toLowerCase()))
                .filter(property -> location == null || property.getLocation().toLowerCase().contains(location.toLowerCase()))
                .filter(property -> minPrice == null || property.getPrice() >= minPrice)
                .filter(property -> maxPrice == null || property.getPrice() <= maxPrice)
                .filter(property -> propertyType == null || property.getType().equalsIgnoreCase(propertyType))
                .collect(Collectors.toList());
    }
}
