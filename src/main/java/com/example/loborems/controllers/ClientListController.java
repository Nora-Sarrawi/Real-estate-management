package com.example.loborems.controllers;

import com.example.loborems.interfaces.ClientListDAO;
import com.example.loborems.services.ClientListDAOImp;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Callback;
import java.io.IOException;
import com.example.loborems.models.Client;
import org.hibernate.exception.ConstraintViolationException;
import com.example.loborems.models.User;
import com.example.loborems.services.UserDOAimp;
import javafx.scene.control.Alert;

public class ClientListController {

    @FXML
    private TableView<Client> clientTable;
    @FXML
    private TableColumn<Client, String> idColumn;
    @FXML
    private TableColumn<Client, String> nameColumn;
    @FXML
    private TableColumn<Client, String> emailColumn;
    @FXML
    private TableColumn<Client, String> phoneColumn;
    @FXML
    private TableColumn<Client, String> propertyColumn;
    @FXML
    private TableColumn<Client, String> roleColumn;
    @FXML
    private TableColumn<Client, Void> editColumn;
    @FXML
    private TableColumn<Client, Void> removeColumn;
    @FXML
    private TextField nameField, emailField, phoneField, propertyField, roleField, searchField;
    @FXML
    private Button addClientButton;
    @FXML
    private Button backButton;

    private final ObservableList<Client> clientList = FXCollections.observableArrayList();
    private final ClientListDAO clientDAO = new ClientListDAOImp();
    private Client currentlyEditingClient = null;

