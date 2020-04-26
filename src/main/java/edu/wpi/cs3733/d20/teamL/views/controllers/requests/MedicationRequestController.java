package edu.wpi.cs3733.d20.teamL.views.controllers.requests;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.ResourceBundle;

import edu.wpi.cs3733.d20.teamL.services.db.IDatabaseCache;
import edu.wpi.cs3733.d20.teamL.services.search.SearchFields;
import edu.wpi.cs3733.d20.teamL.services.users.ILoginManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;

import com.google.inject.Inject;

import com.jfoenix.controls.JFXAutoCompletePopup;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;

import edu.wpi.cs3733.d20.teamL.services.db.DBConstants;
import edu.wpi.cs3733.d20.teamL.services.db.IDatabaseService;
import edu.wpi.cs3733.d20.teamL.services.db.SQLEntry;
import edu.wpi.cs3733.d20.teamL.util.FXMLLoaderHelper;
import edu.wpi.cs3733.d20.teamL.util.io.DBTableFormatter;

public class MedicationRequestController implements Initializable {
	DBTableFormatter formatter = new DBTableFormatter();
    private FXMLLoaderHelper loaderHelper = new FXMLLoaderHelper();
	private SearchFields sf;
	private JFXAutoCompletePopup<String> autoCompletePopup;
	@Inject
	private IDatabaseService db;
	@Inject
	private IDatabaseCache dbCache;
	@Inject
	private ILoginManager loginManager;
    @FXML
    private Label confirmation;
    @FXML
    private JFXButton btnCancel, btnSubmit;
    @FXML
    private JFXTextField docFNameText, docLNameText, medTypeText, doseText, patFNameText, patLNameText, roomNumText, addInfoText;

	@FXML
	public void initialize(URL location, ResourceBundle resources) {
        dbCache.cacheAllFromDB();

        sf = new SearchFields(dbCache.getNodeCache());
        sf.getFields().add(SearchFields.Field.nodeID);
        sf.populateSearchFields();
        autoCompletePopup = new JFXAutoCompletePopup<>();
        autoCompletePopup.getSuggestions().addAll(sf.getSuggestions());
	}

    /**
     * shows autocomplete options when searching for a room
     */
    @FXML
    private void autocomplete() {
        sf.applyAutocomplete(roomNumText, autoCompletePopup);
    }

    /**
     * handles buttons "cancel" and "submit" when clicked in a medication service request
     * @param e tracks when button is pressed
     * @throws IOException
     */
    @FXML
    public void handleButtonAction(ActionEvent e) throws IOException {
        //goe back to Staff View
        if (e.getSource() == btnCancel) {
            Parent root = loaderHelper.getFXMLLoader("StaffView").load();
            loaderHelper.setupScene(new Scene(root));

        //sumbits request
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
            String user = loginManager.getCurrentUser().getUsername();
			// Adds request info to database
			String doctorID = db.getTableFromResultSet(db.executeQuery(new SQLEntry(DBConstants.GET_DOCTOR_ID, new ArrayList<>(Arrays.asList(doctorFName, doctorLName))))).get(0).get(0);
			String patientID = db.getTableFromResultSet(db.executeQuery(new SQLEntry(DBConstants.GET_PATIENT_ID, new ArrayList<>(Arrays.asList(patientFName, patientLName))))).get(0).get(0);
			// TODO: Get name of nurse from current user
			int rows = db.executeUpdate(new SQLEntry(DBConstants.ADD_MEDICATION_REQUEST, new ArrayList<>(Arrays.asList(doctorID, patientID, user, dose, medType, additionalInfo, status, dateAndTime))));
			//formatter.reportQueryResults(db.executeQuery(new SQLEntry(DBConstants.SELECT_ALL_MEDICATION_REQUESTS)));
			// TODO: Check if any info is invalid before sending request

            if (rows == 0) {
                confirmation.setTextFill(Color.RED);
                confirmation.setText("Submission failed      ");
            } else {
                confirmation.setTextFill(Color.BLACK);
                confirmation.setText("Medication Request Sent");

                docFNameText.setText("");
                docLNameText.setText("");
                medTypeText.setText("");
                doseText.setText("");
                patFNameText.setText("");
                patLNameText.setText("");
                roomNumText.setText("");
                addInfoText.setText("");
            }

            loaderHelper.showAndFade(confirmation);
        }
    }
}
