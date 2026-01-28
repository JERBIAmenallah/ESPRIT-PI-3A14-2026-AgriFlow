package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import models.Annonce;
import models.TypeAnnonce;
import services.AnnonceService;

import java.io.File;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class AjouterAnnonceController implements Initializable {

    @FXML
    private TextField titreField;
    @FXML
    private TextArea descriptionArea;
    @FXML
    private ComboBox<TypeAnnonce> typeCombo;
    @FXML
    private TextField prixField;
    @FXML
    private ComboBox<String> localisationCombo;
    @FXML
    private DatePicker dateDispoField;
    @FXML
    private ImageView imagePreview;
    @FXML
    private Label imagePlaceholder;
    @FXML
    private Label imagePathLabel;
    @FXML
    private Label messageLabel;

    private AnnonceService annonceService;
    private String selectedImagePath;

    // Simuler l'utilisateur connecté (normalement vient de la session d'Ayoub)
    private int currentUserId = 1;

    private final String[] GOUVERNORATS = {
            "Tunis", "Ariana", "Ben Arous", "Manouba", "Nabeul", "Zaghouan",
            "Bizerte", "Béja", "Jendouba", "Le Kef", "Siliana", "Sousse",
            "Monastir", "Mahdia", "Sfax", "Kairouan", "Kasserine", "Sidi Bouzid",
            "Gabès", "Médenine", "Tataouine", "Gafsa", "Tozeur", "Kébili"
    };

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        annonceService = new AnnonceService();
        typeCombo.getItems().addAll(TypeAnnonce.values());
        localisationCombo.getItems().addAll(GOUVERNORATS);
        dateDispoField.setValue(LocalDate.now());
    }

    @FXML
    private void handleSelectImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sélectionner une image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif"));

        File file = fileChooser.showOpenDialog(titreField.getScene().getWindow());
        if (file != null) {
            selectedImagePath = file.getAbsolutePath();
            imagePreview.setImage(new Image(file.toURI().toString()));
            imagePlaceholder.setVisible(false);
            imagePathLabel.setText(file.getName());
        }
    }

    @FXML
    private void handlePublier() {
        // Validation
        if (titreField.getText().isEmpty()) {
            showError("Le titre est obligatoire");
            return;
        }
        if (typeCombo.getValue() == null) {
            showError("Sélectionnez un type");
            return;
        }
        if (prixField.getText().isEmpty()) {
            showError("Le prix est obligatoire");
            return;
        }
        if (localisationCombo.getValue() == null) {
            showError("Sélectionnez une localisation");
            return;
        }

        try {
            double prix = Double.parseDouble(prixField.getText());

            Annonce annonce = new Annonce(
                    titreField.getText(),
                    descriptionArea.getText(),
                    prix,
                    typeCombo.getValue(),
                    localisationCombo.getValue(),
                    dateDispoField.getValue(),
                    currentUserId);
            annonce.setImagePath(selectedImagePath);

            annonceService.add(annonce);
            showSuccess("✅ Annonce publiée avec succès !");

            // Retour à la liste après 1 seconde
            new Thread(() -> {
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                }
                javafx.application.Platform.runLater(this::handleRetour);
            }).start();

        } catch (NumberFormatException e) {
            showError("Prix invalide");
        } catch (Exception e) {
            showError("Erreur: " + e.getMessage());
        }
    }

    @FXML
    private void handleRetour() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/views/Marketplace.fxml"));
            Stage stage = (Stage) titreField.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showError(String msg) {
        messageLabel.setText("❌ " + msg);
        messageLabel.setStyle("-fx-text-fill: #c62828;");
    }

    private void showSuccess(String msg) {
        messageLabel.setText(msg);
        messageLabel.setStyle("-fx-text-fill: #2e7d32;");
    }
}
