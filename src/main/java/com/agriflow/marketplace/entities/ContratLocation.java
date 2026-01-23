package com.agriflow.marketplace.entities;

import java.time.LocalDate;

/**
 * Entité représentant un contrat de location de matériel agricole.
 * Correspond à la table 'contrat_location' dans la base de données.
 * 
 * @author AgriFlow Team
 * @version 1.0
 */
public class ContratLocation {

    // =========================================================================
    // Attributs
    // =========================================================================
    
    /** Identifiant unique du contrat (clé primaire) */
    private int id;
    
    /** Date de début de la location */
    private LocalDate dateDebut;
    
    /** Date de fin de la location */
    private LocalDate dateFin;
    
    /** Statut du contrat (EN_ATTENTE, ACTIF, TERMINE, ANNULE) */
    private String statut;
    
    /** Identifiant de l'équipement loué (clé étrangère) */
    private int idEquipement;
    
    /** Identifiant de l'agriculteur locataire */
    private int idLocataire;

    // =========================================================================
    // Constantes pour les statuts
    // =========================================================================
    
    /** Contrat en attente de validation */
    public static final String STATUT_EN_ATTENTE = "EN_ATTENTE";
    
    /** Contrat actif (location en cours) */
    public static final String STATUT_ACTIF = "ACTIF";
    
    /** Contrat terminé */
    public static final String STATUT_TERMINE = "TERMINE";
    
    /** Contrat annulé */
    public static final String STATUT_ANNULE = "ANNULE";

    // =========================================================================
    // Constructeurs
    // =========================================================================
    
    /**
     * Constructeur par défaut.
     */
    public ContratLocation() {
    }

    /**
     * Constructeur sans l'identifiant (pour l'ajout d'un nouveau contrat).
     * 
     * @param dateDebut    Date de début de la location
     * @param dateFin      Date de fin de la location
     * @param statut       Statut du contrat
     * @param idEquipement Identifiant de l'équipement loué
     * @param idLocataire  Identifiant du locataire
     */
    public ContratLocation(LocalDate dateDebut, LocalDate dateFin, String statut, int idEquipement, int idLocataire) {
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.statut = statut;
        this.idEquipement = idEquipement;
        this.idLocataire = idLocataire;
    }

    /**
     * Constructeur complet avec tous les attributs.
     * 
     * @param id           Identifiant unique du contrat
     * @param dateDebut    Date de début de la location
     * @param dateFin      Date de fin de la location
     * @param statut       Statut du contrat
     * @param idEquipement Identifiant de l'équipement loué
     * @param idLocataire  Identifiant du locataire
     */
    public ContratLocation(int id, LocalDate dateDebut, LocalDate dateFin, String statut, int idEquipement, int idLocataire) {
        this.id = id;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.statut = statut;
        this.idEquipement = idEquipement;
        this.idLocataire = idLocataire;
    }

    // =========================================================================
    // Getters et Setters
    // =========================================================================
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public int getIdEquipement() {
        return idEquipement;
    }

    public void setIdEquipement(int idEquipement) {
        this.idEquipement = idEquipement;
    }

    public int getIdLocataire() {
        return idLocataire;
    }

    public void setIdLocataire(int idLocataire) {
        this.idLocataire = idLocataire;
    }

    // =========================================================================
    // Méthode toString
    // =========================================================================
    
    /**
     * Retourne une représentation textuelle du contrat de location.
     * 
     * @return Chaîne de caractères décrivant le contrat
     */
    @Override
    public String toString() {
        return "ContratLocation{" +
                "id=" + id +
                ", dateDebut=" + dateDebut +
                ", dateFin=" + dateFin +
                ", statut='" + statut + '\'' +
                ", idEquipement=" + idEquipement +
                ", idLocataire=" + idLocataire +
                '}';
    }
}
