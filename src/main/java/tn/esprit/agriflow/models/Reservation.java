package tn.esprit.agriflow.models;

import tn.esprit.agriflow.models.enums.StatutReservation;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Reservation entity - represents a P2P booking
 */
public class Reservation {
    private int id;
    private int annonceId;
    private int renterUserId;
    private LocalDate startDate;
    private LocalDate endDate;
    private StatutReservation status;
    private double totalPrice;
    private LocalDateTime createdAt;
    
    // Additional fields for displaying related entities (not in DB)
    private Annonce annonce;
    private User renter;
    
    // Constructors
    public Reservation() {
        this.status = StatutReservation.EN_ATTENTE;
    }
    
    public Reservation(int annonceId, int renterUserId, LocalDate startDate, 
                      LocalDate endDate, double totalPrice) {
        this.annonceId = annonceId;
        this.renterUserId = renterUserId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalPrice = totalPrice;
        this.status = StatutReservation.EN_ATTENTE;
    }
    
    // Getters and Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getAnnonceId() {
        return annonceId;
    }
    
    public void setAnnonceId(int annonceId) {
        this.annonceId = annonceId;
    }
    
    public int getRenterUserId() {
        return renterUserId;
    }
    
    public void setRenterUserId(int renterUserId) {
        this.renterUserId = renterUserId;
    }
    
    public LocalDate getStartDate() {
        return startDate;
    }
    
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }
    
    public LocalDate getEndDate() {
        return endDate;
    }
    
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
    
    public StatutReservation getStatus() {
        return status;
    }
    
    public void setStatus(StatutReservation status) {
        this.status = status;
    }
    
    public double getTotalPrice() {
        return totalPrice;
    }
    
    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public Annonce getAnnonce() {
        return annonce;
    }
    
    public void setAnnonce(Annonce annonce) {
        this.annonce = annonce;
    }
    
    public User getRenter() {
        return renter;
    }
    
    public void setRenter(User renter) {
        this.renter = renter;
    }
    
    @Override
    public String toString() {
        return "Reservation{" +
                "id=" + id +
                ", annonceId=" + annonceId +
                ", renterUserId=" + renterUserId +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", status=" + status +
                ", totalPrice=" + totalPrice +
                '}';
    }
}
