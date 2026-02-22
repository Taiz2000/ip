package sagiri;

import java.io.InputStream;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 * Controller for the main GUI.
 */
public class MainWindow extends AnchorPane {
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private Sagiri sagiri;

    private final Image userImage = loadImageOrPlaceholder("/images/Masamune.jpg");
    private final Image sagiriImage = loadImageOrPlaceholder("/images/Sagiri.jpg");

    /**
     * Initializes the main window, setting up the scroll pane and dialog container.
     */
    @FXML
    public void initialize() {
        scrollPane.setFitToWidth(true);
        dialogContainer.heightProperty().addListener((observable) -> scrollPane.setVvalue(1.0));
        scrollPane.addEventFilter(ScrollEvent.SCROLL, event -> {
            double contentHeight = dialogContainer.getBoundsInLocal().getHeight();
            double viewportHeight = scrollPane.getViewportBounds().getHeight();
            double scrollRange = contentHeight - viewportHeight;

            if (scrollRange <= 0) {
                return;
            }

            double speedMultiplier = 2.0;
            double delta = (event.getDeltaY() * speedMultiplier) / scrollRange;
            double nextValue = scrollPane.getVvalue() - delta;
            scrollPane.setVvalue(Math.max(0.0, Math.min(1.0, nextValue)));
            event.consume();
        });
    }

    /**
     * Initial Sagiri message on ui load
     */
    public void setSagiri(Sagiri s) {
        assert s != null : "Sagiri instance should not be null";
        sagiri = s;
        dialogContainer.getChildren().add(DialogBox.getSagiriDialog(
                "Hi, my name is Sagiri, how can I help you?",
                sagiriImage));
    }

    /**
     * Creates two dialog boxes, one echoing user input and the other containing
     * Sagiri's reply and then appends them to the dialog container.
     * Clears the user input after processing.
     */
    @FXML
    private void handleUserInput() {
        assert sagiri != null : "Sagiri should be set before handling input";
        assert userInput != null : "userInput should be injected from FXML";
        assert sendButton != null : "sendButton should be injected from FXML";
        String input = userInput.getText();
        assert input != null : "TextField should return a non-null input string";
        String response = sagiri.getResponse(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getSagiriDialog(response, sagiriImage)
        );
        userInput.clear();

        if ("bye".equals(input.trim())) {
            userInput.setDisable(true);
            sendButton.setDisable(true);
            PauseTransition delay = new PauseTransition(Duration.seconds(1));
            delay.setOnFinished(event -> Platform.exit());
            delay.play();
        }
    }

    private Image loadImageOrPlaceholder(String resourcePath) {
        InputStream stream = this.getClass().getResourceAsStream(resourcePath);
        if (stream == null) {
            return new WritableImage(100, 100);
        }
        return new Image(stream);
    }
}
