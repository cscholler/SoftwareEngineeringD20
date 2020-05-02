package edu.wpi.cs3733.d20.teamL.views.controllers.requests;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;

import java.io.IOException;

public class SanitationPaneController {
    @FXML
    final ToggleGroup priorityGroup = new ToggleGroup();
    @FXML
    private JFXTextField incidentLocationText;
    @FXML
    private JFXCheckBox bioHazardCheckBox, spillCheckBox;
    @FXML
    private JFXTextArea additionalNotesText;

    public void submitServiceRequest(ActionEvent actionEvent) {
    }
}
