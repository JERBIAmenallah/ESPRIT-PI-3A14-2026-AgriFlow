package com.agriflow.marketplace.services;

import com.agriflow.marketplace.entities.Equipement;
import com.agriflow.marketplace.utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Service gérant les opérations CRUD sur les équipements agricoles.
 * Implémente l'interface IService pour standardiser les opérations.
 * 
 * @author AgriFlow Team
 * @version 1.0
 */
public class ServiceEquipement implements IService<Equipement> {

    // =========================================================================
    // Attributs
    // =========================================================================
    
    /** Connexion à la base de données */
    private final Connection connection;

    // =========================================================================
    // Constructeur
    // =========================================================================
    
    /**
     * Constructeur qui récupère la connexion via le Singleton MyDatabase.
     */
    public ServiceEquipement() {
        this.connection = MyDatabase.getInstance().getConnection();
    }

    // =========================================================================
    // Implémentation des méthodes CRUD
    // =========================================================================
    
    /**
     * Ajoute un nouvel équipement dans la base de données.
     * 
     * @param equipement L'équipement à ajouter
     * @throws SQLException En cas d'erreur SQL
     */
    @Override
    public void ajouter(Equipement equipement) throws SQLException {
        // Requête SQL avec PreparedStatement pour éviter les injections SQL
        String sql = "INSERT INTO equipement (nom, type, prix_location, disponibilite, id_agriculteur) VALUES (?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            // Définition des paramètres
            pstmt.setString(1, equipement.getNom());
            pstmt.setString(2, equipement.getType());
            pstmt.setDouble(3, equipement.getPrixLocation());
            pstmt.setBoolean(4, equipement.isDisponibilite());
            pstmt.setInt(5, equipement.getIdAgriculteur());
            
            // Exécution de la requête
            int rowsAffected = pstmt.executeUpdate();
            
            // Récupération de l'ID généré
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        equipement.setId(generatedKeys.getInt(1));
                    }
                }
                System.out.println("[ServiceEquipement] Équipement ajouté avec succès : " + equipement.getNom());
            }
        } catch (SQLException e) {
            System.err.println("[ServiceEquipement] Erreur lors de l'ajout de l'équipement : " + e.getMessage());
            throw e;
        }
    }

    /**
     * Modifie un équipement existant dans la base de données.
     * 
     * @param equipement L'équipement avec les nouvelles valeurs
     * @throws SQLException En cas d'erreur SQL
     */
    @Override
    public void modifier(Equipement equipement) throws SQLException {
        String sql = "UPDATE equipement SET nom = ?, type = ?, prix_location = ?, disponibilite = ?, id_agriculteur = ? WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, equipement.getNom());
            pstmt.setString(2, equipement.getType());
            pstmt.setDouble(3, equipement.getPrixLocation());
            pstmt.setBoolean(4, equipement.isDisponibilite());
            pstmt.setInt(5, equipement.getIdAgriculteur());
            pstmt.setInt(6, equipement.getId());
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("[ServiceEquipement] Équipement modifié avec succès : ID " + equipement.getId());
            } else {
                System.out.println("[ServiceEquipement] Aucun équipement trouvé avec l'ID : " + equipement.getId());
            }
        } catch (SQLException e) {
            System.err.println("[ServiceEquipement] Erreur lors de la modification de l'équipement : " + e.getMessage());
            throw e;
        }
    }

    /**
     * Supprime un équipement de la base de données.
     * 
     * @param id L'identifiant de l'équipement à supprimer
     * @throws SQLException En cas d'erreur SQL
     */
    @Override
    public void supprimer(int id) throws SQLException {
        String sql = "DELETE FROM equipement WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("[ServiceEquipement] Équipement supprimé avec succès : ID " + id);
            } else {
                System.out.println("[ServiceEquipement] Aucun équipement trouvé avec l'ID : " + id);
            }
        } catch (SQLException e) {
            System.err.println("[ServiceEquipement] Erreur lors de la suppression de l'équipement : " + e.getMessage());
            throw e;
        }
    }

    /**
     * Récupère tous les équipements de la base de données.
     * 
     * @return Liste de tous les équipements
     * @throws SQLException En cas d'erreur SQL
     */
    @Override
    public List<Equipement> afficher() throws SQLException {
        List<Equipement> equipements = new ArrayList<>();
        String sql = "SELECT * FROM equipement ORDER BY id";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Equipement equipement = new Equipement(
                    rs.getInt("id"),
                    rs.getString("nom"),
                    rs.getString("type"),
                    rs.getDouble("prix_location"),
                    rs.getBoolean("disponibilite"),
                    rs.getInt("id_agriculteur")
                );
                equipements.add(equipement);
            }
            
            System.out.println("[ServiceEquipement] " + equipements.size() + " équipement(s) récupéré(s).");
        } catch (SQLException e) {
            System.err.println("[ServiceEquipement] Erreur lors de la récupération des équipements : " + e.getMessage());
            throw e;
        }
        
        return equipements;
    }

    // =========================================================================
    // Méthodes supplémentaires
    // =========================================================================
    
    /**
     * Recherche un équipement par son identifiant.
     * 
     * @param id L'identifiant de l'équipement recherché
     * @return L'équipement trouvé ou null si non trouvé
     * @throws SQLException En cas d'erreur SQL
     */
    public Equipement rechercherParId(int id) throws SQLException {
        String sql = "SELECT * FROM equipement WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Equipement(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("type"),
                        rs.getDouble("prix_location"),
                        rs.getBoolean("disponibilite"),
                        rs.getInt("id_agriculteur")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("[ServiceEquipement] Erreur lors de la recherche de l'équipement : " + e.getMessage());
            throw e;
        }
        
        return null;
    }

    /**
     * Récupère tous les équipements disponibles à la location.
     * 
     * @return Liste des équipements disponibles
     * @throws SQLException En cas d'erreur SQL
     */
    public List<Equipement> afficherDisponibles() throws SQLException {
        List<Equipement> equipements = new ArrayList<>();
        String sql = "SELECT * FROM equipement WHERE disponibilite = TRUE ORDER BY id";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Equipement equipement = new Equipement(
                    rs.getInt("id"),
                    rs.getString("nom"),
                    rs.getString("type"),
                    rs.getDouble("prix_location"),
                    rs.getBoolean("disponibilite"),
                    rs.getInt("id_agriculteur")
                );
                equipements.add(equipement);
            }
            
            System.out.println("[ServiceEquipement] " + equipements.size() + " équipement(s) disponible(s) récupéré(s).");
        } catch (SQLException e) {
            System.err.println("[ServiceEquipement] Erreur lors de la récupération des équipements disponibles : " + e.getMessage());
            throw e;
        }
        
        return equipements;
    }
}
