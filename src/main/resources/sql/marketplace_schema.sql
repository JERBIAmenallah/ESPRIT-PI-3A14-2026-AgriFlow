-- AgriFlow Marketplace Module - Database Schema
-- Drop existing tables if they exist (for development/testing)
DROP TABLE IF EXISTS reservation;
DROP TABLE IF EXISTS annonce;
DROP TABLE IF EXISTS user;

-- User table (for authentication and ownership)
CREATE TABLE user (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100),
    phone VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Annonce table (Marketplace listings)
CREATE TABLE annonce (
    id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(200) NOT NULL,
    description TEXT NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    type ENUM('LOCATION', 'VENTE') NOT NULL,
    category ENUM('TRACTEUR', 'MOISSONNEUSE', 'SEMENCES', 'ENGRAIS', 'IRRIGATION', 'AUTRE') NOT NULL,
    image_path VARCHAR(255),
    location VARCHAR(100) NOT NULL,
    status ENUM('DISPONIBLE', 'LOUE', 'VENDU') DEFAULT 'DISPONIBLE',
    availability_start DATE,
    availability_end DATE,
    user_id INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
    INDEX idx_status (status),
    INDEX idx_type (type),
    INDEX idx_category (category),
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Reservation table (P2P bookings)
CREATE TABLE reservation (
    id INT PRIMARY KEY AUTO_INCREMENT,
    annonce_id INT NOT NULL,
    renter_user_id INT NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    status ENUM('EN_ATTENTE', 'CONFIRMEE', 'ANNULEE') DEFAULT 'EN_ATTENTE',
    total_price DECIMAL(10, 2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (annonce_id) REFERENCES annonce(id) ON DELETE CASCADE,
    FOREIGN KEY (renter_user_id) REFERENCES user(id) ON DELETE CASCADE,
    INDEX idx_annonce_id (annonce_id),
    INDEX idx_renter_user_id (renter_user_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Sample data for testing (optional)
-- NOTE: In production, passwords MUST be hashed using BCrypt, Argon2, or similar
-- These are plain text passwords for TESTING ONLY
-- Insert sample user
INSERT INTO user (username, email, password, full_name, phone) 
VALUES 
    ('farmer1', 'farmer1@agriflow.tn', 'password123', 'Ahmed Ben Ali', '+216 20 123 456'),
    ('farmer2', 'farmer2@agriflow.tn', 'password123', 'Fatma Hammami', '+216 22 654 321');

-- Insert sample annonces
INSERT INTO annonce (title, description, price, type, category, location, status, availability_start, availability_end, user_id)
VALUES
    ('Tracteur John Deere 5055E', 'Tracteur en excellent état, 55 CV, idéal pour petites et moyennes exploitations', 150.00, 'LOCATION', 'TRACTEUR', 'Tunis', 'DISPONIBLE', '2026-02-01', '2026-12-31', 1),
    ('Moissonneuse-batteuse CLAAS', 'Moissonneuse performante, largeur de coupe 6m', 8500.00, 'VENTE', 'MOISSONNEUSE', 'Sfax', 'DISPONIBLE', NULL, NULL, 1),
    ('Semences de blé dur', 'Semences certifiées, variété résistante à la sécheresse', 50.00, 'VENTE', 'SEMENCES', 'Bizerte', 'DISPONIBLE', NULL, NULL, 2);
