package sagiri;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * A GUI for Sagiri using FXML.
 */
public class Main extends Application {

    private final Sagiri sagiri = new Sagiri();

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane ap = fxmlLoader.load();
            Scene scene = new Scene(ap);
            stage.setTitle("Sagiri");
            stage.setScene(scene);
            fxmlLoader.<MainWindow>getController().setSagiri(sagiri);
            stage.show();
        } catch (IOException e) {
            throw new IllegalStateException("Failed to start Sagiri UI", e);
        }
    }
}