    @FXML
    public void initialize() {
        try {
            loadAllClients();
            bindTableColumns();
            addEditButtonToTable();
            addDeleteButtonToTable();
            setupSearchField();
        } catch (Exception e) {
            showAlert("Initialization Error", "Failed to initialize the view: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private User currentUser;
    private final UserDOAimp userService = new UserDOAimp();

    public void setCurrentUser(User user) {
        this.currentUser = user;
        Platform.runLater(() -> updateUIBasedOnPermissions());
    }

    private void updateUIBasedOnPermissions() {
        if (currentUser != null && currentUser.getRole() != null) {
            int roleId = currentUser.getRole().getId();
            System.out.println("Current user role in ClientList: " + roleId);

            // Both Role 1 and 2 can see the Add button
            addClientButton.setVisible(true);
            addClientButton.setManaged(true);

            // Only Role 2 can see edit/delete columns
            boolean hasFullAccess = roleId == 2;
            editColumn.setVisible(hasFullAccess);
            removeColumn.setVisible(hasFullAccess);
        } else {
            // No user or no role - hide all controls
            addClientButton.setVisible(false);
            editColumn.setVisible(false);
            removeColumn.setVisible(false);
        }
    }


    private void loadAllClients() {
        try {
            clientList.clear();
            clientList.addAll(clientDAO.getAllClients());
            clientTable.setItems(clientList);
        } catch (Exception e) {
            showAlert("Loading Error", "Failed to load clients: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void bindTableColumns() {
        idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty().asString());
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        emailColumn.setCellValueFactory(cellData -> cellData.getValue().emailProperty());
        phoneColumn.setCellValueFactory(cellData -> cellData.getValue().phoneProperty());
        propertyColumn.setCellValueFactory(cellData -> cellData.getValue().propertyProperty());
        roleColumn.setCellValueFactory(cellData -> cellData.getValue().roleProperty());
    }

    private void setupSearchField() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                if (newValue == null || newValue.isEmpty()) {
                    loadAllClients();
                } else {
                    clientList.clear();
                    clientList.addAll(clientDAO.searchClientsByName(newValue));
                }
            } catch (Exception e) {
                showAlert("Search Error", "Error searching clients: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        });
    }

    @FXML
    private void onAddClientButtonClicked() {
        if (currentUser == null || currentUser.getRole() == null) {
            showAlert("Access Denied", "You must be logged in to perform this action.", Alert.AlertType.WARNING);
            return;
        }

        // If we're in edit mode and the user doesn't have full access, prevent the edit
        if (currentlyEditingClient != null && !checkFullAccess()) {
            showAlert("Access Denied", "Only users with Role 2 can edit clients.", Alert.AlertType.WARNING);
            return;
        }

        try {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();
            String property = propertyField.getText().trim();
            String role = roleField.getText().trim();

            if (validateFields(name, email, phone, role)) {
                if (currentlyEditingClient == null) {
                    // Add new client - both roles can do this
                    Client newClient = new Client(name, email, phone, property, role);
                    clientDAO.addClient(newClient);
                    clientList.add(newClient);
                    showAlert("Success", "Client added successfully!", Alert.AlertType.INFORMATION);
                } else {
                    // Edit existing client - only Role 2 can do this
                    if (checkFullAccess()) {
                        currentlyEditingClient.setName(name);
                        currentlyEditingClient.setEmail(email);
                        currentlyEditingClient.setPhone(phone);
                        currentlyEditingClient.setProperty(property);
                        currentlyEditingClient.setRole(role);

                        clientDAO.updateClient(currentlyEditingClient);
                        clientTable.refresh();
                        showAlert("Success", "Client updated successfully!", Alert.AlertType.INFORMATION);
                    }
                }
                clearFields();
                loadAllClients();
            }
        } catch (ConstraintViolationException e) {
            showAlert("Database Error", "This client information conflicts with existing data.", Alert.AlertType.ERROR);
        } catch (Exception e) {
            showAlert("Error", "Failed to save client: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void addEditButtonToTable() {
        Callback<TableColumn<Client, Void>, TableCell<Client, Void>> cellFactory = param -> new TableCell<>() {
            private final Button editButton = new Button("Edit");

            {
                editButton.setStyle("-fx-background-color: #14274d; -fx-text-fill: white;");
                editButton.setOnAction(event -> {
                    if (checkFullAccess()) {
                        currentlyEditingClient = getTableView().getItems().get(getIndex());
                        populateFieldsForEdit(currentlyEditingClient);
                    } else {
                        showAlert("Access Denied", "Only users with Role 2 can edit clients.", Alert.AlertType.WARNING);
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : editButton);
            }
        };
        editColumn.setCellFactory(cellFactory);
    }

    private void addDeleteButtonToTable() {
        Callback<TableColumn<Client, Void>, TableCell<Client, Void>> cellFactory = param -> new TableCell<>() {
            private final Button deleteButton = new Button("Delete");

            {
                deleteButton.setStyle("-fx-background-color: #14274d; -fx-text-fill: white;");
                deleteButton.setOnAction(event -> {
                    if (checkFullAccess()) {
                        Client clientToDelete = getTableView().getItems().get(getIndex());
                        handleDeleteClient(clientToDelete);
                    } else {
                        showAlert("Access Denied", "Only users with Role 2 can delete clients.", Alert.AlertType.WARNING);
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : deleteButton);
            }
        };
        removeColumn.setCellFactory(cellFactory);
    }

    private void handleDeleteClient(Client client) {
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Confirm Delete");
        confirmDialog.setContentText("Are you sure you want to delete this client?");

        confirmDialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    clientDAO.deleteClient(client.getId());
                    clientList.remove(client);
                    showAlert("Success", "Client deleted successfully!", Alert.AlertType.INFORMATION);
                } catch (Exception e) {
                    showAlert("Error", "Failed to delete client: " + e.getMessage(), Alert.AlertType.ERROR);
                }
            }
        });
    }

    private void populateFieldsForEdit(Client client) {
        nameField.setText(client.getName());
        emailField.setText(client.getEmail());
        phoneField.setText(client.getPhone());
        propertyField.setText(client.getProperty());
        roleField.setText(client.getRole());
        addClientButton.setText("Update Client");
    }

    private void clearFields() {
        nameField.clear();
        emailField.clear();
        phoneField.clear();
        propertyField.clear();
        roleField.clear();
        addClientButton.setText("Add Client");
        currentlyEditingClient = null;
    }

    private boolean validateFields(String name, String email, String phone, String role) {
        StringBuilder errorMessage = new StringBuilder();

        if (name.isEmpty()) {
            errorMessage.append("Name is required.\n");
        }
        if (email.isEmpty()) {
            errorMessage.append("Email is required.\n");
        }
        if (phone.isEmpty()) {
            errorMessage.append("Phone is required.\n");
        }
        if (role.isEmpty()) {
            errorMessage.append("Role is required.\n");
        }

        if (!email.isEmpty() && !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            errorMessage.append("Invalid email format.\n");
        }

        if (!phone.isEmpty() && !phone.matches("^[0-9+()-]{8,}$")) {
            errorMessage.append("Invalid phone format.\n");
        }

        if (errorMessage.length() > 0) {
            showAlert("Validation Error", errorMessage.toString(), Alert.AlertType.ERROR);
            return false;
        }
        return true;
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void onBackButtonClicked() {
        try {
            Stage stage = (Stage) backButton.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/loborems/Dashboard/dashboard.fxml"));

            // Load the FXML first
            Parent root = loader.load();

            // Then get the controller and set the user
            DashboardController dashboardController = loader.getController();
            dashboardController.setCurrentUser(currentUser);

            // Create and set the scene
            Scene scene = new Scene(root);
            stage.setScene(scene);
        } catch (IOException e) {
            showAlert("Navigation Error", "Failed to load Dashboard: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    public void refreshTable() {
        loadAllClients();
    }

    private boolean checkFullAccess() {
        if (currentUser == null || currentUser.getRole() == null) {
            return false;
        }
        return currentUser.getRole().getId() == 2;
    }
}