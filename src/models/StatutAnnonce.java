package models;

/**
 * Statuts possibles pour une annonce sur la Marketplace.
 * Gère le cycle de vie d'une annonce (Location ou Vente).
 */
public enum StatutAnnonce {
    DISPONIBLE("Disponible"), // Annonce active, peut être réservée
    RESERVE("Réservé"), // En attente de confirmation/paiement
    LOUE("Loué"), // Matériel actuellement en location
    VENDU("Vendu"), // Produit vendu (transaction terminée)
    INDISPONIBLE("Indisponible"); // Temporairement retiré par le propriétaire

    private final String libelle;

    StatutAnnonce(String libelle) {
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
