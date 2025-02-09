package com.example.loborems.controllers;

import com.example.loborems.models.User;
import com.example.loborems.services.UserDOAimp;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.Base64;

public class ForgotPasswordController {

    @FXML
    private TextField emailField;

    private final UserDOAimp userDAO = new UserDOAimp();

    /**
     * Generates a secure random token for resetting the password.
     */
    private String generateResetToken() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[24];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    @FXML
    public void sendResetLink(ActionEvent event) {
        String email = emailField.getText().trim();

        try {
            // Fetch the user by email using UserDAO
            User user = userDAO.getByEmail(email);

            if (user != null) {
                String token = generateResetToken();

                // Save the token and expiration in the User entity
                user.setResetToken(token);
                user.setTokenExpiration(System.currentTimeMillis() + 15 * 60 * 1000); // Token valid for 15 minutes
                userDAO.update(user); // Update the user with the token and expiration

                // Generate the reset link
                String emailBody = "Use the following token to reset your password: " + token;
                EmailSender.sendResetEmail(email, emailBody);

                showAlert("Success", "A password reset link has been sent to your email.");

                // Redirect to Reset Password page
                loadResetPasswordPage(token); // Pass the token to the next page
            } else {
                showAlert("Error", "Email not found in the database.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Email Error", "Failed to send the reset email.");
        }
    }

    /**
     * Redirects the user to the Reset Password page and passes the token.
     */
    private void loadResetPasswordPage(String token) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/loborems/ResetPassword/reset-password.fxml")); // Update the path
        try {
            Parent root = loader.load();

            // Pass the token to ResetPasswordController
            ResetPasswordController controller = loader.getController();
            controller.setToken(token);

            // Show the Reset Password page
            Stage stage = new Stage();
            stage.setTitle("Reset Password");
            stage.setScene(new Scene(root));
            stage.show();

            // Optionally close the current window
            Stage currentStage = (Stage) emailField.getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load the Reset Password page.");
        }
    }

    /**
     * Displays an alert dialog with the specified title and message.
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void backToSignIn(ActionEvent event) throws IOException {
        Parent secondRoot = FXMLLoader.load(getClass().getResource("/com/example/loborems/Login/login.fxml"));
        Scene newScene = new Scene(secondRoot);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(newScene);

    }
}