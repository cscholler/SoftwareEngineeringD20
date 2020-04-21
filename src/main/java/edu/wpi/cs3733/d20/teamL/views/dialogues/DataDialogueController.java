package edu.wpi.cs3733.d20.teamL.views.dialogues;

import java.io.File;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;

public class DataDialogueController {

    DataDialogue owner;
	private boolean isSaving = false;

    @FXML
    private TextField nodeField;
    @FXML
    private TextField edgeField;
    @FXML
	private Button confirmBtn;

    private final FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("CSV files .csv", "*.csv");

    @FXML
    private void cancel(ActionEvent event) {
        owner.stage.close();
    }

    @FXML
    private void confirm(ActionEvent event) {
        owner.nodeFile = new File(nodeField.getText());
        if(edgeField.getText().length() > 0) owner.edgeFile = new File(edgeField.getText());
        owner.stage.close();
    }

    @FXML
    private void fillNode() {
        fillField(nodeField);
    }

    @FXML
    private void fillEdge() {
        fillField(edgeField);
    }

    private void fillField(TextField field) {
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(filter);
        chooser.setSelectedExtensionFilter(filter);
		File chosenFile;
        if (!isSaving()) {
        	chosenFile = chooser.showOpenDialog(owner.stage);
		} else {
			chosenFile = chooser.showSaveDialog(owner.stage);
		}
        if (chosenFile != null) field.setText(chosenFile.getPath());
    }

	public boolean isSaving() {
		return isSaving;
	}

	public void setSaving(boolean saving) {
    	if (saving) {
    		confirmBtn.setText("Save");
		} else {
			confirmBtn.setText("Open");
		}
		isSaving = saving;
	}
}
