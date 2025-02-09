package com.example.loborems.controllers;

import com.example.loborems.interfaces.PropertyDAO;
import com.example.loborems.models.Property;
import com.example.loborems.models.ResidentialProperty;
import com.example.loborems.models.CommercialProperty;
import com.example.loborems.services.PropertyDAOImpl;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.scene.text.Text;
import javafx.concurrent.Task;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import com.example.loborems.models.User;
import com.example.loborems.services.UserDOAimp;
import javafx.scene.control.Alert;

public class PropertyDetailsController {

    @FXML
    private Text titleLabel;
    @FXML
    private Label locationLabel;
    @FXML
    private Label sizeLabel;
    @FXML
    private Label priceLabel;
    @FXML
    private Label typeLabel;
    @FXML
    private Label statusLabel;
    @FXML
    private Label featuresLabel;

    // Residential specific labels
    @FXML
    private Label bedroomsLabel;
    @FXML
    private Label bedroomsTitleLabel;
    @FXML
    private Label gardenLabel;
    @FXML
    private Label gardenTitleLabel;

    // Commercial specific labels
    @FXML
    private Label floorsLabel;
    @FXML
    private Label floorsTitleLabel;
    @FXML
    private Label parkingLabel;
    @FXML
    private Label parkingTitleLabel;

    @FXML
    private ImageView mainImageView;
    @FXML
    private ImageView thumbImage1;
    @FXML
    private ImageView thumbImage2;
    @FXML
    private ImageView thumbImage3;
    @FXML
    private ImageView thumbImage4;
    @FXML
    private Button backButton;
    @FXML
    private Button editButton;
    @FXML
    private Button deleteButton;

    private Property currentProperty;
    private final ExecutorService executorService = Executors.newFixedThreadPool(2);
    private Image[] loadedImages;



    public void initialize() {
        // Hide all property-specific labels by default
        hideAllPropertySpecificLabels();
    }

    private User currentUser;
    private final UserDOAimp userService = new UserDOAimp();

    public void setCurrentUser(User user) {
        this.currentUser = user;
        System.out.println("PropertyDetailsController - Received User Role: " +
                (user != null && user.getRole() != null ? user.getRole().getId() : "null"));

        Platform.runLater(() -> updateUIBasedOnPermissions());
    }

    private void updateUIBasedOnPermissions() {
        boolean hasFullAccess = false;
        if (currentUser != null && currentUser.getRole() != null) {
            int roleId = currentUser.getRole().getId();
            hasFullAccess = roleId == 2;
            System.out.println("PropertyDetailsController updateUIBasedOnPermissions - User Role: " + roleId +
                    ", HasFullAccess: " + hasFullAccess);
        } else {
            System.out.println("PropertyDetailsController updateUIBasedOnPermissions - User or Role is null");
        }

        editButton.setVisible(hasFullAccess);
        editButton.setManaged(hasFullAccess);
        deleteButton.setVisible(hasFullAccess);
        deleteButton.setManaged(hasFullAccess);
    }


    private void hideAllPropertySpecificLabels() {
        // Hide residential labels
        bedroomsTitleLabel.setVisible(false);
        bedroomsLabel.setVisible(false);
        gardenTitleLabel.setVisible(false);
        gardenLabel.setVisible(false);

        // Hide commercial labels
        floorsTitleLabel.setVisible(false);
        floorsLabel.setVisible(false);
        parkingTitleLabel.setVisible(false);
        parkingLabel.setVisible(false);
    }

    public void setProperty(Property property) {
        this.currentProperty = property;
        populateData();
        loadImagesAsync();
    }

    private void populateData() {
        // Populate common fields
        titleLabel.setText(currentProperty.getTitle());
        locationLabel.setText(currentProperty.getLocation());
        sizeLabel.setText(String.format("%.2f sqm", currentProperty.getSize()));
        priceLabel.setText(String.format("$%.2f", currentProperty.getPrice()));
        typeLabel.setText(currentProperty.getType());
        statusLabel.setText(currentProperty.getStatus());
        featuresLabel.setText(currentProperty.getFeatures());

        // Handle property-specific fields
        if (currentProperty instanceof ResidentialProperty) {
            populateResidentialData((ResidentialProperty) currentProperty);
        } else if (currentProperty instanceof CommercialProperty) {
            populateCommercialData((CommercialProperty) currentProperty);
        }
    }

    private void populateResidentialData(ResidentialProperty property) {
        // Handle bedrooms
        if (property.getNumberOfBedrooms() > 0) {
            bedroomsTitleLabel.setVisible(true);
            bedroomsLabel.setVisible(true);
            bedroomsLabel.setText(String.format("%d Bedrooms", property.getNumberOfBedrooms()));
        }

        // Handle garden
        gardenTitleLabel.setVisible(true);
        gardenLabel.setVisible(true);
        gardenLabel.setText(property.isHasGarden() ? "Yes" : "No");
    }

