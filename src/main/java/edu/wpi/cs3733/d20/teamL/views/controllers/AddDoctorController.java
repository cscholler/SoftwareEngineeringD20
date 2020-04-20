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

public class AddDoctorController {
    @FXML
    JFXButton btnCancel, btnSubmit;

    @FXML
    JFXTextField fNameText, lNameText, emailText, doctorIDText, officeNumText, addInfoText;

    @FXML
    public void handleButtonAction(ActionEvent e) throws IOException {
        Stage stage;
        Parent root;

        if(e.getSource() == btnCancel){
            stage = (Stage) btnCancel.getScene().getWindow();
            //stage = new Stage();
            root = FXMLLoader.load(getClass().getResource("/edu/wpi/cs3733/d20/teamL/views/AdminView.fxml"));
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.show();

        } else if (e.getSource() == btnSubmit){
            String fName = fNameText.getText();
            String lName = lNameText.getText();
            String patID = emailText.getText();
            String docID = doctorIDText.getText();
            String roomNum = officeNumText.getText();
            String additionalInfo = addInfoText.getText();

        }
    }
}


