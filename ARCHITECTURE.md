# AgriFlow Marketplace Module - Architecture Documentation

## System Architecture

### 1. Three-Tier Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                    PRESENTATION LAYER                        │
│                     (JavaFX Views)                          │
├─────────────────────────────────────────────────────────────┤
│  marketplace.fxml    annonce_form.fxml    annonce_detail.fxml│
│  mes_annonces.fxml   mes_reservations.fxml                   │
└─────────────────────────────────────────────────────────────┘
                              ↓↑
┌─────────────────────────────────────────────────────────────┐
│                     CONTROLLER LAYER                         │
│                     (MVC Controllers)                        │
├─────────────────────────────────────────────────────────────┤
│  MarketplaceController  │  AnnonceFormController             │
│  AnnonceDetailController│  MesAnnoncesController             │
│  MesReservationsController                                   │
└─────────────────────────────────────────────────────────────┘
                              ↓↑
┌─────────────────────────────────────────────────────────────┐
│                      SERVICE LAYER                           │
│                  (Business Logic)                            │
├─────────────────────────────────────────────────────────────┤
│  IService<T> (Generic CRUD Interface)                       │
│  AnnonceService  │  ReservationService                      │
└─────────────────────────────────────────────────────────────┘
                              ↓↑
┌─────────────────────────────────────────────────────────────┐
│                       MODEL LAYER                            │
│                      (Entities)                             │
├─────────────────────────────────────────────────────────────┤
│  User  │  Annonce  │  Reservation                           │
│  TypeAnnonce  │  CategorieAnnonce                           │
│  StatutAnnonce  │  StatutReservation                        │
└─────────────────────────────────────────────────────────────┘
                              ↓↑
┌─────────────────────────────────────────────────────────────┐
│                       DATA LAYER                             │
│                     (MySQL Database)                         │
├─────────────────────────────────────────────────────────────┤
│  user table  │  annonce table  │  reservation table         │
│  DatabaseConnection (Singleton Pattern)                     │
└─────────────────────────────────────────────────────────────┘
```

## 2. P2P Transaction Flow

### Scenario: Farmer B rents equipment from Farmer A

```
Farmer B                     System                        Farmer A
   │                           │                              │
   │  1. Browse Marketplace    │                              │
   ├──────────────────────────>│                              │
   │                           │                              │
   │  2. View Listing Details  │                              │
   ├──────────────────────────>│                              │
   │                           │                              │
   │  3. Click "Reserve"       │                              │
   ├──────────────────────────>│                              │
   │                           │                              │
   │                           │ 4. Create Reservation        │
   │                           │    (Status: EN_ATTENTE)      │
   │                           │                              │
   │                           │ 5. Auto-confirm (P2P)        │
   │                           │    (Status: CONFIRMEE)       │
   │                           │                              │
   │                           │ 6. Update Annonce Status     │
   │                           │    (DISPONIBLE → LOUE)       │
   │                           │                              │
   │  7. Confirmation Message  │                              │
   │<──────────────────────────┤                              │
   │                           │                              │
   │                           │ 8. Annonce now unavailable   │
   │                           │    for other users           │
   │                           │                              │
```

### Key P2P Features:
- ✅ No admin approval required
- ✅ Automatic status updates
- ✅ Atomic transactions (all-or-nothing)
- ✅ Direct farmer-to-farmer interaction

## 3. Database Schema

### Entity Relationships

```
┌──────────────┐
│     User     │
├──────────────┤
│ id (PK)      │
│ username     │
│ email        │
│ password     │
│ full_name    │
│ phone        │
└──────────────┘
       │ 1
       │
       │ owns
       │
       │ *
┌──────────────┐
│   Annonce    │
├──────────────┤
│ id (PK)      │
│ title        │
│ description  │
│ price        │
│ type         │──────> (LOCATION, VENTE)
│ category     │──────> (TRACTEUR, MOISSONNEUSE, etc.)
│ status       │──────> (DISPONIBLE, LOUE, VENDU)
│ user_id (FK) │
└──────────────┘
       │ 1
       │
       │ has
       │
       │ *
