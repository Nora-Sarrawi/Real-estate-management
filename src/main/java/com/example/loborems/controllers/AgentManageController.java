package com.example.loborems.controllers;

import com.example.loborems.models.Role;
import com.example.loborems.models.User;
import com.example.loborems.services.UserDOAimp;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Logger;

public class AgentManageController implements Initializable {
    private static final Logger logger = Logger.getLogger(AgentManageController.class.getName());

    private static User loggedInUser; //store logged in user

    @FXML private TableView<User> agentTable;
    @FXML private TableColumn<User, Integer> idColumn;
    @FXML private TableColumn<User, String> nameColumn;
    @FXML private TableColumn<User, String> emailColumn;
    @FXML private TableColumn<User, String> roleColumn;
    @FXML private TableColumn<User, String> statusColumn;

    @FXML private JFXTextField searchField;
    @FXML private JFXTextField full_name;
    @FXML private JFXTextField emailField;
    @FXML private JFXTextField passwordField;
    @FXML private JFXComboBox<Integer> roleChoiceBox;
    @FXML private JFXButton editButton;
    @FXML private JFXButton deleteButton;
    @FXML private Stage stage;
    @FXML private Scene scene;

    private final UserDOAimp userDAO;
    private ObservableList<User> agentList;
    private User selectedUser;
    private final Map<Integer, String> roleMap;

    public AgentManageController() {
        this.userDAO = new UserDOAimp();
        this.roleMap = new HashMap<>();
        roleMap.put(1, "Admin");
        roleMap.put(2, "Agent");
    }

    //  set logged in user
    public static void setLoggedInUser(User user) {
        loggedInUser = user;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        // Check permission

        if (!isAdminUser()) {
            disableAllControls();
            showAlert(Alert.AlertType.ERROR, "Access Denied",
                    "Only administrators can access this page.");

            try {
                handleBackClick(new ActionEvent());
            } catch (IOException e) {
                logger.severe("Error redirecting to dashboard: " + e.getMessage());
                e.printStackTrace();
            }
            return;
        }

        setupTableColumns();
        setupRoleChoiceBox();
        loadAgents();
        setupSearchField();
        setupTableSelection();
        setupButtons();
    }

    private boolean isAdminUser() {
        return loggedInUser != null &&
                loggedInUser.getRole() != null &&
                loggedInUser.getRole().getId() == 1;
    }

    private void disableAllControls() {
        agentTable.setDisable(true);
        searchField.setDisable(true);
        full_name.setDisable(true);
        emailField.setDisable(true);
        passwordField.setDisable(true);
        roleChoiceBox.setDisable(true);
        editButton.setDisable(true);
        deleteButton.setDisable(true);
    }

    private void setupTableColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        roleColumn.setCellValueFactory(cellData -> {
            if (cellData.getValue().getRole() != null) {
                int roleId = cellData.getValue().getRole().getId();
                return javafx.beans.binding.Bindings.createStringBinding(
                        () -> roleMap.getOrDefault(roleId, "Unknown")
                );
            }
            return javafx.beans.binding.Bindings.createStringBinding(() -> "No Role");
        });
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
    }

    private void setupRoleChoiceBox() {
        roleChoiceBox.setItems(FXCollections.observableArrayList(1, 2));
        roleChoiceBox.setConverter(new StringConverter<Integer>() {
            @Override
            public String toString(Integer roleId) {
                return roleId == null ? null : roleMap.getOrDefault(roleId, "Unknown");
            }

            @Override
            public Integer fromString(String string) {
                return null;
            }
        });
    }

    private void loadAgents() {
        agentList = FXCollections.observableArrayList(userDAO.getAll());
        agentTable.setItems(agentList);
    }

    private void setupSearchField() {
        FilteredList<User> filteredData = new FilteredList<>(agentList, p -> true);
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(user -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();
                String roleName = user.getRole() != null ?
                        roleMap.getOrDefault(user.getRole().getId(), "Unknown").toLowerCase() : "";

                return user.getFullName().toLowerCase().contains(lowerCaseFilter) ||
                        user.getEmail().toLowerCase().contains(lowerCaseFilter) ||
                        roleName.contains(lowerCaseFilter);
            });
        });
        agentTable.setItems(filteredData);
    }

    private void setupTableSelection() {
        agentTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedUser = newSelection;
                populateFields(selectedUser);
            }
        });
    }

    private void setupButtons() {
        editButton.setOnAction(e -> handleEdit());
        deleteButton.setOnAction(e -> handleDelete());
    }

    private void populateFields(User user) {
        full_name.setText(user.getFullName());
        emailField.setText(user.getEmail());
        passwordField.setText(""); // For security reasons, I don't populate password
        if (user.getRole() != null) {
            roleChoiceBox.setValue(user.getRole().getId());
        }
    }

    private void handleEdit() {
        // permission check
        if (!isAdminUser()) {
            showAlert(Alert.AlertType.ERROR, "Access Denied",
                    "Only administrators can edit agent information.");
            return;
        }

        if (selectedUser == null) {
            showAlert(Alert.AlertType.WARNING, "Selection Required",
                    "Please select an agent to edit");
            return;
        }

        try {
            selectedUser.setFullName(full_name.getText());
            selectedUser.setEmail(emailField.getText());
            if (!passwordField.getText().isEmpty()) {
                selectedUser.setPassword(passwordField.getText());
            }

            if (roleChoiceBox.getValue() != null) {
                Role role = new Role();
                role.setId(roleChoiceBox.getValue());
                role.setName(roleMap.get(roleChoiceBox.getValue()));
                selectedUser.setRole(role);
            }

            userDAO.update(selectedUser);
            loadAgents();
            clearFields();
            showAlert(Alert.AlertType.INFORMATION, "Success",
                    "Agent updated successfully");
        } catch (Exception e) {
            logger.severe("Error during edit: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error",
                    "Error updating agent: " + e.getMessage());
        }
    }

    private void handleDelete() {
        // Verify admin permission
        if (!isAdminUser()) {
            showAlert(Alert.AlertType.ERROR, "Access Denied",
                    "Only administrators can delete agents.");
            return;
        }

        if (selectedUser == null) {
            showAlert(Alert.AlertType.WARNING, "Selection Required",
                    "Please select an agent to delete");
            return;
        }

        try {
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Confirm Delete");
            confirmAlert.setHeaderText(null);
            confirmAlert.setContentText("Are you sure you want to delete this agent?");

            Optional<ButtonType> result = confirmAlert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                userDAO.delete(selectedUser);
                loadAgents();
                clearFields();
                showAlert(Alert.AlertType.INFORMATION, "Success",
                        "Agent deleted successfully");
            }
        } catch (Exception e) {
            logger.severe("Error during delete: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error",
                    "Error deleting agent: " + e.getMessage());
        }
    }

    private void clearFields() {
        full_name.clear();
        emailField.clear();
        passwordField.clear();
        roleChoiceBox.setValue(null);
        selectedUser = null;
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showAlert(Alert.AlertType alertType, String message) {
        showAlert(alertType, alertType.toString(), message);
    }

    @FXML
    public void handleBackClick(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/com/example/loborems/Dashboard/dashboard.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

}