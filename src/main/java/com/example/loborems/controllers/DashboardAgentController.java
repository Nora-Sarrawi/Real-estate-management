package com.example.loborems.controllers;

import com.example.loborems.models.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class DashboardAgentController {
    private User currentUser;

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    private void setFullScreen(Stage window) {
        window.setMaximized(true);
        window.setFullScreen(true);
    }

    public void logout(ActionEvent event) throws IOException {
        Parent loginRoot = FXMLLoader.load(getClass().getResource("/com/example/loborems/Login/login.fxml"));
        Scene loginScene = new Scene(loginRoot);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(loginScene);
        setFullScreen(window);
    }

    public void goToDashboard(ActionEvent event) throws IOException {
        Parent dashboardRoot = FXMLLoader.load(getClass().getResource("/com/example/loborems/Dashboard/dashboard.fxml"));
        Scene dashboardScene = new Scene(dashboardRoot);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(dashboardScene);
        setFullScreen(window);
    }

    public void goToInteractions(ActionEvent event) throws IOException {
        Parent interactionsRoot = FXMLLoader.load(getClass().getResource("/com/example/loborems/ClientInteraction/client-interaction.fxml"));
        Scene interactionsScene = new Scene(interactionsRoot);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(interactionsScene);
        setFullScreen(window);
    }

    public void goToClients(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/loborems/ClientList/client-list.fxml"));
        Parent clientRoot = loader.load();

        ClientListController controller = loader.getController();
        controller.setCurrentUser(currentUser);

        Scene clientScene = new Scene(clientRoot);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(clientScene);
        setFullScreen(window);
    }

    public void goToProperties(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/loborems/PropertyListing/property-listing.fxml"));
        Parent propertyRoot = loader.load();

        PropertyListingController controller = loader.getController();
        System.out.println("DashboardController goToProperties - Passing User Role: " +
                (currentUser != null && currentUser.getRole() != null ? currentUser.getRole().getId() : "null"));

        controller.setCurrentUser(currentUser);

        Scene propertyScene = new Scene(propertyRoot);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(propertyScene);
        setFullScreen(window);
    }

    public void goToAddAgents(ActionEvent event) throws IOException {
        Parent addAgentRoot = FXMLLoader.load(getClass().getResource("/com/example/loborems/AddAgent/addAgent.fxml"));
        Scene addAgentScene = new Scene(addAgentRoot);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(addAgentScene);
        setFullScreen(window);
    }

    public void goToCategorisation(ActionEvent event) throws IOException {
        Parent categorizationRoot = FXMLLoader.load(getClass().getResource("/com/example/loborems/ClientCategorization/client-categorization.fxml"));
        Scene categorizationScene = new Scene(categorizationRoot);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(categorizationScene);
        setFullScreen(window);
    }

    public void goToOffers(ActionEvent event) throws IOException {
        Parent offersRoot = FXMLLoader.load(getClass().getResource("/com/example/loborems/Offers/offers.fxml"));
        Scene offersScene = new Scene(offersRoot);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(offersScene);
        setFullScreen(window);
    }

    public void goToManageAgent(ActionEvent event) throws IOException {
        Parent manageAgentRoot = FXMLLoader.load(getClass().getResource("/com/example/loborems/AgentManage/AgentManage.fxml"));
        Scene manageAgentScene = new Scene(manageAgentRoot);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(manageAgentScene);
        setFullScreen(window);
    }
}