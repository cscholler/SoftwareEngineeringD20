package edu.wpi.cs3733.d20.teamL.views.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MedicationReqController {

    @FXML
    JFXButton btnCancel, btnSubmit;

    @FXML
    JFXTextField nameText, medTypeText, doseText, patientText, roomNumText, addInfoText;

    @FXML
    public void handleButtonAction(ActionEvent e) throws IOException {
        Stage stage = null;
        Parent root;

        if(e.getSource() == btnCancel){
            stage = (Stage) btnCancel.getScene().getWindow();
            //stage = new Stage();
            root = FXMLLoader.load(getClass().getResource("/edu/wpi/cs3733/d20/teamL/views/StaffView.fxml"));
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setFullScreen(true);
            stage.show();

        } else if (e.getSource() == btnSubmit){
            String name = nameText.getText();
            String medType = medTypeText.getText();
            String dose = doseText.getText();
            String patient = patientText.getText();
            String roomNum = roomNumText.getText();
            String additionalInfo = addInfoText.getText();

        }
    }
}
