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
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;

import edu.wpi.cs3733.d20.teamL.util.FXMLLoaderHelper;
import javafx.util.Duration;

import javax.inject.Inject;

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
    public void handleButtonAction(ActionEvent e) throws IOException {
        Stage stage;
        Parent root;

        if (e.getSource() == btnCancel){
            stage = (Stage) btnCancel.getScene().getWindow();
            root = loaderHelper.getFXMLLoader("StaffView").load();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.hide();
            stage.setMaximized(true);
            stage.show();
            stage.setWidth(App.SCREEN_WIDTH);
            stage.setHeight(App.SCREEN_HEIGHT);

        } else if (e.getSource() == btnSubmit){
			String patID = IDText.getText();
            String fName = fNameText.getText();
            String lName = lNameText.getText();
            String docID = doctorIDText.getText();
            String roomNum = roomNumText.getText();
            //String additionalInfo = addInfoText.getText();

			if (db.executeUpdate(DBConstants.addPatient, new ArrayList<>(Arrays.asList(patID, fName, lName, docID, roomNum))) == 0) {
			    lblsubmitted.setText("Submission failed!");
			    lblsubmitted.setTextFill(Color.RED);

            }
			else {

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
            FadeTransition fadeTransition = new FadeTransition(Duration.millis(2000), lblsubmitted);
            fadeTransition.setDelay(Duration.millis(2000));
            fadeTransition.setFromValue(1.0);
            fadeTransition.setToValue(0.0);
            fadeTransition.setCycleCount(1);
            fadeTransition.play();
        }
    }
}


