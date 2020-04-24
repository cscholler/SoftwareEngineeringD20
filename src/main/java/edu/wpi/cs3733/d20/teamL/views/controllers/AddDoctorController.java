package edu.wpi.cs3733.d20.teamL.views.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import com.jfoenix.controls.JFXAutoCompletePopup;
import edu.wpi.cs3733.d20.teamL.services.db.DBCache;
import edu.wpi.cs3733.d20.teamL.services.db.DBConstants;
import edu.wpi.cs3733.d20.teamL.services.db.IDatabaseService;
import edu.wpi.cs3733.d20.teamL.services.navSearch.SearchFields;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;

import com.jfoenix.controls.JFXTextField;

import edu.wpi.cs3733.d20.teamL.util.FXMLLoaderHelper;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
@Slf4j
public class AddDoctorController {
	private FXMLLoaderHelper loaderHelper = new FXMLLoaderHelper();
	@FXML
    private Label confirmation;
    @FXML
    JFXTextField fNameText, lNameText, emailText, doctorIDText, officeNumText, addInfoText;
    @Inject
	IDatabaseService db;

    @Inject
    private DBCache dbCache;

    private SearchFields sf;
    private JFXAutoCompletePopup<String> autoCompletePopup;

    @FXML
    private void initialize() {
        dbCache.cacheAllFromDB();

        sf = new SearchFields(dbCache.getNodeCache());
        sf.getFields().add(SearchFields.Field.nodeID);
        sf.populateSearchFields();
        autoCompletePopup = new JFXAutoCompletePopup<>();
        autoCompletePopup.getSuggestions().addAll(sf.getSuggestions());
    }

    @FXML
    private void autocomplete() {
        sf.applyAutocomplete(officeNumText, autoCompletePopup);
    }

    @FXML
    public void backClicked() {
        try {
            Parent root = loaderHelper.getFXMLLoader("AdminView").load();
            loaderHelper.setupScene(new Scene(root));
        } catch (IOException e) {
            log.error("Encountered IOException", e);
        }
    }

    /**
     * Handles UI portion of submit being clicked giving confirmation when it succeeds
     */
    public void submitClicked(){
			String docID = doctorIDText.getText();
            String fName = fNameText.getText();
            String lName = lNameText.getText();
            String email = emailText.getText();
            String roomNum = officeNumText.getText();
            //String additionalInfo = addInfoText.getText(); We should add this to the database
			int rows = db.executeUpdate(DBConstants.addDoctor, new ArrayList<>(Arrays.asList(docID, fName, lName, email, roomNum)));

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

			loaderHelper.showAndFade(confirmation);//Shows given label and then fades
        }
    }



