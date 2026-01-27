package tn.esprit.agriflow.models;

import tn.esprit.agriflow.models.enums.CategorieAnnonce;
import tn.esprit.agriflow.models.enums.StatutAnnonce;
import tn.esprit.agriflow.models.enums.TypeAnnonce;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Annonce entity - represents a marketplace listing
 */
public class Annonce {
    private int id;
    private String title;
    private String description;
    private double price;
    private TypeAnnonce type;
    private CategorieAnnonce category;
    private String imagePath;
    private String location;
    private StatutAnnonce status;
    private LocalDate availabilityStart;
    private LocalDate availabilityEnd;
    private int userId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Additional field for displaying user info (not in DB)
    private User owner;
    
    // Constructors
    public Annonce() {
        this.status = StatutAnnonce.DISPONIBLE;
    }
    
    public Annonce(String title, String description, double price, TypeAnnonce type, 
                   CategorieAnnonce category, String location, int userId) {
        this.title = title;
        this.description = description;
        this.price = price;
        this.type = type;
        this.category = category;
        this.location = location;
        this.userId = userId;
        this.status = StatutAnnonce.DISPONIBLE;
    }
    
    // Getters and Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public double getPrice() {
        return price;
    }
    
    public void setPrice(double price) {
        this.price = price;
    }
    
    public TypeAnnonce getType() {
        return type;
    }
    
    public void setType(TypeAnnonce type) {
        this.type = type;
    }
    
    public CategorieAnnonce getCategory() {
        return category;
    }
    
    public void setCategory(CategorieAnnonce category) {
        this.category = category;
    }
    
    public String getImagePath() {
        return imagePath;
    }
    
    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
    
    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
    
    public StatutAnnonce getStatus() {
        return status;
    }
    
    public void setStatus(StatutAnnonce status) {
        this.status = status;
    }
    
    public LocalDate getAvailabilityStart() {
        return availabilityStart;
    }
    
    public void setAvailabilityStart(LocalDate availabilityStart) {
        this.availabilityStart = availabilityStart;
    }
    
    public LocalDate getAvailabilityEnd() {
        return availabilityEnd;
    }
    
    public void setAvailabilityEnd(LocalDate availabilityEnd) {
        this.availabilityEnd = availabilityEnd;
    }
    
    public int getUserId() {
        return userId;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public User getOwner() {
        return owner;
    }
    
    public void setOwner(User owner) {
        this.owner = owner;
    }
    
    @Override
    public String toString() {
        return "Annonce{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", price=" + price +
                ", type=" + type +
                ", category=" + category +
                ", location='" + location + '\'' +
                ", status=" + status +
                '}';
    }
}
