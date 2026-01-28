package tn.esprit.agriflow.models.enums;

/**
 * Type of listing - either rental or sale
 */
public enum TypeAnnonce {
    LOCATION("Location"),  // Rental
    VENTE("Vente");        // Sale
    
    private final String displayName;
    
    TypeAnnonce(String displayName) {
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
