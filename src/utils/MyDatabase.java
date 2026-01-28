package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Singleton pour la connexion à la base de données MySQL.
 * Base de données AGRIFLOW partagée avec toute l'équipe TeamSpark.
 */
public class MyDatabase {

    // ==================== CONFIGURATION ====================
    // À adapter selon votre configuration locale
    private static final String URL = "jdbc:mysql://localhost:3306/agriflow";
    private static final String USER = "root";
    private static final String PASSWORD = ""; // Mot de passe MySQL (vide par défaut sur XAMPP)

    // ==================== SINGLETON ====================
    private static MyDatabase instance;
    private Connection connection;

    /**
     * Constructeur privé - Pattern Singleton.
     */
    private MyDatabase() {
        try {
            // Chargement du driver MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Établissement de la connexion
            this.connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("✅ Connexion à la base de données AGRIFLOW réussie !");

        } catch (ClassNotFoundException e) {
            System.err.println("❌ Driver MySQL non trouvé. Ajoutez mysql-connector-java au classpath.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("❌ Erreur de connexion à la base de données.");
            System.err.println("   Vérifiez que MySQL est démarré et que la base 'agriflow' existe.");
            e.printStackTrace();
        }
    }

    /**
     * Retourne l'instance unique de MyDatabase.
     */
    public static MyDatabase getInstance() {
        if (instance == null) {
            instance = new MyDatabase();
        }
        return instance;
    }

    /**
     * Retourne la connexion active.
     */
    public Connection getConnection() {
        try {
            // Vérifie si la connexion est toujours valide
            if (connection == null || connection.isClosed()) {
                // Reconnexion automatique
                this.connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("🔄 Reconnexion à la base de données effectuée.");
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la vérification/reconnexion.");
            e.printStackTrace();
        }
        return connection;
    }

    /**
     * Ferme proprement la connexion (à appeler à la fermeture de l'application).
     */
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("🔒 Connexion à la base de données fermée.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
