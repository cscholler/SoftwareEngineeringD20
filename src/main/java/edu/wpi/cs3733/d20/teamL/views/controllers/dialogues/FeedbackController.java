package edu.wpi.cs3733.d20.teamL.views.controllers.dialogues;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXRadioButton;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class FeedbackController implements Initializable {

    @FXML
    private JFXTextField expectationsText, applicationText, otherText;

    @FXML
    private JFXButton btnCancel, btnSubmit;

    @FXML
    private JFXRadioButton expectationsYes, expectationsNo, easyYes, easyNo, navigateYes, navigateNo, nextYes, nextNo;

    @FXML
    private ToggleGroup expectationsGroup, easyGroup, navigateGroup, nextGroup;

    @FXML
    private Stage stage;


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

        String expectations = expectationsText.getText();
        String application = applicationText.getText();
        String other = otherText.getText();
        String choiceSelect = null;
    }

}
