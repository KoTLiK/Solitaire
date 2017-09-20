package app.game.gui.dialog;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class DialogFX {

    public enum Type { ACCEPT, ERROR, WARNING, INFO, QUESTION, INPUT, CUSTOM, CUSTOMINPUT }

    public interface CallBack {
        void setInput(String input);
        String getInput();
    }

    private Type type;
    private AnchorPane popUp;
    private Stage stage;
    private Scene scene;
    private CallBack callBack;

    // FXMLLoader to load the popup GUI.
    private FXMLLoader fxmlLoader = new FXMLLoader();
    // Instance of the second controller.
    private DialogFXController controller;

    private int buttonSelected = -1;
    private String iconFile = null;
    private String stylesheet = null;
    private List<String> stylesheets = new ArrayList<>();

    public DialogFX() {
        try {
            initDialog(Type.CUSTOM);
        }catch(IOException ex) {
            System.err.println("Unable to initialize the DialogFX");
            System.err.println("Error: " + ex.getMessage());
        }
    }

    public DialogFX(Type t) {
        try {
            initDialog(t);
        }catch(IOException ex) {
            System.err.println("Unable to initialize the DialogFX");
            System.err.println("Error: " + ex.getMessage());
        }
    }

    public DialogFX addButtons(List<String> labels) {
        addButtons(labels, -1, -1);
        return this;
    }

    public DialogFX addButtons(List<String> labels, int defaultBtn) {
        addButtons(labels, defaultBtn, -1);
        return this;
    }

    public DialogFX addButtons(List<String> labels, int defaultBtn, int cancelBtn) {

        for (int i = 0; i < labels.size(); i++) {
            final Button btn = new Button(labels.get(i));
            
            btn.setDefaultButton(i == defaultBtn);
            btn.setCancelButton(i == cancelBtn);

            if (type == Type.INPUT || type == Type.CUSTOMINPUT)
                Platform.runLater(() -> controller.getTextField().requestFocus());
            else if (i == defaultBtn)
                Platform.runLater(btn::requestFocus);

            btn.setOnAction(evt -> {
                    buttonSelected = labels.indexOf(((Button) evt.getSource()).getText());
                    stage.close();
                });
            controller.getButtonHBox().getChildren().add(btn);
            controller.getButtonHBox().setSpacing(5);
        }

        if (cancelBtn == -1)
            controller.getBtnClose().setCancelButton(true);
        controller.getBtnClose().setOnAction(event -> {
            buttonSelected = cancelBtn;
            stage.close();
        });
        return this;
    }

    public DialogFX addStyleSheet(String stylesheet) {
        try {
            String newStyle = this.getClass().getResource(stylesheet).toExternalForm();
            stylesheets.add(newStyle);
        } catch (Exception ex) {
            System.err.println("Unable to find specified stylesheet: " + stylesheet);
            System.err.println("Error: " + ex.getMessage());
        }
        return this;
    }

    public DialogFX addStyleSheetFile(String stylesheet) {
        this.stylesheet = stylesheet;
        return this;
    }

    private void initDialog(Type t) throws IOException {
        stage = new Stage();

        if (t == Type.INPUT || t == Type.CUSTOMINPUT)
            fxmlLoader.setLocation(getClass().getResource("DialogFXinput.fxml"));
        else fxmlLoader.setLocation(getClass().getResource("DialogFX.fxml"));

        popUp = fxmlLoader.load();
        controller = fxmlLoader.getController();

        setType(t);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.initModality(Modality.APPLICATION_MODAL);
    }

    private void loadIconFromResource(String fileName) {
        Image imgIcon = new Image(getClass().getResourceAsStream(fileName));
        controller.getIcon().setPreserveRatio(true);
        controller.getIcon().setFitHeight(32);
        controller.getIcon().setImage(imgIcon);
    }

    public DialogFX setMessage(String msg) {
        controller.getMessage().setText(msg);
        controller.getMessage().setWrapText(true);
        return this;
    }

    public DialogFX setModal(boolean isModal) {
        stage.initModality((isModal ? Modality.APPLICATION_MODAL : Modality.NONE));
        return this;
    }

    public DialogFX setTitleText(String title) {
        controller.getTitle().setText(title);
        return this;
    }

    public DialogFX setType(Type typeToSet) {
        type = typeToSet;
        return this;
    }

    public DialogFX setIcon(String iconFile) {
        this.iconFile = iconFile;
        return this;
    }

    public DialogFX setInputPrompt(String promptText) {
        controller.getTextField().setPromptText(promptText);
        return this;
    }

    public DialogFX setInputCallBack(DialogFX.CallBack callBack) {
        this.callBack = callBack;
        return this;
    }
    
    private void populateStage() {
        
        switch ( type ) {
            case ACCEPT:
                iconFile = "/dialog-ok.png";
                addButtons(Arrays.asList("Ok"), 0, 0);
                break;
            case ERROR:
                iconFile = "/dialog-error.png";
                addButtons(Arrays.asList("Ok"), 0, 0);
                break;
            case WARNING:
                iconFile = "/dialog-warning.png";
                addButtons(Arrays.asList("Ok"), 0, 0);
                break;
            case INFO:
                iconFile = "/dialog-info.png";
                addButtons(Arrays.asList("Ok"), 0, 0);
                break;
            case QUESTION:
                iconFile = "/dialog-question.png";
                addButtons(Arrays.asList("Yes", "No"), 0, 1);
                break;
            case INPUT:
                addButtons(Arrays.asList("Confirm"), 0);
                break;
            case CUSTOM: break;
            case CUSTOMINPUT: break;
            default:
                iconFile = "/dialog-info.png";
                addButtons(Arrays.asList("Ok"), 0, 0);
                break;
        }
        
        try {
            if (iconFile != null) loadIconFromResource(iconFile);
        } catch (Exception ex) {
            System.err.println("Exception trying to load icon file: " + ex.getMessage());
        }

        scene = new Scene(popUp, 300, 130);

        for (String css : stylesheets) {
            try {
                scene.getStylesheets().add(css);
            } catch (Exception ex) {
                System.err.println("Unable to load specified stylesheet: " + css);
                System.err.println(ex.getMessage());
            }
        }

        if (stylesheet != null) scene.getStylesheets().add(stylesheet);

        stage.setScene(scene);
    }

    public int showDialog() {
        populateStage();
        
        stage.setResizable(false);
        stage.sizeToScene();
        stage.centerOnScreen();
        stage.showAndWait();
        if (type == Type.INPUT || type == Type.CUSTOMINPUT)
            callBack.setInput(controller.getTextField().getText());
        return buttonSelected;
    }
}
