package com.example.loborems.services.specifications;
import com.example.loborems.models.Property;

public class LocationSpecification implements Specification<Property> {
    private String location;

    public LocationSpecification(String location) {
        this.location = location;
    }

    @Override
    public boolean isSatisfiedBy(Property property) {
        return location == null || property.getLocation().toLowerCase().contains(location.toLowerCase());
    }
}
