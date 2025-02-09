package com.example.loborems.models;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("Commercial")
public class CommercialProperty extends Property {
    private int numberOfFloors;
    private int parkingSpaces;
    @Override
    public void setType(String type) {
        if (!"Commercial".equals(type)) {
            throw new IllegalArgumentException("Invalid type for CommercialProperty");
        }
    }
    @Override
    public String getType() {
        return "Commercial";
    }
    public int getNumberOfFloors() { return numberOfFloors; }
    public void setNumberOfFloors(int numberOfFloors) { this.numberOfFloors = numberOfFloors; }
    public int getParkingSpaces() { return parkingSpaces; }
    public void setParkingSpaces(int parkingSpaces) { this.parkingSpaces = parkingSpaces; }
}