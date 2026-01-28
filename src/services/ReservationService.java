package services;

import models.Reservation;
import models.Reservation.StatutReservation;
import utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Service CRUD pour les Réservations - Architecture P2P.
 */
public class ReservationService implements IService<Reservation> {

    private Connection connection;
    private AnnonceService annonceService;

    public ReservationService() {
        this.connection = MyDatabase.getInstance().getConnection();
        this.annonceService = new AnnonceService();
    }

    @Override
    public void add(Reservation reservation) throws SQLException {
        String query = "INSERT INTO reservation (annonce_id, client_id, date_debut, date_fin, montant_total, statut, commentaire, date_creation) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, reservation.getAnnonceId());
            ps.setInt(2, reservation.getClientId());
            ps.setDate(3, reservation.getDateDebut() != null ? Date.valueOf(reservation.getDateDebut()) : null);
            ps.setDate(4, reservation.getDateFin() != null ? Date.valueOf(reservation.getDateFin()) : null);
            ps.setDouble(5, reservation.getMontantTotal());
            ps.setString(6, reservation.getStatut().name());
            ps.setString(7, reservation.getCommentaire());
            ps.setTimestamp(8, Timestamp.valueOf(reservation.getDateCreation()));
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next())
                    reservation.setId(rs.getInt(1));
            }
            annonceService.reserverAnnonce(reservation.getAnnonceId());
        }
    }

    @Override
    public void update(Reservation r) throws SQLException {
        String query = "UPDATE reservation SET date_debut=?, date_fin=?, montant_total=?, statut=?, commentaire=? WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setDate(1, r.getDateDebut() != null ? Date.valueOf(r.getDateDebut()) : null);
            ps.setDate(2, r.getDateFin() != null ? Date.valueOf(r.getDateFin()) : null);
            ps.setDouble(3, r.getMontantTotal());
            ps.setString(4, r.getStatut().name());
            ps.setString(5, r.getCommentaire());
            ps.setInt(6, r.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        Reservation r = getById(id);
        try (PreparedStatement ps = connection.prepareStatement("DELETE FROM reservation WHERE id=?")) {
            ps.setInt(1, id);
            if (ps.executeUpdate() > 0 && r != null)
                annonceService.libererAnnonce(r.getAnnonceId());
        }
    }

    @Override
    public List<Reservation> getAll() throws SQLException {
        List<Reservation> list = new ArrayList<>();
        try (Statement st = connection.createStatement();
                ResultSet rs = st.executeQuery("SELECT * FROM reservation ORDER BY date_creation DESC")) {
            while (rs.next())
                list.add(map(rs));
        }
        return list;
    }

    @Override
    public Reservation getById(int id) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement("SELECT * FROM reservation WHERE id=?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    return map(rs);
            }
        }
        return null;
    }

    public List<Reservation> getByClient(int clientId) throws SQLException {
        List<Reservation> list = new ArrayList<>();
        try (PreparedStatement ps = connection
                .prepareStatement("SELECT * FROM reservation WHERE client_id=? ORDER BY date_creation DESC")) {
            ps.setInt(1, clientId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next())
                    list.add(map(rs));
            }
        }
        return list;
    }

    public List<Reservation> getByAnnonce(int annonceId) throws SQLException {
        List<Reservation> list = new ArrayList<>();
        try (PreparedStatement ps = connection
                .prepareStatement("SELECT * FROM reservation WHERE annonce_id=? ORDER BY date_creation DESC")) {
            ps.setInt(1, annonceId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next())
                    list.add(map(rs));
            }
        }
        return list;
    }

    public void confirmerReservation(int id) throws SQLException {
        try (PreparedStatement ps = connection
                .prepareStatement("UPDATE reservation SET statut='CONFIRMEE' WHERE id=? AND statut='EN_ATTENTE'")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public void annulerReservation(int id) throws SQLException {
        Reservation r = getById(id);
        try (PreparedStatement ps = connection.prepareStatement("UPDATE reservation SET statut='ANNULEE' WHERE id=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
        if (r != null)
            annonceService.libererAnnonce(r.getAnnonceId());
    }

    private Reservation map(ResultSet rs) throws SQLException {
        return new Reservation(
                rs.getInt("id"), rs.getInt("annonce_id"), rs.getInt("client_id"),
                rs.getDate("date_debut") != null ? rs.getDate("date_debut").toLocalDate() : null,
                rs.getDate("date_fin") != null ? rs.getDate("date_fin").toLocalDate() : null,
                rs.getDouble("montant_total"), StatutReservation.valueOf(rs.getString("statut")),
                rs.getString("commentaire"), rs.getTimestamp("date_creation").toLocalDateTime());
    }
}
