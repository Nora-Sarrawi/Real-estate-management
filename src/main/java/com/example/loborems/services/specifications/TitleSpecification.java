package com.example.loborems.services.specifications;

import com.example.loborems.models.Property;

public class TitleSpecification implements Specification<Property> {
    private String title;

    public TitleSpecification(String title) {
        this.title = title;
    }


    @Override
    public boolean isSatisfiedBy(Property item) {
        return title == null || item.getTitle().toLowerCase().contains(title.toLowerCase());
    }
}
