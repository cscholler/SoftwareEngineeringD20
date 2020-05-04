package edu.wpi.cs3733.d20.teamL.views.controllers.dialogues;

import com.google.inject.Inject;
import com.jfoenix.controls.JFXComboBox;
import edu.wpi.cs3733.d20.teamL.App;
import edu.wpi.cs3733.d20.teamL.services.db.DBConstants;
import edu.wpi.cs3733.d20.teamL.services.db.IDatabaseService;
import edu.wpi.cs3733.d20.teamL.services.db.SQLEntry;
import edu.wpi.cs3733.d20.teamL.util.FXMLLoaderFactory;
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

public class ExportDialogue {

    private final Map<String, String> tables = new HashMap<>();

    @Inject
    private IDatabaseService dbService;
    private FXMLLoaderFactory loaderHelper = new FXMLLoaderFactory();

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
        tableSelector.getItems().add("All");
    }

    private void populateTables() {
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
			App.allowCacheUpdates = false;
			if (!selected.equals("All")) {
				ResultSet resultSet = dbService.executeQuery(new SQLEntry(tables.get(selected)));
				ArrayList<ArrayList<String>> dbTable = new ArrayList<>();
				dbTable.add(dbService.getColumnNames(resultSet));
				dbTable.addAll(dbService.getTableFromResultSet(resultSet));

				FileChooser fileChooser = new FileChooser();
				fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV files .csv", "*.csv"));
				fileChooser.setTitle("Save " + selected + " Table");
				fileChooser.setInitialFileName(selected + "_table");

				File savedFile = fileChooser.showSaveDialog(root.getScene().getWindow());

				CSVHelper csvHelper = new CSVHelper();
				csvHelper.writeToCSV(savedFile.getPath(), dbTable);

				showMessage("Saved " + savedFile.getPath());
			} else {
				DirectoryChooser directoryChooser = new DirectoryChooser();
				directoryChooser.setTitle("Save all tables");
				File directory = directoryChooser.showDialog(root.getScene().getWindow());
				for (String table : tables.keySet()) {
					String query = tables.get(table);

					ResultSet resultSet = dbService.executeQuery(new SQLEntry(query));
					ArrayList<ArrayList<String>> dbTable = new ArrayList<>();
					dbTable.add(dbService.getColumnNames(resultSet));
					dbTable.addAll(dbService.getTableFromResultSet(resultSet));

					File savedFile = new File(directory.getPath() + "/" + table + "_table.csv");

					CSVHelper csvHelper = new CSVHelper();
					csvHelper.writeToCSV(savedFile.getPath(), dbTable);
				}
				showMessage("Saved tables to " + directory.getPath());
			}
        } else {
            showErrorMessage("Please select a table to export");
        }
		App.allowCacheUpdates = true;
    }

    private void showMessage(String msg) {
        message.setTextFill(Color.WHITE);
        message.setText(msg);

        loaderHelper.showAndFade(message);
    }

    private void showErrorMessage(String msg) {
        message.setTextFill(Color.RED);
        message.setText(msg);

        loaderHelper.showAndFade(message);
    }
}
