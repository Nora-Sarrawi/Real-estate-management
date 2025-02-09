package com.example.loborems.controllers;

import com.example.loborems.models.User;
import com.example.loborems.services.UserDOAimp;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ResetPasswordController {

    @FXML
    private PasswordField newPasswordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private TextField resetCodeField;
    private String token;

    public void setToken(String token) {
        this.token = token;
    }

    private final UserDOAimp userDAO = new UserDOAimp();

    @FXML
    public void resetPassword(ActionEvent event) {
        String newPassword = newPasswordField.getText().trim();
        String confirmPassword = confirmPasswordField.getText().trim();
        String resetCode = resetCodeField.getText().trim();

        if (newPassword.isEmpty() || confirmPassword.isEmpty() || resetCode.isEmpty()) {
            showAlert("Error", "All fields are required.");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            showAlert("Error", "Passwords do not match.");
            return;
        }

        try {
            // Validate the reset code
            User user = userDAO.getByResetToken(resetCode);

            if (user != null && System.currentTimeMillis() < user.getTokenExpiration()) {
                // Update the user's password
                user.setPassword(newPassword); // Hash the password in production
                user.setResetToken(null); // Clear the reset token
                user.setTokenExpiration(0L); // Clear the token expiration
                userDAO.update(user);

                showAlert("Success", "Your password has been reset successfully.");

                // Close the current window
                Stage currentStage = (Stage) newPasswordField.getScene().getWindow();
                currentStage.close();
            } else {
                showAlert("Error", "Invalid or expired reset code.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "An error occurred while resetting your password.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}