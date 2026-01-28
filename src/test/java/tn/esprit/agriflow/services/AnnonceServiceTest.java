package tn.esprit.agriflow.services;

import org.junit.jupiter.api.Test;
import tn.esprit.agriflow.models.Annonce;
import tn.esprit.agriflow.models.enums.CategorieAnnonce;
import tn.esprit.agriflow.models.enums.StatutAnnonce;
import tn.esprit.agriflow.models.enums.TypeAnnonce;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Basic unit tests for AnnonceService
 * Note: These tests require a running MySQL database with the schema loaded
 */
public class AnnonceServiceTest {
    
    @Test
    public void testAnnonceCreation() {
        // Test creating an Annonce object
        Annonce annonce = new Annonce(
            "Test Tracteur",
            "Tracteur de test pour l'unité de test",
            150.0,
            TypeAnnonce.LOCATION,
            CategorieAnnonce.TRACTEUR,
            "Tunis",
            1
        );
        
        assertNotNull(annonce);
        assertEquals("Test Tracteur", annonce.getTitle());
        assertEquals(150.0, annonce.getPrice());
        assertEquals(TypeAnnonce.LOCATION, annonce.getType());
        assertEquals(CategorieAnnonce.TRACTEUR, annonce.getCategory());
        assertEquals(StatutAnnonce.DISPONIBLE, annonce.getStatus());
    }
    
    @Test
    public void testAnnonceSettersAndGetters() {
        Annonce annonce = new Annonce();
        
        annonce.setTitle("Moissonneuse");
        annonce.setDescription("Test description");
        annonce.setPrice(8500.0);
        annonce.setType(TypeAnnonce.VENTE);
        annonce.setCategory(CategorieAnnonce.MOISSONNEUSE);
        annonce.setLocation("Sfax");
        annonce.setStatus(StatutAnnonce.DISPONIBLE);
        annonce.setAvailabilityStart(LocalDate.now());
        annonce.setAvailabilityEnd(LocalDate.now().plusDays(30));
        annonce.setUserId(1);
        
        assertEquals("Moissonneuse", annonce.getTitle());
        assertEquals("Test description", annonce.getDescription());
        assertEquals(8500.0, annonce.getPrice());
        assertEquals(TypeAnnonce.VENTE, annonce.getType());
        assertEquals(CategorieAnnonce.MOISSONNEUSE, annonce.getCategory());
        assertEquals("Sfax", annonce.getLocation());
        assertEquals(StatutAnnonce.DISPONIBLE, annonce.getStatus());
        assertEquals(1, annonce.getUserId());
        assertNotNull(annonce.getAvailabilityStart());
        assertNotNull(annonce.getAvailabilityEnd());
    }
    
    @Test
    public void testEnumValues() {
        // Test TypeAnnonce enum
        assertEquals("Location", TypeAnnonce.LOCATION.getDisplayName());
        assertEquals("Vente", TypeAnnonce.VENTE.getDisplayName());
        
        // Test CategorieAnnonce enum
        assertEquals("Tracteur", CategorieAnnonce.TRACTEUR.getDisplayName());
        assertEquals("Moissonneuse", CategorieAnnonce.MOISSONNEUSE.getDisplayName());
        
        // Test StatutAnnonce enum
        assertEquals("Disponible", StatutAnnonce.DISPONIBLE.getDisplayName());
        assertEquals("Loué", StatutAnnonce.LOUE.getDisplayName());
        assertEquals("Vendu", StatutAnnonce.VENDU.getDisplayName());
    }
}
