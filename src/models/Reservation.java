package models;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entité Reservation - Lie un agriculteur (acheteur/locataire) à une annonce.
 * Architecture P2P : Transaction directe sans validation admin.
 */
public class Reservation {

    private int id;
    private int annonceId; // FK vers Annonce
    private int clientId; // FK vers User (l'agriculteur qui réserve)
    private LocalDate dateDebut; // Date de début de location
    private LocalDate dateFin; // Date de fin de location (null si vente)
    private double montantTotal; // Prix final de la transaction
    private StatutReservation statut;
    private String commentaire; // Message optionnel du client
    private LocalDateTime dateCreation;

    /**
     * Statuts possibles pour une réservation.
     */
    public enum StatutReservation {
        EN_ATTENTE("En attente"), // Demande envoyée
        CONFIRMEE("Confirmée"), // Acceptée par le propriétaire
        ANNULEE("Annulée"), // Annulée par l'une des parties
        TERMINEE("Terminée"); // Location/Vente terminée

        private final String libelle;

        StatutReservation(String libelle) {
            this.libelle = libelle;
        }

        public String getLibelle() {
            return libelle;
        }

        @Override
        public String toString() {
            return libelle;
        }
    }

    // ==================== CONSTRUCTEURS ====================

    public Reservation() {
        this.statut = StatutReservation.EN_ATTENTE;
        this.dateCreation = LocalDateTime.now();
    }

    public Reservation(int annonceId, int clientId, LocalDate dateDebut,
            LocalDate dateFin, double montantTotal) {
        this();
        this.annonceId = annonceId;
        this.clientId = clientId;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.montantTotal = montantTotal;
    }

    // Constructeur complet (récupération DB)
    public Reservation(int id, int annonceId, int clientId, LocalDate dateDebut,
            LocalDate dateFin, double montantTotal,
            StatutReservation statut, String commentaire,
            LocalDateTime dateCreation) {
        this.id = id;
        this.annonceId = annonceId;
        this.clientId = clientId;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.montantTotal = montantTotal;
        this.statut = statut;
        this.commentaire = commentaire;
        this.dateCreation = dateCreation;
    }

    // ==================== GETTERS & SETTERS ====================

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

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public LocalDate getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(LocalDate dateDebut) {
        this.dateDebut = dateDebut;
    }

    public LocalDate getDateFin() {
        return dateFin;
    }

    public void setDateFin(LocalDate dateFin) {
        this.dateFin = dateFin;
    }

    public double getMontantTotal() {
        return montantTotal;
    }

    public void setMontantTotal(double montantTotal) {
        this.montantTotal = montantTotal;
    }

    public StatutReservation getStatut() {
        return statut;
    }

    public void setStatut(StatutReservation statut) {
        this.statut = statut;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    // ==================== MÉTHODES UTILITAIRES ====================

    /**
     * Confirme la réservation (action du propriétaire).
     */
    public void confirmer() {
        if (this.statut == StatutReservation.EN_ATTENTE) {
            this.statut = StatutReservation.CONFIRMEE;
        }
    }

    /**
     * Annule la réservation.
     */
    public void annuler() {
        if (this.statut != StatutReservation.TERMINEE) {
            this.statut = StatutReservation.ANNULEE;
        }
    }

    /**
     * Marque la réservation comme terminée.
     */
    public void terminer() {
        if (this.statut == StatutReservation.CONFIRMEE) {
            this.statut = StatutReservation.TERMINEE;
        }
    }

    /**
     * Calcule la durée de location en jours.
     */
    public long getDureeJours() {
        if (dateDebut != null && dateFin != null) {
            return java.time.temporal.ChronoUnit.DAYS.between(dateDebut, dateFin);
        }
        return 0;
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "id=" + id +
                ", annonceId=" + annonceId +
                ", clientId=" + clientId +
                ", dateDebut=" + dateDebut +
                ", dateFin=" + dateFin +
                ", montantTotal=" + montantTotal + " TND" +
                ", statut=" + statut +
                '}';
    }
}
