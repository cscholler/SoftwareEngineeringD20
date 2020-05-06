package edu.wpi.cs3733.d20.teamL.views.LoggedInView;

import java.util.ArrayList;
import java.util.Arrays;
import javax.inject.Inject;

import com.jfoenix.controls.JFXTextArea;
import edu.wpi.cs3733.d20.teamL.services.db.IDatabaseCache;
import edu.wpi.cs3733.d20.teamL.util.search.SearchFields;
import edu.wpi.cs3733.d20.teamL.util.io.DBTableFormatter;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;

import com.jfoenix.controls.JFXAutoCompletePopup;
import com.jfoenix.controls.JFXTextField;

import lombok.extern.slf4j.Slf4j;

import edu.wpi.cs3733.d20.teamL.services.db.DBConstants;
import edu.wpi.cs3733.d20.teamL.services.db.IDatabaseService;
import edu.wpi.cs3733.d20.teamL.services.db.SQLEntry;
import edu.wpi.cs3733.d20.teamL.util.FXMLLoaderFactory;

@Slf4j
public class AddPatientController {
    private FXMLLoaderFactory loaderHelper = new FXMLLoaderFactory();
    private DBTableFormatter formatter = new DBTableFormatter();
    private SearchFields sf;
    private JFXAutoCompletePopup<String> autoCompletePopup;
    @FXML
    private JFXTextField fNameText, lNameText, IDText, doctorIDText, roomNumText;
    @FXML
    private JFXTextArea addInfoText;
    @FXML
    private Label lblConfirmation;
    @Inject
    private IDatabaseService db;
    @Inject
    private IDatabaseCache dbCache;

    @FXML
    private void initialize() {
        lblConfirmation.setVisible(false);
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
     * goes back to staff_view page when back button is clicked
     */
    @FXML
    private void btnBackClicked() {
        loaderHelper.goBack();
    }

    /**
     * Handles UI portion of submit being clicked giving confirmation when it succeeds
     */
    @FXML
    private void btnSubmitClicked() {
        String patID = IDText.getText();
        String fName = fNameText.getText();
        String lName = lNameText.getText();
        String docID = doctorIDText.getText();
        String roomNum = roomNumText.getText();
        String additionalInfo = addInfoText.getText();

        if (db.executeUpdate(new SQLEntry(DBConstants.ADD_PATIENT, new ArrayList<>(Arrays.asList(patID, fName, lName, docID, roomNum, additionalInfo)))) == 0) {
            lblConfirmation.setText("Submission failed!");
            lblConfirmation.setTextFill(Color.RED);
        } else {
            //show the submitted label and clear the fields
            lblConfirmation.setText("Patient Submitted!");
            lblConfirmation.setTextFill(Color.BLACK);
            IDText.setText("");
            fNameText.setText("");
            lNameText.setText("");
            doctorIDText.setText("");
            roomNumText.setText("");
            addInfoText.setText("");
        }
        lblConfirmation.setVisible(true);
        //fade the label out
        loaderHelper.showAndFade(lblConfirmation);
        formatter.reportQueryResults(db.executeQuery(new SQLEntry(DBConstants.SELECT_ALL_PATIENTS)));
    }
}


