package com.example.loborems.controllers;

import com.example.loborems.models.CommercialProperty;
import com.example.loborems.models.Property;
import com.example.loborems.models.ResidentialProperty;

import com.example.loborems.models.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;
import javafx.scene.Parent;


public class PropertyItemController {
    @FXML
    private Label propertyName;
    @FXML
    private Label locationLabel;
    @FXML
    private Label priceLabel;
    @FXML
    private Label sizeLabel;

    @FXML
    private Label statusLabel;
    @FXML
    private Label typeLabel;

    // Labels for type-specific fields
    @FXML
    private Label bedroomsLabel;
    @FXML
    private Label gardenLabel;
    @FXML
    private Label floorsLabel;
    @FXML
    private Label parkingLabel;
    @FXML
    private ImageView propertyImage;
    @FXML
    private Button viewMoreButton;

    private Property property;

    public void setPropertyDetails(Property property) {
        if (property == null) return;

        this.property = property;
        // Shared fields
        propertyName.setText(property.getTitle());
        locationLabel.setText("📍 " + property.getLocation());
        priceLabel.setText("💰 " + String.format("%,.2f", property.getPrice()));
        sizeLabel.setText("📐 " + property.getSize());
        statusLabel.setText(property.getStatus() != null ? property.getStatus() : "No Status");
        typeLabel.setText(property.getType());

        // Type-specific logic
        if (property instanceof ResidentialProperty rp) {
            bedroomsLabel.setVisible(true);
            bedroomsLabel.setManaged(true);
            gardenLabel.setVisible(true);
            gardenLabel.setManaged(true);

            floorsLabel.setVisible(false);
            floorsLabel.setManaged(true);   // keep space
            parkingLabel.setVisible(false);
            parkingLabel.setManaged(true);

            bedroomsLabel.setText("Bedrooms: " + rp.getNumberOfBedrooms());
            gardenLabel.setText("Garden: " + (rp.isHasGarden() ? "Yes" : "No"));
        } else if (property instanceof CommercialProperty cp) {
            floorsLabel.setVisible(true);
            floorsLabel.setManaged(true);
            parkingLabel.setVisible(true);
            parkingLabel.setManaged(true);

            bedroomsLabel.setVisible(false);
            bedroomsLabel.setManaged(true); // keep space
            gardenLabel.setVisible(false);
            gardenLabel.setManaged(true);

            floorsLabel.setText("Floors: " + cp.getNumberOfFloors());
            parkingLabel.setText("Parking: " + cp.getParkingSpaces());
        }


        if (property.getImages() != null && !property.getImages().isEmpty()) {

            String[] base64Images = property.getImages().split(",");
            if (base64Images.length > 0) {
                String firstBase64 = base64Images[0];  // The first image's Base64
                try {
                    byte[] decodedBytes = Base64.getDecoder().decode(firstBase64);
                    Image firstImage = new Image(new ByteArrayInputStream(decodedBytes));
                    propertyImage.setImage(firstImage);
                } catch (IllegalArgumentException ex) {
                    ex.printStackTrace();
                    System.out.println("Failed to decode base64 image. Using placeholder if any.");

                }
            }
        }

    }

    private User currentUser;

    public void setCurrentUser(User user) {
        this.currentUser = user;
        System.out.println("PropertyItemController - Current User Role: " +
                (user != null && user.getRole() != null ? user.getRole().getId() : "null"));
    }

    @FXML
    public void onViewButtonClicked() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/loborems/PropertyDetails/property-details.fxml"));
            Parent root = loader.load();

            PropertyDetailsController controller = loader.getController();
            controller.setProperty(property);
            System.out.println("PropertyItemController onViewButtonClicked - Passing User Role: " +
                    (currentUser != null && currentUser.getRole() != null ? currentUser.getRole().getId() : "null"));
            controller.setCurrentUser(currentUser);  // Make sure this line is called

            Stage stage = (Stage) viewMoreButton.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}