┌──────────────┐
│ Reservation  │
├──────────────┤
│ id (PK)      │
│ annonce_id   │────> FK to Annonce
│ renter_id    │────> FK to User
│ start_date   │
│ end_date     │
│ status       │──────> (EN_ATTENTE, CONFIRMEE, ANNULEE)
│ total_price  │
└──────────────┘
```

## 4. Design Patterns Used

### 4.1 Singleton Pattern
**Class:** `DatabaseConnection`
- Ensures single database connection instance
- Thread-safe implementation
- Connection pooling and reuse

```java
public static DatabaseConnection getInstance() {
    if (instance == null || !isConnectionValid()) {
        synchronized (DatabaseConnection.class) {
            if (instance == null || !isConnectionValid()) {
                instance = new DatabaseConnection();
            }
        }
    }
    return instance;
}
```

### 4.2 Generic CRUD Pattern
**Interface:** `IService<T>`
- Reusable across all entity types
- Consistent API for database operations
- Type-safe implementation

```java
public interface IService<T> {
    boolean add(T entity);
    boolean update(T entity);
    boolean delete(int id);
    T getById(int id);
    List<T> getAll();
}
```

### 4.3 MVC Pattern
- **Model:** Entity classes (User, Annonce, Reservation)
- **View:** FXML files (marketplace.fxml, etc.)
- **Controller:** Controller classes (MarketplaceController, etc.)

### 4.4 Service Layer Pattern
- Separates business logic from presentation
- Encapsulates database operations
- Transaction management

## 5. Key Features Implementation

### 5.1 Search & Filter
**Location:** `AnnonceService.search()`
- Multi-criteria filtering
- SQL parameterized queries
- Prevents SQL injection

```java
public List<Annonce> search(String keyword, TypeAnnonce type, 
                           CategorieAnnonce category, 
                           Double minPrice, Double maxPrice, 
                           String location)
```

### 5.2 P2P Booking Logic
**Location:** `ReservationService.add()`
- Transaction-based operation
- Automatic status updates
- Rollback on failure

```java
// Start transaction
connection.setAutoCommit(false);

// 1. Create reservation
// 2. Update annonce status
// 3. Commit or rollback

connection.commit();
```

### 5.3 Date Validation
**Location:** `AnnonceDetailController`
- DatePicker constraints
- Availability range checking
- Duration calculation

```java
startDatePicker.setDayCellFactory(picker -> new DateCell() {
    @Override
    public void updateItem(LocalDate date, boolean empty) {
        super.updateItem(date, empty);
        setDisable(empty || 
                  date.isBefore(annonce.getAvailabilityStart()) ||
                  date.isAfter(annonce.getAvailabilityEnd()));
    }
});
```

## 6. Security Features

### 6.1 SQL Injection Prevention
- All queries use `PreparedStatement`
- Parameterized queries
- No string concatenation in SQL

```java
String query = "INSERT INTO annonce (title, description, ...) VALUES (?, ?, ...)";
PreparedStatement stmt = connection.prepareStatement(query);
stmt.setString(1, annonce.getTitle());
```

### 6.2 Input Validation
- Form validation on client side
- Type checking (price, dates)
- Required field checks
- Business rule validation

## 7. Future Enhancements

### 7.1 Authentication System
- User registration/login
- Session management
- Role-based access control

### 7.2 Image Upload
- File storage service
- Image compression
- CDN integration

### 7.3 Payment Integration
- Payment gateway (e.g., Flouci, D17)
- Transaction history
- Refund management

### 7.4 Notifications
- Email notifications
- Push notifications
- SMS alerts

### 7.5 Rating & Review System
- User ratings
- Product reviews
- Trust scores

### 7.6 Advanced Search
- Geolocation-based search
- Price recommendations
- Similar listings

## 8. Technology Stack

| Component | Technology | Version |
|-----------|-----------|---------|
| Framework | JavaFX | 17.0.2 |
| Database | MySQL | 8.0.33 |
| Build Tool | Maven | 3.6+ |
| Java Version | Java SE | 11+ |
| Testing | JUnit | 5.9.3 |

## 9. Performance Considerations

### 9.1 Database Indexing
- Primary keys on all ID columns
- Foreign key indexes
- Composite indexes for common queries

### 9.2 Connection Management
- Singleton pattern for connection
- Connection validation
- Automatic reconnection

### 9.3 Query Optimization
- Selective column retrieval
- JOIN operations minimized
- Pagination support ready

## 10. Deployment Checklist

- [ ] MySQL database created
- [ ] Schema and sample data loaded
- [ ] Database credentials configured
- [ ] Maven dependencies resolved
- [ ] Application compiled successfully
- [ ] Tests passing
- [ ] FXML files in correct location
- [ ] JavaFX runtime configured
