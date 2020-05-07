package edu.wpi.cs3733.d20.teamL.views.controllers.dialogues;

import com.google.inject.Inject;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXComboBox;
import edu.wpi.cs3733.d20.teamL.App;
import edu.wpi.cs3733.d20.teamL.services.db.IDatabaseService;
import edu.wpi.cs3733.d20.teamL.util.AsyncTaskManager;
import edu.wpi.cs3733.d20.teamL.util.FXMLLoaderFactory;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;

import java.io.File;
import java.sql.SQLException;

public class ImportDialogue {
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
	private JFXCheckBox appendCheckbox;

    @FXML
    private void importClicked() {
    	boolean doAppend = appendCheckbox.isSelected();
        String selected = tableSelector.getSelectionModel().getSelectedItem();

        if (selected != null) {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV files .csv", "*.csv"));
            fileChooser.setTitle("Load " + selected + " table file");

            File loadedFile = fileChooser.showOpenDialog(root.getScene().getWindow());
			AsyncTaskManager.startTaskWithPopup(() -> populateTable(loadedFile, selected, doAppend), "Importing CSV file...", "Finished importing");
        } else {
            showErrorMessage("Please select a table to update");
        }
    }

    private void populateTable(File loadedFile, String selected, boolean doAppend) {
		App.allowCacheUpdates = false;
		try {
			dbService.populateFromCSV(loadedFile, selected, doAppend);
			showMessage(selected + " table updated successfully");
		} catch (SQLException ex) {
			showErrorMessage("Failed to add one or more row(s) to the database");
		}
		App.allowCacheUpdates = true;
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
