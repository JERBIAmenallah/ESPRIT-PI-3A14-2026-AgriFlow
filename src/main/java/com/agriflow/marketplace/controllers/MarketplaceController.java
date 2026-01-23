package com.agriflow.marketplace.controllers;

import com.agriflow.marketplace.entities.Equipement;
import com.agriflow.marketplace.services.ServiceEquipement;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Contrôleur pour l'interface Marketplace.
 * Gère l'affichage et les interactions utilisateur pour la gestion des équipements.
 * 
 * @author AgriFlow Team
 * @version 1.0
 */
public class MarketplaceController implements Initializable {

    // =========================================================================
    // Composants FXML - TableView
    // =========================================================================
    
    @FXML
    private TableView<Equipement> tableEquipements;
    
    @FXML
    private TableColumn<Equipement, Integer> colId;
    
    @FXML
    private TableColumn<Equipement, String> colNom;
    
    @FXML
    private TableColumn<Equipement, String> colType;
    
    @FXML
    private TableColumn<Equipement, Double> colPrix;
    
    @FXML
    private TableColumn<Equipement, Boolean> colDisponibilite;
    
    @FXML
    private TableColumn<Equipement, Integer> colAgriculteur;

    // =========================================================================
    // Composants FXML - Formulaire
    // =========================================================================
    
    @FXML
    private TextField txtNom;
    
    @FXML
    private ComboBox<String> comboType;
    
    @FXML
    private TextField txtPrix;
    
    @FXML
    private CheckBox checkDisponible;
    
    @FXML
    private TextField txtAgriculteur;
    
    @FXML
    private Label lblMessage;

    // =========================================================================
    // Attributs
    // =========================================================================
    
    /** Service pour les opérations CRUD sur les équipements */
    private ServiceEquipement serviceEquipement;
    
    /** Liste observable pour la TableView */
    private ObservableList<Equipement> equipementsList;
    
    /** Équipement actuellement sélectionné pour modification */
    private Equipement equipementSelectionne;

    // =========================================================================
    // Initialisation
    // =========================================================================
    
    /**
     * Méthode d'initialisation appelée automatiquement après le chargement du FXML.
     * Configure la TableView et charge les données initiales.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialisation du service
        serviceEquipement = new ServiceEquipement();
        equipementsList = FXCollections.observableArrayList();
        
        // Configuration des colonnes de la TableView
        configurerTableView();
        
        // Chargement des données
        chargerEquipements();
        
        // Configuration du listener de sélection
        configurerSelectionListener();
        
        // Message de bienvenue
        afficherMessage("Bienvenue dans la Marketplace AgriFlow ! Sélectionnez un équipement ou ajoutez-en un nouveau.", false);
    }

    /**
     * Configure les colonnes de la TableView avec les propriétés de l'entité Equipement.
     */
    private void configurerTableView() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colPrix.setCellValueFactory(new PropertyValueFactory<>("prixLocation"));
        colAgriculteur.setCellValueFactory(new PropertyValueFactory<>("idAgriculteur"));
        
