package com.agriflow.marketplace;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import com.agriflow.marketplace.utils.MyDatabase;

/**
 * Classe principale de l'application AgriFlow Marketplace.
 * Point d'entrée de l'application JavaFX.
 * 
 * @author AgriFlow Team
 * @version 1.0
 */
public class MainApp extends Application {

    // =========================================================================
    // Constantes
    // =========================================================================
    
    /** Titre de l'application */
    private static final String APP_TITLE = "AgriFlow - Marketplace de Location";
    
    /** Largeur de la fenêtre */
    private static final int WINDOW_WIDTH = 1200;
    
    /** Hauteur de la fenêtre */
    private static final int WINDOW_HEIGHT = 700;

    // =========================================================================
    // Méthode principale
    // =========================================================================
    
    /**
     * Point d'entrée de l'application.
     * 
     * @param args Arguments de la ligne de commande
     */
    public static void main(String[] args) {
        // Initialisation de la connexion à la base de données
        System.out.println("[MainApp] Démarrage de l'application AgriFlow Marketplace...");
        
        // Lancement de l'application JavaFX
        launch(args);
    }

    // =========================================================================
    // Méthodes JavaFX
    // =========================================================================
    
    /**
     * Méthode de démarrage de l'application JavaFX.
     * Charge le fichier FXML et affiche la fenêtre principale.
     * 
     * @param primaryStage La scène principale de l'application
     * @throws Exception En cas d'erreur lors du chargement
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            // Chargement du fichier FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/agriflow/marketplace/views/Marketplace.fxml"));
            Parent root = loader.load();
            
            // Création de la scène
            Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
            
            // Configuration de la fenêtre
            primaryStage.setTitle(APP_TITLE);
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(900);
            primaryStage.setMinHeight(600);
            
            // Affichage de la fenêtre
            primaryStage.show();
            
            System.out.println("[MainApp] Application démarrée avec succès.");
            
        } catch (Exception e) {
            System.err.println("[MainApp] Erreur lors du démarrage de l'application : " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Méthode appelée lors de la fermeture de l'application.
     * Ferme proprement la connexion à la base de données.
     */
    @Override
    public void stop() {
        System.out.println("[MainApp] Fermeture de l'application...");
        MyDatabase.getInstance().closeConnection();
        System.out.println("[MainApp] Application fermée.");
    }
}
