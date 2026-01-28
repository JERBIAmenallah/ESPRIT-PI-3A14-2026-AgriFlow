package com.agriflow.marketplace.services;

import java.util.List;

/**
 * Interface générique définissant les opérations CRUD de base.
 * Toutes les classes de service doivent implémenter cette interface.
 * 
 * @param <T> Type de l'entité gérée par le service
 * @author AgriFlow Team
 * @version 1.0
 */
public interface IService<T> {

    /**
     * Ajoute une nouvelle entité dans la base de données.
     * 
     * @param entity L'entité à ajouter
     * @throws Exception En cas d'erreur lors de l'ajout
     */
    void ajouter(T entity) throws Exception;

    /**
     * Modifie une entité existante dans la base de données.
     * 
     * @param entity L'entité avec les nouvelles valeurs
     * @throws Exception En cas d'erreur lors de la modification
     */
    void modifier(T entity) throws Exception;

    /**
     * Supprime une entité de la base de données.
     * 
     * @param id L'identifiant de l'entité à supprimer
     * @throws Exception En cas d'erreur lors de la suppression
     */
    void supprimer(int id) throws Exception;

    /**
     * Récupère toutes les entités de la base de données.
     * 
     * @return Liste de toutes les entités
     * @throws Exception En cas d'erreur lors de la récupération
     */
    List<T> afficher() throws Exception;
}
