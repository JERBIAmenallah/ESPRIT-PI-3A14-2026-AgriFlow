package models;

/**
 * Types d'annonces disponibles sur la Marketplace AGRIFLOW.
 * Catégorisation du matériel agricole et des produits.
 */
public enum TypeAnnonce {
    // Matériel Agricole - Location
    TRACTEUR("Tracteur"),
    MOISSONNEUSE("Moissonneuse-batteuse"),
    SEMOIR("Semoir"),
    PULVERISATEUR("Pulvérisateur"),
    REMORQUE("Remorque agricole"),
    IRRIGATION("Système d'irrigation"),
    
    // Produits - Vente
    SEMENCES("Semences"),
    ENGRAIS("Engrais"),
    PRODUIT_RECOLTE("Produit de récolte"),
    AUTRE("Autre");

    private final String libelle;

    TypeAnnonce(String libelle) {
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
