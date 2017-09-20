package app.game.gui;

import app.Solitaire;
import app.game.Delta;
import app.game.Game;
import app.game.gui.dialog.DialogFX;
import app.game.model.cards.Card;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Arrays;

public class GameController {

    @FXML
    private Label msg_box;

    @FXML
    private Button btn_new;

    @FXML
    private Button btn_minimize;

    @FXML
    private AnchorPane rootPane;

    @FXML
    private HBox menuBox;

    @FXML
    private Button btn_save;

    @FXML
    private Button btn_undo;

    @FXML
    private Button btn_hint;

    @FXML
    private Pane contentPane;

    @FXML
    private Button btn_close;

    private Game game;
    private Label[] wLabel = new Label[2];
    private Label[] fLabel = new Label[4];
    private Label[] tLabel = new Label[7];
    private int undoSize = 0;

    @FXML
    void btn_new_click() {
        // TODO dialog, asking if you want to save old game?
        if (undoSize != game.getUndoSize()) {
            int result = new DialogFX(DialogFX.Type.QUESTION)
                    .setTitleText("New Game")
                    .setMessage("You have unsaved progress.\nDo you want to loose it?")
                    .addStyleSheetFile("/dialogFX.css")
                    .showDialog();
            if (result == 0) startNewGame();
        } else startNewGame();
    }

    @FXML
    void btn_save_click() {
        // Starting saving dialog session
        String result = saveDialog("Choose filename:");

        if (result != null) { // Save command
            msg_box.setText("Game has been saved!");
            saveGameAsFile(game, result);
        }
    }

    @FXML
    void btn_hint_click() {
        msg_box.setText("Not supported yet.");
    }

    @FXML
    void btn_undo_click() {
        game.undo();
    }

    @FXML
    void btn_minimize_click() {
        Solitaire.minimizeWindow(rootPane, true);
    }

