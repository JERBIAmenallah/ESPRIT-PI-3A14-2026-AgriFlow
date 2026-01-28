import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Point d'entrée de l'application AGRIFLOW - Module Marketplace.
 * Lance l'interface JavaFX.
 */
public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Charger la vue principale
        Parent root = FXMLLoader.load(getClass().getResource("/views/Marketplace.fxml"));

        // Configurer la fenêtre
        primaryStage.setTitle("🌾 AGRIFLOW - Marketplace");
        primaryStage.setScene(new Scene(root, 1200, 700));
        primaryStage.setMinWidth(900);
        primaryStage.setMinHeight(600);

        // Afficher
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
