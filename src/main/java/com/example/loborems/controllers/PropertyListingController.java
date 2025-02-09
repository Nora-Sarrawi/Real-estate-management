package com.example.loborems.controllers;

import com.example.loborems.models.Property;
import com.example.loborems.models.ResidentialProperty;
import com.example.loborems.models.User;
import com.example.loborems.services.PropertyDAOImpl;
import com.example.loborems.services.PropertyService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class PropertyListingController implements javafx.fxml.Initializable {
    private final PropertyService propertyService = new PropertyService();

    @FXML
    public Button backButton;

    @FXML
    private FlowPane propertyListContainer;
    @FXML
    private Button addPropertyButton;
    @FXML
    private Button filterButton;
    @FXML
    private ComboBox<String> propertyTypeComboBox;
    @FXML
    private TextField locationField;
    @FXML
    private TextField minPriceField;
    @FXML
    private TextField maxPriceField;
    @FXML
    private CheckBox hasGardenCheckBox;

    @FXML
    private Stage stage;
    @FXML
    private Scene scene;
    @FXML
    private Button searchButton;
    @FXML
    private TextField searchBar;
    @FXML
    private VBox sidebar; // The sidebar VBox

    private List<Property> properties;
    private User currentUser;

    public void setCurrentUser(User user) {
        this.currentUser = user;
        System.out.println("PropertyListingController - Current User Role: " +
                (user != null && user.getRole() != null ? user.getRole().getId() : "null"));

        if (propertyListContainer != null) {
            refreshPropertyList();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("Initializing property listing...");

        // Fetch all properties
        properties = propertyService.getAllProperties();
        if (properties != null && !properties.isEmpty()) {
            System.out.println("Found " + properties.size() + " properties.");

            // Add each property card
            for (Property property : properties) {
                addPropertyItem(property);
            }
        } else {
            System.out.println("No properties found.");
        }
    }

    private void addPropertyItem(Property property) {
        try {
            // Load the property item FXML
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/loborems/PropertyListing/property-item.fxml")
            );
            Node propertyCard = loader.load();

            PropertyItemController itemController = loader.getController();

            itemController.setCurrentUser(currentUser);
            itemController.setPropertyDetails(property);
            System.out.println("PropertyListingController addPropertyItem - Passing User Role: " +
                    (currentUser != null && currentUser.getRole() != null ? currentUser.getRole().getId() : "null"));


            propertyListContainer.getChildren().add(propertyCard);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Handle click to navigate to Add Property Page
    @FXML
    public void handleButtonClick(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/loborems/AddProperty/add-property.fxml"));
            Parent root = loader.load();

            AddPropertyController controller = loader.getController();
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root, 900, 750);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Handle Back Button click to return to Dashboard
    @FXML
    public void handleBackClick(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/com/example/loborems/Dashboard/dashboard.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void handleSearchClick(ActionEvent actionEvent) {
        // Get the search text entered by the user
        String searchText = searchBar.getText().trim().toLowerCase();

        // If the search bar is empty, display all properties
        if (searchText.isEmpty()) {
            // If no search text, display all properties again
            propertyListContainer.getChildren().clear();
            for (Property property : properties) {
                addPropertyItem(property);
            }
            return;
        }

        // Filter properties by title
        List<Property> filteredProperties = new ArrayList<>();
        for (Property property : properties) {
            if (property.getTitle().toLowerCase().contains(searchText)) {
                filteredProperties.add(property);
            }
        }

        // Clear the previous properties and show filtered ones
        propertyListContainer.getChildren().clear();
        if (filteredProperties.isEmpty()) {
            // If no results found, show a message or some placeholder
            showAlert("No Results", "No properties found matching the search criteria.");
        } else {
            // Display the filtered properties
            for (Property p : filteredProperties) {
                addPropertyItem(p);
            }
        }
    }


    @FXML
    private void handleFilterApply() {
        String location = locationField.getText().trim().toLowerCase();
        String minPriceText = minPriceField.getText().trim();
        String maxPriceText = maxPriceField.getText().trim();
        boolean hasGarden = hasGardenCheckBox.isSelected();
        String propertyType = propertyTypeComboBox.getValue();

        PropertyDAOImpl propertyDAO = new PropertyDAOImpl();

        // Get all properties from DAO
        List<Property> filteredProperties = new ArrayList<>(propertyDAO.getAllProperties());

        try {
            // Apply location filter
            if (!location.isEmpty()) {
                filteredProperties.removeIf(p -> !p.getLocation().toLowerCase().contains(location));
            }

            // Apply minimum price filter
            if (!minPriceText.isEmpty()) {
                double minPrice = Double.parseDouble(minPriceText);
                filteredProperties.removeIf(p -> p.getPrice() < minPrice);
            }

            // Apply maximum price filter
            if (!maxPriceText.isEmpty()) {
                double maxPrice = Double.parseDouble(maxPriceText);
                filteredProperties.removeIf(p -> p.getPrice() > maxPrice);
            }

            // Apply property type filter
            if (propertyType != null && !propertyType.isEmpty()) {
                filteredProperties.removeIf(p -> !p.getType().equalsIgnoreCase(propertyType));
            }

            // Apply hasGarden filter for residential properties
            if (hasGarden) {
                filteredProperties.removeIf(p -> !(p instanceof ResidentialProperty) || !((ResidentialProperty) p).isHasGarden());
            }
        } catch (NumberFormatException e) {
            showAlert("Invalid Input", "Please enter valid numbers for price filters.");
            return;
        }

        // Clear the previous properties and show filtered ones
        propertyListContainer.getChildren().clear();
        for (Property p : filteredProperties) {
            addPropertyItem(p);
        }
    }




    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void handleToggleSidebar(ActionEvent actionEvent) {
        sidebar.setVisible(!sidebar.isVisible());
    }

    private void refreshPropertyList() {
        propertyListContainer.getChildren().clear();
        properties = propertyService.getAllProperties();
        if (properties != null && !properties.isEmpty()) {
            for (Property property : properties) {
                addPropertyItem(property);
            }
        }
    }
}