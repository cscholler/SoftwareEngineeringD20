package edu.wpi.cs3733.d20.teamL.views.controllers.dialogues;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXRadioButton;
import com.jfoenix.controls.JFXTextArea;
import edu.wpi.cs3733.d20.teamL.services.db.DBConstants;
import edu.wpi.cs3733.d20.teamL.services.db.IDatabaseService;
import edu.wpi.cs3733.d20.teamL.services.db.SQLEntry;
import edu.wpi.cs3733.d20.teamL.util.FXMLLoaderFactory;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;

@Slf4j
public class FeedbackController implements Initializable {

    @FXML
    private JFXTextArea expectationsText, applicationText, otherText;

    @FXML
    private JFXButton btnCancel;

    @FXML
    private JFXRadioButton expectationsYes, expectationsNo, easyYes, easyNo, navigateYes, navigateNo, nextYes, nextNo;

    @FXML
    private ToggleGroup expectationsGroup, easyGroup, navigateGroup, nextGroup;

    @FXML
    private Label submissionLabel;

    @FXML
    private Stage stage;

    @Inject
    private IDatabaseService db;
    FXMLLoaderFactory loader = new FXMLLoaderFactory();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        expectationsYes.setToggleGroup(expectationsGroup);
        expectationsNo.setToggleGroup(expectationsGroup);
        easyYes.setToggleGroup(easyGroup);
        easyNo.setToggleGroup(easyGroup);
        navigateYes.setToggleGroup(navigateGroup);
        navigateNo.setToggleGroup(navigateGroup);
        nextYes.setToggleGroup(nextGroup);
        nextNo.setToggleGroup(nextGroup);
    }

    @FXML
    void handleCancel() {

        stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }

    @FXML
    void handleSubmit() {

        boolean validFields = true;
        String meetExpectation = null;
        String easy = null;
        String navigate = null;
        String next = null;
        String exceedExpectations = expectationsText.getText();
        String application = applicationText.getText();
        String other = otherText.getText();

        RadioButton rbExpectation = (RadioButton)expectationsGroup.getSelectedToggle();
        if(rbExpectation != null) {
            meetExpectation = rbExpectation.getText();
        }

        RadioButton rbEasy = (RadioButton)easyGroup.getSelectedToggle();
        if(rbEasy != null) {
            easy = rbEasy.getText();
        }

        RadioButton rbNavigate = (RadioButton)navigateGroup.getSelectedToggle();
        if(rbNavigate != null) {
            navigate = rbNavigate.getText();
        }

        RadioButton rbNext = (RadioButton)nextGroup.getSelectedToggle();
        if(rbNext != null) {
            next = rbNext.getText();
        }


        if(rbExpectation == null) {
            validFields = false;
        } else {
            validFields = true;
        }

        if(rbEasy == null) {
            validFields = false;
        } else {
            validFields = true;
        }

        if(rbNavigate == null) {
            validFields = false;
        } else {
            validFields = true;
        }

        if(rbNext == null) {
            validFields = false;
        } else {
            validFields = true;
        }

        if (exceedExpectations == null || exceedExpectations.isEmpty()) {
            validFields = false;
        } else {
            validFields = true;
        }
        if (application == null || application.isEmpty()) {
            validFields = false;
        } else {
            validFields = true;
        }
        if (other == null || other.isEmpty()) {
            validFields = false;
        } else {
            validFields = true;
        }

        int rows = 0;
        if (validFields) {
            rows = db.executeUpdate((new SQLEntry(DBConstants.ADD_FEEDBACK, new ArrayList<>(Arrays.asList(meetExpectation, easy, navigate, next, exceedExpectations, application, other)))));
        }
        if (rows == 0) {

            submissionLabel.setTextFill(Color.RED);
            submissionLabel.setText("Please fill out all fields");
        } else if (rows == 1) {
            submissionLabel.setTextFill(Color.BLACK);
            submissionLabel.setText("Thank you for your time!");

            expectationsText.setText("");
            applicationText.setText("");
            otherText.setText("");
            expectationsYes.setSelected(false);
            expectationsNo.setSelected(false);
            easyYes.setSelected(false);
            easyNo.setSelected(false);
            navigateYes.setSelected(false);
            navigateNo.setSelected(false);
            nextYes.setSelected(false);
            nextNo.setSelected(false);
        } else {
			log.error("SQL update affected more than 1 row.");
		}
        loader.showAndFade(submissionLabel);
    }
}
