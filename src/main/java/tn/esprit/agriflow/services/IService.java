package tn.esprit.agriflow.services;

import java.util.List;

/**
 * Generic CRUD interface for service layer
 * @param <T> Entity type
 */
public interface IService<T> {
    /**
     * Add a new entity
     * @param entity Entity to add
     * @return true if successful, false otherwise
     */
    boolean add(T entity);
    
    /**
     * Update an existing entity
     * @param entity Entity to update
     * @return true if successful, false otherwise
     */
    boolean update(T entity);
    
    /**
     * Delete an entity by ID
     * @param id Entity ID
     * @return true if successful, false otherwise
     */
    boolean delete(int id);
    
    /**
     * Get an entity by ID
     * @param id Entity ID
     * @return Entity if found, null otherwise
     */
    T getById(int id);
    
    /**
     * Get all entities
     * @return List of all entities
     */
    List<T> getAll();
}
