package com.example.loborems.controllers;

import com.example.loborems.models.*;
import com.example.loborems.services.PropertyService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.Node;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.function.Predicate;

public class AddPropertyController {
    private PropertyService propertyService = new PropertyService();
    @FXML public TextField priceField;
    @FXML public TextArea featuresArea;
    @FXML public ComboBox<String> statusComboBox;
    @FXML public Label photoCountLabel;
    @FXML public Button uploadPhotosButton;
    @FXML public ComboBox<String> propertyTypeComboBox;
    @FXML public TextField locationField;
    @FXML public TextField titleField;
    @FXML public TextField sizeField;

    // New fields for property types
    @FXML public VBox residentialFields;
    @FXML public VBox commercialFields;
    @FXML public TextField bedroomsField;
    @FXML public CheckBox hasGardenCheckBox;
    @FXML public TextField floorsField;
    @FXML public TextField parkingField;


    private List<File> selectedPhotos = new ArrayList<>();
    private Property currentProperty;


    @FXML
    public void initialize() {
        propertyTypeComboBox.setItems(FXCollections.observableArrayList("Residential", "Commercial"));
        statusComboBox.setItems(FXCollections.observableArrayList("Available", "Sold", "Rented"));

        // Find the save button and update its text if in edit mode
        if (isEditMode) {
            Button saveButton = (Button) uploadPhotosButton.getScene().lookup("#saveButton");
            if (saveButton != null) {
                saveButton.setText("Update Property");
            }
        }
    }
    @FXML
    public void handlePropertyTypeChange(ActionEvent event) {
        String selectedType = propertyTypeComboBox.getValue();

        if (selectedType == null || selectedType.trim().isEmpty()) {
            System.out.println("Please select a property type.");
            return;
        }

        // If we're in edit mode and changing property type
        if (isEditMode && currentProperty != null && !currentProperty.getType().equals(selectedType)) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Change Property Type");
            alert.setHeaderText("Warning: Changing Property Type");
            alert.setContentText("Changing property type will reset type-specific fields. Continue?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                // Clear type-specific fields
                if ("Residential".equals(selectedType)) {
                    floorsField.clear();
                    parkingField.clear();
                    bedroomsField.clear();
                    hasGardenCheckBox.setSelected(false);
                } else if ("Commercial".equals(selectedType)) {
                    bedroomsField.clear();
                    hasGardenCheckBox.setSelected(false);
                    floorsField.clear();
                    parkingField.clear();
                }
            } else {
                // User cancelled - revert combo box selection
                propertyTypeComboBox.setValue(currentProperty.getType());
                return;
            }
        }

        // Update UI visibility
        boolean isResidential = "Residential".equals(selectedType);
        boolean isCommercial = "Commercial".equals(selectedType);

        residentialFields.setVisible(isResidential);
        residentialFields.setManaged(isResidential);
        commercialFields.setVisible(isCommercial);
        commercialFields.setManaged(isCommercial);

