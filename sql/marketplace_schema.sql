-- ============================================================
-- AGRIFLOW - Module Marketplace
-- Script de création des tables
-- Base de données : agriflow (MySQL)
-- ============================================================

-- Note : La table `user` est gérée par Ayoub (module authentification)
-- On suppose qu'elle existe avec au minimum : id, nom, prenom, email

-- ============================================================
-- TABLE ANNONCE
-- Représente une offre de location ou vente P2P
-- ============================================================
CREATE TABLE IF NOT EXISTS annonce (
    id INT AUTO_INCREMENT PRIMARY KEY,
    titre VARCHAR(255) NOT NULL,
    description TEXT,
    prix DECIMAL(10, 2) NOT NULL,
    type ENUM('TRACTEUR', 'MOISSONNEUSE', 'SEMOIR', 'PULVERISATEUR', 
              'REMORQUE', 'IRRIGATION', 'SEMENCES', 'ENGRAIS', 
              'PRODUIT_RECOLTE', 'AUTRE') NOT NULL,
    statut ENUM('DISPONIBLE', 'RESERVE', 'LOUE', 'VENDU', 'INDISPONIBLE') 
           NOT NULL DEFAULT 'DISPONIBLE',
    image_path VARCHAR(500),
    localisation VARCHAR(100),  -- Gouvernorat / Ville en Tunisie
    date_disponibilite DATE,
    date_fin_disponibilite DATE,
    date_creation DATETIME DEFAULT CURRENT_TIMESTAMP,
    date_modification DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    proprietaire_id INT NOT NULL,
    
    -- Clé étrangère vers la table user (gérée par Ayoub)
    FOREIGN KEY (proprietaire_id) REFERENCES user(id) ON DELETE CASCADE,
    
    -- Index pour optimiser les recherches
    INDEX idx_type (type),
    INDEX idx_statut (statut),
    INDEX idx_localisation (localisation),
    INDEX idx_proprietaire (proprietaire_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- TABLE RESERVATION
-- Liaison entre un client (agriculteur) et une annonce
-- Architecture P2P : transaction directe sans validation admin
-- ============================================================
CREATE TABLE IF NOT EXISTS reservation (
    id INT AUTO_INCREMENT PRIMARY KEY,
    annonce_id INT NOT NULL,
    client_id INT NOT NULL,  -- L'agriculteur qui réserve
    date_debut DATE,
    date_fin DATE,           -- NULL si c'est une vente
    montant_total DECIMAL(10, 2) NOT NULL,
    statut ENUM('EN_ATTENTE', 'CONFIRMEE', 'ANNULEE', 'TERMINEE') 
           NOT NULL DEFAULT 'EN_ATTENTE',
    commentaire TEXT,
    date_creation DATETIME DEFAULT CURRENT_TIMESTAMP,
    
    -- Clés étrangères
    FOREIGN KEY (annonce_id) REFERENCES annonce(id) ON DELETE CASCADE,
    FOREIGN KEY (client_id) REFERENCES user(id) ON DELETE CASCADE,
    
    -- Index pour optimiser les requêtes
    INDEX idx_annonce (annonce_id),
    INDEX idx_client (client_id),
    INDEX idx_statut (statut)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- DONNÉES DE TEST (optionnel)
-- ============================================================
-- INSERT INTO annonce (titre, description, prix, type, localisation, proprietaire_id) VALUES
-- ('Tracteur John Deere 5075E', 'Tracteur en excellent état, 75CV', 150.00, 'TRACTEUR', 'Nabeul', 1),
-- ('Moissonneuse Claas Lexion', 'Disponible pour la saison', 500.00, 'MOISSONNEUSE', 'Béja', 1),
-- ('Semences de blé dur', 'Variété Karim, certifiée', 25.00, 'SEMENCES', 'Bizerte', 2);
