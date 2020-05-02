package edu.wpi.cs3733.d20.teamL.views.controllers.requests;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.ResourceBundle;

import edu.wpi.cs3733.d20.teamL.services.db.IDatabaseCache;
import edu.wpi.cs3733.d20.teamL.util.search.SearchFields;
import edu.wpi.cs3733.d20.teamL.services.users.ILoginManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

import com.google.inject.Inject;

import com.jfoenix.controls.JFXAutoCompletePopup;
import com.jfoenix.controls.JFXTextField;

import edu.wpi.cs3733.d20.teamL.services.db.DBConstants;
import edu.wpi.cs3733.d20.teamL.services.db.IDatabaseService;
import edu.wpi.cs3733.d20.teamL.services.db.SQLEntry;
import edu.wpi.cs3733.d20.teamL.util.FXMLLoaderHelper;
import edu.wpi.cs3733.d20.teamL.util.io.DBTableFormatter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MedicationRequestController implements Initializable {
	DBTableFormatter formatter = new DBTableFormatter();
    private FXMLLoaderHelper loaderHelper = new FXMLLoaderHelper();
	private SearchFields sf;
	private JFXAutoCompletePopup<String> autoCompletePopup;
	@Inject
	private IDatabaseService db;
	@Inject
	private IDatabaseCache cache;
	@Inject
	private ILoginManager loginManager;
    @FXML
    private Label lblConfirmation;
    @FXML
    private JFXTextField docFNameText, docLNameText, medTypeText, doseText, patFNameText, patLNameText, roomNumText, addInfoText;
	@FXML
	private ImageView requestReceived;
	@FXML
	private BorderPane borderPane;
	@FXML
	private StackPane stackPane;
	@FXML
	public void initialize(URL location, ResourceBundle resources) {

        sf = new SearchFields(cache.getNodeCache());
        sf.getFields().add(SearchFields.Field.nodeID);
        sf.populateSearchFields();
        autoCompletePopup = new JFXAutoCompletePopup<>();
        autoCompletePopup.getSuggestions().addAll(sf.getSuggestions());
		formatter.reportQueryResults(db.executeQuery(new SQLEntry(DBConstants.SELECT_ALL_DOCTORS)));
		formatter.reportQueryResults(db.executeQuery(new SQLEntry(DBConstants.SELECT_ALL_PATIENTS)));
		borderPane.prefWidthProperty().bind(stackPane.widthProperty());
		borderPane.prefHeightProperty().bind(stackPane.heightProperty());
	}

    /**
     * Applies autocomplete to the room number field
     */
    @FXML
    private void autocomplete() {
        sf.applyAutocomplete(roomNumText, autoCompletePopup);
    }

    @FXML
    private void btnCancelClicked() throws IOException {
		loaderHelper.goBack();
	}

	@FXML
	private void btnSubmitClicked() throws IOException {
		String doctorFName = docFNameText.getText();
		String doctorLName = docLNameText.getText();
		String medType = medTypeText.getText();
		String dose = doseText.getText();
		String patientFName = patFNameText.getText();
		String patientLName = patLNameText.getText();
		String roomNum = roomNumText.getText();
		String additionalInfo = addInfoText.getText();

		// Status codes-- 0: pending, 1: approved, 2: delivered, 3: denied,
		String status = "0";
		String dateAndTime = new SimpleDateFormat("M/dd/yy | h:mm aa").format(new Date());
		String nurseUsername = loginManager.getCurrentUser().getUsername();
		// Adds request info to database
		String doctorID = db.getTableFromResultSet(db.executeQuery(new SQLEntry(DBConstants.GET_DOCTOR_ID_BY_NAME, new ArrayList<>(Arrays.asList(doctorFName, doctorLName))))).get(0).get(0);
		String patientID = db.getTableFromResultSet(db.executeQuery(new SQLEntry(DBConstants.GET_PATIENT_ID, new ArrayList<>(Arrays.asList(patientFName, patientLName))))).get(0).get(0);
		String patientRoomNum = db.getTableFromResultSet(db.executeQuery(new SQLEntry(DBConstants.GET_PATIENT_ROOM, new ArrayList<>(Collections.singletonList(patientID))))).get(0).get(0);
		int rows = 0;
		rows = db.executeUpdate(new SQLEntry(DBConstants.ADD_MEDICATION_REQUEST, new ArrayList<>(Arrays.asList(doctorID, patientID, nurseUsername, null, dose, medType, additionalInfo, status, dateAndTime))));
		formatter.reportQueryResults(db.executeQuery(new SQLEntry(DBConstants.SELECT_ALL_MEDICATION_REQUESTS)));
		if (rows == 0) {
			lblConfirmation.setTextFill(Color.RED);
			lblConfirmation.setText("Submission failed");
		} else if (rows == 1) {
			docFNameText.setText("");
			docLNameText.setText("");
			medTypeText.setText("");
			doseText.setText("");
			patFNameText.setText("");
			patLNameText.setText("");
			roomNumText.setText("");
			addInfoText.setText("");
			loaderHelper.showAndFade(requestReceived);
		} else {
			log.error("SQL update affected more than 1 row.");
		}
		loaderHelper.showAndFade(lblConfirmation);
	}
}
