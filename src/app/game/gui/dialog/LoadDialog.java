package app.game.gui.dialog;

import app.Solitaire;
import app.game.Game;
import app.game.gui.RootController;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class LoadDialog {

    @FXML
    private Button btn_load;

    @FXML
    private Button btn_delete;

    @FXML
    private AnchorPane rootPane;

    @FXML
    private Label title;

    @FXML
    private Label message;

    @FXML
    private TableColumn<FileRow, String> table_date;

    @FXML
    private TableColumn<FileRow, String> table_name;

    @FXML
    private TableView<FileRow> table;

    @FXML
    private Button btn_close;

    private RootController root;
    private ObservableList<FileRow> data = FXCollections.observableArrayList();

    @FXML
    void btn_close_click() {
        Solitaire.closeWindow(rootPane);
        root.unlockLoad();
    }

    @FXML
    void btn_load_click() {
        if (table.getSelectionModel().getSelectedItem() != null) { // Selected row
            String filename = table.getSelectionModel().getSelectedItem().fileName.get();

            try { // Load game and start game session
                Solitaire.gameStart(new Stage(), loadFile(filename));
            } catch (Exception e) {
                e.printStackTrace();
                new DialogFX(DialogFX.Type.ERROR)
                        .setTitleText("Solitaire ERROR")
                        .setMessage("Unable to load game due to\nwrong file or internal error.")
                        .addStyleSheetFile("/dialogFX.css")
                        .showDialog();
                return;
            }
            // Close menu
            Solitaire.closeWindow(rootPane);
            root.closeWindow();
        }
        if (data.size() == 0) message.setText("Not available any saved game.");
    }

    @FXML
    void btn_delete_click() {
        FileRow selectedRow = table.getSelectionModel().getSelectedItem();
        if (selectedRow != null) {
            try {
                Files.delete(Paths.get(Solitaire.SAVE_PATH + selectedRow.fileName.get() + ".save"));
                table.getItems().remove(selectedRow);
                message.setText("Game '" + selectedRow.fileName.get() + "' has been deleted.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    void initialize() {
        assert btn_load != null : "fx:id=\"btn_load\" was not injected: check your FXML file 'test.fxml'.";
        assert btn_delete != null : "fx:id=\"btn_delete\" was not injected: check your FXML file 'test.fxml'.";
        assert table_date != null : "fx:id=\"table_date\" was not injected: check your FXML file 'test.fxml'.";
        assert rootPane != null : "fx:id=\"rootPane\" was not injected: check your FXML file 'test.fxml'.";
        assert title != null : "fx:id=\"title\" was not injected: check your FXML file 'test.fxml'.";
        assert message != null : "fx:id=\"message\" was not injected: check your FXML file 'test.fxml'.";
        assert table_name != null : "fx:id=\"table_name\" was not injected: check your FXML file 'test.fxml'.";
        assert table != null : "fx:id=\"table\" was not injected: check your FXML file 'test.fxml'.";
        assert btn_close != null : "fx:id=\"btn_close\" was not injected: check your FXML file 'test.fxml'.";
        // Setup table view
        table.setEditable(true);
        table_name.setCellValueFactory(new PropertyValueFactory<>("fileName"));
        table_date.setCellValueFactory(new PropertyValueFactory<>("dateFormat"));
        table.setItems(data);

        // Open/Create folder with save files
        File file = new File(Solitaire.SAVE_PATH);
        File[] files = file.listFiles();
        if (!file.exists()) file.mkdir();


        DateFormat df = new SimpleDateFormat("dd.MM.yyyy  HH:mm");
        int count = 0;
        try {
            if (files != null) {
                for (File f : files) {
                    if (f.isFile()) {
                        BasicFileAttributes attr = Files.getFileAttributeView(
                                Paths.get(f.getPath()),
                                BasicFileAttributeView.class
                        ).readAttributes();
                        // Adding rows into table view
                        String name = f.getName().replaceAll(".save$", "");
                        data.add(new FileRow(name, df.format(attr.lastModifiedTime().toMillis())));
                        count++;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (count == 0) message.setText("Not available any saved game.");

        // Visual bugfix
        if (count < 7) table_date.setPrefWidth(153); // Magic number ;)
    }

    public void setController(RootController root) {
        this.root = root;
    }

    public class FileRow {
        private final SimpleStringProperty fileName;
        private final SimpleStringProperty dateFormat;

        public FileRow(String filename, String date) {
            fileName = new SimpleStringProperty(filename);
            dateFormat = new SimpleStringProperty(date);
        }

        public String getFileName() {
            return fileName.get();
        }

        public String getDateFormat() {
            return dateFormat.get();
        }
    }

    private static Game loadFile(String filename) throws Exception {
        FileInputStream loadFile = new FileInputStream(Solitaire.SAVE_PATH + filename + ".save");
        ObjectInputStream loadObject = new ObjectInputStream(loadFile);
        return (Game) loadObject.readObject();
    }
}
