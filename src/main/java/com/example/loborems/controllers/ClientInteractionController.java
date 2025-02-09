package com.example.loborems.controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;

import java.io.IOException;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.example.loborems.models.Client;
import com.example.loborems.models.Interaction;
import com.example.loborems.services.DOAClientImpl;
import com.example.loborems.services.DOAInteractionImpl;

import javafx.stage.Stage;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;

public class ClientInteractionController {

    private static final Logger LOGGER = Logger.getLogger(ClientInteractionController.class.getName());
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final String[] INTERACTION_TYPES = {"Call", "Email", "Meeting"};

    @FXML
    private TableColumn<Interaction, String> clientIDColumn;
    @FXML
    private TableColumn<Interaction, String> interactionTypeColumn;
    @FXML
    private TableColumn<Interaction, String> dateColumn;
    @FXML
    private TableColumn<Interaction, String> statusColumn;

    @FXML
    private ComboBox<Client> clientSelect;

    @FXML
    private ComboBox<String> interactionType;

    @FXML
    private DatePicker datePicker;

    @FXML
    private TextArea interactionDetails;

    @FXML
    private Button cancelButton;

    @FXML
    private Button saveButton;
    @FXML
    private Button backButton;

    @FXML
    private TableView<Interaction> tableView;

    public ClientInteractionController() {}

    @FXML
    void initialize() {

        // Set table resize policy
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Set preferred width for other columns
        clientIDColumn.setPrefWidth(150);
        dateColumn.setPrefWidth(100);
        interactionTypeColumn.setPrefWidth(200);

        // Let the Status column take the remaining space
        statusColumn.setMaxWidth(Double.MAX_VALUE);
        LOGGER.info("Initializing ClientInteractionController.");

        setupTableColumns();
        interactionType.getItems().addAll(INTERACTION_TYPES);
        loadClients();
        loadTableData();

        saveButton.setOnAction(event -> {
            LOGGER.info("Save button clicked.");
            saveInteraction();
        });
        backButton.setOnAction(event -> {
            LOGGER.info("Back button clicked.");
            try {
                goToDashboard(event);
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Error navigating to the dashboard.", e);
                showAlert(AlertType.ERROR, "Error", "Failed to navigate to the dashboard. Check the logs for details.");
            }
        });

        cancelButton.setOnAction(event -> {
            LOGGER.info("Cancel button clicked.");
            clearFields();
        });
    }


    private void setupTableColumns() {
        clientIDColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getClient().getName()));
        interactionTypeColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getInteraction_type()));
        dateColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDate()));
        statusColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDetails()));

        LOGGER.info("Table columns configured.");
    }

    public void goToDashboard(ActionEvent event) throws IOException {
        Parent secondRoot = FXMLLoader.load(getClass().getResource("/com/example/loborems/Dashboard/dashboard.fxml"));
        Scene newScene = new Scene(secondRoot);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(newScene);
        window.show();
    }

    private void loadTableData() {
        try {
            DOAInteractionImpl doaInteraction = new DOAInteractionImpl();
            List<Interaction> interactions = doaInteraction.findAll();

            if (interactions == null || interactions.isEmpty()
            ) {
                LOGGER.warning("No interactions found.");
                return;
            }

            LOGGER.info("Loaded " + interactions.size() + " interactions.");

            ObservableList<Interaction> interactionList = FXCollections.observableArrayList(interactions);
            tableView.setItems(interactionList);

            clientIDColumn.setSortType(TableColumn.SortType.ASCENDING);
            tableView.getSortOrder().add(clientIDColumn);

            LOGGER.info("Table data loaded successfully in ascending order.");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error loading table data.", e);
            showAlert(AlertType.ERROR, "Error", "Failed to load table data. Check the logs for details.");
        }
    }



    private void saveInteraction() {
        try {
            if (interactionType.getValue() == null || datePicker.getValue() == null ||
                    interactionDetails.getText().isEmpty() || clientSelect.getValue() == null) {
                showAlert(AlertType.WARNING, "Validation Error", "All fields must be filled out.");
                return;
            }

            String formattedDate = datePicker.getValue().format(DateTimeFormatter.ofPattern(DATE_FORMAT));
            Interaction interaction = new Interaction(
                    interactionType.getValue(),
                    formattedDate,
                    interactionDetails.getText(),
                    clientSelect.getValue()
            );

            DOAInteractionImpl doaInteraction = new DOAInteractionImpl();
            doaInteraction.save(interaction);

            LOGGER.info("Interaction saved successfully.");
            showAlert(AlertType.INFORMATION, "Success", "Interaction saved successfully.");

            loadTableData();
            clearFields();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error saving interaction.", e);
            showAlert(AlertType.ERROR, "Error", "Failed to save interaction. Check the logs for details.");
        }
    }

    private void loadClients() {
        try {
            DOAClientImpl doaClient = new DOAClientImpl();
            List<Client> clients = doaClient.findAll();

            ObservableList<Client> clientNames = FXCollections.observableArrayList(clients);
            clientSelect.setItems(clientNames);

            clientSelect.setCellFactory(comboBox -> new ListCell<>() {
                @Override
                protected void updateItem(Client client, boolean empty) {
                    super.updateItem(client, empty);
                    setText(empty || client == null ? null : client.getName());
                }
            });

            clientSelect.setButtonCell(new ListCell<>() {
                @Override
                protected void updateItem(Client client, boolean empty) {
                    super.updateItem(client, empty);
                    setText(empty || client == null ? null : client.getName());
                }
            });

            LOGGER.info("Clients loaded successfully and displayed by name.");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error loading clients.", e);
            showAlert(AlertType.ERROR, "Error", "Failed to load clients. Check the logs for details.");
        }
    }

    private void clearFields() {
        interactionType.setValue(null);
        datePicker.setValue(null);
        interactionDetails.clear();
        clientSelect.setValue(null);
        LOGGER.info("Fields cleared.");
    }

    private void showAlert(AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