        // Formatage personnalisé pour la colonne Disponibilité
        colDisponibilite.setCellValueFactory(new PropertyValueFactory<>("disponibilite"));
        colDisponibilite.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Boolean disponible, boolean empty) {
                super.updateItem(disponible, empty);
                if (empty || disponible == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(disponible ? "✅ Oui" : "❌ Non");
                    setStyle(disponible ? "-fx-text-fill: green;" : "-fx-text-fill: red;");
                }
            }
        });
        
        // Formatage personnalisé pour la colonne Prix
        colPrix.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Double prix, boolean empty) {
                super.updateItem(prix, empty);
                if (empty || prix == null) {
                    setText(null);
                } else {
                    setText(String.format("%.2f €", prix));
                }
            }
        });
        
        tableEquipements.setItems(equipementsList);
    }

    /**
     * Configure le listener pour la sélection d'un équipement dans la TableView.
     * Remplit le formulaire avec les données de l'équipement sélectionné.
     */
    private void configurerSelectionListener() {
        tableEquipements.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> {
                if (newValue != null) {
                    equipementSelectionne = newValue;
                    remplirFormulaire(newValue);
                    afficherMessage("Équipement sélectionné : " + newValue.getNom(), false);
                }
            }
        );
    }

    /**
     * Charge tous les équipements depuis la base de données.
     */
    private void chargerEquipements() {
        try {
            equipementsList.clear();
            equipementsList.addAll(serviceEquipement.afficher());
            afficherMessage(equipementsList.size() + " équipement(s) chargé(s).", false);
        } catch (SQLException e) {
            afficherMessage("Erreur lors du chargement des équipements : " + e.getMessage(), true);
        }
    }

    /**
     * Remplit le formulaire avec les données d'un équipement.
     * 
     * @param equipement L'équipement dont les données seront affichées
     */
    private void remplirFormulaire(Equipement equipement) {
        txtNom.setText(equipement.getNom());
        comboType.setValue(equipement.getType());
        txtPrix.setText(String.valueOf(equipement.getPrixLocation()));
        checkDisponible.setSelected(equipement.isDisponibilite());
        txtAgriculteur.setText(String.valueOf(equipement.getIdAgriculteur()));
    }

    // =========================================================================
    // Gestionnaires d'événements
    // =========================================================================
    
    /**
     * Gère l'ajout d'un nouvel équipement.
     */
    @FXML
    private void handleAjouter() {
        // Validation des champs
        if (!validerFormulaire()) {
            return;
        }
        
        try {
            // Création de l'objet Equipement
            Equipement equipement = new Equipement(
                txtNom.getText().trim(),
                comboType.getValue(),
                Double.parseDouble(txtPrix.getText().trim()),
                checkDisponible.isSelected(),
                Integer.parseInt(txtAgriculteur.getText().trim())
            );
            
            // Ajout via le service
            serviceEquipement.ajouter(equipement);
            
            // Rafraîchissement et message de succès
            chargerEquipements();
            viderFormulaire();
            afficherMessage("✅ Équipement ajouté avec succès : " + equipement.getNom(), false);
            
        } catch (NumberFormatException e) {
            afficherMessage("❌ Erreur : Veuillez entrer des valeurs numériques valides.", true);
        } catch (SQLException e) {
            afficherMessage("❌ Erreur lors de l'ajout : " + e.getMessage(), true);
        }
    }

    /**
     * Gère la modification d'un équipement existant.
     */
    @FXML
    private void handleModifier() {
        // Vérification qu'un équipement est sélectionné
        if (equipementSelectionne == null) {
            afficherMessage("⚠️ Veuillez d'abord sélectionner un équipement à modifier.", true);
            return;
        }
        
        // Validation des champs
        if (!validerFormulaire()) {
            return;
        }
        
        try {
            // Mise à jour de l'objet avec les nouvelles valeurs
            equipementSelectionne.setNom(txtNom.getText().trim());
            equipementSelectionne.setType(comboType.getValue());
            equipementSelectionne.setPrixLocation(Double.parseDouble(txtPrix.getText().trim()));
            equipementSelectionne.setDisponibilite(checkDisponible.isSelected());
            equipementSelectionne.setIdAgriculteur(Integer.parseInt(txtAgriculteur.getText().trim()));
            
            // Modification via le service
            serviceEquipement.modifier(equipementSelectionne);
            
            // Rafraîchissement et message de succès
            chargerEquipements();
            afficherMessage("✅ Équipement modifié avec succès : " + equipementSelectionne.getNom(), false);
            
        } catch (NumberFormatException e) {
            afficherMessage("❌ Erreur : Veuillez entrer des valeurs numériques valides.", true);
        } catch (SQLException e) {
            afficherMessage("❌ Erreur lors de la modification : " + e.getMessage(), true);
        }
    }

    /**
     * Gère la suppression d'un équipement.
     */
    @FXML
    private void handleDelete() {
        // Vérification qu'un équipement est sélectionné
        Equipement selection = tableEquipements.getSelectionModel().getSelectedItem();
        if (selection == null) {
            afficherMessage("⚠️ Veuillez d'abord sélectionner un équipement à supprimer.", true);
            return;
        }
        
        // Confirmation de suppression
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Supprimer l'équipement ?");
        alert.setContentText("Voulez-vous vraiment supprimer l'équipement \"" + selection.getNom() + "\" ?");
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                serviceEquipement.supprimer(selection.getId());
                chargerEquipements();
                viderFormulaire();
                afficherMessage("✅ Équipement supprimé avec succès.", false);
            } catch (SQLException e) {
                afficherMessage("❌ Erreur lors de la suppression : " + e.getMessage(), true);
            }
        }
    }

    /**
     * Rafraîchit la liste des équipements.
     */
    @FXML
    private void handleRefresh() {
        chargerEquipements();
        afficherMessage("🔄 Liste actualisée.", false);
    }

    /**
     * Vide le formulaire.
     */
    @FXML
    private void handleClear() {
        viderFormulaire();
        equipementSelectionne = null;
        tableEquipements.getSelectionModel().clearSelection();
        afficherMessage("🧹 Formulaire effacé.", false);
    }

    // =========================================================================
    // Méthodes utilitaires
    // =========================================================================
    
    /**
     * Valide les champs du formulaire.
     * 
     * @return true si tous les champs sont valides, false sinon
     */
    private boolean validerFormulaire() {
        StringBuilder erreurs = new StringBuilder();
        
        if (txtNom.getText() == null || txtNom.getText().trim().isEmpty()) {
            erreurs.append("- Le nom est obligatoire.\n");
        }
        
        if (comboType.getValue() == null) {
            erreurs.append("- Le type est obligatoire.\n");
        }
        
        if (txtPrix.getText() == null || txtPrix.getText().trim().isEmpty()) {
            erreurs.append("- Le prix est obligatoire.\n");
        } else {
            try {
                double prix = Double.parseDouble(txtPrix.getText().trim());
                if (prix < 0) {
                    erreurs.append("- Le prix doit être positif.\n");
                }
            } catch (NumberFormatException e) {
                erreurs.append("- Le prix doit être un nombre valide.\n");
            }
        }
        
        if (txtAgriculteur.getText() == null || txtAgriculteur.getText().trim().isEmpty()) {
            erreurs.append("- L'ID du propriétaire est obligatoire.\n");
        } else {
            try {
                Integer.parseInt(txtAgriculteur.getText().trim());
            } catch (NumberFormatException e) {
                erreurs.append("- L'ID du propriétaire doit être un nombre entier.\n");
            }
        }
        
        if (erreurs.length() > 0) {
            afficherMessage("❌ Erreurs de validation :\n" + erreurs, true);
            return false;
        }
        
        return true;
    }

    /**
     * Vide tous les champs du formulaire.
     */
    private void viderFormulaire() {
        txtNom.clear();
        comboType.setValue(null);
        txtPrix.clear();
        checkDisponible.setSelected(true);
        txtAgriculteur.clear();
    }

    /**
     * Affiche un message dans le label de message.
     * 
     * @param message Le message à afficher
     * @param isError true si c'est un message d'erreur, false sinon
     */
    private void afficherMessage(String message, boolean isError) {
        lblMessage.setText(message);
        if (isError) {
            lblMessage.setStyle("-fx-background-color: #ffebee; -fx-text-fill: #c62828; -fx-padding: 10px; -fx-background-radius: 4px;");
        } else {
            lblMessage.setStyle("-fx-background-color: #e8f5e9; -fx-text-fill: #2e7d32; -fx-padding: 10px; -fx-background-radius: 4px;");
        }
    }
}
