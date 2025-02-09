package com.example.loborems.interfaces;

import com.example.loborems.models.Property;
import java.util.List;

public interface PropertyDAO {
    void save(Property property);
    Property getById(long id);
    void update(Property property);
    void delete(Property property);
    List<Property> getAllProperties();
    // this method to handle type changes explicitly
    void handleTypeChange(Property oldProperty, Property newProperty);
}