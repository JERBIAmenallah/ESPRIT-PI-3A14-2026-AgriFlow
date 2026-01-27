package tn.esprit.agriflow.services;

import tn.esprit.agriflow.models.Reservation;
import tn.esprit.agriflow.models.enums.StatutAnnonce;
import tn.esprit.agriflow.models.enums.StatutReservation;
import tn.esprit.agriflow.utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Service for managing Reservation (P2P bookings)
 */
public class ReservationService implements IService<Reservation> {
    private Connection connection;
    private AnnonceService annonceService;
    
    public ReservationService() {
        this.connection = DatabaseConnection.getInstance().getConnection();
        this.annonceService = new AnnonceService();
    }
    
    @Override
    public boolean add(Reservation reservation) {
        String query = "INSERT INTO reservation (annonce_id, renter_user_id, start_date, " +
                      "end_date, status, total_price) VALUES (?, ?, ?, ?, ?, ?)";
        
        try {
            // Start transaction
            connection.setAutoCommit(false);
            
            try (PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setInt(1, reservation.getAnnonceId());
                stmt.setInt(2, reservation.getRenterUserId());
                stmt.setDate(3, Date.valueOf(reservation.getStartDate()));
                stmt.setDate(4, Date.valueOf(reservation.getEndDate()));
                stmt.setString(5, reservation.getStatus().name());
                stmt.setDouble(6, reservation.getTotalPrice());
                
                int rowsAffected = stmt.executeUpdate();
                
                if (rowsAffected > 0) {
                    // Automatically update annonce status to LOUE (for rental) or VENDU (for sale)
                    // This is P2P logic - direct transaction
                    var annonce = annonceService.getById(reservation.getAnnonceId());
                    if (annonce != null) {
                        StatutAnnonce newStatus = annonce.getType().name().equals("LOCATION") ? 
                                                 StatutAnnonce.LOUE : StatutAnnonce.VENDU;
                        annonceService.updateStatus(reservation.getAnnonceId(), newStatus);
                    }
                    
                    // Automatically confirm the reservation (P2P model)
                    ResultSet generatedKeys = stmt.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        int reservationId = generatedKeys.getInt(1);
                        updateReservationStatus(reservationId, StatutReservation.CONFIRMEE);
                    }
                    
                    connection.commit();
                    return true;
                }
                
                connection.rollback();
                return false;
            }
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            System.err.println("Error adding reservation: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    @Override
    public boolean update(Reservation reservation) {
        String query = "UPDATE reservation SET annonce_id = ?, renter_user_id = ?, " +
                      "start_date = ?, end_date = ?, status = ?, total_price = ? WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, reservation.getAnnonceId());
            stmt.setInt(2, reservation.getRenterUserId());
            stmt.setDate(3, Date.valueOf(reservation.getStartDate()));
            stmt.setDate(4, Date.valueOf(reservation.getEndDate()));
            stmt.setString(5, reservation.getStatus().name());
            stmt.setDouble(6, reservation.getTotalPrice());
            stmt.setInt(7, reservation.getId());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating reservation: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public boolean delete(int id) {
        String query = "DELETE FROM reservation WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting reservation: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public Reservation getById(int id) {
        String query = "SELECT * FROM reservation WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return extractReservationFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error getting reservation by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    @Override
    public List<Reservation> getAll() {
        List<Reservation> reservations = new ArrayList<>();
        String query = "SELECT * FROM reservation ORDER BY created_at DESC";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                reservations.add(extractReservationFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting all reservations: " + e.getMessage());
            e.printStackTrace();
        }
        return reservations;
    }
    
    /**
     * Get reservations by renter user ID
     */
    public List<Reservation> getByRenterUserId(int userId) {
        List<Reservation> reservations = new ArrayList<>();
        String query = "SELECT * FROM reservation WHERE renter_user_id = ? ORDER BY created_at DESC";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                reservations.add(extractReservationFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting reservations by renter user ID: " + e.getMessage());
            e.printStackTrace();
        }
        return reservations;
    }
    
    /**
     * Get reservations by annonce ID
     */
    public List<Reservation> getByAnnonceId(int annonceId) {
        List<Reservation> reservations = new ArrayList<>();
        String query = "SELECT * FROM reservation WHERE annonce_id = ? ORDER BY created_at DESC";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, annonceId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                reservations.add(extractReservationFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting reservations by annonce ID: " + e.getMessage());
            e.printStackTrace();
        }
        return reservations;
    }
    
    /**
     * Update reservation status
     */
    public boolean updateReservationStatus(int reservationId, StatutReservation status) {
        String query = "UPDATE reservation SET status = ? WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, status.name());
            stmt.setInt(2, reservationId);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating reservation status: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Cancel a reservation and restore annonce status
     */
    public boolean cancelReservation(int reservationId) {
        try {
            connection.setAutoCommit(false);
            
            Reservation reservation = getById(reservationId);
            if (reservation != null) {
                // Update reservation status to ANNULEE
                updateReservationStatus(reservationId, StatutReservation.ANNULEE);
                
                // Restore annonce status to DISPONIBLE
                annonceService.updateStatus(reservation.getAnnonceId(), StatutAnnonce.DISPONIBLE);
                
                connection.commit();
                return true;
            }
            
            connection.rollback();
            return false;
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            System.err.println("Error canceling reservation: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Extract Reservation object from ResultSet
     */
    private Reservation extractReservationFromResultSet(ResultSet rs) throws SQLException {
        Reservation reservation = new Reservation();
        reservation.setId(rs.getInt("id"));
        reservation.setAnnonceId(rs.getInt("annonce_id"));
        reservation.setRenterUserId(rs.getInt("renter_user_id"));
        
        Date startDate = rs.getDate("start_date");
        if (startDate != null) {
            reservation.setStartDate(startDate.toLocalDate());
        }
        
        Date endDate = rs.getDate("end_date");
        if (endDate != null) {
            reservation.setEndDate(endDate.toLocalDate());
        }
        
        reservation.setStatus(StatutReservation.valueOf(rs.getString("status")));
        reservation.setTotalPrice(rs.getDouble("total_price"));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            reservation.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        return reservation;
    }
}
