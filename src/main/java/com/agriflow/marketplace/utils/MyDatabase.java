package com.agriflow.marketplace.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Classe de connexion à la base de données MySQL.
 * Implémente le pattern Singleton pour garantir une seule instance de connexion.
 * 
 * @author AgriFlow Team
 * @version 1.0
 */
public class MyDatabase {

    // =========================================================================
    // Attributs de connexion
    // =========================================================================
    
    /** URL de connexion à la base de données */
    private static final String URL = "jdbc:mysql://localhost:3306/agriflow_marketplace";
    
    /** Nom d'utilisateur pour la connexion */
    private static final String USER = "root";
    
    /** Mot de passe pour la connexion */
    private static final String PASSWORD = "";
    
    // =========================================================================
    // Instance Singleton
    // =========================================================================
    
    /** Instance unique de la classe */
    private static MyDatabase instance;
    
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
            // Chargement du driver MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Établissement de la connexion
            this.connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("[MyDatabase] Connexion à la base de données établie avec succès.");
            
        } catch (ClassNotFoundException e) {
            System.err.println("[MyDatabase] Erreur : Driver MySQL introuvable.");
            e.printStackTrace();
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
     * Crée l'instance si elle n'existe pas encore.
     * 
     * @return L'instance unique de MyDatabase
     */
    public static synchronized MyDatabase getInstance() {
        if (instance == null) {
            instance = new MyDatabase();
        }
        return instance;
    }

    /**
     * Retourne l'objet Connection pour effectuer des requêtes SQL.
     * 
     * @return L'objet Connection JDBC
     */
    public Connection getConnection() {
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
