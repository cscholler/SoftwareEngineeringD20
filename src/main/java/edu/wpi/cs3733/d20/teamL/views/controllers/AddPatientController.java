package edu.wpi.cs3733.d20.teamL.views.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;

import edu.wpi.cs3733.d20.teamL.util.FXMLLoaderHelper;

import java.io.IOException;

public class AddPatientController {
    @FXML
    JFXButton btnCancel, btnSubmit;

    @FXML
    JFXTextField fNameText, lNameText, IDText, doctorIDText, roomNumText, addInfoText;

	FXMLLoaderHelper loaderHelper = new FXMLLoaderHelper();

    @FXML
    public void handleButtonAction(ActionEvent e) throws IOException {
        Stage stage;
        Parent root;

        if(e.getSource() == btnCancel){
            stage = (Stage) btnCancel.getScene().getWindow();
            root = loaderHelper.getFXMLLoader("StaffView").load();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.show();

        } else if (e.getSource() == btnSubmit){
            String fName = fNameText.getText();
            String lName = lNameText.getText();
            String patID = IDText.getText();
            String docID = doctorIDText.getText();
            String roomNum = roomNumText.getText();
            String additionalInfo = addInfoText.getText();

        }
    }
}


