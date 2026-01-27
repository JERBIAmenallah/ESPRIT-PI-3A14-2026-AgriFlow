package tn.esprit.agriflow.models.enums;

/**
 * Status of a listing
 */
public enum StatutAnnonce {
    DISPONIBLE("Disponible"),  // Available
    LOUE("Loué"),              // Rented
    VENDU("Vendu");            // Sold
    
    private final String displayName;
    
    StatutAnnonce(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    @Override
    public String toString() {
        return displayName;
    }
}
