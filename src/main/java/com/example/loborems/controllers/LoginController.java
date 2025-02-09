package com.example.loborems.controllers;

import com.example.loborems.models.User;
import com.example.loborems.services.UserDOAimp;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;

public class LoginController {
    private final UserDOAimp userDOAimp = new UserDOAimp();

    @FXML
    private TextField emailField;

    @FXML
    private TextField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Hyperlink forgotPasswordLink;

    @FXML
    public void initialize() {
        // Add event handler for loginButton click
        loginButton.setOnAction(e -> handleLogin());
    }

    @FXML
    private void handleForgotPassword() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/loborems/ForgotPassword/forgot-password.fxml"));
            Parent forgotPasswordRoot = loader.load();
            Scene currentScene = forgotPasswordLink.getScene();
            currentScene.setRoot(forgotPasswordRoot);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Unable to load the forgot password page.");
        }
    }

    private void handleLogin() {
        // Fetch input values
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();

        // Validate input
        if (email.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please fill out all required fields.");
            return;
        }

        try {
            // Get user from database to check both credentials and role
            User user = validateLoginAndGetUser(email, password);

            if (user != null) {
                // Set the logged-in user in AgentManageController
                AgentManageController.setLoggedInUser(user);

                // Navigate based on user's role
                navigateToAppropriateView(user);
            } else {
                showAlert(Alert.AlertType.ERROR, "Login Failed", "Invalid email or password. Please try again.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "An error occurred during login: " + e.getMessage());
        }
    }

    private User validateLoginAndGetUser(String email, String password) {
        try {
            //bring user from database by email
            User user = userDOAimp.getByEmail(email);

            // Check if user exists and password matches with one that hashed in database
            if (user != null && BCrypt.checkpw(password, user.getPassword())) {
                return user;
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void navigateToAppropriateView(User user) {
        String fxmlPath;

        // Determine which dashboard to load based on role ID (1->admin,2->agent)
        if (user.getRole() != null) {
            try {
                if (user.getRole().getId() == 1) { // Admin

                    fxmlPath = "/com/example/loborems/Dashboard/dashboard.fxml";
                    loadAndSetRoot(fxmlPath, user, true);
                } else if (user.getRole().getId() == 2) { // Agent
                    fxmlPath = "/com/example/loborems/Dashboard/dashboard-agent.fxml";
                    loadAndSetRoot(fxmlPath, user, false);
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Invalid role assignment.");
                    return;
                }
            } catch (IOException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Error", "Unable to load the Dashboard.");
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "User role not assigned.");
        }
    }

    private void loadAndSetRoot(String fxmlPath, User user, boolean isAdmin) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent dashboardRoot = loader.load();


        Object controller = loader.getController();
        if (controller instanceof DashboardController) {
            ((DashboardController) controller).setCurrentUser(user);
        } else if (controller instanceof DashboardAgentController) {
            ((DashboardAgentController) controller).setCurrentUser(user);
        }

        // If admin, also allow AddAgent access
        if (isAdmin) {
            addAgentController.setLoggedInUser(user);
        }

        // Shahd permission
        Scene currentScene = loginButton.getScene();
        currentScene.setRoot(dashboardRoot);


        Stage stage = (Stage) currentScene.getWindow();
        stage.setMaximized(true);
        stage.setFullScreen(true);
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}