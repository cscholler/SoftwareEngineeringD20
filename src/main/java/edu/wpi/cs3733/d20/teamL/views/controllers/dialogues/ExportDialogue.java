package edu.wpi.cs3733.d20.teamL.views.controllers.dialogues;

import com.google.inject.Inject;
import com.jfoenix.controls.JFXComboBox;
import edu.wpi.cs3733.d20.teamL.services.db.DBConstants;
import edu.wpi.cs3733.d20.teamL.services.db.IDatabaseService;
import edu.wpi.cs3733.d20.teamL.services.db.SQLEntry;
import edu.wpi.cs3733.d20.teamL.util.io.CSVHelper;
import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ExportDialogue {

    private final Map<String, String> tables = new HashMap<>();

    @Inject
    private IDatabaseService dbService;

    @FXML
    private BorderPane root;
    @FXML
    private JFXComboBox<String> tableSelector;

    @FXML
    private void initialize() {
        populateTables();

        tableSelector.getItems().addAll(tables.keySet());
    }

    private void populateTables() {
        tables.put("Nodes", DBConstants.SELECT_ALL_NODES);
        tables.put("Edges", DBConstants.SELECT_ALL_EDGES);
        tables.put("Users", DBConstants.SELECT_ALL_USERS);
    }

    @FXML
    private void exportClicked() {
        String selected = tableSelector.getSelectionModel().getSelectedItem();

        ArrayList<ArrayList<String>> dbTable =
                dbService.getTableFromResultSet(dbService.executeQuery(new SQLEntry(tables.get(selected))));

        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV files .csv", "*.csv"));
        fileChooser.setTitle("Save " + selected + " Table");
        fileChooser.setInitialFileName(selected + "_table");

        File savedFile = fileChooser.showSaveDialog(root.getScene().getWindow());

        CSVHelper csvHelper = new CSVHelper();
        csvHelper.writeToCSV(savedFile.getPath(), dbTable);
    }

}
