package edu.wpi.cs3733.d20.teamL.views.controllers.dialogues;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXRadioButton;
import com.jfoenix.controls.JFXTextField;
import edu.wpi.cs3733.d20.teamL.services.db.DBConstants;
import edu.wpi.cs3733.d20.teamL.services.db.IDatabaseService;
import edu.wpi.cs3733.d20.teamL.services.db.SQLEntry;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;

import javax.inject.Inject;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
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
    @Inject
    private IDatabaseService db;


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

        int rows = db.executeUpdate((new SQLEntry(DBConstants.ADD_FEEDBACK,
                new ArrayList<>(Arrays.asList(expectations, application, other)))));

        if (rows == 1) {
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
        }
    }

}
