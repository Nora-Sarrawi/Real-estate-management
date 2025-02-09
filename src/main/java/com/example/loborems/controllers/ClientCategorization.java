package com.example.loborems.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;

public class ClientCategorization {

    @FXML
    private TextField textField;

    @FXML
    private Button resetFiltersButton;

    @FXML
    private ComboBox<String> activityLevelComboBox;

    @FXML
    private ComboBox<String> propertyTypeComboBox;

    @FXML
    private ComboBox<String> budgetRangeComboBox;

    @FXML
    private TableView<Client> tableView;

    @FXML
    private TableColumn<Client, String> nameColumn;

    @FXML
    private TableColumn<Client, String> activityColumn;

    @FXML
    private TableColumn<Client, String> propertyColumn;

    @FXML
    private TableColumn<Client, String> budgetColumn;

    @FXML
    private TableColumn<Client, String> interactionColumn;

    @FXML
    private Button dashboardButton;

    private ObservableList<Client> clients; // Original data
    private FilteredList<Client> filteredClients; // Filtered data

    public static class Client {
        private final String name;
        private final String activityLevel;
        private final String propertyPreferences;
        private final String budgetRange;
        private final String recentInteractions;

        public Client(String name, String activityLevel, String propertyPreferences, String budgetRange, String recentInteractions) {
            this.name = name;
            this.activityLevel = activityLevel;
            this.propertyPreferences = propertyPreferences;
            this.budgetRange = budgetRange;
            this.recentInteractions = recentInteractions;
        }

        public String getName() {
            return name;
        }

        public String getActivityLevel() {
            return activityLevel;
        }

        public String getPropertyPreferences() {
            return propertyPreferences;
        }

        public String getBudgetRange() {
            return budgetRange;
        }

        public String getRecentInteractions() {
            return recentInteractions;
        }
    }

    @FXML
    public void initialize() {
        // Initialize ComboBox options
        activityLevelComboBox.setItems(FXCollections.observableArrayList("Low", "Medium", "High"));
        propertyTypeComboBox.setItems(FXCollections.observableArrayList("Apartment", "Condo", "Villa", "Townhouse"));
        budgetRangeComboBox.setItems(FXCollections.observableArrayList("$1000-$1500", "$1500-$2000", "$2000-$2500", "$3000-$3500"));

        // Set up TableView columns
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        activityColumn.setCellValueFactory(new PropertyValueFactory<>("activityLevel"));
        propertyColumn.setCellValueFactory(new PropertyValueFactory<>("propertyPreferences"));
        budgetColumn.setCellValueFactory(new PropertyValueFactory<>("budgetRange"));
        interactionColumn.setCellValueFactory(new PropertyValueFactory<>("recentInteractions"));

        // Initialize client data
        clients = FXCollections.observableArrayList(
                new Client("mahmoud", "High", "Apartment", "$1000-$1500", "Phone Call"),
                new Client("admin", "Medium", "Condo", "$2000-$2500", "Email"),
                new Client("nosa", "Low", "Villa", "$3000-$3500", "In-Person"),
                new Client("noser", "High", "Townhouse", "$1500-$2000", "Phone Call"),
                new Client("ahmad yaseen", "Medium", "Apartment", "$1000-$1200", "Online Chat")
        );

        // Wrap the client list in a FilteredList
        filteredClients = new FilteredList<>(clients, p -> true);

        // Bind the FilteredList to the TableView
        tableView.setItems(filteredClients);

        // Add listeners to filters
        activityLevelComboBox.valueProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        propertyTypeComboBox.valueProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        budgetRangeComboBox.valueProperty().addListener((obs, oldVal, newVal) -> applyFilters());

        // Reset Filters button functionality
        resetFiltersButton.setOnAction(event -> resetFilters());

    }

    private void applyFilters() {
        filteredClients.setPredicate(client -> {
            // Check activity level filter
            String selectedActivity = activityLevelComboBox.getValue();
            if (selectedActivity != null && !selectedActivity.equals(client.getActivityLevel())) {
                return false;
            }

            // Check property type filter
            String selectedProperty = propertyTypeComboBox.getValue();
            if (selectedProperty != null && !selectedProperty.equals(client.getPropertyPreferences())) {
                return false;
            }

            // Check budget range filter
            String selectedBudget = budgetRangeComboBox.getValue();
            if (selectedBudget != null && !selectedBudget.equals(client.getBudgetRange())) {
                return false;
            }

            // All filters passed
            return true;
        });
    }

    private void resetFilters() {
        // Clear ComboBox selections
        activityLevelComboBox.getSelectionModel().clearSelection();
        propertyTypeComboBox.getSelectionModel().clearSelection();
        budgetRangeComboBox.getSelectionModel().clearSelection();

        // Reset the filter predicate to show all items
        filteredClients.setPredicate(p -> true);
    }

    public void goToDashboard(ActionEvent event) throws IOException {
        Parent secondRoot = FXMLLoader.load(getClass().getResource("/com/example/loborems/Dashboard/dashboard.fxml"));
        Scene newScene = new Scene(secondRoot);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(newScene);
    }
}
