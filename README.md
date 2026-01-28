# AgriFlow - Marketplace de Location

Module de gestion de la location de matériel agricole entre agriculteurs.

## 📋 Description

Ce module permet aux agriculteurs de :
- Publier leurs équipements agricoles à la location
- Consulter les équipements disponibles
- Créer et gérer des contrats de location

## 🛠️ Technologies Utilisées

- **Langage** : Java 17
- **Interface** : JavaFX avec FXML
- **Base de données** : MySQL
- **Build** : Maven
- **Pattern** : MVC (Model - View - Controller) avec couche Service

## 📁 Structure du Projet

```
src/main/java/com/agriflow/marketplace/
├── MainApp.java                    # Point d'entrée de l'application
├── controllers/
│   └── MarketplaceController.java  # Contrôleur de l'interface principale
├── entities/
│   ├── Equipement.java             # Entité équipement agricole
│   └── ContratLocation.java        # Entité contrat de location
├── services/
│   ├── IService.java               # Interface générique CRUD
│   ├── ServiceEquipement.java      # Service pour les équipements
│   └── ServiceLocation.java        # Service pour les contrats
└── utils/
    └── MyDatabase.java             # Singleton de connexion BDD

src/main/resources/com/agriflow/marketplace/views/
├── Marketplace.fxml                # Interface utilisateur
└── styles.css                      # Feuille de styles

sql/
└── schema.sql                      # Script de création des tables
```

## ⚙️ Installation

### Prérequis
- Java 17+
- Maven 3.6+
- MySQL 8.0+

### Configuration de la Base de Données

1. Créez la base de données en exécutant le script SQL :
```bash
mysql -u root -p < sql/schema.sql
```

2. Modifiez les paramètres de connexion dans `MyDatabase.java` si nécessaire :
```java
private static final String URL = "jdbc:mysql://localhost:3306/agriflow_marketplace";
private static final String USER = "root";
private static final String PASSWORD = "";
```

### Compilation et Exécution

```bash
# Compiler le projet
mvn compile

# Exécuter l'application
mvn javafx:run
```

## 📊 Schéma de la Base de Données

### Table `equipement`
| Colonne | Type | Description |
|---------|------|-------------|
| id | INT (PK) | Identifiant unique |
| nom | VARCHAR(255) | Nom de l'équipement |
| type | VARCHAR(100) | Type de matériel |
| prix_location | DOUBLE | Prix journalier (€) |
| disponibilite | BOOLEAN | Disponible à la location |
| id_agriculteur | INT | ID du propriétaire |

### Table `contrat_location`
| Colonne | Type | Description |
|---------|------|-------------|
| id | INT (PK) | Identifiant unique |
| date_debut | DATE | Date de début |
| date_fin | DATE | Date de fin |
| statut | VARCHAR(50) | Statut du contrat |
| id_equipement | INT (FK) | ID de l'équipement |
| id_locataire | INT | ID du locataire |

## 👥 Équipe

Projet réalisé dans le cadre du module PI - ESPRIT 3A14 2026

## 📄 Licence

Ce projet est destiné à un usage éducatif.