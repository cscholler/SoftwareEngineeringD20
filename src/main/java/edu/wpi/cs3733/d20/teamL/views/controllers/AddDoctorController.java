package edu.wpi.cs3733.d20.teamL.views.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import edu.wpi.cs3733.d20.teamL.services.db.IDatabaseCache;
import edu.wpi.cs3733.d20.teamL.services.search.SearchFields;
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
	private SearchFields sf;
	private JFXAutoCompletePopup<String> autoCompletePopup;
    @Inject
	private IDatabaseService db;
    @Inject
    private IDatabaseCache cache;
	@FXML
	private Label confirmation;
	@FXML
	private JFXTextField fNameText, lNameText, emailText, doctorIDText, officeNumText, addInfoText;


    @FXML
    private void initialize() {
        cache.cacheAllFromDB();
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
    public void backClicked() {
        try {
            Parent root = loaderHelper.getFXMLLoader("AdminView").load();
            loaderHelper.setupScene(new Scene(root));
        } catch (IOException ex) {
            log.error("Encountered IOException", ex);
        }
    }

    /**
     * Handles UI portion of submit being clicked giving confirmation when it succeeds
     */
    public void submitClicked() {
			String docID = doctorIDText.getText();
            String fName = fNameText.getText();
            String lName = lNameText.getText();
            String email = emailText.getText();
            String roomNum = officeNumText.getText();
            String additionalInfo = addInfoText.getText(); //We should add this to the database
			int rows = db.executeUpdate(new SQLEntry(DBConstants.ADD_DOCTOR, new ArrayList<>(Arrays.asList(docID, fName, lName, email, roomNum))));
			if (rows == 0) {
			    confirmation.setTextFill(Color.RED);
			    confirmation.setText("Submission failed");
            } else {
                confirmation.setTextFill(Color.BLACK);
                confirmation.setText("Doctor Added");
                fNameText.setText("");
                lNameText.setText("");
                emailText.setText("");
                doctorIDText.setText("");
                officeNumText.setText("");
                addInfoText.setText("");
            }
			loaderHelper.showAndFade(confirmation);
        }
    }



