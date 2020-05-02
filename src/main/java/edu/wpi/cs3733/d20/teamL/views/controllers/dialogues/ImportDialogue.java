package edu.wpi.cs3733.d20.teamL.views.controllers.dialogues;

import com.google.inject.Inject;
import com.jfoenix.controls.JFXComboBox;
import edu.wpi.cs3733.d20.teamL.services.db.DBConstants;
import edu.wpi.cs3733.d20.teamL.services.db.IDatabaseService;
import edu.wpi.cs3733.d20.teamL.services.db.SQLEntry;
import edu.wpi.cs3733.d20.teamL.util.FXMLLoaderHelper;
import edu.wpi.cs3733.d20.teamL.util.io.CSVHelper;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

import java.io.File;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ImportDialogue {
    private final Map<String, String> tables = new HashMap<>();

    @Inject
    private IDatabaseService dbService;
    private FXMLLoaderHelper loaderHelper = new FXMLLoaderHelper();

    @FXML
    private BorderPane root;
    @FXML
    private JFXComboBox<String> tableSelector;
    @FXML
    private Label message;

    @FXML
    private void initialize() {
        populateTables();

        tableSelector.getItems().addAll(tables.keySet());
    }

    private void populateTables() { // TODO: Change these to the queries for replacing the table
        tables.put("Nodes", DBConstants.SELECT_ALL_NODES);
        tables.put("Edges", DBConstants.SELECT_ALL_EDGES);
        tables.put("Users", DBConstants.SELECT_ALL_USERS);
        tables.put("Doctors", DBConstants.SELECT_ALL_DOCTORS);
        tables.put("Gifts", DBConstants.SELECT_ALL_GIFTS);
    }

    @FXML
    private void exportClicked() {
        String selected = tableSelector.getSelectionModel().getSelectedItem();

        if (selected != null) {

            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV files .csv", "*.csv"));
            fileChooser.setTitle("Load " + selected + " Table");

            File loadedFile = fileChooser.showSaveDialog(root.getScene().getWindow());

            CSVHelper csvHelper = new CSVHelper();
            ArrayList<ArrayList<String>> importedTable = csvHelper.readCSVFile(loadedFile, false);
            // Replace database table with csv file

        }else {
            showErrorMessage("Please select a table to import to");
        }
    }

    /**
     * Shows a message in white
     *
     * @param msg
     */
    private void showMessage(String msg) {
        message.setTextFill(Color.WHITE);
        message.setText(msg);

        loaderHelper.showAndFade(message);
    }

    /**
     * Shows a message in red
     *
     * @param msg
     */
    private void showErrorMessage(String msg) {
        message.setTextFill(Color.RED);
        message.setText(msg);

        loaderHelper.showAndFade(message);
    }
}
