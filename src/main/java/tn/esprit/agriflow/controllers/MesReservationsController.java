package tn.esprit.agriflow.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import tn.esprit.agriflow.models.Reservation;
import tn.esprit.agriflow.models.enums.StatutReservation;
import tn.esprit.agriflow.services.ReservationService;

import java.time.LocalDate;

/**
 * Controller for managing user's reservations
 */
public class MesReservationsController {
    @FXML private TableView<Reservation> reservationsTable;
    @FXML private TableColumn<Reservation, Integer> idColumn;
    @FXML private TableColumn<Reservation, Integer> annonceIdColumn;
    @FXML private TableColumn<Reservation, LocalDate> startDateColumn;
    @FXML private TableColumn<Reservation, LocalDate> endDateColumn;
    @FXML private TableColumn<Reservation, Double> totalPriceColumn;
    @FXML private TableColumn<Reservation, String> statusColumn;
    @FXML private Button cancelButton;
    @FXML private Button refreshButton;
    
    private ReservationService reservationService;
    private int currentUserId;
    
    @FXML
    public void initialize() {
        reservationService = new ReservationService();
        
        // Configure table columns
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        annonceIdColumn.setCellValueFactory(new PropertyValueFactory<>("annonceId"));
        startDateColumn.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        endDateColumn.setCellValueFactory(new PropertyValueFactory<>("endDate"));
        totalPriceColumn.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
        
        statusColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getStatus().getDisplayName()));
        
        // Disable cancel button until selection
        cancelButton.setDisable(true);
        
        reservationsTable.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> {
                boolean canCancel = newSelection != null && 
                                   newSelection.getStatus() != StatutReservation.ANNULEE;
                cancelButton.setDisable(!canCancel);
            }
        );
    }
    
    public void setCurrentUserId(int userId) {
        this.currentUserId = userId;
        loadUserReservations();
    }
    
    private void loadUserReservations() {
        ObservableList<Reservation> reservations = FXCollections.observableArrayList(
            reservationService.getByRenterUserId(currentUserId)
        );
        reservationsTable.setItems(reservations);
    }
    
    @FXML
    private void handleCancel() {
        Reservation selected = reservationsTable.getSelectionModel().getSelectedItem();
        if (selected != null && selected.getStatus() != StatutReservation.ANNULEE) {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirmer l'annulation");
            confirm.setHeaderText("Annuler la réservation #" + selected.getId());
            confirm.setContentText("Voulez-vous annuler cette réservation ?");
            
            if (confirm.showAndWait().get() == ButtonType.OK) {
                if (reservationService.cancelReservation(selected.getId())) {
                    showAlert("Succès", "Réservation annulée avec succès", Alert.AlertType.INFORMATION);
                    loadUserReservations();
                } else {
                    showAlert("Erreur", "Échec de l'annulation", Alert.AlertType.ERROR);
                }
            }
        }
    }
    
    @FXML
    private void handleRefresh() {
        loadUserReservations();
    }
    
    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
