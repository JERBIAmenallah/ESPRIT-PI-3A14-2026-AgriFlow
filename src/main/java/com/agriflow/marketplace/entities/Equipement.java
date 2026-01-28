package com.agriflow.marketplace.entities;

/**
 * Entité représentant un équipement agricole disponible à la location.
 * Correspond à la table 'equipement' dans la base de données.
 * 
 * @author AgriFlow Team
 * @version 1.0
 */
public class Equipement {

    // =========================================================================
    // Attributs
    // =========================================================================
    
    /** Identifiant unique de l'équipement (clé primaire) */
    private int id;
    
    /** Nom de l'équipement */
    private String nom;
    
    /** Type de matériel (Tracteur, Moissonneuse, etc.) */
    private String type;
    
    /** Prix de location journalier en euros */
    private double prixLocation;
    
    /** Indique si l'équipement est disponible à la location */
    private boolean disponibilite;
    
    /** Identifiant du propriétaire (agriculteur) */
    private int idAgriculteur;

    // =========================================================================
    // Constructeurs
    // =========================================================================
    
    /**
     * Constructeur par défaut.
     */
    public Equipement() {
    }

    /**
     * Constructeur sans l'identifiant (pour l'ajout d'un nouvel équipement).
     * 
     * @param nom           Nom de l'équipement
     * @param type          Type de matériel
     * @param prixLocation  Prix de location journalier
     * @param disponibilite Disponibilité de l'équipement
     * @param idAgriculteur Identifiant du propriétaire
     */
    public Equipement(String nom, String type, double prixLocation, boolean disponibilite, int idAgriculteur) {
        this.nom = nom;
        this.type = type;
        this.prixLocation = prixLocation;
        this.disponibilite = disponibilite;
        this.idAgriculteur = idAgriculteur;
    }

    /**
     * Constructeur complet avec tous les attributs.
     * 
     * @param id            Identifiant unique
     * @param nom           Nom de l'équipement
     * @param type          Type de matériel
     * @param prixLocation  Prix de location journalier
     * @param disponibilite Disponibilité de l'équipement
     * @param idAgriculteur Identifiant du propriétaire
     */
    public Equipement(int id, String nom, String type, double prixLocation, boolean disponibilite, int idAgriculteur) {
        this.id = id;
        this.nom = nom;
        this.type = type;
        this.prixLocation = prixLocation;
        this.disponibilite = disponibilite;
        this.idAgriculteur = idAgriculteur;
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

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getPrixLocation() {
        return prixLocation;
    }

    public void setPrixLocation(double prixLocation) {
        this.prixLocation = prixLocation;
    }

    public boolean isDisponibilite() {
        return disponibilite;
    }

    public void setDisponibilite(boolean disponibilite) {
        this.disponibilite = disponibilite;
    }

    public int getIdAgriculteur() {
        return idAgriculteur;
    }

    public void setIdAgriculteur(int idAgriculteur) {
        this.idAgriculteur = idAgriculteur;
    }

    // =========================================================================
    // Méthode toString
    // =========================================================================
    
    /**
     * Retourne une représentation textuelle de l'équipement.
     * 
     * @return Chaîne de caractères décrivant l'équipement
     */
    @Override
    public String toString() {
        return "Equipement{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", type='" + type + '\'' +
                ", prixLocation=" + prixLocation + " €/jour" +
                ", disponibilite=" + (disponibilite ? "Oui" : "Non") +
                ", idAgriculteur=" + idAgriculteur +
                '}';
    }
}
