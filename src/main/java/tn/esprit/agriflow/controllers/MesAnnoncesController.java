package tn.esprit.agriflow.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import tn.esprit.agriflow.models.Annonce;
import tn.esprit.agriflow.models.enums.StatutAnnonce;
import tn.esprit.agriflow.services.AnnonceService;

import java.io.IOException;

/**
 * Controller for managing user's own annonces
 */
public class MesAnnoncesController {
    @FXML private TableView<Annonce> annoncesTable;
    @FXML private TableColumn<Annonce, Integer> idColumn;
    @FXML private TableColumn<Annonce, String> titleColumn;
    @FXML private TableColumn<Annonce, Double> priceColumn;
    @FXML private TableColumn<Annonce, String> typeColumn;
    @FXML private TableColumn<Annonce, String> categoryColumn;
    @FXML private TableColumn<Annonce, String> statusColumn;
    @FXML private Button editButton;
    @FXML private Button deleteButton;
    @FXML private Button refreshButton;
    
    private AnnonceService annonceService;
    private int currentUserId;
    
    @FXML
    public void initialize() {
        annonceService = new AnnonceService();
        
        // Configure table columns
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        
        typeColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getType().getDisplayName()));
        
        categoryColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getCategory().getDisplayName()));
        
        statusColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getStatus().getDisplayName()));
        
        // Disable edit/delete buttons until selection
        editButton.setDisable(true);
        deleteButton.setDisable(true);
        
        annoncesTable.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> {
                boolean hasSelection = newSelection != null;
                editButton.setDisable(!hasSelection);
                deleteButton.setDisable(!hasSelection);
            }
        );
    }
    
    public void setCurrentUserId(int userId) {
        this.currentUserId = userId;
        loadUserAnnonces();
    }
    
    private void loadUserAnnonces() {
        ObservableList<Annonce> annonces = FXCollections.observableArrayList(
            annonceService.getByUserId(currentUserId)
        );
        annoncesTable.setItems(annonces);
    }
    
    @FXML
    private void handleEdit() {
        Annonce selected = annoncesTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/annonce_form.fxml"));
                Parent root = loader.load();
                
                AnnonceFormController controller = loader.getController();
                controller.setCurrentUserId(currentUserId);
                controller.setAnnonce(selected);
                
                Stage stage = new Stage();
                stage.setTitle("Modifier l'annonce");
                stage.setScene(new Scene(root));
                stage.showAndWait();
                
                // Refresh the list
                loadUserAnnonces();
            } catch (IOException e) {
                e.printStackTrace();
                showAlert("Erreur", "Impossible d'ouvrir le formulaire", Alert.AlertType.ERROR);
            }
        }
    }
    
    @FXML
    private void handleDelete() {
        Annonce selected = annoncesTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirmer la suppression");
            confirm.setHeaderText("Supprimer l'annonce: " + selected.getTitle());
            confirm.setContentText("Cette action est irréversible. Continuer ?");
            
            if (confirm.showAndWait().get() == ButtonType.OK) {
                if (annonceService.delete(selected.getId())) {
                    showAlert("Succès", "Annonce supprimée avec succès", Alert.AlertType.INFORMATION);
                    loadUserAnnonces();
                } else {
                    showAlert("Erreur", "Échec de la suppression", Alert.AlertType.ERROR);
                }
            }
        }
    }
    
    @FXML
    private void handleRefresh() {
        loadUserAnnonces();
    }
    
    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
