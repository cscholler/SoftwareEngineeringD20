package edu.wpi.cs3733.d20.teamL.views.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import com.jfoenix.controls.JFXAutoCompletePopup;
import edu.wpi.cs3733.d20.teamL.App;
import edu.wpi.cs3733.d20.teamL.services.db.DBCache;
import edu.wpi.cs3733.d20.teamL.services.db.DBConstants;
import edu.wpi.cs3733.d20.teamL.services.db.IDatabaseService;
import edu.wpi.cs3733.d20.teamL.services.navSearch.SearchFields;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;

import edu.wpi.cs3733.d20.teamL.util.FXMLLoaderHelper;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;

@Slf4j
public class AddPatientController {
	private FXMLLoaderHelper loaderHelper = new FXMLLoaderHelper();
    @FXML
    JFXButton btnCancel, btnSubmit;
    @FXML
    JFXTextField fNameText, lNameText, IDText, doctorIDText, roomNumText, addInfoText;
    @FXML
    Label lblsubmitted;
    @Inject
	IDatabaseService db;

    @Inject
    private DBCache dbCache;

    private SearchFields sf;
    private JFXAutoCompletePopup<String> autoCompletePopup;

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

    @FXML
    private void backClicked() {
        try {
            Parent root = loaderHelper.getFXMLLoader("StaffView").load();
            loaderHelper.setupScene(new Scene(root));
        } catch (IOException e) {
            log.error("Encountered IOException", e);
        }
    }

    @FXML
    private void submitClicked() {
        String patID = IDText.getText();
        String fName = fNameText.getText();
        String lName = lNameText.getText();
        String docID = doctorIDText.getText();
        String roomNum = roomNumText.getText();
        //String additionalInfo = addInfoText.getText();

        if (db.executeUpdate(DBConstants.addPatient, new ArrayList<>(Arrays.asList(patID, fName, lName, docID, roomNum))) == 0) {
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


