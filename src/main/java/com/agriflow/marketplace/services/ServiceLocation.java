package com.agriflow.marketplace.services;

import com.agriflow.marketplace.entities.ContratLocation;
import com.agriflow.marketplace.utils.MyDatabase;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Service gérant les opérations CRUD sur les contrats de location.
 * Implémente l'interface IService pour standardiser les opérations.
 * 
 * @author AgriFlow Team
 * @version 1.0
 */
public class ServiceLocation implements IService<ContratLocation> {

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
    public ServiceLocation() {
        this.connection = MyDatabase.getInstance().getConnection();
    }

    // =========================================================================
    // Implémentation des méthodes CRUD
    // =========================================================================
    
    /**
     * Ajoute un nouveau contrat de location dans la base de données.
     * 
     * @param contrat Le contrat à ajouter
     * @throws SQLException En cas d'erreur SQL
     */
    @Override
    public void ajouter(ContratLocation contrat) throws SQLException {
        String sql = "INSERT INTO contrat_location (date_debut, date_fin, statut, id_equipement, id_locataire) VALUES (?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            // Définition des paramètres
            pstmt.setDate(1, Date.valueOf(contrat.getDateDebut()));
            pstmt.setDate(2, Date.valueOf(contrat.getDateFin()));
            pstmt.setString(3, contrat.getStatut());
            pstmt.setInt(4, contrat.getIdEquipement());
            pstmt.setInt(5, contrat.getIdLocataire());
            
            // Exécution de la requête
            int rowsAffected = pstmt.executeUpdate();
            
            // Récupération de l'ID généré
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        contrat.setId(generatedKeys.getInt(1));
                    }
                }
                System.out.println("[ServiceLocation] Contrat ajouté avec succès : ID " + contrat.getId());
            }
        } catch (SQLException e) {
            System.err.println("[ServiceLocation] Erreur lors de l'ajout du contrat : " + e.getMessage());
            throw e;
        }
    }

    /**
     * Modifie un contrat existant dans la base de données.
     * 
     * @param contrat Le contrat avec les nouvelles valeurs
     * @throws SQLException En cas d'erreur SQL
     */
    @Override
    public void modifier(ContratLocation contrat) throws SQLException {
        String sql = "UPDATE contrat_location SET date_debut = ?, date_fin = ?, statut = ?, id_equipement = ?, id_locataire = ? WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setDate(1, Date.valueOf(contrat.getDateDebut()));
            pstmt.setDate(2, Date.valueOf(contrat.getDateFin()));
            pstmt.setString(3, contrat.getStatut());
            pstmt.setInt(4, contrat.getIdEquipement());
            pstmt.setInt(5, contrat.getIdLocataire());
            pstmt.setInt(6, contrat.getId());
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("[ServiceLocation] Contrat modifié avec succès : ID " + contrat.getId());
            } else {
                System.out.println("[ServiceLocation] Aucun contrat trouvé avec l'ID : " + contrat.getId());
            }
        } catch (SQLException e) {
            System.err.println("[ServiceLocation] Erreur lors de la modification du contrat : " + e.getMessage());
            throw e;
        }
    }

    /**
     * Supprime un contrat de la base de données.
     * 
     * @param id L'identifiant du contrat à supprimer
     * @throws SQLException En cas d'erreur SQL
     */
    @Override
    public void supprimer(int id) throws SQLException {
        String sql = "DELETE FROM contrat_location WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("[ServiceLocation] Contrat supprimé avec succès : ID " + id);
            } else {
                System.out.println("[ServiceLocation] Aucun contrat trouvé avec l'ID : " + id);
            }
        } catch (SQLException e) {
            System.err.println("[ServiceLocation] Erreur lors de la suppression du contrat : " + e.getMessage());
            throw e;
        }
    }

    /**
     * Récupère tous les contrats de location de la base de données.
     * 
     * @return Liste de tous les contrats
     * @throws SQLException En cas d'erreur SQL
     */
    @Override
    public List<ContratLocation> afficher() throws SQLException {
        List<ContratLocation> contrats = new ArrayList<>();
        String sql = "SELECT * FROM contrat_location ORDER BY id";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                ContratLocation contrat = new ContratLocation(
                    rs.getInt("id"),
                    rs.getDate("date_debut").toLocalDate(),
                    rs.getDate("date_fin").toLocalDate(),
                    rs.getString("statut"),
                    rs.getInt("id_equipement"),
                    rs.getInt("id_locataire")
                );
                contrats.add(contrat);
            }
            
            System.out.println("[ServiceLocation] " + contrats.size() + " contrat(s) récupéré(s).");
        } catch (SQLException e) {
            System.err.println("[ServiceLocation] Erreur lors de la récupération des contrats : " + e.getMessage());
            throw e;
        }
        
        return contrats;
    }

    // =========================================================================
    // Méthodes supplémentaires
    // =========================================================================
    
    /**
     * Recherche un contrat par son identifiant.
     * 
     * @param id L'identifiant du contrat recherché
     * @return Le contrat trouvé ou null si non trouvé
     * @throws SQLException En cas d'erreur SQL
     */
    public ContratLocation rechercherParId(int id) throws SQLException {
        String sql = "SELECT * FROM contrat_location WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new ContratLocation(
                        rs.getInt("id"),
                        rs.getDate("date_debut").toLocalDate(),
                        rs.getDate("date_fin").toLocalDate(),
                        rs.getString("statut"),
                        rs.getInt("id_equipement"),
                        rs.getInt("id_locataire")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("[ServiceLocation] Erreur lors de la recherche du contrat : " + e.getMessage());
            throw e;
        }
        
        return null;
    }

    /**
     * Récupère tous les contrats actifs.
     * 
     * @return Liste des contrats actifs
     * @throws SQLException En cas d'erreur SQL
     */
    public List<ContratLocation> afficherContratsActifs() throws SQLException {
        List<ContratLocation> contrats = new ArrayList<>();
        String sql = "SELECT * FROM contrat_location WHERE statut = ? ORDER BY date_debut";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, ContratLocation.STATUT_ACTIF);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    ContratLocation contrat = new ContratLocation(
                        rs.getInt("id"),
                        rs.getDate("date_debut").toLocalDate(),
                        rs.getDate("date_fin").toLocalDate(),
                        rs.getString("statut"),
                        rs.getInt("id_equipement"),
                        rs.getInt("id_locataire")
                    );
                    contrats.add(contrat);
                }
            }
            
            System.out.println("[ServiceLocation] " + contrats.size() + " contrat(s) actif(s) récupéré(s).");
        } catch (SQLException e) {
            System.err.println("[ServiceLocation] Erreur lors de la récupération des contrats actifs : " + e.getMessage());
            throw e;
        }
        
        return contrats;
    }

    /**
     * Récupère les contrats d'un locataire spécifique.
     * 
     * @param idLocataire L'identifiant du locataire
     * @return Liste des contrats du locataire
     * @throws SQLException En cas d'erreur SQL
     */
    public List<ContratLocation> afficherParLocataire(int idLocataire) throws SQLException {
        List<ContratLocation> contrats = new ArrayList<>();
        String sql = "SELECT * FROM contrat_location WHERE id_locataire = ? ORDER BY date_debut DESC";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, idLocataire);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    ContratLocation contrat = new ContratLocation(
                        rs.getInt("id"),
                        rs.getDate("date_debut").toLocalDate(),
                        rs.getDate("date_fin").toLocalDate(),
                        rs.getString("statut"),
                        rs.getInt("id_equipement"),
                        rs.getInt("id_locataire")
                    );
                    contrats.add(contrat);
                }
            }
            
            System.out.println("[ServiceLocation] " + contrats.size() + " contrat(s) récupéré(s) pour le locataire ID " + idLocataire);
        } catch (SQLException e) {
            System.err.println("[ServiceLocation] Erreur lors de la récupération des contrats par locataire : " + e.getMessage());
            throw e;
        }
        
        return contrats;
    }
}
