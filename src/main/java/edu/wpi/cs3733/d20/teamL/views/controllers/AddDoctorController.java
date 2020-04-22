package edu.wpi.cs3733.d20.teamL.views.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import com.jfoenix.controls.JFXAutoCompletePopup;
import edu.wpi.cs3733.d20.teamL.services.db.DBCache;
import edu.wpi.cs3733.d20.teamL.services.db.DBConstants;
import edu.wpi.cs3733.d20.teamL.services.db.IDatabaseService;
import edu.wpi.cs3733.d20.teamL.services.navSearch.SearchFields;
import javafx.animation.FadeTransition;
import javafx.animation.Transition;
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

public class AddDoctorController {
	FXMLLoaderHelper loaderHelper = new FXMLLoaderHelper();
	@FXML
    private Label confirmation;
    @FXML
    private JFXButton btnCancel, btnSubmit;
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
        sf.getFields().clear();
        sf.getFields().add(SearchFields.Field.nodeID);
        sf.populateSearchFields();
        autoCompletePopup = new JFXAutoCompletePopup<>();
        autoCompletePopup.getSuggestions().addAll(sf.getSuggestions());
    }

    @FXML
    private void autocomplete() {
        autoCompletePopup.setSelectionHandler(event -> officeNumText.setText(event.getObject()));
        officeNumText.textProperty().addListener(observable -> {
            autoCompletePopup.filter(string ->
                    string.toLowerCase().contains(officeNumText.getText().toLowerCase()));
            if (autoCompletePopup.getFilteredSuggestions().isEmpty() ||
                    officeNumText.getText().isEmpty()) {
                autoCompletePopup.hide();
            } else {
                autoCompletePopup.show(officeNumText);
            }
        });
    }

    @FXML
    public void handleButtonAction(ActionEvent e) throws IOException {
        Stage stage;
        Parent root;

        if (e.getSource() == btnCancel){
            stage = (Stage) btnCancel.getScene().getWindow();
            root = loaderHelper.getFXMLLoader("AdminView").load();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.show();

        } else if (e.getSource() == btnSubmit){
			String docID = doctorIDText.getText();
            String fName = fNameText.getText();
            String lName = lNameText.getText();
            String email = emailText.getText();
            String roomNum = officeNumText.getText();
            //String additionalInfo = addInfoText.getText();
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

			confirmation.setVisible(true);

            FadeTransition fadeTransition = new FadeTransition(Duration.millis(2000), confirmation);
            fadeTransition.setDelay(Duration.millis(2000));
            fadeTransition.setFromValue(1.0);
            fadeTransition.setToValue(0.0);
            fadeTransition.setCycleCount(1);

            fadeTransition.play();
        }
    }
}


