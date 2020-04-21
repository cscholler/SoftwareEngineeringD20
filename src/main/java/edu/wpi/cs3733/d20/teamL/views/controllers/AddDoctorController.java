package edu.wpi.cs3733.d20.teamL.views.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import edu.wpi.cs3733.d20.teamL.services.db.DBConstants;
import edu.wpi.cs3733.d20.teamL.services.db.IDatabaseService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;

import edu.wpi.cs3733.d20.teamL.util.FXMLLoaderHelper;

import javax.inject.Inject;

public class AddDoctorController {
	FXMLLoaderHelper loaderHelper = new FXMLLoaderHelper();
    @FXML
    JFXButton btnCancel, btnSubmit;
    @FXML
    JFXTextField fNameText, lNameText, emailText, doctorIDText, officeNumText, addInfoText;
    @Inject
	IDatabaseService db;

    @FXML
    public void handleButtonAction(ActionEvent e) throws IOException {
        Stage stage;
        Parent root;

        if (e.getSource() == btnCancel){
            stage = (Stage) btnCancel.getScene().getWindow();
            root = loaderHelper.getFXMLLoader("AdminView").load();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.show();

        } else if (e.getSource() == btnSubmit){
			String docID = doctorIDText.getText();
            String fName = fNameText.getText();
            String lName = lNameText.getText();
            String email = emailText.getText();
            String roomNum = officeNumText.getText();
            //String additionalInfo = addInfoText.getText();
			db.executeUpdate(DBConstants.addDoctor, new ArrayList<>(Arrays.asList(docID, fName, lName, email, roomNum)));
        }
    }
}