        System.out.println("Selected Property Type: " + selectedType);
    }



    @FXML
    public void handlePhotoUpload(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Property Photos");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        List<File> files = fileChooser.showOpenMultipleDialog(uploadPhotosButton.getScene().getWindow());

        if (files != null) {
            selectedPhotos.addAll(files);
            photoCountLabel.setText(selectedPhotos.size() + " photo(s) selected");
        }
    }

    @FXML
    public void handleSaveProperty(ActionEvent actionEvent) {
        if (validateInput()) {
            if (!isEditMode && selectedPhotos.size() != 5) {
                showAlert("Validation Error", "Please upload 5 photos.");
                return;
            }

            try {
                String propertyType = propertyTypeComboBox.getValue();
                String title = titleField.getText();
                String location = locationField.getText();
                double size = Double.parseDouble(sizeField.getText());
                double price = Double.parseDouble(priceField.getText());
                String features = featuresArea.getText();
                String status = statusComboBox.getValue();

                if (isEditMode) {
                    // Create new property instance of the selected type
                    Property property = PropertyFactory.createProperty(propertyType);
                    property.setId(editPropertyId);
                    property.setTitle(title);
                    property.setLocation(location);
                    property.setSize(size);
                    property.setPrice(price);
                    property.setFeatures(features);
                    property.setStatus(status);

                    // Keep existing images
                    if (currentProperty != null && currentProperty.getImages() != null) {
                        property.setImages(currentProperty.getImages());
                    }

                    // Set type-specific fields based on the new type
                    if (property instanceof ResidentialProperty) {
                        ResidentialProperty rp = (ResidentialProperty) property;
                        if (!bedroomsField.getText().trim().isEmpty()) {
                            rp.setNumberOfBedrooms(Integer.parseInt(bedroomsField.getText().trim()));
                        }
                        rp.setHasGarden(hasGardenCheckBox.isSelected());
                    } else if (property instanceof CommercialProperty) {
                        CommercialProperty cp = (CommercialProperty) property;
                        if (!floorsField.getText().trim().isEmpty()) {
                            cp.setNumberOfFloors(Integer.parseInt(floorsField.getText().trim()));
                        }
                        if (!parkingField.getText().trim().isEmpty()) {
                            cp.setParkingSpaces(Integer.parseInt(parkingField.getText().trim()));
                        }
                    }

                    propertyService.updateProperty(property);
                    showAlert("Success", "Property has been successfully updated.");
                    navigateToPropertyListing(actionEvent);
                }  else {
                    if ("Residential".equals(propertyType)) {
                        int bedrooms = Integer.parseInt(bedroomsField.getText());
                        propertyService.saveProperty(
                                propertyType, title, location, size, price, features, status,
                                bedrooms, hasGardenCheckBox, null, null, selectedPhotos
                        );
                    } else if ("Commercial".equals(propertyType)) {
                        int floors = Integer.parseInt(floorsField.getText());
                        int parking = Integer.parseInt(parkingField.getText());
                        propertyService.saveProperty(
                                propertyType, title, location, size, price, features, status,
                                null, null, floors, parking, selectedPhotos
                        );
                    }
                    showAlert("Success", "Property has been successfully added.");
                    navigateToPropertyListing(actionEvent);
                }
            } catch (NumberFormatException e) {
                showAlert("Error", "Please check numeric fields for valid numbers.");
            } catch (Exception e) {
                showAlert("Error", "An error occurred: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void navigateToPropertyListing(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/loborems/PropertyListing/property-listing.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((javafx.scene.Node) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            showAlert("Navigation Error", "Could not load the previous page: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void handleCancel(ActionEvent actionEvent) {
        if (areAllFieldsEmpty()) {
            navigateToPropertyListing(actionEvent);
        } else {
            clearForm();
        }
    }

    private boolean areAllFieldsEmpty() {
        return allFieldsEmpty().test(titleField) &&
                allFieldsEmpty().test(locationField) &&
                allFieldsEmpty().test(priceField) &&
                allFieldsEmpty().test(featuresArea) &&
                allFieldsEmpty().test(sizeField) &&
                propertyTypeComboBox.getValue() == null &&
                statusComboBox.getValue() == null &&
                selectedPhotos.isEmpty() &&
                allFieldsEmpty().test(bedroomsField) &&
                !hasGardenCheckBox.isSelected() &&
                allFieldsEmpty().test(floorsField) &&
                allFieldsEmpty().test(parkingField);
    }

    private Predicate<javafx.scene.Node> allFieldsEmpty() {
        return field -> field instanceof TextField && ((TextField) field).getText().trim().isEmpty() ||
                field instanceof TextArea && ((TextArea) field).getText().trim().isEmpty() ||
                field instanceof ComboBox && ((ComboBox<?>) field).getValue() == null;
    }

    private boolean validateInput() {
        StringBuilder errors = new StringBuilder();

        validateField(titleField, "Property title is required.", errors);
        validateField(locationField, "Location is required.", errors);
        validateComboBox(propertyTypeComboBox, "Property type must be selected.", errors);
        validateComboBox(statusComboBox, "Property status must be selected.", errors);
        validatePrice(errors);
        validateSize(errors);
        validateTypeSpecificFields(errors);

        if (errors.length() > 0) {
            showAlert("Validation Error", errors.toString());
            return false;
        }
        return true;
    }
    private void validateTypeSpecificFields(StringBuilder errors) {
        String propertyType = propertyTypeComboBox.getValue();
        if ("Residential".equals(propertyType)) {
            validateField(bedroomsField, "Number of bedrooms is required.", errors);
            try {
                if (!bedroomsField.getText().trim().isEmpty()) {
                    int bedrooms = Integer.parseInt(bedroomsField.getText().trim());
                    if (bedrooms <= 0) {
                        errors.append("Number of bedrooms must be greater than 0.\n");
                    }
                }
            } catch (NumberFormatException e) {
                errors.append("Invalid number of bedrooms format.\n");
            }
        } else if ("Commercial".equals(propertyType)) {
            validateField(floorsField, "Number of floors is required.", errors);
            validateField(parkingField, "Number of parking spaces is required.", errors);

            try {
                if (!floorsField.getText().trim().isEmpty()) {
                    int floors = Integer.parseInt(floorsField.getText().trim());
                    if (floors <= 0) {
                        errors.append("Number of floors must be greater than 0.\n");
                    }
                }
            } catch (NumberFormatException e) {
                errors.append("Invalid number of floors format.\n");
            }

            try {
                if (!parkingField.getText().trim().isEmpty()) {
                    int parkingSpaces = Integer.parseInt(parkingField.getText().trim());
                    if (parkingSpaces < 0) {
                        errors.append("Number of parking spaces cannot be negative.\n");
                    }
                }
            } catch (NumberFormatException e) {
                errors.append("Invalid number of parking spaces format.\n");
            }
        }
    }

    private void validateField(TextField field, String errorMessage, StringBuilder errors) {
        if (field.getText().trim().isEmpty()) {
            errors.append(errorMessage).append("\n");
        }
    }

    private void validateComboBox(ComboBox<String> comboBox, String errorMessage, StringBuilder errors) {
        if (comboBox.getValue() == null) {
            errors.append(errorMessage).append("\n");
        }
    }

    private void validatePrice(StringBuilder errors) {
        try {
            String priceText = priceField.getText().trim();
            if (priceText.isEmpty()) {
                errors.append("Price is required.\n");
            } else {
                double price = Double.parseDouble(priceText);
                if (price <= 0) {
                    errors.append("Price must be greater than 0.\n");
                }
            }
        } catch (NumberFormatException e) {
            errors.append("Invalid price format.\n");
        }
    }

    private void validateSize(StringBuilder errors) {
        try {
            String sizeText = sizeField.getText().trim();
            if (sizeText.isEmpty()) {
                errors.append("Property size is required.\n");
            } else {
                double size = Double.parseDouble(sizeText);
                if (size <= 0) {
                    errors.append("Property size must be greater than 0.\n");
                }
            }
        } catch (NumberFormatException e) {
            errors.append("Invalid size format.\n");
        }
    }

    private void clearForm() {
        titleField.clear();
        locationField.clear();
        priceField.clear();
        featuresArea.clear();
        sizeField.clear();
        propertyTypeComboBox.setValue(null);
        statusComboBox.setValue(null);
        bedroomsField.clear();
        hasGardenCheckBox.setSelected(false);
        floorsField.clear();
        parkingField.clear();
        selectedPhotos.clear();
        photoCountLabel.setText("No photos selected");

        // Hide both type-specific field containers
        residentialFields.setVisible(false);
        residentialFields.setManaged(false);
        commercialFields.setVisible(false);
        commercialFields.setManaged(false);
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void handleBack(ActionEvent actionEvent) {
        if (hasUnsavedChanges()) {
            if (confirmDiscardChanges()) {
                navigateToPropertyListing(actionEvent);
            }
        } else {
            navigateToPropertyListing(actionEvent);
        }
    }

    private boolean hasUnsavedChanges() {
        return anyFieldModified().test(titleField) ||
                anyFieldModified().test(locationField) ||
                anyFieldModified().test(priceField) ||
                anyFieldModified().test(featuresArea) ||
                anyFieldModified().test(sizeField) ||
                propertyTypeComboBox.getValue() != null ||
                statusComboBox.getValue() != null ||
                !selectedPhotos.isEmpty() ||
                anyFieldModified().test(bedroomsField) ||
                hasGardenCheckBox.isSelected() ||
                anyFieldModified().test(floorsField) ||
                anyFieldModified().test(parkingField);
    }

    private Predicate<javafx.scene.Node> anyFieldModified() {
        return field -> field instanceof TextField && !((TextField) field).getText().trim().isEmpty() ||
                field instanceof TextArea && !((TextArea) field).getText().trim().isEmpty() ||
                field instanceof ComboBox && ((ComboBox<?>) field).getValue() != null;
    }

    private boolean confirmDiscardChanges() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Unsaved Changes");
        alert.setHeaderText(null);
        alert.setContentText("You have unsaved changes. Do you want to discard them?");
        return alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
    }

    private boolean isEditMode = false;
    private Long editPropertyId;

    public void setEditMode(Property property) {
        isEditMode = true;
        editPropertyId = property.getId();
        currentProperty = property; // Store reference to current property

        // Set basic fields
        Platform.runLater(() -> {
            propertyTypeComboBox.setValue(property.getType());
            handlePropertyTypeChange(new ActionEvent()); // Trigger field visibility

            titleField.setText(property.getTitle());
            locationField.setText(property.getLocation());
            sizeField.setText(String.valueOf(property.getSize()));
            priceField.setText(String.valueOf(property.getPrice()));
            featuresArea.setText(property.getFeatures());
            statusComboBox.setValue(property.getStatus());

            // Set type-specific fields
            if (property instanceof ResidentialProperty) {
                ResidentialProperty rp = (ResidentialProperty) property;
                bedroomsField.setText(String.valueOf(rp.getNumberOfBedrooms()));
                hasGardenCheckBox.setSelected(rp.isHasGarden());
            } else if (property instanceof CommercialProperty) {
                CommercialProperty cp = (CommercialProperty) property;
                floorsField.setText(String.valueOf(cp.getNumberOfFloors()));
                parkingField.setText(String.valueOf(cp.getParkingSpaces()));
            }

            // Update button text
            Button saveButton = (Button) titleField.getScene().lookup(".primary-button");
            if (saveButton != null) {
                saveButton.setText("Update Property");
            }
        });
    }


}