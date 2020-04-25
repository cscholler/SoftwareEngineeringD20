package edu.wpi.cs3733.d20.teamL.views.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import javax.inject.Inject;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;

import com.jfoenix.controls.JFXAutoCompletePopup;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;

import lombok.extern.slf4j.Slf4j;

import edu.wpi.cs3733.d20.teamL.services.db.DBCache;
import edu.wpi.cs3733.d20.teamL.services.db.DBConstants;
import edu.wpi.cs3733.d20.teamL.services.db.IDatabaseService;
import edu.wpi.cs3733.d20.teamL.services.db.SQLEntry;
import edu.wpi.cs3733.d20.teamL.services.navSearch.SearchFields;
import edu.wpi.cs3733.d20.teamL.util.FXMLLoaderHelper;

@Slf4j
public class AddPatientController {
	private FXMLLoaderHelper loaderHelper = new FXMLLoaderHelper();
	private SearchFields sf;
	private JFXAutoCompletePopup<String> autoCompletePopup;
    @FXML
	private JFXButton btnCancel, btnSubmit;
    @FXML
    private JFXTextField fNameText, lNameText, IDText, doctorIDText, roomNumText, addInfoText;
    @FXML
    private Label lblsubmitted;
    @Inject
	private IDatabaseService db;
    @Inject
    private DBCache dbCache;

    @FXML
    private void initialize() {
        dbCache.cacheAllFromDB();
        lblsubmitted.setVisible(false);
        sf = new SearchFields(dbCache.getNodeCache());
        sf.getFields().add(SearchFields.Field.nodeID);
        sf.populateSearchFields();
        autoCompletePopup = new JFXAutoCompletePopup<>();
        autoCompletePopup.getSuggestions().addAll(sf.getSuggestions());
    }

    @FXML
    private void autocomplete() {
        sf.applyAutocomplete(roomNumText, autoCompletePopup);
    }

    /**
     * goes back to staff view page when back button is clicked
     */
    @FXML
    private void backClicked() {
        try {
            Parent root = loaderHelper.getFXMLLoader("StaffView").load();
            loaderHelper.setupScene(new Scene(root));
        } catch (IOException ex) {
            log.error("Encountered IOException", ex);
        }
    }

    /**
     * Handles UI portion of submit being clicked giving confirmation when it succeeds
     */
    @FXML
    private void submitClicked() {
        String patID = IDText.getText();
        String fName = fNameText.getText();
        String lName = lNameText.getText();
        String docID = doctorIDText.getText();
        String roomNum = roomNumText.getText();
        String additionalInfo = addInfoText.getText();
        if (db.executeUpdate(new SQLEntry(DBConstants.ADD_PATIENT, new ArrayList<>(Arrays.asList(patID, fName, lName, docID, roomNum)))) == 0) {
            lblsubmitted.setText("Submission failed!");
            lblsubmitted.setTextFill(Color.RED);
        } else {
            //show the submitted label and clear the fields
            lblsubmitted.setText("Patient Submitted!");
            lblsubmitted.setTextFill(Color.BLACK);
            IDText.setText("");
            fNameText.setText("");
            lNameText.setText("");
            doctorIDText.setText("");
            roomNumText.setText("");
        }
        lblsubmitted.setVisible(true);
        //fade the label out
        loaderHelper.showAndFade(lblsubmitted);
    }
}


