package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import models.Annonce;
import models.TypeAnnonce;
import services.AnnonceService;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class MarketplaceController implements Initializable {

    @FXML
    private TextField searchField;
    @FXML
    private ComboBox<TypeAnnonce> typeCombo;
    @FXML
    private ComboBox<String> localisationCombo;
    @FXML
    private Slider prixSlider;
    @FXML
    private Label prixLabel;
    @FXML
    private Label countLabel;
    @FXML
    private FlowPane annoncesContainer;

    private AnnonceService annonceService;

    // Gouvernorats de Tunisie
    private final String[] GOUVERNORATS = {
            "Tunis", "Ariana", "Ben Arous", "Manouba", "Nabeul", "Zaghouan",
            "Bizerte", "Béja", "Jendouba", "Le Kef", "Siliana", "Sousse",
            "Monastir", "Mahdia", "Sfax", "Kairouan", "Kasserine", "Sidi Bouzid",
            "Gabès", "Médenine", "Tataouine", "Gafsa", "Tozeur", "Kébili"
    };

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        annonceService = new AnnonceService();

        // Remplir les ComboBox
        typeCombo.getItems().add(null); // Option "Tous"
        typeCombo.getItems().addAll(TypeAnnonce.values());
        localisationCombo.getItems().add("Toute la Tunisie");
        localisationCombo.getItems().addAll(GOUVERNORATS);

        // Listener pour le slider de prix
        prixSlider.valueProperty().addListener((obs, old, val) -> {
            prixLabel.setText(String.format("%.0f TND", val.doubleValue()));
        });

        // Charger les annonces au démarrage
        loadAnnonces();
    }

    private void loadAnnonces() {
        try {
            List<Annonce> annonces = annonceService.getAnnoncesDisponibles();
            displayAnnonces(annonces);
        } catch (SQLException e) {
            showError("Erreur lors du chargement des annonces: " + e.getMessage());
        }
    }

    private void displayAnnonces(List<Annonce> annonces) {
        annoncesContainer.getChildren().clear();
        countLabel.setText("(" + annonces.size() + " résultats)");

        for (Annonce annonce : annonces) {
            VBox card = createAnnonceCard(annonce);
            annoncesContainer.getChildren().add(card);
        }
    }

    private VBox createAnnonceCard(Annonce annonce) {
        VBox card = new VBox(10);
        card.setStyle("-fx-background-color: white; -fx-padding: 15; -fx-background-radius: 10; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 8, 0, 0, 2); -fx-pref-width: 260;");

        Label titre = new Label(annonce.getTitre());
        titre.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        titre.setWrapText(true);

        Label type = new Label(annonce.getType().getLibelle());
        type.setStyle(
                "-fx-background-color: #2e7d32; -fx-text-fill: white; -fx-padding: 3 8; -fx-background-radius: 3;");

        Label localisation = new Label("📍 " + annonce.getLocalisation());
        localisation.setStyle("-fx-text-fill: #666;");

        Label prix = new Label(String.format("%.2f TND", annonce.getPrix()));
        prix.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2e7d32;");

        Button reserverBtn = new Button("📅 Réserver");
        reserverBtn.setStyle("-fx-background-color: #2e7d32; -fx-text-fill: white; -fx-cursor: hand;");
        reserverBtn.setOnAction(e -> handleReserverAnnonce(annonce));

        card.getChildren().addAll(type, titre, localisation, prix, reserverBtn);
        return card;
    }

    @FXML
    private void handleSearch() {
        try {
            String keyword = searchField.getText();
            TypeAnnonce type = typeCombo.getValue();
            String loc = localisationCombo.getValue();
            if ("Toute la Tunisie".equals(loc))
                loc = null;
            Double prixMax = prixSlider.getValue();

            List<Annonce> results = annonceService.searchAdvanced(keyword, type, loc, prixMax);
            displayAnnonces(results);
        } catch (SQLException e) {
            showError("Erreur de recherche: " + e.getMessage());
        }
    }

    @FXML
    private void handleNouvelleAnnonce() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/views/AjouterAnnonce.fxml"));
            Stage stage = (Stage) annoncesContainer.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            showError("Erreur: " + e.getMessage());
        }
    }

    @FXML
    private void handleMesAnnonces() {
        // TODO: Filtrer par currentUser.getId()
        showInfo("Fonctionnalité 'Mes Annonces' - À implémenter avec la session utilisateur");
    }

    private void handleReserverAnnonce(Annonce annonce) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmer la réservation");
        confirm.setHeaderText("Réserver: " + annonce.getTitre());
        confirm.setContentText("Voulez-vous réserver ce matériel pour " + annonce.getPrix() + " TND ?");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    annonceService.reserverAnnonce(annonce.getId());
                    showInfo("✅ Réservation effectuée avec succès !");
                    loadAnnonces(); // Rafraîchir
                } catch (SQLException e) {
                    showError("Erreur: " + e.getMessage());
                }
            }
        });
    }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        alert.showAndWait();
    }

    private void showInfo(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        alert.showAndWait();
    }
}
