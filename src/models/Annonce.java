package models;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entité Annonce - Représente une offre de location ou vente sur la
 * Marketplace.
 * Architecture P2P : Un agriculteur publie, un autre réserve directement.
 */
public class Annonce {

    private int id;
    private String titre;
    private String description;
    private double prix;
    private TypeAnnonce type;
    private StatutAnnonce statut;
    private String imagePath; // Chemin vers l'image de l'annonce
    private String localisation; // Gouvernorat / Ville en Tunisie
    private LocalDate dateDisponibilite; // Date à partir de laquelle c'est dispo
    private LocalDate dateFinDisponibilite; // Date de fin (pour location)
    private LocalDateTime dateCreation;
    private LocalDateTime dateModification;

    // Clé étrangère vers User (géré par Ayoub)
    private int proprietaireId; // ID de l'agriculteur qui poste l'annonce

    // ==================== CONSTRUCTEURS ====================

    public Annonce() {
        this.statut = StatutAnnonce.DISPONIBLE;
        this.dateCreation = LocalDateTime.now();
        this.dateModification = LocalDateTime.now();
    }

    public Annonce(String titre, String description, double prix, TypeAnnonce type,
            String localisation, LocalDate dateDisponibilite, int proprietaireId) {
        this();
        this.titre = titre;
        this.description = description;
        this.prix = prix;
        this.type = type;
        this.localisation = localisation;
        this.dateDisponibilite = dateDisponibilite;
        this.proprietaireId = proprietaireId;
    }

    // Constructeur complet (pour récupération depuis DB)
    public Annonce(int id, String titre, String description, double prix,
            TypeAnnonce type, StatutAnnonce statut, String imagePath,
            String localisation, LocalDate dateDisponibilite,
            LocalDate dateFinDisponibilite, LocalDateTime dateCreation,
            LocalDateTime dateModification, int proprietaireId) {
        this.id = id;
        this.titre = titre;
        this.description = description;
        this.prix = prix;
        this.type = type;
        this.statut = statut;
        this.imagePath = imagePath;
        this.localisation = localisation;
        this.dateDisponibilite = dateDisponibilite;
        this.dateFinDisponibilite = dateFinDisponibilite;
        this.dateCreation = dateCreation;
        this.dateModification = dateModification;
        this.proprietaireId = proprietaireId;
    }

    // ==================== GETTERS & SETTERS ====================

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
        this.dateModification = LocalDateTime.now();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
        this.dateModification = LocalDateTime.now();
    }

    public double getPrix() {
        return prix;
    }

    public void setPrix(double prix) {
        this.prix = prix;
        this.dateModification = LocalDateTime.now();
    }

    public TypeAnnonce getType() {
        return type;
    }

    public void setType(TypeAnnonce type) {
        this.type = type;
        this.dateModification = LocalDateTime.now();
    }

    public StatutAnnonce getStatut() {
        return statut;
    }

    public void setStatut(StatutAnnonce statut) {
        this.statut = statut;
        this.dateModification = LocalDateTime.now();
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
        this.dateModification = LocalDateTime.now();
    }

    public String getLocalisation() {
        return localisation;
    }

    public void setLocalisation(String localisation) {
        this.localisation = localisation;
        this.dateModification = LocalDateTime.now();
    }

    public LocalDate getDateDisponibilite() {
        return dateDisponibilite;
    }

    public void setDateDisponibilite(LocalDate dateDisponibilite) {
        this.dateDisponibilite = dateDisponibilite;
        this.dateModification = LocalDateTime.now();
    }

    public LocalDate getDateFinDisponibilite() {
        return dateFinDisponibilite;
    }

    public void setDateFinDisponibilite(LocalDate dateFinDisponibilite) {
        this.dateFinDisponibilite = dateFinDisponibilite;
        this.dateModification = LocalDateTime.now();
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    public LocalDateTime getDateModification() {
        return dateModification;
    }

    public void setDateModification(LocalDateTime dateModification) {
        this.dateModification = dateModification;
    }

    public int getProprietaireId() {
        return proprietaireId;
    }

    public void setProprietaireId(int proprietaireId) {
        this.proprietaireId = proprietaireId;
    }

    // ==================== MÉTHODES UTILITAIRES ====================

    /**
     * Vérifie si l'annonce est disponible pour réservation.
     */
    public boolean isDisponible() {
        return this.statut == StatutAnnonce.DISPONIBLE;
    }

    /**
     * Marque l'annonce comme réservée (P2P : action directe de l'acheteur).
     */
    public void reserver() {
        if (isDisponible()) {
            this.statut = StatutAnnonce.RESERVE;
            this.dateModification = LocalDateTime.now();
        }
    }

    /**
     * Confirme la location (après accord entre agriculteurs).
     */
    public void confirmerLocation() {
        if (this.statut == StatutAnnonce.RESERVE) {
            this.statut = StatutAnnonce.LOUE;
            this.dateModification = LocalDateTime.now();
        }
    }

    /**
     * Confirme la vente (produit vendu).
     */
    public void confirmerVente() {
        if (this.statut == StatutAnnonce.RESERVE) {
            this.statut = StatutAnnonce.VENDU;
            this.dateModification = LocalDateTime.now();
        }
    }

    /**
     * Remet l'annonce en disponible (annulation ou fin de location).
     */
    public void liberer() {
        this.statut = StatutAnnonce.DISPONIBLE;
        this.dateModification = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "Annonce{" +
                "id=" + id +
                ", titre='" + titre + '\'' +
                ", type=" + type +
                ", prix=" + prix + " TND" +
                ", statut=" + statut +
                ", localisation='" + localisation + '\'' +
                ", proprietaireId=" + proprietaireId +
                '}';
    }
}
