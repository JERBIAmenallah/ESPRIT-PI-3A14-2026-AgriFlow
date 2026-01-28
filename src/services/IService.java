package services;

import java.sql.SQLException;
import java.util.List;

/**
 * Interface générique CRUD pour tous les services AGRIFLOW.
 * Design Pattern : Service + Interface (imposé par le cahier des charges
 * PIDEV).
 * 
 * @param <T> Type de l'entité (Annonce, Reservation, etc.)
 */
public interface IService<T> {

    /**
     * Ajoute une nouvelle entité dans la base de données.
     * 
     * @param entity L'entité à ajouter
     * @throws SQLException En cas d'erreur SQL
     */
    void add(T entity) throws SQLException;

    /**
     * Met à jour une entité existante.
     * 
     * @param entity L'entité avec les nouvelles valeurs
     * @throws SQLException En cas d'erreur SQL
     */
    void update(T entity) throws SQLException;

    /**
     * Supprime une entité par son ID.
     * 
     * @param id L'identifiant de l'entité à supprimer
     * @throws SQLException En cas d'erreur SQL
     */
    void delete(int id) throws SQLException;

    /**
     * Récupère toutes les entités.
     * 
     * @return Liste de toutes les entités
     * @throws SQLException En cas d'erreur SQL
     */
    List<T> getAll() throws SQLException;

    /**
     * Récupère une entité par son ID.
     * 
     * @param id L'identifiant de l'entité
     * @return L'entité correspondante ou null si non trouvée
     * @throws SQLException En cas d'erreur SQL
     */
    T getById(int id) throws SQLException;
}
