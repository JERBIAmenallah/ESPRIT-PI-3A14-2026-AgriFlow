package tn.esprit.agriflow.services;

import tn.esprit.agriflow.models.Annonce;
import tn.esprit.agriflow.models.enums.CategorieAnnonce;
import tn.esprit.agriflow.models.enums.StatutAnnonce;
import tn.esprit.agriflow.models.enums.TypeAnnonce;
import tn.esprit.agriflow.utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Service for managing Annonce (marketplace listings)
 */
public class AnnonceService implements IService<Annonce> {
    private Connection connection;
    
    public AnnonceService() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }
    
    @Override
    public boolean add(Annonce annonce) {
        String query = "INSERT INTO annonce (title, description, price, type, category, " +
                      "image_path, location, status, availability_start, availability_end, user_id) " +
                      "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, annonce.getTitle());
            stmt.setString(2, annonce.getDescription());
            stmt.setDouble(3, annonce.getPrice());
            stmt.setString(4, annonce.getType().name());
            stmt.setString(5, annonce.getCategory().name());
            stmt.setString(6, annonce.getImagePath());
            stmt.setString(7, annonce.getLocation());
            stmt.setString(8, annonce.getStatus().name());
            stmt.setDate(9, annonce.getAvailabilityStart() != null ? 
                         Date.valueOf(annonce.getAvailabilityStart()) : null);
            stmt.setDate(10, annonce.getAvailabilityEnd() != null ? 
                         Date.valueOf(annonce.getAvailabilityEnd()) : null);
            stmt.setInt(11, annonce.getUserId());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error adding annonce: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public boolean update(Annonce annonce) {
        String query = "UPDATE annonce SET title = ?, description = ?, price = ?, type = ?, " +
                      "category = ?, image_path = ?, location = ?, status = ?, " +
                      "availability_start = ?, availability_end = ? WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, annonce.getTitle());
            stmt.setString(2, annonce.getDescription());
            stmt.setDouble(3, annonce.getPrice());
            stmt.setString(4, annonce.getType().name());
            stmt.setString(5, annonce.getCategory().name());
            stmt.setString(6, annonce.getImagePath());
            stmt.setString(7, annonce.getLocation());
            stmt.setString(8, annonce.getStatus().name());
            stmt.setDate(9, annonce.getAvailabilityStart() != null ? 
                         Date.valueOf(annonce.getAvailabilityStart()) : null);
            stmt.setDate(10, annonce.getAvailabilityEnd() != null ? 
                         Date.valueOf(annonce.getAvailabilityEnd()) : null);
            stmt.setInt(11, annonce.getId());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating annonce: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public boolean delete(int id) {
        String query = "DELETE FROM annonce WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting annonce: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public Annonce getById(int id) {
        String query = "SELECT * FROM annonce WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return extractAnnonceFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error getting annonce by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    @Override
    public List<Annonce> getAll() {
        List<Annonce> annonces = new ArrayList<>();
        String query = "SELECT * FROM annonce ORDER BY created_at DESC";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                annonces.add(extractAnnonceFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting all annonces: " + e.getMessage());
            e.printStackTrace();
        }
        return annonces;
    }
    
    /**
     * Get annonces by user ID
     */
    public List<Annonce> getByUserId(int userId) {
        List<Annonce> annonces = new ArrayList<>();
        String query = "SELECT * FROM annonce WHERE user_id = ? ORDER BY created_at DESC";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                annonces.add(extractAnnonceFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting annonces by user ID: " + e.getMessage());
            e.printStackTrace();
        }
        return annonces;
    }
    
    /**
     * Search and filter annonces
     */
    public List<Annonce> search(String keyword, TypeAnnonce type, CategorieAnnonce category, 
                                Double minPrice, Double maxPrice, String location) {
        List<Annonce> annonces = new ArrayList<>();
        StringBuilder query = new StringBuilder("SELECT * FROM annonce WHERE 1=1");
        List<Object> params = new ArrayList<>();
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            query.append(" AND (title LIKE ? OR description LIKE ?)");
            String keywordPattern = "%" + keyword + "%";
            params.add(keywordPattern);
            params.add(keywordPattern);
        }
        
        if (type != null) {
            query.append(" AND type = ?");
            params.add(type.name());
        }
        
        if (category != null) {
            query.append(" AND category = ?");
            params.add(category.name());
        }
        
        if (minPrice != null) {
            query.append(" AND price >= ?");
            params.add(minPrice);
        }
        
        if (maxPrice != null) {
            query.append(" AND price <= ?");
            params.add(maxPrice);
        }
        
        if (location != null && !location.trim().isEmpty()) {
            query.append(" AND location LIKE ?");
            params.add("%" + location + "%");
        }
        
        query.append(" ORDER BY created_at DESC");
        
        try (PreparedStatement stmt = connection.prepareStatement(query.toString())) {
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                annonces.add(extractAnnonceFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error searching annonces: " + e.getMessage());
            e.printStackTrace();
        }
        return annonces;
    }
    
    /**
     * Get available annonces (status = DISPONIBLE)
     */
    public List<Annonce> getAvailable() {
        List<Annonce> annonces = new ArrayList<>();
        String query = "SELECT * FROM annonce WHERE status = 'DISPONIBLE' ORDER BY created_at DESC";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                annonces.add(extractAnnonceFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting available annonces: " + e.getMessage());
            e.printStackTrace();
        }
        return annonces;
    }
    
    /**
     * Update annonce status
     */
    public boolean updateStatus(int annonceId, StatutAnnonce status) {
        String query = "UPDATE annonce SET status = ? WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, status.name());
            stmt.setInt(2, annonceId);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating annonce status: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Extract Annonce object from ResultSet
     */
    private Annonce extractAnnonceFromResultSet(ResultSet rs) throws SQLException {
        Annonce annonce = new Annonce();
        annonce.setId(rs.getInt("id"));
        annonce.setTitle(rs.getString("title"));
        annonce.setDescription(rs.getString("description"));
        annonce.setPrice(rs.getDouble("price"));
        annonce.setType(TypeAnnonce.valueOf(rs.getString("type")));
        annonce.setCategory(CategorieAnnonce.valueOf(rs.getString("category")));
        annonce.setImagePath(rs.getString("image_path"));
        annonce.setLocation(rs.getString("location"));
        annonce.setStatus(StatutAnnonce.valueOf(rs.getString("status")));
        
        Date availStart = rs.getDate("availability_start");
        if (availStart != null) {
            annonce.setAvailabilityStart(availStart.toLocalDate());
        }
        
        Date availEnd = rs.getDate("availability_end");
        if (availEnd != null) {
            annonce.setAvailabilityEnd(availEnd.toLocalDate());
        }
        
        annonce.setUserId(rs.getInt("user_id"));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            annonce.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            annonce.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        
        return annonce;
    }
}
