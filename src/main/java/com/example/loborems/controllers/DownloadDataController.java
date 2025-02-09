package com.example.loborems.controllers;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;

import com.example.loborems.models.Client;
import com.example.loborems.services.DOAClientImpl;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Pagination;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class DownloadDataController {

    @FXML
    private TextField searchTextField;

    @FXML
    private TableView<Client> clientInfoTable;

    @FXML
    private TableColumn<Client, Integer> idColumn;

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
    private Button downloadButton;

    @FXML
    private ProgressIndicator downloadProgress;

    @FXML
    private Pagination pagination;

    private final ObservableList<Client> clientDataList = FXCollections.observableArrayList();
    private final DOAClientImpl doaClientV = new DOAClientImpl();
    private List<Client> searchResults;
    private Client selectedClient;

    @FXML
    private void initialize() {
        downloadButton.setDisable(true); // Initially disabled
        clientInfoTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> downloadButton.setDisable(newValue == null)
        );

        // Other initialization code
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        propertyColumn.setCellValueFactory(new PropertyValueFactory<>("property"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));
        clientInfoTable.setItems(clientDataList);
    }

    @FXML
    private void searchClient() {
        String searchQuery = searchTextField.getText();
        if (searchQuery.isEmpty()) {
            return;
        }

        searchResults = doaClientV.findAll().stream()
                .filter(client -> client.getName().toLowerCase().contains(searchQuery.toLowerCase())
                || String.valueOf(client.getId()).equals(searchQuery))
                .collect(Collectors.toList());

        updatePagination();
    }

    private void updatePagination() {
        int pages = (int) Math.ceil((double) searchResults.size() / 10);
        pagination.setPageCount(pages);
        pagination.setCurrentPageIndex(0);
        createPage(0);
    }

    private Node createPage(int pageIndex) {
        int fromIndex = pageIndex * 10;
        int toIndex = Math.min(fromIndex + 10, searchResults.size());
        clientDataList.setAll(searchResults.subList(fromIndex, toIndex));
        return clientInfoTable;
    }

    @FXML
    private void downloadData() {
        selectedClient = clientInfoTable.getSelectionModel().getSelectedItem();
        if (selectedClient == null) {
            return;
        }

        downloadButton.setDisable(true);
        downloadProgress.setVisible(true);

        new Thread(() -> {
            try {
                // Step 1: Gather Client Data
                String clientData = String.format("ID: %d%nName: %s%nEmail: %s%nPhone: %s%nProperty: %s%nRole: %s%n",
                        selectedClient.getId(),
                        selectedClient.getName(),
                        selectedClient.getEmail(),
                        selectedClient.getPhone(),
                        selectedClient.getProperty(),
                        selectedClient.getRole());

                // Step 2: Create Temporary Files
                File tempDir = new File(System.getProperty("java.io.tmpdir"), "clientData");
                if (!tempDir.exists()) {
                    tempDir.mkdirs();
                }
                File clientFile = new File(tempDir, "client_data.txt");
                try (FileWriter writer = new FileWriter(clientFile)) {
                    writer.write(clientData);
                }

                // Step 3: Package into a .zip File
                File zipFile = new File(tempDir, "client_data.zip");
                try (ArchiveOutputStream archive = new ZipArchiveOutputStream(Files.newOutputStream(Paths.get(zipFile.toURI())))) {
                    ArchiveEntry entry = new ZipArchiveEntry(clientFile.getName());
                    archive.putArchiveEntry(entry);
                    Files.copy(clientFile.toPath(), archive);
                    archive.closeArchiveEntry();
                }

                // Provide a link or prompt for downloading the .zip file
                System.out.println("Client data packaged successfully! Download from: " + zipFile.getAbsolutePath());

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                downloadButton.setDisable(false);
                downloadProgress.setVisible(false);
            }
        }).start();
    }

    @FXML
    public void handleBack(ActionEvent event) throws IOException {
        Parent secondRoot = FXMLLoader.load(getClass().getResource("/com/example/loborems/Dashboard/dashboard.fxml"));
        Scene newScene = new Scene(secondRoot);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(newScene);
    }
}
