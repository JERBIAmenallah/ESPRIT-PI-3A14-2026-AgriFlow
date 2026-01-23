package com.agriflow.marketplace.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Classe de connexion à la base de données MySQL.
 * Implémente le pattern Singleton pour garantir une seule instance de connexion.
 * 
 * Note : Dans un environnement de production, les paramètres de connexion
 * devraient être externalisés dans un fichier de configuration ou des
 * variables d'environnement.
 * 
 * @author AgriFlow Team
 * @version 1.0
 */
public class MyDatabase {

    // =========================================================================
    // Attributs de connexion
    // =========================================================================
    
    /** URL de connexion à la base de données (configurable via variable d'environnement) */
    private static final String URL = System.getenv("AGRIFLOW_DB_URL") != null 
            ? System.getenv("AGRIFLOW_DB_URL") 
            : "jdbc:mysql://localhost:3306/agriflow_marketplace";
    
    /** Nom d'utilisateur pour la connexion (configurable via variable d'environnement) */
    private static final String USER = System.getenv("AGRIFLOW_DB_USER") != null 
            ? System.getenv("AGRIFLOW_DB_USER") 
            : "root";
    
    /** Mot de passe pour la connexion (configurable via variable d'environnement) */
    private static final String PASSWORD = System.getenv("AGRIFLOW_DB_PASSWORD") != null 
            ? System.getenv("AGRIFLOW_DB_PASSWORD") 
            : "";
    
    // =========================================================================
    // Instance Singleton (Initialization-on-demand holder idiom)
    // =========================================================================
    
    /** Holder interne pour l'initialisation paresseuse thread-safe */
    private static class SingletonHolder {
        private static final MyDatabase INSTANCE = new MyDatabase();
    }
    
    /** Objet de connexion JDBC */
    private Connection connection;

    // =========================================================================
    // Constructeur privé (Singleton)
    // =========================================================================
    
    /**
     * Constructeur privé pour empêcher l'instanciation directe.
     * Établit la connexion à la base de données MySQL.
     */
    private MyDatabase() {
        try {
            // Établissement de la connexion (le driver est chargé automatiquement depuis JDBC 4.0)
            this.connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("[MyDatabase] Connexion à la base de données établie avec succès.");
            
        } catch (SQLException e) {
            System.err.println("[MyDatabase] Erreur : Impossible de se connecter à la base de données.");
            e.printStackTrace();
        }
    }

    // =========================================================================
    // Méthodes publiques
    // =========================================================================
    
    /**
     * Retourne l'instance unique de la classe (Singleton).
     * Utilise le pattern Initialization-on-demand holder pour une initialisation
     * paresseuse et thread-safe sans synchronisation explicite.
     * 
     * @return L'instance unique de MyDatabase
     */
    public static MyDatabase getInstance() {
        return SingletonHolder.INSTANCE;
    }

    /**
     * Retourne l'objet Connection pour effectuer des requêtes SQL.
     * Vérifie si la connexion est toujours valide et tente de la reconnecter si nécessaire.
     * 
     * @return L'objet Connection JDBC
     */
    public Connection getConnection() {
        try {
            // Vérification de la validité de la connexion (timeout de 5 secondes)
            if (this.connection == null || this.connection.isClosed() || !this.connection.isValid(5)) {
                System.out.println("[MyDatabase] Reconnexion à la base de données...");
                this.connection = DriverManager.getConnection(URL, USER, PASSWORD);
            }
        } catch (SQLException e) {
            System.err.println("[MyDatabase] Erreur lors de la vérification/reconnexion : " + e.getMessage());
        }
        return this.connection;
    }

    /**
     * Ferme la connexion à la base de données.
     * À appeler lors de la fermeture de l'application.
     */
    public void closeConnection() {
        if (this.connection != null) {
            try {
                this.connection.close();
                System.out.println("[MyDatabase] Connexion à la base de données fermée.");
            } catch (SQLException e) {
                System.err.println("[MyDatabase] Erreur lors de la fermeture de la connexion.");
                e.printStackTrace();
            }
        }
    }
}
