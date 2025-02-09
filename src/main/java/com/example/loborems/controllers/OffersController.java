package com.example.loborems.controllers;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import com.example.loborems.interfaces.OfferDAO;
import com.example.loborems.models.Client;
import com.example.loborems.models.Offer;
import com.example.loborems.models.Offer.OfferType;
import com.example.loborems.models.Offer.PropertyType;
import com.example.loborems.models.Offer.Status;
import com.example.loborems.services.DOAClientImpl;
import com.example.loborems.services.OfferDAOImpl;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;

public class OffersController {

    @FXML
    private Button backButtonOffers, addOfferButton, cancelEditButton;

    @FXML
    private ComboBox<Client> clientNameComboBox;
    @FXML
    private ComboBox<PropertyType> propertyTypeCombo;
    @FXML
    private ComboBox<OfferType> offerTypeCombo;
    @FXML
    private TextField priceField;
    @FXML
    private TableView<Offer> offersTable;
    @FXML
    private TableColumn<Offer, String> clientNameColumn;
    @FXML
    private TableColumn<Offer, PropertyType> propertyTypeColumn;
    @FXML
    private TableColumn<Offer, OfferType> offerTypeColumn;
    @FXML
    private TableColumn<Offer, Double> priceColumn;
    @FXML
    private TableColumn<Offer, Status> statusColumn;

    private ObservableList<Offer> offerData = FXCollections.observableArrayList();
    private OfferDAO offerDAO = new OfferDAOImpl();
    private DOAClientImpl clientDAO = new DOAClientImpl();
    private Offer selectedOffer;

    @FXML
    public void initialize() {
        // Initialize ComboBoxes with enum values
        propertyTypeCombo.setItems(FXCollections.observableArrayList(PropertyType.values()));
        offerTypeCombo.setItems(FXCollections.observableArrayList(OfferType.values()));

        // Populate the client ComboBox
        List<Client> clients = clientDAO.findAll();
        ObservableList<Client> clientList = FXCollections.observableArrayList(clients);
        clientNameComboBox.setItems(clientList);
        clientNameComboBox.setConverter(new javafx.util.StringConverter<Client>() {
            @Override
            public String toString(Client client) {
                return client != null ? client.getName() : "";
            }

            @Override
            public Client fromString(String string) {
                return clients.stream().filter(client -> client.getName().equals(string)).findFirst().orElse(null);
            }
        });

        // Set up TableColumns for the offersTable
        clientNameColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getClientName()));
        propertyTypeColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getPropertyType()));
        offerTypeColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getOfferType()));
        priceColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleDoubleProperty(data.getValue().getPrice()).asObject());
        statusColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getStatus()));
        offersTable.setItems(offerData);

        // Add context menu to table rows
        offersTable.setRowFactory(tableView -> {
            final TableRow<Offer> row = new TableRow<>();
            final ContextMenu contextMenu = new ContextMenu();
            final MenuItem editMenuItem = new MenuItem("Edit");
            final MenuItem deleteMenuItem = new MenuItem("Delete");

            editMenuItem.setOnAction(event -> handleEditOffer(row.getItem()));
            deleteMenuItem.setOnAction(event -> handleDeleteOffer(row.getItem()));

            contextMenu.getItems().addAll(editMenuItem, deleteMenuItem);
            row.contextMenuProperty().bind(javafx.beans.binding.Bindings.when(row.emptyProperty()).then((ContextMenu) null).otherwise(contextMenu));
            return row;
        });

        // Load existing offers
        loadOffers();
    }

    private void loadOffers() {
        List<Offer> offers = offerDAO.findAll();
        offerData.setAll(offers);
    }

    @FXML
    public void handleAddOffer(ActionEvent event) {
        if (selectedOffer != null) {
            // Edit mode
            Client selectedClient = clientNameComboBox.getSelectionModel().getSelectedItem();
            if (selectedClient == null) {
                System.out.println("Please select a client!");
                return;
            }

            selectedOffer.setClientId(selectedClient.getId());
            selectedOffer.setClientName(selectedClient.getName());
            selectedOffer.setPropertyType(propertyTypeCombo.getSelectionModel().getSelectedItem());
            selectedOffer.setOfferType(offerTypeCombo.getSelectionModel().getSelectedItem());
            selectedOffer.setPrice(Double.parseDouble(priceField.getText()));

            offerDAO.update(selectedOffer);
            offersTable.refresh();

            selectedOffer = null;
            addOfferButton.setText("Add Offer");
            cancelEditButton.setVisible(false);
            clearInputFields();
        } else {
            // Add mode
            Client selectedClient = clientNameComboBox.getSelectionModel().getSelectedItem();
            if (selectedClient == null) {
                // Handle case where no client is selected
                System.out.println("Please select a client!");
                return;
            }

            PropertyType propertyType = propertyTypeCombo.getSelectionModel().getSelectedItem();
            OfferType offerType = offerTypeCombo.getSelectionModel().getSelectedItem();
            double price = Double.parseDouble(priceField.getText());

            Offer newOffer = new Offer(selectedClient.getId(), selectedClient.getName(), propertyType, offerType, price, Status.PENDING);
            offerDAO.save(newOffer);
            offerData.add(newOffer);

            clearInputFields();
        }
    }

    private void handleEditOffer(Offer offer) {
        selectedOffer = offer;

        // Populate fields with selected offer's data
        clientNameComboBox.getSelectionModel().select(findClientById(offer.getClientId()));
        propertyTypeCombo.getSelectionModel().select(offer.getPropertyType());
        offerTypeCombo.getSelectionModel().select(offer.getOfferType());
        priceField.setText(String.valueOf(offer.getPrice()));

        addOfferButton.setText("Edit Offer");
        cancelEditButton.setVisible(true);
    }

    private void handleDeleteOffer(Offer offer) {
        offerDAO.delete(offer.getId());
        offerData.remove(offer);
        System.out.println("Deleted offer: " + offer);
    }

    private Client findClientById(int clientId) {
        return clientDAO.findById(clientId);
    }

    @FXML
    private void handleCancelEdit(ActionEvent event) {
        selectedOffer = null;
        addOfferButton.setText("Add Offer");
        cancelEditButton.setVisible(false);
        clearInputFields();
    }

    private void clearInputFields() {
        clientNameComboBox.getSelectionModel().clearSelection();
        propertyTypeCombo.getSelectionModel().clearSelection();
        offerTypeCombo.getSelectionModel().clearSelection();
        priceField.clear();
    }

    @FXML
    public void handleBack(ActionEvent event) throws IOException {
        Parent secondRoot = FXMLLoader.load(getClass().getResource("/com/example/loborems/Dashboard/dashboard.fxml"));
        Scene newScene = new Scene(secondRoot);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(newScene);
    }

    @FXML
    public void handleDownloadData(ActionEvent event) throws IOException {
        Parent downloadDataRoot = FXMLLoader.load(getClass().getResource("/com/example/loborems/DownloadData/download-data.fxml"));
        Scene downloadDataScene = new Scene(downloadDataRoot);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(downloadDataScene);
    }
}
