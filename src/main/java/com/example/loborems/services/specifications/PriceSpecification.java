package com.example.loborems.services.specifications;


import com.example.loborems.models.Property;

public class PriceSpecification implements Specification<Property> {
    private Double minPrice;
    private Double maxPrice;

    public PriceSpecification(Double minPrice, Double maxPrice) {
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
    }

    @Override
    public boolean isSatisfiedBy(Property property) {
        return (minPrice == null || property.getPrice() >= minPrice) &&
                (maxPrice == null || property.getPrice() <= maxPrice);
    }
}
