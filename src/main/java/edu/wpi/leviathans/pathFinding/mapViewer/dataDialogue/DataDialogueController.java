package edu.wpi.leviathans.pathFinding.mapViewer.dataDialogue;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;

import java.io.File;

public class DataDialogueController {

    DataDialogue owner;

    @FXML
    private TextField nodeField;
    @FXML
    private TextField edgeField;

    private final FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("CSV files .csv", "*.csv");

    @FXML
    private void cancel(ActionEvent event) {
        owner.stage.close();
    }

    @FXML
    private void confirm(ActionEvent event) {
        owner.nodeFile = new File(nodeField.getText());
        owner.edgeFile = new File(edgeField.getText());
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

        File chosenFile = chooser.showOpenDialog(owner.stage);
        if(chosenFile != null) field.setText(chosenFile.getPath());
    }

}
