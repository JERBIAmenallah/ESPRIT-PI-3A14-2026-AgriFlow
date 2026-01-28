package services;

import models.Annonce;
import models.StatutAnnonce;
import models.TypeAnnonce;
import utils.MyDatabase;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Service CRUD pour la gestion des Annonces.
 * Implémente IService<Annonce> avec des méthodes de recherche spécifiques.
 */
public class AnnonceService implements IService<Annonce> {

    private Connection connection;

    public AnnonceService() {
        this.connection = MyDatabase.getInstance().getConnection();
    }

    // ==================== CRUD DE BASE ====================

    @Override
    public void add(Annonce annonce) throws SQLException {
        String query = """
                INSERT INTO annonce (titre, description, prix, type, statut, image_path,
                                     localisation, date_disponibilite, date_fin_disponibilite,
                                     date_creation, date_modification, proprietaire_id)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, annonce.getTitre());
            ps.setString(2, annonce.getDescription());
            ps.setDouble(3, annonce.getPrix());
            ps.setString(4, annonce.getType().name());
            ps.setString(5, annonce.getStatut().name());
            ps.setString(6, annonce.getImagePath());
            ps.setString(7, annonce.getLocalisation());
            ps.setDate(8, annonce.getDateDisponibilite() != null ? Date.valueOf(annonce.getDateDisponibilite()) : null);
            ps.setDate(9,
                    annonce.getDateFinDisponibilite() != null ? Date.valueOf(annonce.getDateFinDisponibilite()) : null);
            ps.setTimestamp(10, Timestamp.valueOf(annonce.getDateCreation()));
            ps.setTimestamp(11, Timestamp.valueOf(annonce.getDateModification()));
            ps.setInt(12, annonce.getProprietaireId());

            ps.executeUpdate();

            // Récupération de l'ID généré
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    annonce.setId(generatedKeys.getInt(1));
                }
            }

            System.out.println("✅ Annonce ajoutée avec succès : " + annonce.getTitre());
        }
    }

    @Override
    public void update(Annonce annonce) throws SQLException {
        String query = """
                UPDATE annonce SET
                    titre = ?, description = ?, prix = ?, type = ?, statut = ?,
                    image_path = ?, localisation = ?, date_disponibilite = ?,
                    date_fin_disponibilite = ?, date_modification = ?
                WHERE id = ?
                """;

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, annonce.getTitre());
            ps.setString(2, annonce.getDescription());
            ps.setDouble(3, annonce.getPrix());
            ps.setString(4, annonce.getType().name());
            ps.setString(5, annonce.getStatut().name());
            ps.setString(6, annonce.getImagePath());
            ps.setString(7, annonce.getLocalisation());
            ps.setDate(8, annonce.getDateDisponibilite() != null ? Date.valueOf(annonce.getDateDisponibilite()) : null);
            ps.setDate(9,
                    annonce.getDateFinDisponibilite() != null ? Date.valueOf(annonce.getDateFinDisponibilite()) : null);
            ps.setTimestamp(10, Timestamp.valueOf(LocalDateTime.now()));
            ps.setInt(11, annonce.getId());

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("✅ Annonce mise à jour : " + annonce.getTitre());
            }
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String query = "DELETE FROM annonce WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, id);
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("✅ Annonce supprimée (ID: " + id + ")");
            }
        }
    }

    @Override
    public List<Annonce> getAll() throws SQLException {
        List<Annonce> annonces = new ArrayList<>();
        String query = "SELECT * FROM annonce ORDER BY date_creation DESC";

        try (Statement st = connection.createStatement();
                ResultSet rs = st.executeQuery(query)) {
            while (rs.next()) {
                annonces.add(mapResultSetToAnnonce(rs));
            }
        }
        return annonces;
    }

    @Override
    public Annonce getById(int id) throws SQLException {
        String query = "SELECT * FROM annonce WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToAnnonce(rs);
                }
            }
        }
        return null;
    }

    // ==================== MÉTHODES DE RECHERCHE SPÉCIFIQUES ====================

    /**
     * Récupère toutes les annonces disponibles.
     */
    public List<Annonce> getAnnoncesDisponibles() throws SQLException {
        List<Annonce> annonces = new ArrayList<>();
        String query = "SELECT * FROM annonce WHERE statut = 'DISPONIBLE' ORDER BY date_creation DESC";

        try (Statement st = connection.createStatement();
                ResultSet rs = st.executeQuery(query)) {
            while (rs.next()) {
                annonces.add(mapResultSetToAnnonce(rs));
            }
        }
        return annonces;
    }

    /**
     * Récupère les annonces par type (ex: tous les tracteurs).
     */
    public List<Annonce> getByType(TypeAnnonce type) throws SQLException {
        List<Annonce> annonces = new ArrayList<>();
        String query = "SELECT * FROM annonce WHERE type = ? AND statut = 'DISPONIBLE' ORDER BY date_creation DESC";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, type.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    annonces.add(mapResultSetToAnnonce(rs));
                }
            }
        }
        return annonces;
    }

    /**
     * Récupère les annonces par localisation (Gouvernorat).
     */
    public List<Annonce> getByLocalisation(String localisation) throws SQLException {
        List<Annonce> annonces = new ArrayList<>();
        String query = "SELECT * FROM annonce WHERE localisation LIKE ? AND statut = 'DISPONIBLE' ORDER BY date_creation DESC";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, "%" + localisation + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    annonces.add(mapResultSetToAnnonce(rs));
                }
            }
        }
        return annonces;
    }

    /**
     * Récupère les annonces dans une fourchette de prix.
     */
    public List<Annonce> getByPrixRange(double prixMin, double prixMax) throws SQLException {
        List<Annonce> annonces = new ArrayList<>();
        String query = "SELECT * FROM annonce WHERE prix BETWEEN ? AND ? AND statut = 'DISPONIBLE' ORDER BY prix ASC";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setDouble(1, prixMin);
            ps.setDouble(2, prixMax);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    annonces.add(mapResultSetToAnnonce(rs));
                }
            }
        }
        return annonces;
    }

    /**
     * Récupère les annonces d'un propriétaire spécifique (Mes Annonces).
     */
    public List<Annonce> getByProprietaire(int proprietaireId) throws SQLException {
        List<Annonce> annonces = new ArrayList<>();
        String query = "SELECT * FROM annonce WHERE proprietaire_id = ? ORDER BY date_creation DESC";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, proprietaireId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    annonces.add(mapResultSetToAnnonce(rs));
                }
            }
        }
        return annonces;
    }

    /**
     * Recherche par mot-clé dans le titre et la description.
     */
    public List<Annonce> search(String keyword) throws SQLException {
        List<Annonce> annonces = new ArrayList<>();
        String query = """
                SELECT * FROM annonce
                WHERE (titre LIKE ? OR description LIKE ?)
                AND statut = 'DISPONIBLE'
                ORDER BY date_creation DESC
                """;

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            String pattern = "%" + keyword + "%";
            ps.setString(1, pattern);
            ps.setString(2, pattern);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    annonces.add(mapResultSetToAnnonce(rs));
                }
            }
        }
        return annonces;
    }

    /**
     * Recherche avancée avec filtres multiples.
     */
    public List<Annonce> searchAdvanced(String keyword, TypeAnnonce type,
            String localisation, Double prixMax) throws SQLException {
        List<Annonce> annonces = new ArrayList<>();
        StringBuilder queryBuilder = new StringBuilder(
                "SELECT * FROM annonce WHERE statut = 'DISPONIBLE'");
        List<Object> params = new ArrayList<>();

        if (keyword != null && !keyword.isEmpty()) {
            queryBuilder.append(" AND (titre LIKE ? OR description LIKE ?)");
            params.add("%" + keyword + "%");
            params.add("%" + keyword + "%");
        }

        if (type != null) {
            queryBuilder.append(" AND type = ?");
            params.add(type.name());
        }

        if (localisation != null && !localisation.isEmpty()) {
            queryBuilder.append(" AND localisation LIKE ?");
            params.add("%" + localisation + "%");
        }

        if (prixMax != null) {
            queryBuilder.append(" AND prix <= ?");
            params.add(prixMax);
        }

        queryBuilder.append(" ORDER BY date_creation DESC");

        try (PreparedStatement ps = connection.prepareStatement(queryBuilder.toString())) {
            for (int i = 0; i < params.size(); i++) {
                Object param = params.get(i);
                if (param instanceof String) {
                    ps.setString(i + 1, (String) param);
                } else if (param instanceof Double) {
                    ps.setDouble(i + 1, (Double) param);
                }
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    annonces.add(mapResultSetToAnnonce(rs));
                }
            }
        }
        return annonces;
    }

    // ==================== MÉTHODES P2P SPÉCIFIQUES ====================

    /**
     * Réserve une annonce (P2P : action directe de l'agriculteur B).
     */
    public void reserverAnnonce(int annonceId) throws SQLException {
        String query = "UPDATE annonce SET statut = 'RESERVE', date_modification = ? WHERE id = ? AND statut = 'DISPONIBLE'";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            ps.setInt(2, annonceId);

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("✅ Annonce réservée (ID: " + annonceId + ")");
            } else {
                System.out.println("⚠️ L'annonce n'est plus disponible.");
            }
        }
    }

    /**
     * Libère une annonce (Retour à disponible).
     */
    public void libererAnnonce(int annonceId) throws SQLException {
        String query = "UPDATE annonce SET statut = 'DISPONIBLE', date_modification = ? WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            ps.setInt(2, annonceId);
            ps.executeUpdate();
            System.out.println("✅ Annonce libérée (ID: " + annonceId + ")");
        }
    }

    // ==================== MAPPING ====================

    /**
     * Convertit un ResultSet en objet Annonce.
     */
    private Annonce mapResultSetToAnnonce(ResultSet rs) throws SQLException {
        return new Annonce(
                rs.getInt("id"),
                rs.getString("titre"),
                rs.getString("description"),
                rs.getDouble("prix"),
                TypeAnnonce.valueOf(rs.getString("type")),
                StatutAnnonce.valueOf(rs.getString("statut")),
                rs.getString("image_path"),
                rs.getString("localisation"),
                rs.getDate("date_disponibilite") != null ? rs.getDate("date_disponibilite").toLocalDate() : null,
                rs.getDate("date_fin_disponibilite") != null ? rs.getDate("date_fin_disponibilite").toLocalDate()
                        : null,
                rs.getTimestamp("date_creation").toLocalDateTime(),
                rs.getTimestamp("date_modification").toLocalDateTime(),
                rs.getInt("proprietaire_id"));
    }
}
