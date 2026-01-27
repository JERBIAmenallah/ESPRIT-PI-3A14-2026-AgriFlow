package tn.esprit.agriflow.controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import tn.esprit.agriflow.models.Annonce;
import tn.esprit.agriflow.models.enums.CategorieAnnonce;
import tn.esprit.agriflow.models.enums.TypeAnnonce;
import tn.esprit.agriflow.services.AnnonceService;

import java.io.File;
import java.time.LocalDate;

/**
 * Controller for creating/editing annonces
 */
public class AnnonceFormController {
    @FXML private TextField titleField;
    @FXML private TextArea descriptionArea;
    @FXML private TextField priceField;
    @FXML private ComboBox<String> typeComboBox;
    @FXML private ComboBox<String> categoryComboBox;
    @FXML private TextField locationField;
    @FXML private DatePicker availabilityStartPicker;
    @FXML private DatePicker availabilityEndPicker;
    @FXML private TextField imagePathField;
    @FXML private Button browseImageButton;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;
    
    private AnnonceService annonceService;
    private Annonce currentAnnonce; // For editing
    private int currentUserId;
    
    @FXML
    public void initialize() {
        annonceService = new AnnonceService();
        
        // Initialize combo boxes
        typeComboBox.setItems(FXCollections.observableArrayList("Location", "Vente"));
        categoryComboBox.setItems(FXCollections.observableArrayList(
            "Tracteur", "Moissonneuse", "Semences", "Engrais", "Irrigation", "Autre"
        ));
    }
    
    @FXML
    private void handleBrowseImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
        
        File file = fileChooser.showOpenDialog(browseImageButton.getScene().getWindow());
        if (file != null) {
            imagePathField.setText(file.getAbsolutePath());
        }
    }
    
    @FXML
    private void handleSave() {
        if (!validateInput()) {
            return;
        }
        
        try {
            if (currentAnnonce == null) {
                // Creating new annonce
                Annonce annonce = new Annonce();
                annonce.setUserId(currentUserId);
                fillAnnonceFromForm(annonce);
                
                if (annonceService.add(annonce)) {
                    showAlert("Succès", "Annonce créée avec succès", Alert.AlertType.INFORMATION);
                    closeWindow();
                } else {
                    showAlert("Erreur", "Échec de la création de l'annonce", Alert.AlertType.ERROR);
                }
            } else {
                // Updating existing annonce
                fillAnnonceFromForm(currentAnnonce);
                
                if (annonceService.update(currentAnnonce)) {
                    showAlert("Succès", "Annonce mise à jour avec succès", Alert.AlertType.INFORMATION);
                    closeWindow();
                } else {
                    showAlert("Erreur", "Échec de la mise à jour de l'annonce", Alert.AlertType.ERROR);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Une erreur est survenue: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    @FXML
    private void handleCancel() {
        closeWindow();
    }
    
    private boolean validateInput() {
        if (titleField.getText().trim().isEmpty()) {
            showAlert("Validation", "Veuillez saisir un titre", Alert.AlertType.WARNING);
            return false;
        }
        
        if (descriptionArea.getText().trim().isEmpty()) {
            showAlert("Validation", "Veuillez saisir une description", Alert.AlertType.WARNING);
            return false;
        }
        
        if (priceField.getText().trim().isEmpty()) {
            showAlert("Validation", "Veuillez saisir un prix", Alert.AlertType.WARNING);
            return false;
        }
        
        try {
            Double.parseDouble(priceField.getText().trim());
        } catch (NumberFormatException e) {
            showAlert("Validation", "Prix invalide", Alert.AlertType.WARNING);
            return false;
        }
        
        if (typeComboBox.getValue() == null) {
            showAlert("Validation", "Veuillez sélectionner un type", Alert.AlertType.WARNING);
            return false;
        }
        
        if (categoryComboBox.getValue() == null) {
            showAlert("Validation", "Veuillez sélectionner une catégorie", Alert.AlertType.WARNING);
            return false;
        }
        
        if (locationField.getText().trim().isEmpty()) {
            showAlert("Validation", "Veuillez saisir une localisation", Alert.AlertType.WARNING);
            return false;
        }
        
        // Validate dates for rental
        if (typeComboBox.getValue().equals("Location")) {
            if (availabilityStartPicker.getValue() == null || availabilityEndPicker.getValue() == null) {
                showAlert("Validation", "Veuillez sélectionner les dates de disponibilité", Alert.AlertType.WARNING);
                return false;
            }
            
            if (availabilityEndPicker.getValue().isBefore(availabilityStartPicker.getValue())) {
                showAlert("Validation", "La date de fin doit être après la date de début", Alert.AlertType.WARNING);
                return false;
            }
        }
        
        return true;
    }
    
    private void fillAnnonceFromForm(Annonce annonce) {
        annonce.setTitle(titleField.getText().trim());
        annonce.setDescription(descriptionArea.getText().trim());
        annonce.setPrice(Double.parseDouble(priceField.getText().trim()));
        annonce.setType(typeComboBox.getValue().equals("Location") ? TypeAnnonce.LOCATION : TypeAnnonce.VENTE);
        annonce.setCategory(CategorieAnnonce.valueOf(categoryComboBox.getValue().toUpperCase()));
        annonce.setLocation(locationField.getText().trim());
        annonce.setImagePath(imagePathField.getText().trim());
        
        if (typeComboBox.getValue().equals("Location")) {
            annonce.setAvailabilityStart(availabilityStartPicker.getValue());
            annonce.setAvailabilityEnd(availabilityEndPicker.getValue());
        }
    }
    
    public void setAnnonce(Annonce annonce) {
        this.currentAnnonce = annonce;
        
        // Fill form with annonce data
        titleField.setText(annonce.getTitle());
        descriptionArea.setText(annonce.getDescription());
        priceField.setText(String.valueOf(annonce.getPrice()));
        typeComboBox.setValue(annonce.getType().getDisplayName());
        categoryComboBox.setValue(annonce.getCategory().getDisplayName());
        locationField.setText(annonce.getLocation());
        imagePathField.setText(annonce.getImagePath());
        
        if (annonce.getAvailabilityStart() != null) {
            availabilityStartPicker.setValue(annonce.getAvailabilityStart());
        }
        if (annonce.getAvailabilityEnd() != null) {
            availabilityEndPicker.setValue(annonce.getAvailabilityEnd());
        }
    }
    
    public void setCurrentUserId(int userId) {
        this.currentUserId = userId;
    }
    
    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    private void closeWindow() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
}
