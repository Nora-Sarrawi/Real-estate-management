package com.example.loborems.controllers;

import com.example.loborems.models.Role;
import com.example.loborems.models.User;
import com.example.loborems.services.RoleDOAimp;
import com.example.loborems.services.UserDOAimp;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.util.logging.Logger;

public class addAgentController {
    private static final Logger logger = Logger.getLogger(addAgentController.class.getName());
    private final UserDOAimp userDOAimp = new UserDOAimp();
    private final RoleDOAimp roleDOAimp = new RoleDOAimp();
    private static User loggedInUser;

    @FXML
    private JFXTextField emailField;

    @FXML
    private JFXTextField full_name;

    @FXML
    private JFXTextField passwordField;

    @FXML
    private JFXButton addButton1;

    @FXML
    private JFXCheckBox acceptTerms;

    @FXML
    private JFXComboBox<String> roleChoiceBox;

    @FXML
    private Stage stage;

    @FXML
    private Scene scene;

// set logged in user

    public static void setLoggedInUser(User user) {
        loggedInUser = user;
    }


    @FXML
    public void initialize() {

    //permission check
        if (loggedInUser == null || loggedInUser.getRole() == null || loggedInUser.getRole().getId() != 1) {
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


        roleChoiceBox.setItems(FXCollections.observableArrayList("admin", "agent"));



        full_name.textProperty().addListener((observable, oldValue, newValue) -> validateFields());
        emailField.textProperty().addListener((observable, oldValue, newValue) -> validateFields());
        passwordField.textProperty().addListener((observable, oldValue, newValue) -> validateFields());
        acceptTerms.selectedProperty().addListener((observable, oldValue, newValue) -> validateFields());
        roleChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> validateFields());
    }

    private void disableAllControls() {
        emailField.setDisable(true);
        full_name.setDisable(true);
        passwordField.setDisable(true);
        addButton1.setDisable(true);
        acceptTerms.setDisable(true);
        roleChoiceBox.setDisable(true);
    }

    @FXML
    public void event(ActionEvent event) {

        if (loggedInUser == null || loggedInUser.getRole() == null || loggedInUser.getRole().getId() != 1) {
            showAlert(Alert.AlertType.ERROR, "Access Denied",
                    "Only administrators can add new agents. Please log in with an admin account.");
            try {
                handleBackClick(event);
            } catch (IOException e) {
                logger.severe("Error redirecting to dashboard: " + e.getMessage());
                e.printStackTrace();
            }
            return;
        }

        if (!validateFields()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error",
                    "Please fill all required fields, select a role, and accept the terms.");
            return;
        }

        try {

            if (userDOAimp.getByEmail(emailField.getText().trim()) != null) {
                showAlert(Alert.AlertType.ERROR, "Registration Error", "Email already exists");
                return;
            }


            User newUser = new User();
            newUser.setFullName(full_name.getText());
            newUser.setEmail(emailField.getText().trim());

            // Hash the password before saving

            String hashedPassword = hashPassword(passwordField.getText().trim());
            newUser.setPassword(hashedPassword);

            // save the role
            String selectedRole = roleChoiceBox.getValue();
            Role role = roleDOAimp.findByName(selectedRole);

            if (role == null) {
                showAlert(Alert.AlertType.ERROR, "Role Error", "Selected role does not exist");
                return;
            }


            if (role.getId() == 1 && loggedInUser.getRole().getId() != 1) {
                showAlert(Alert.AlertType.ERROR, "Access Denied",
                        "You do not have permission to create admin users.");
                return;
            }

            newUser.setRole(role);

            // Save the user
            userDOAimp.save(newUser);

            showAlert(Alert.AlertType.INFORMATION, "Success", "Agent account created successfully!");
            clearFields();

        } catch (Exception e) {
            logger.severe("Error creating new user: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Registration Error",
                    "An error occurred while creating the account: " + e.getMessage());
            e.printStackTrace();
        }
    }
        //BCrypt hashing

    private String hashPassword(String plainTextPassword) {
        return BCrypt.hashpw(plainTextPassword, BCrypt.gensalt(12));
    }

    private boolean validateFields() {
        addButton1.setDisable(true);

        if (isFieldEmpty(emailField) ||
                isFieldEmpty(passwordField) ||
                isFieldEmpty(full_name)) {
            return false;
        }

        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        if (!emailField.getText().trim().matches(emailRegex)) {
            return false;
        }

        String password = passwordField.getText().trim();
        if (!isPasswordValid(password)) {
            return false;
        }

        if (roleChoiceBox.getValue() == null || roleChoiceBox.getValue().trim().isEmpty()) {
            return false;
        }

        if (!acceptTerms.isSelected()) {
            return false;
        }

        addButton1.setDisable(false);
        return true;
    }

    private boolean isPasswordValid(String password) {
        if (password.length() < 8) return false;
        if (!password.matches(".*[a-z].*")) return false;
        if (!password.matches(".*[A-Z].*")) return false;
        if (!password.matches(".*[0-9].*")) return false;
        if (!password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*")) return false;
        return true;
    }

    private boolean isFieldEmpty(JFXTextField field) {
        return field.getText() == null || field.getText().trim().isEmpty();
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void clearFields() {
        emailField.clear();
        passwordField.clear();
        full_name.clear();
        acceptTerms.setSelected(false);
        roleChoiceBox.getSelectionModel().clearSelection();
    }

    public void handleBackClick(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/com/example/loborems/Dashboard/dashboard.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}