package app.game.gui.dialog;

import app.Solitaire;
import app.game.Delta;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;

import java.net.URL;
import java.util.ResourceBundle;

public class DialogFXController implements Initializable {

    @FXML
    private AnchorPane root;

    @FXML
    private ImageView icon;

    @FXML
    private Label title;

    @FXML
    private Label message;

    @FXML
    private TextField textField;

    @FXML
    private HBox buttonHBox;

    @FXML
    private Button btnClose;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Delta delta = new Delta();
        title.setOnMousePressed(mouseEvent -> Solitaire.windowMousePressed(mouseEvent, root, delta));
        title.setOnMouseDragged(mouseEvent -> Solitaire.windowMouseDragged(mouseEvent, root, delta));
    }

    HBox getButtonHBox() {
        return buttonHBox;
    }

    ImageView getIcon() {
        return icon;
    }

    Label getMessage() {
        return message;
    }

    Label getTitle() {
        return title;
    }

    Button getBtnClose() {
        return btnClose;
    }

    TextField getTextField() {
        return textField;
    }
}
