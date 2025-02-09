package com.example.loborems.services.specifications;

import com.example.loborems.models.Property;

public class PropertyTypeSpecification implements Specification<Property> {
    private String propertyType;

    public PropertyTypeSpecification(String propertyType) {
        this.propertyType = propertyType;
    }

    @Override
    public boolean isSatisfiedBy(Property property) {
        return propertyType == null || property.getType().equalsIgnoreCase(propertyType);
    }
}