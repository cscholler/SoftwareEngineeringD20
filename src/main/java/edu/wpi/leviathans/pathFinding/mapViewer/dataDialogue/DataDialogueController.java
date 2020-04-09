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
        FileChooser nodeChooser = new FileChooser();
        nodeField.setText(nodeChooser.showOpenDialog(owner.stage).getPath());
    }

    @FXML
    private void fillEdge() {
        FileChooser edgeChooser = new FileChooser();
        edgeField.setText(edgeChooser.showOpenDialog(owner.stage).getPath());
    }

}
