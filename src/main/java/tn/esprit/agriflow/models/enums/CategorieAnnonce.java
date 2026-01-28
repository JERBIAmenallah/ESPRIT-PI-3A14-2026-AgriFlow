package tn.esprit.agriflow.models.enums;

/**
 * Category of equipment or product
 */
public enum CategorieAnnonce {
    TRACTEUR("Tracteur"),           // Tractor
    MOISSONNEUSE("Moissonneuse"),   // Harvester
    SEMENCES("Semences"),           // Seeds
    ENGRAIS("Engrais"),             // Fertilizer
    IRRIGATION("Irrigation"),       // Irrigation
    AUTRE("Autre");                 // Other
    
    private final String displayName;
    
    CategorieAnnonce(String displayName) {
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
