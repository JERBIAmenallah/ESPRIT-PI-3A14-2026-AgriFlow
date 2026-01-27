package tn.esprit.agriflow.models.enums;

/**
 * Status of a reservation
 */
public enum StatutReservation {
    EN_ATTENTE("En attente"),  // Pending
    CONFIRMEE("Confirmée"),    // Confirmed
    ANNULEE("Annulée");        // Cancelled
    
    private final String displayName;
    
    StatutReservation(String displayName) {
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