    @FXML
    void btn_close_click() {
        // Prompt
        int result = new DialogFX(DialogFX.Type.CUSTOM)
                .setTitleText("Solitiare")
                .setMessage("Do you really want to exit game?\nUnsaved progress will be lost.")
                .setIcon("/dialog-question.png")
                .addButtons(Arrays.asList("Yes", "Return to menu"), 0)
                .addStyleSheetFile("/dialogFX.css")
                .showDialog();

        if (result == 0) Platform.exit(); // Exit
        else if (result == -1) return; // Cancelled

        try { // Return to menu
            Solitaire.rootFrame(new Stage());
            Solitaire.closeWindow(rootPane);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void initialize() {
        assert msg_box != null : "fx:id=\"msg_box\" was not injected: check your FXML file 'GameFrame.fxml'.";
        assert btn_new != null : "fx:id=\"btn_new\" was not injected: check your FXML file 'GameFrame.fxml'.";
        assert btn_minimize != null : "fx:id=\"btn_minimize\" was not injected: check your FXML file 'GameFrame.fxml'.";
        assert rootPane != null : "fx:id=\"rootPane\" was not injected: check your FXML file 'GameFrame.fxml'.";
        assert menuBox != null : "fx:id=\"menuBox\" was not injected: check your FXML file 'GameFrame.fxml'.";
        assert btn_save != null : "fx:id=\"btn_save\" was not injected: check your FXML file 'GameFrame.fxml'.";
        assert btn_undo != null : "fx:id=\"btn_undo\" was not injected: check your FXML file 'GameFrame.fxml'.";
        assert btn_hint != null : "fx:id=\"btn_hint\" was not injected: check your FXML file 'GameFrame.fxml'.";
        assert contentPane != null : "fx:id=\"contentPane\" was not injected: check your FXML file 'GameFrame.fxml'.";
        assert btn_close != null : "fx:id=\"btn_close\" was not injected: check your FXML file 'GameFrame.fxml'.";

        // Dragging whole root window by label (title)
        final Delta dragDelta = new Delta();
        menuBox.setOnMousePressed(mouseEvent -> Solitaire.windowMousePressed(mouseEvent, rootPane, dragDelta));
        menuBox.setOnMouseDragged(mouseEvent -> Solitaire.windowMouseDragged(mouseEvent, rootPane, dragDelta));
        msg_box.setOnMousePressed(mouseEvent -> Solitaire.windowMousePressed(mouseEvent, rootPane, dragDelta));
        msg_box.setOnMouseDragged(mouseEvent -> Solitaire.windowMouseDragged(mouseEvent, rootPane, dragDelta));

        // initialize board
        initPiles(wLabel, 2);
        initPiles(fLabel, 4);
        initPiles(tLabel, 7);
        initPilesPosition();
    }

    private void initPiles(Label[] a, int n) {
        ImageView local;

        for (int i = 0; i < n; i++) {
            local = new ImageView("/cards/empty.png");
            local.setPreserveRatio(true);
            local.setSmooth(true);
            local.setFitHeight(Card.Dimension.HEIGHT);
            local.setFitWidth(Card.Dimension.WIDTH);

            a[i] = new Label();
            a[i].prefHeight(Card.Dimension.HEIGHT);
            a[i].prefWidth(Card.Dimension.WIDTH);
            a[i].setGraphic(local);

            contentPane.getChildren().add(a[i]);
        }
    }

    private void initPilesPosition() {
        double offsetX = 60;
        double offsetY = 20;
        double spacing = 10;

        // Main Deck
        wLabel[0].setLayoutX(offsetX);
        wLabel[0].setLayoutY(offsetY);
        // Waste Pile
        wLabel[1].setLayoutX(offsetX + Card.Dimension.WIDTH + spacing);
        wLabel[1].setLayoutY(offsetY);

        // Foundation piles
        double foundationOffset = offsetX + (3 * (Card.Dimension.WIDTH + spacing));
        for (int i = 0; i < 4; i++) {
            fLabel[i].setLayoutX(foundationOffset + (i * (Card.Dimension.WIDTH + spacing)));
            fLabel[i].setLayoutY(offsetY);
        }

        // Tableau Piles
        for (int i = 0; i < 7; i++) {
            tLabel[i].setLayoutX(offsetX + (i * (Card.Dimension.WIDTH + spacing)));
            tLabel[i].setLayoutY(2 * offsetY + Card.Dimension.HEIGHT);
        }
    }

    public void setGame(Game game) {
        this.game = game;
        this.game.initializeMoves();
        this.game.setBoard(contentPane, wLabel, fLabel, tLabel, msg_box);
    }

    private void startNewGame() {
        msg_box.setText("Welcome to the Solitaire 2");
        contentPane.getChildren().clear();
        initPiles(wLabel, 2);
        initPiles(fLabel, 4);
        initPiles(tLabel, 7);
        initPilesPosition();
        setGame(new Game());
        undoSize = 0;
    }

    private String saveDialog(String msg) {
        DialogFX.CallBack cc = new DialogFX.CallBack() {
            private String text;
            @Override
            public void setInput(String input) {
                text = input;
            }
            @Override
            public String getInput() {
                return text;
            }
        };

        int result = new DialogFX(DialogFX.Type.INPUT)
                .setTitleText("Save Game")
                .setMessage(msg)
                .addStyleSheetFile("/dialogFX.css")
                .setInputPrompt("filename")
                .setInputCallBack(cc)
                .showDialog();

        if (result == -1 || cc.getInput().length() == 0) return null;

        // Opening/Creating folder for save files
        File file = new File(Solitaire.SAVE_PATH);
        File[] files = file.listFiles();
        if (!file.exists()) file.mkdir();

        // Checking
        boolean sameName = false;
        if (files != null) {
            if (files.length == 0) return cc.getInput(); // Empty folder, everything is fine
            for (File f : files) {
                if (f.isFile()) {
                    String name = f.getName().replaceAll(".save$", "");
                    if (name.equals(cc.getInput())) {
                        sameName = true;
                        break;
                    }
                }
            }
        }

        // Saving behavior
        if (sameName) {
            result = new DialogFX(DialogFX.Type.QUESTION)
                    .setTitleText("Warning !")
                    .setMessage("Save already exist.\nDo you want to override it?")
                    .addStyleSheetFile("/dialogFX.css")
                    .showDialog();
            if (result == 0) return cc.getInput(); // Override
            else return saveDialog("Try another filename:"); // Change
        } else return cc.getInput(); // Everything is fine
    }

    private void saveGameAsFile(Game game, String filename) {
        try {
            FileOutputStream saveFile = new FileOutputStream(Solitaire.SAVE_PATH + filename + ".save");
            ObjectOutputStream save = new ObjectOutputStream(saveFile);
            save.writeObject(game);
            save.close();
            undoSize = game.getUndoSize();
        } catch (IOException e) {
            e.printStackTrace();
            msg_box.setText("Game was not saved. Error while saving.");
            new DialogFX(DialogFX.Type.ERROR)
                    .setTitleText("Save Game")
                    .setMessage("Saving game failed!")
                    .addStyleSheetFile("/dialogFX.css")
                    .showDialog();
        }
    }
}