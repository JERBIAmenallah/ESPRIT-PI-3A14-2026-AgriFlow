# ESPRIT-PI-3A14-2026-AgriFlow

## AgriFlow - Smart Farming Platform for Tunisia

### Marketplace Module (Sprint 1)

A JavaFX-based Peer-to-Peer marketplace for agricultural equipment and products, allowing farmers to directly rent or sell items without admin validation.

## Features

### P2P Architecture
- Direct transactions between farmers
- Automatic status updates (Available → Rented/Sold)
- No admin approval required

### Core Functionality
- **Marketplace**: Browse available listings with search and filter options
- **Listing Management**: Create, edit, and delete your listings
- **Reservations**: Reserve equipment or purchase products
- **My Annonces**: Manage your own listings
- **My Reservations**: View and manage your bookings

### Technical Stack
- **Framework**: JavaFX 17
- **Database**: MySQL 8.0
- **Build Tool**: Maven
- **Architecture**: MVC with Service Layer
- **Design Patterns**: Generic CRUD Interface (`IService<T>`)

## Project Structure

```
src/main/java/tn/esprit/agriflow/
├── models/
│   ├── User.java
│   ├── Annonce.java
│   ├── Reservation.java
│   └── enums/
│       ├── TypeAnnonce.java (LOCATION/VENTE)
│       ├── CategorieAnnonce.java (TRACTEUR, MOISSONNEUSE, etc.)
│       ├── StatutAnnonce.java (DISPONIBLE/LOUE/VENDU)
│       └── StatutReservation.java (EN_ATTENTE/CONFIRMEE/ANNULEE)
├── services/
│   ├── IService.java (Generic CRUD interface)
│   ├── AnnonceService.java
│   └── ReservationService.java
├── controllers/
│   ├── MarketplaceController.java
│   ├── AnnonceFormController.java
│   ├── AnnonceDetailController.java
│   ├── MesAnnoncesController.java
│   └── MesReservationsController.java
├── utils/
│   └── DatabaseConnection.java
└── Main.java

src/main/resources/
├── fxml/
│   ├── marketplace.fxml
│   ├── annonce_form.fxml
│   ├── annonce_detail.fxml
│   ├── mes_annonces.fxml
│   └── mes_reservations.fxml
└── sql/
    └── marketplace_schema.sql
```

## Setup Instructions

### Prerequisites
- Java 11 or higher
- Maven 3.6+
- MySQL 8.0+

### Database Setup

1. Create the MySQL database:
```sql
CREATE DATABASE agriflow CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. Run the schema file:
```bash
mysql -u root -p agriflow < src/main/resources/sql/marketplace_schema.sql
```

3. Configure database connection in `DatabaseConnection.java` if needed:
```java
// Uses environment variables for security:
// DB_URL, DB_USERNAME, DB_PASSWORD
// Or defaults to:
private static final String URL = "jdbc:mysql://localhost:3306/agriflow";
private static final String USERNAME = "root";
private static final String PASSWORD = "";
```

**For production:** Set environment variables:
```bash
export DB_URL="jdbc:mysql://localhost:3306/agriflow"
export DB_USERNAME="your_db_user"
export DB_PASSWORD="your_secure_password"
```

### Build & Run

1. Clone the repository:
```bash
git clone https://github.com/JERBIAmenallah/ESPRIT-PI-3A14-2026-AgriFlow.git
cd ESPRIT-PI-3A14-2026-AgriFlow
```

2. Build the project:
```bash
mvn clean install
```

3. Run the application:
```bash
mvn javafx:run
```

## Database Schema

### User Table
- id, username, email, password, full_name, phone
- Tracks user accounts for authentication

### Annonce Table
- id, title, description, price, type, category, image_path, location
- status, availability_start, availability_end, user_id
- Stores marketplace listings

### Reservation Table
- id, annonce_id, renter_user_id, start_date, end_date
- status, total_price
- Tracks P2P bookings and purchases

## Key Features Implementation

### Search & Filter
- Keyword search (title/description)
- Filter by type (Location/Vente)
- Filter by category (Tracteur, Moissonneuse, etc.)
- Price range filtering
- Location filtering

### P2P Business Logic
- When a reservation is created:
  1. Reservation is automatically confirmed
  2. Annonce status is updated to LOUE or VENDU
  3. Transaction is atomic (both or neither)

### Date Validation
- Rental periods must be within availability range
- End date must be after start date
- Date pickers are restricted to available dates

### Security
- All database queries use prepared statements
- SQL injection prevention
- Input validation on all forms

## Testing

Sample data is included in the schema file for testing:
- 2 sample users (farmer1, farmer2)
- 3 sample listings (tracteur, moissonneuse, semences)

Default credentials (for testing):
- Username: farmer1 / farmer2
- Password: password123

**IMPORTANT:** These are plain text passwords for TESTING ONLY. In production, implement proper password hashing (BCrypt, Argon2) and secure authentication.

## Future Enhancements
- Image upload and storage
- Payment integration
- User authentication system
- Rating and review system
- Real-time notifications
- Mobile application

## License
This project is part of ESPRIT academic curriculum.

## Contributors
- ESPRIT PI 3A14 Team
- Academic Year: 2026