    private void populateCommercialData(CommercialProperty property) {
        // Handle floors
        if (property.getNumberOfFloors() > 0) {
            floorsTitleLabel.setVisible(true);
            floorsLabel.setVisible(true);
            floorsLabel.setText(String.format("%d Floors", property.getNumberOfFloors()));
        }

        // Handle parking spaces
        if (property.getParkingSpaces() > 0) {
            parkingTitleLabel.setVisible(true);
            parkingLabel.setVisible(true);
            parkingLabel.setText(String.format("%d Parking Spaces", property.getParkingSpaces()));
        }
    }

    private void loadImagesAsync() {
        if (currentProperty.getImages() == null || currentProperty.getImages().isEmpty()) {
            return;
        }

        Task<Image[]> imageLoadTask = new Task<>() {
            @Override
            protected Image[] call() {
                String[] imageStrings = currentProperty.getImages().split(",");
                Image[] images = new Image[imageStrings.length];

                for (int i = 0; i < imageStrings.length; i++) {
                    if (isCancelled()) {
                        return null;
                    }
                    images[i] = decodeBase64Image(imageStrings[i]);
                    final int index = i;
                    Platform.runLater(() -> updateImageView(index, images[index]));
                }
                return images;
            }
        };

        imageLoadTask.setOnSucceeded(event -> {
            loadedImages = imageLoadTask.getValue();
            if (loadedImages != null && loadedImages.length > 0) {
                setupImageClickHandlers();
            }
        });

        executorService.submit(imageLoadTask);
    }

    private void updateImageView(int index, Image image) {
        if (image == null) {
            return;
        }

        if (index == 0) {
            mainImageView.setImage(image);
        }

        ImageView[] thumbnails = {thumbImage1, thumbImage2, thumbImage3, thumbImage4};
        if (index < thumbnails.length) {
            thumbnails[index].setImage(image);
        }
    }

    private void setupImageClickHandlers() {
        ImageView[] thumbnails = {thumbImage1, thumbImage2, thumbImage3, thumbImage4};
        for (int i = 0; i < thumbnails.length; i++) {
            if (i < loadedImages.length) {
                final int index = i;
                thumbnails[i].setOnMouseClicked(e -> updateMainImage(loadedImages[index]));
            }
        }
    }

    private void updateMainImage(Image newImage) {
        if (newImage != null && !newImage.equals(mainImageView.getImage())) {
            mainImageView.setImage(newImage);
        }
    }

    private Image decodeBase64Image(String base64) {
        try {
            byte[] imageData = Base64.getDecoder().decode(base64.trim());
            return new Image(new ByteArrayInputStream(imageData));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @FXML
    private void onBackButtonClicked() {
        try {
            // Load the property listing FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/loborems/PropertyListing/property-listing.fxml"));
            Parent root = loader.load();

            // Get the controller and set the current user
            PropertyListingController propertyListingController = loader.getController();
            propertyListingController.setCurrentUser(currentUser);

            // Get the stage and set the new scene
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to return to property listing page: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void onEditButtonClicked() {
        if (!checkFullAccess()) {
            showAlert("Access Denied", "Only users with Role 2 can edit properties.", Alert.AlertType.WARNING);
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/loborems/AddProperty/add-property.fxml"));
            Parent root = loader.load();
            AddPropertyController controller = loader.getController();
            controller.setEditMode(currentProperty); // New method
            Stage stage = (Stage) editButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void cleanup() {
        executorService.shutdown();
    }

    @FXML
    private void oneDleteButtonClicked() {
        if (!checkFullAccess()) {
            showAlert("Access Denied", "Only users with Role 2 can delete properties.", Alert.AlertType.WARNING);
            return;
        }

        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Confirm Delete");
        confirmDialog.setContentText("Are you sure you want to delete this property?");

        confirmDialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    PropertyDAO propertyDAO = new PropertyDAOImpl();
                    propertyDAO.delete(currentProperty);

                    Stage stage = (Stage) deleteButton.getScene().getWindow();
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/loborems/PropertyListing/property-listing.fxml"));
                    Scene newScene = new Scene(loader.load());

                    // إضافة السطرين الجديدين
                    PropertyListingController propertyListingController = loader.getController();
                    propertyListingController.setCurrentUser(currentUser);

                    stage.setScene(newScene);
                } catch (Exception e) {
                    e.printStackTrace();
                    showAlert("Error", "Failed to delete property: " + e.getMessage(), Alert.AlertType.ERROR);
                }
            }
        });
    }

    private boolean checkFullAccess() {
        if (currentUser == null || currentUser.getRole() == null) {
            return false;
        }
        return currentUser.getRole().getId() == 2;
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}