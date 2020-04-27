package edu.wpi.cs3733.d20.teamL.views.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import edu.wpi.cs3733.d20.teamL.services.db.IDatabaseCache;
import edu.wpi.cs3733.d20.teamL.util.search.SearchFields;
import edu.wpi.cs3733.d20.teamL.util.io.DBTableFormatter;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;


import com.jfoenix.controls.JFXAutoCompletePopup;
import com.jfoenix.controls.JFXTextField;

import lombok.extern.slf4j.Slf4j;

import edu.wpi.cs3733.d20.teamL.services.db.DBConstants;
import edu.wpi.cs3733.d20.teamL.services.db.IDatabaseService;
import edu.wpi.cs3733.d20.teamL.services.db.SQLEntry;
import edu.wpi.cs3733.d20.teamL.util.FXMLLoaderHelper;

import javax.inject.Inject;

@Slf4j
public class AddDoctorController {
    private FXMLLoaderHelper loaderHelper = new FXMLLoaderHelper();
    private DBTableFormatter formatter = new DBTableFormatter();
    private SearchFields sf;
    private JFXAutoCompletePopup<String> autoCompletePopup;
    @Inject
    private IDatabaseService db;
    @Inject
    private IDatabaseCache cache;
    @FXML
    private Label lblConfirmation;
    @FXML
    private JFXTextField fNameText, lNameText, emailText, doctorIDText, officeNumText, addInfoText;


    @FXML
    private void initialize() {
        cache.cacheAllFromDB();
        lblConfirmation.setVisible(false);
        sf = new SearchFields(cache.getNodeCache());
        sf.getFields().add(SearchFields.Field.nodeID);
        sf.populateSearchFields();
        autoCompletePopup = new JFXAutoCompletePopup<>();
        autoCompletePopup.getSuggestions().addAll(sf.getSuggestions());
    }

    @FXML
    private void autocomplete() {
        sf.applyAutocomplete(officeNumText, autoCompletePopup);
    }

    /**
     * goes back to admin view page when back button is clicked
     */
    @FXML
    public void btnBackClicked() {
        loaderHelper.goBack();
    }

    /**
     * Handles UI portion of submit being clicked giving confirmation when it succeeds
     */
    @FXML
    private void btnSubmitClicked() {
        String docID = doctorIDText.getText();
        String fName = fNameText.getText();
        String lName = lNameText.getText();
        String email = emailText.getText();
        String roomNum = officeNumText.getText();
        String additionalInfo = addInfoText.getText();
        if (db.executeUpdate(new SQLEntry(DBConstants.ADD_DOCTOR, new ArrayList<>(Arrays.asList(docID, fName, lName, null, roomNum, additionalInfo)))) == 0) {
            lblConfirmation.setTextFill(Color.RED);
            lblConfirmation.setText("Submission failed");
        } else {
            lblConfirmation.setTextFill(Color.BLACK);
            lblConfirmation.setText("Doctor Added");
            fNameText.setText("");
            lNameText.setText("");
            emailText.setText("");
            doctorIDText.setText("");
            officeNumText.setText("");
            addInfoText.setText("");
        }
        loaderHelper.showAndFade(lblConfirmation);
        formatter.reportQueryResults(db.executeQuery(new SQLEntry(DBConstants.SELECT_ALL_DOCTORS)));
    }
}



