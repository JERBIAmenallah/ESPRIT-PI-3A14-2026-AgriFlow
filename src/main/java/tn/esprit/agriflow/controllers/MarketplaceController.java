package tn.esprit.agriflow.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import tn.esprit.agriflow.models.Annonce;
import tn.esprit.agriflow.models.enums.CategorieAnnonce;
import tn.esprit.agriflow.models.enums.TypeAnnonce;
import tn.esprit.agriflow.services.AnnonceService;

import java.io.IOException;
import java.util.List;

/**
 * Controller for the main marketplace view
 */
public class MarketplaceController {
    @FXML private TextField searchField;
    @FXML private ComboBox<String> typeComboBox;
    @FXML private ComboBox<String> categoryComboBox;
    @FXML private TextField minPriceField;
    @FXML private TextField maxPriceField;
    @FXML private TextField locationField;
    @FXML private Button searchButton;
    @FXML private Button addAnnonceButton;
    @FXML private Button myAnnoncesButton;
    @FXML private Button myReservationsButton;
    @FXML private GridPane annoncesGrid;
    @FXML private ScrollPane scrollPane;
    
    private AnnonceService annonceService;
    private int currentUserId = 1; // Simulated current user (in real app, get from session)
    
    @FXML
    public void initialize() {
        annonceService = new AnnonceService();
        
        // Initialize combo boxes
        initializeComboBoxes();
        
        // Load all available annonces
        loadAnnonces(annonceService.getAvailable());
    }
    
    private void initializeComboBoxes() {
        // Type combo box
        ObservableList<String> types = FXCollections.observableArrayList("Tous", "Location", "Vente");
        typeComboBox.setItems(types);
        typeComboBox.setValue("Tous");
        
        // Category combo box
        ObservableList<String> categories = FXCollections.observableArrayList(
            "Toutes", "Tracteur", "Moissonneuse", "Semences", "Engrais", "Irrigation", "Autre"
        );
        categoryComboBox.setItems(categories);
        categoryComboBox.setValue("Toutes");
    }
    
    @FXML
    private void handleSearch() {
        String keyword = searchField.getText().trim();
        
        TypeAnnonce type = null;
        String typeStr = typeComboBox.getValue();
        if (typeStr != null && !typeStr.equals("Tous")) {
            type = typeStr.equals("Location") ? TypeAnnonce.LOCATION : TypeAnnonce.VENTE;
        }
        
        CategorieAnnonce category = null;
        String categoryStr = categoryComboBox.getValue();
        if (categoryStr != null && !categoryStr.equals("Toutes")) {
            try {
                category = CategorieAnnonce.valueOf(categoryStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                // Ignore invalid category
            }
        }
        
        Double minPrice = null;
        if (!minPriceField.getText().trim().isEmpty()) {
            try {
                minPrice = Double.parseDouble(minPriceField.getText().trim());
            } catch (NumberFormatException e) {
                showAlert("Erreur", "Prix minimum invalide", Alert.AlertType.ERROR);
                return;
            }
        }
        
        Double maxPrice = null;
        if (!maxPriceField.getText().trim().isEmpty()) {
            try {
                maxPrice = Double.parseDouble(maxPriceField.getText().trim());
            } catch (NumberFormatException e) {
                showAlert("Erreur", "Prix maximum invalide", Alert.AlertType.ERROR);
                return;
            }
        }
        
        String location = locationField.getText().trim();
        
        List<Annonce> results = annonceService.search(keyword, type, category, minPrice, maxPrice, location);
        loadAnnonces(results);
    }
    
    @FXML
    private void handleAddAnnonce() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/annonce_form.fxml"));
            Parent root = loader.load();
            
            AnnonceFormController controller = loader.getController();
            controller.setCurrentUserId(currentUserId);
            
            Stage stage = new Stage();
            stage.setTitle("Nouvelle Annonce");
            stage.setScene(new Scene(root));
            stage.showAndWait();
            
            // Refresh the list
            loadAnnonces(annonceService.getAvailable());
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ouvrir le formulaire", Alert.AlertType.ERROR);
        }
    }
    
    @FXML
    private void handleMyAnnonces() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/mes_annonces.fxml"));
            Parent root = loader.load();
            
            MesAnnoncesController controller = loader.getController();
            controller.setCurrentUserId(currentUserId);
            
            Stage stage = new Stage();
            stage.setTitle("Mes Annonces");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ouvrir la page", Alert.AlertType.ERROR);
        }
    }
    
    @FXML
    private void handleMyReservations() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/mes_reservations.fxml"));
            Parent root = loader.load();
            
            MesReservationsController controller = loader.getController();
            controller.setCurrentUserId(currentUserId);
            
            Stage stage = new Stage();
            stage.setTitle("Mes Réservations");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ouvrir la page", Alert.AlertType.ERROR);
        }
    }
    
    private void loadAnnonces(List<Annonce> annonces) {
        annoncesGrid.getChildren().clear();
        
        int column = 0;
        int row = 0;
        
        for (Annonce annonce : annonces) {
            VBox annonceCard = createAnnonceCard(annonce);
            annoncesGrid.add(annonceCard, column, row);
            
            column++;
            if (column == 3) { // 3 columns
                column = 0;
                row++;
            }
        }
    }
    
    private VBox createAnnonceCard(Annonce annonce) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(10));
        card.setStyle("-fx-border-color: #ccc; -fx-border-width: 1; -fx-background-color: white;");
        card.setPrefWidth(200);
        
        Label title = new Label(annonce.getTitle());
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        title.setWrapText(true);
        
        Label price = new Label(String.format("%.2f TND", annonce.getPrice()));
        price.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
        
        Label type = new Label(annonce.getType().getDisplayName());
        Label category = new Label(annonce.getCategory().getDisplayName());
        Label location = new Label(annonce.getLocation());
        Label status = new Label(annonce.getStatus().getDisplayName());
        status.setStyle("-fx-text-fill: " + (annonce.getStatus().name().equals("DISPONIBLE") ? "green" : "red"));
        
        Button viewButton = new Button("Voir détails");
        viewButton.setOnAction(e -> openAnnonceDetail(annonce));
        
        card.getChildren().addAll(title, price, type, category, location, status, viewButton);
        
        return card;
    }
    
    private void openAnnonceDetail(Annonce annonce) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/annonce_detail.fxml"));
            Parent root = loader.load();
            
            AnnonceDetailController controller = loader.getController();
            controller.setAnnonce(annonce);
            controller.setCurrentUserId(currentUserId);
            
            Stage stage = new Stage();
            stage.setTitle("Détails de l'annonce");
            stage.setScene(new Scene(root));
            stage.showAndWait();
            
            // Refresh the list
            handleSearch();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ouvrir les détails", Alert.AlertType.ERROR);
        }
    }
    
    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    public void setCurrentUserId(int userId) {
        this.currentUserId = userId;
    }
}
