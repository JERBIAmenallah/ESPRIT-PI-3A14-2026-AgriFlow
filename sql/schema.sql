-- =============================================================================
-- AgriFlow - Marketplace de Location
-- Script de création des tables pour la base de données MySQL
-- =============================================================================

-- Création de la base de données (à exécuter une seule fois)
CREATE DATABASE IF NOT EXISTS agriflow_marketplace
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE agriflow_marketplace;

-- =============================================================================
-- Table : equipement
-- Description : Stocke les informations sur le matériel agricole disponible
-- =============================================================================
CREATE TABLE IF NOT EXISTS equipement (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(255) NOT NULL COMMENT 'Nom de l''équipement',
    type VARCHAR(100) NOT NULL COMMENT 'Type de matériel (Tracteur, Moissonneuse, etc.)',
    prix_location DOUBLE NOT NULL COMMENT 'Prix de location journalier en euros',
    disponibilite BOOLEAN DEFAULT TRUE COMMENT 'Indique si l''équipement est disponible',
    id_agriculteur INT NOT NULL COMMENT 'Identifiant du propriétaire de l''équipement',
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_type (type),
    INDEX idx_disponibilite (disponibilite),
    INDEX idx_agriculteur (id_agriculteur)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================================================
-- Table : contrat_location
-- Description : Stocke les contrats de location entre agriculteurs
-- =============================================================================
CREATE TABLE IF NOT EXISTS contrat_location (
    id INT AUTO_INCREMENT PRIMARY KEY,
    date_debut DATE NOT NULL COMMENT 'Date de début de la location',
    date_fin DATE NOT NULL COMMENT 'Date de fin de la location',
    statut VARCHAR(50) NOT NULL DEFAULT 'EN_ATTENTE' COMMENT 'Statut du contrat (EN_ATTENTE, ACTIF, TERMINE, ANNULE)',
    id_equipement INT NOT NULL COMMENT 'Référence vers l''équipement loué',
    id_locataire INT NOT NULL COMMENT 'Identifiant de l''agriculteur locataire',
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_equipement FOREIGN KEY (id_equipement) 
        REFERENCES equipement(id) 
        ON DELETE CASCADE 
        ON UPDATE CASCADE,
    INDEX idx_statut (statut),
    INDEX idx_locataire (id_locataire),
    INDEX idx_dates (date_debut, date_fin)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================================================
-- Données de test (optionnel)
-- =============================================================================
INSERT INTO equipement (nom, type, prix_location, disponibilite, id_agriculteur) VALUES
    ('Tracteur John Deere 6M', 'Tracteur', 150.00, TRUE, 1),
    ('Moissonneuse Claas Lexion', 'Moissonneuse', 300.00, TRUE, 1),
    ('Semoir Amazone', 'Semoir', 80.00, TRUE, 2),
    ('Pulvérisateur Berthoud', 'Pulvérisateur', 120.00, FALSE, 2),
    ('Remorque Joskin', 'Remorque', 50.00, TRUE, 3);

INSERT INTO contrat_location (date_debut, date_fin, statut, id_equipement, id_locataire) VALUES
    ('2026-01-15', '2026-01-20', 'ACTIF', 1, 4),
    ('2026-02-01', '2026-02-10', 'EN_ATTENTE', 2, 5);
