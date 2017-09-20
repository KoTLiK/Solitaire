package app.game.gui;

import app.Solitaire;
import app.game.Delta;
import app.game.Game;
import app.game.gui.dialog.LoadDialog;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class RootController {

    @FXML
    private Button btn_loadGame;

    @FXML
    private Button btn_exitGame;

    @FXML
    private Label title;

    @FXML
    private AnchorPane contentPane;

    @FXML
    private Button btn_newGame;

    private Stage loadDialog;

    @FXML
    void btn_newGame_click() {
        try {
            Solitaire.gameStart(new Stage(), new Game());
        } catch (Exception e) {
            e.printStackTrace();
        }

        Solitaire.closeWindow(contentPane);
        if (btn_loadGame.isDisabled()) loadDialog.close();
    }

    @FXML
    void btn_loadGame_click() {
        try {
            loadDialog = new Stage();
            loadDialog(loadDialog, this);
            btn_loadGame.setDisable(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void btn_exitGame_click() {
        Platform.exit();
    }

    @FXML
    void initialize() {
        assert btn_loadGame != null : "fx:id=\"btn_loadGame\" was not injected: check your FXML file 'RootFrame.fxml'.";
        assert btn_exitGame != null : "fx:id=\"btn_exitGame\" was not injected: check your FXML file 'RootFrame.fxml'.";
        assert title != null : "fx:id=\"title\" was not injected: check your FXML file 'RootFrame.fxml'.";
        assert contentPane != null : "fx:id=\"contentPane\" was not injected: check your FXML file 'RootFrame.fxml'.";
        assert btn_newGame != null : "fx:id=\"btn_newGame\" was not injected: check your FXML file 'RootFrame.fxml'.";

        // Dragging whole root window by label (title)
        final Delta dragDelta = new Delta();
        title.setOnMousePressed(mouseEvent -> Solitaire.windowMousePressed(mouseEvent, contentPane, dragDelta));
        title.setOnMouseDragged(mouseEvent -> {
            Solitaire.windowMouseDragged(mouseEvent, contentPane, dragDelta);
            // Copying movement of main stage
            if (loadDialog != null) {
                loadDialog.setX(mouseEvent.getScreenX() + dragDelta.x
                        + contentPane.getScene().getWindow().getWidth());
                loadDialog.setY(mouseEvent.getScreenY() + dragDelta.y);
            }
        });
    }

    private void loadDialog(Stage stage, final RootController controller) throws Exception {
        FXMLLoader loader = new FXMLLoader(Solitaire.class.getResource("game/gui/dialog/LoadDialog.fxml"));
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setScene(new Scene(loader.load(), 400, 260));
        // Set position
        stage.setX(contentPane.getScene().getWindow().getX());
        stage.setY(contentPane.getScene().getWindow().getY());
        contentPane.getScene().getWindow().setX(
                contentPane.getScene().getWindow().getX() - contentPane.getScene().getWindow().getWidth());
        // Connection with main stage
        LoadDialog dialog = loader.getController();
        dialog.setController(controller);

        stage.getScene().getStylesheets().add("/load_dialog.css");
        stage.show();
    }

    public void unlockLoad() {
        contentPane.getScene().getWindow().centerOnScreen();
        btn_loadGame.setDisable(false);
    }

    public void closeWindow() {
        Solitaire.closeWindow(contentPane);
    }
}
