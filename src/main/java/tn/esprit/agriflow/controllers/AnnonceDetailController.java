package tn.esprit.agriflow.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import tn.esprit.agriflow.models.Annonce;
import tn.esprit.agriflow.models.Reservation;
import tn.esprit.agriflow.models.enums.StatutAnnonce;
import tn.esprit.agriflow.models.enums.TypeAnnonce;
import tn.esprit.agriflow.services.AnnonceService;
import tn.esprit.agriflow.services.ReservationService;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Controller for viewing annonce details and making reservations
 */
public class AnnonceDetailController {
    @FXML private Label titleLabel;
    @FXML private Label priceLabel;
    @FXML private Label typeLabel;
    @FXML private Label categoryLabel;
    @FXML private Label locationLabel;
    @FXML private Label statusLabel;
    @FXML private TextArea descriptionArea;
    @FXML private Label availabilityLabel;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private Label totalPriceLabel;
    @FXML private Button reserveButton;
    @FXML private Button closeButton;
    
    private Annonce annonce;
    private AnnonceService annonceService;
    private ReservationService reservationService;
    private int currentUserId;
    
    @FXML
    public void initialize() {
        annonceService = new AnnonceService();
        reservationService = new ReservationService();
        
        // Add listener to calculate total price when dates change
        startDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> calculateTotalPrice());
        endDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> calculateTotalPrice());
    }
    
    public void setAnnonce(Annonce annonce) {
        this.annonce = annonce;
        displayAnnonceDetails();
    }
    
    private void displayAnnonceDetails() {
        titleLabel.setText(annonce.getTitle());
        priceLabel.setText(String.format("%.2f TND", annonce.getPrice()));
        typeLabel.setText("Type: " + annonce.getType().getDisplayName());
        categoryLabel.setText("Catégorie: " + annonce.getCategory().getDisplayName());
        locationLabel.setText("Localisation: " + annonce.getLocation());
        statusLabel.setText("Statut: " + annonce.getStatus().getDisplayName());
        descriptionArea.setText(annonce.getDescription());
        
        // Set status color
        if (annonce.getStatus() == StatutAnnonce.DISPONIBLE) {
            statusLabel.setStyle("-fx-text-fill: green;");
        } else {
            statusLabel.setStyle("-fx-text-fill: red;");
        }
        
        // Show availability dates for rentals
        if (annonce.getAvailabilityStart() != null && annonce.getAvailabilityEnd() != null) {
            availabilityLabel.setText(String.format("Disponible du %s au %s",
                annonce.getAvailabilityStart(), annonce.getAvailabilityEnd()));
            
            startDatePicker.setDayCellFactory(picker -> new DateCell() {
                @Override
                public void updateItem(LocalDate date, boolean empty) {
                    super.updateItem(date, empty);
                    setDisable(empty || 
                              date.isBefore(annonce.getAvailabilityStart()) ||
                              date.isAfter(annonce.getAvailabilityEnd()));
                }
            });
            
            endDatePicker.setDayCellFactory(picker -> new DateCell() {
                @Override
                public void updateItem(LocalDate date, boolean empty) {
                    super.updateItem(date, empty);
                    setDisable(empty || 
                              date.isBefore(annonce.getAvailabilityStart()) ||
                              date.isAfter(annonce.getAvailabilityEnd()));
                }
            });
        } else {
            availabilityLabel.setText("Article à vendre");
            startDatePicker.setVisible(false);
            endDatePicker.setVisible(false);
            totalPriceLabel.setVisible(false);
        }
        
        // Disable reserve button if not available or if user is the owner
        if (annonce.getStatus() != StatutAnnonce.DISPONIBLE || annonce.getUserId() == currentUserId) {
            reserveButton.setDisable(true);
            if (annonce.getUserId() == currentUserId) {
                reserveButton.setText("Votre annonce");
            } else {
                reserveButton.setText("Non disponible");
            }
        }
    }
    
    private void calculateTotalPrice() {
        if (startDatePicker.getValue() != null && endDatePicker.getValue() != null) {
            LocalDate start = startDatePicker.getValue();
            LocalDate end = endDatePicker.getValue();
            
            if (end.isAfter(start)) {
                long days = ChronoUnit.DAYS.between(start, end);
                double total = days * annonce.getPrice();
                totalPriceLabel.setText(String.format("Prix total: %.2f TND (%d jours)", total, days));
            }
        }
    }
    
    @FXML
    private void handleReserve() {
        if (annonce.getType() == TypeAnnonce.LOCATION) {
            // Rental - need dates
            if (startDatePicker.getValue() == null || endDatePicker.getValue() == null) {
                showAlert("Validation", "Veuillez sélectionner les dates de location", Alert.AlertType.WARNING);
                return;
            }
            
            LocalDate start = startDatePicker.getValue();
            LocalDate end = endDatePicker.getValue();
            
            if (end.isBefore(start) || end.isEqual(start)) {
                showAlert("Validation", "La date de fin doit être après la date de début", Alert.AlertType.WARNING);
                return;
            }
            
            long days = ChronoUnit.DAYS.between(start, end);
            double totalPrice = days * annonce.getPrice();
            
            // Confirm reservation
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirmer la réservation");
            confirm.setHeaderText(String.format("Total: %.2f TND (%d jours)", totalPrice, days));
            confirm.setContentText("Voulez-vous confirmer cette réservation ?");
            
            if (confirm.showAndWait().get() == ButtonType.OK) {
                Reservation reservation = new Reservation(
                    annonce.getId(), currentUserId, start, end, totalPrice
                );
                
                if (reservationService.add(reservation)) {
                    showAlert("Succès", "Réservation effectuée avec succès!", Alert.AlertType.INFORMATION);
                    closeWindow();
                } else {
                    showAlert("Erreur", "Échec de la réservation", Alert.AlertType.ERROR);
                }
            }
        } else {
            // Sale - direct purchase
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirmer l'achat");
            confirm.setHeaderText(String.format("Prix: %.2f TND", annonce.getPrice()));
            confirm.setContentText("Voulez-vous confirmer cet achat ?");
            
            if (confirm.showAndWait().get() == ButtonType.OK) {
                Reservation reservation = new Reservation(
                    annonce.getId(), currentUserId, LocalDate.now(), LocalDate.now(), annonce.getPrice()
                );
                
                if (reservationService.add(reservation)) {
                    showAlert("Succès", "Achat effectué avec succès!", Alert.AlertType.INFORMATION);
                    closeWindow();
                } else {
                    showAlert("Erreur", "Échec de l'achat", Alert.AlertType.ERROR);
                }
            }
        }
    }
    
    @FXML
    private void handleClose() {
        closeWindow();
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
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }
}
