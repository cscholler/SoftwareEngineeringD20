package edu.wpi.cs3733.d20.teamL.views.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import edu.wpi.cs3733.d20.teamL.services.db.DBConstants;
import edu.wpi.cs3733.d20.teamL.services.db.DatabaseService;
import edu.wpi.cs3733.d20.teamL.util.io.DBTableFormatter;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;

public class MedicationReqController implements Initializable {
	DatabaseService db = new DatabaseService(false);
	DBTableFormatter formatter = new DBTableFormatter();

    @FXML
    JFXButton btnCancel, btnSubmit;

    @FXML
    JFXTextField doctorNameText, medTypeText, doseText, patientText, roomNumText, addInfoText;

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

        if(e.getSource() == btnCancel){
            stage = (Stage) btnCancel.getScene().getWindow();
            //stage = new Stage();
            root = FXMLLoader.load(getClass().getResource("/edu/wpi/cs3733/d20/teamL/views/StaffView.fxml"));
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setFullScreen(true);
            stage.show();

        } else if (e.getSource() == btnSubmit){
            String doctorName = doctorNameText.getText();
            String medType = medTypeText.getText();
            String dose = doseText.getText();
            String patientName = patientText.getText();
            String roomNum = roomNumText.getText();
            String additionalInfo = addInfoText.getText();

            //TODO: update cache
			
			String doctorFName = doctorName.substring(0, doctorName.indexOf(" "));
			String doctorLName = doctorName.substring(doctorName.indexOf(" ") + 1);
			String doctorID = db.getTableFromResultSet(db.executeQuery(DBConstants.getDoctorID, new ArrayList<>(Arrays.asList(doctorFName, doctorLName)))).get(0).get(0);
			String patientFName = patientName.substring(0, patientName.indexOf(" "));
			String patientLName = patientName.substring(patientName.indexOf(" ") + 1);
			String patientID = db.getTableFromResultSet(db.executeQuery(DBConstants.getPatientID, new ArrayList<>(Arrays.asList(patientFName, patientLName)))).get(0).get(0);

			db.executeUpdate(DBConstants.addMedicationRequest, new ArrayList<>(Arrays.asList(doctorID, patientID, "Placeholder nurse name", dose, medType, additionalInfo)));
			formatter.reportQueryResults(db.executeQuery(DBConstants.selectAllMedicationRequests));
        }
    }
}
