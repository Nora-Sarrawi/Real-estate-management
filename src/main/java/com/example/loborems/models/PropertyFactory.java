package com.example.loborems.models;

public class PropertyFactory {
    public static Property createProperty(String type) {
        if (type == null || type.isEmpty()) {
            throw new IllegalArgumentException("Property type cannot be null or empty");
        }

        switch (type) {
            case "Residential":
                return new ResidentialProperty();
            case "Commercial":
                return new CommercialProperty();
            default:
                throw new IllegalArgumentException("Unknown property type: " + type);
        }
    }
}
