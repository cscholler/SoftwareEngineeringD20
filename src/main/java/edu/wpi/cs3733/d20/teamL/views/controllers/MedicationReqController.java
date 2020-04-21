package edu.wpi.cs3733.d20.teamL.views.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import edu.wpi.cs3733.d20.teamL.services.db.DBConstants;
import edu.wpi.cs3733.d20.teamL.services.db.DatabaseService;
import edu.wpi.cs3733.d20.teamL.services.db.IDatabaseService;
import edu.wpi.cs3733.d20.teamL.util.FXMLLoaderHelper;
import edu.wpi.cs3733.d20.teamL.util.io.DBTableFormatter;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javax.inject.Inject;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.ResourceBundle;

public class MedicationReqController implements Initializable {
	@Inject
	IDatabaseService db;
	DBTableFormatter formatter = new DBTableFormatter();
    private FXMLLoaderHelper loaderHelper = new FXMLLoaderHelper();

    @FXML
    private JFXButton btnCancel, btnSubmit;

    @FXML
    private JFXTextField docFNameText, docLNameText, medTypeText, doseText, patFNameText, patLNameText, roomNumText, addInfoText;


	@FXML
	public void initialize(URL location, ResourceBundle resources) {
		// Print test data before request is made
		formatter.reportQueryResults(db.executeQuery(DBConstants.selectAllDoctors));
		System.out.print("\n");
		formatter.reportQueryResults(db.executeQuery(DBConstants.selectAllPatients));
		formatter.reportQueryResults(db.executeQuery(DBConstants.selectAllMedicationRequests));
	}

    @FXML
    public void handleButtonAction(ActionEvent e) throws IOException {
        Stage stage = null;
        Parent root;

        if (e.getSource() == btnCancel){
            stage = (Stage) btnCancel.getScene().getWindow();
            //stage = new Stage();
            root = loaderHelper.getFXMLLoader("StaffView").load();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.hide();
            stage.setMaximized(true);
            stage.show();

        } else if (e.getSource() == btnSubmit){
            String doctorFName = docFNameText.getText();
            String doctorLName = docLNameText.getText();
            String medType = medTypeText.getText();
            String dose = doseText.getText();
            String patientFName = patFNameText.getText();
            String patientLName = patLNameText.getText();
            String roomNum = roomNumText.getText();
            String additionalInfo = addInfoText.getText();
            // Status codes-- 0: pending, 1: approved, 2: denied
            String status = "0";
            String dateAndTime = new SimpleDateFormat("MM-dd-yyyy hh:mm:ss").format(new Date());

			// Adds request info to database
			String doctorID = db.getTableFromResultSet(db.executeQuery(DBConstants.getDoctorID, new ArrayList<>(Arrays.asList(doctorFName, doctorLName)))).get(0).get(0);
			String patientID = db.getTableFromResultSet(db.executeQuery(DBConstants.getPatientID, new ArrayList<>(Arrays.asList(patientFName, patientLName)))).get(0).get(0);
			// TODO: Get name of nurse from current user
			db.executeUpdate(DBConstants.addMedicationRequest, new ArrayList<>(Arrays.asList(doctorID, patientID, "Nurse", dose, medType, additionalInfo, status, dateAndTime)));
			formatter.reportQueryResults(db.executeQuery(DBConstants.selectAllMedicationRequests));
			// TODO: Check if any info is invalid before sending request
        }
    }
}
