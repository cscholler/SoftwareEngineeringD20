package edu.wpi.cs3733.d20.teamL.views.controllers;

import com.google.inject.Inject;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXTextField;
import edu.wpi.cs3733.d20.teamL.services.db.DBConstants;
import edu.wpi.cs3733.d20.teamL.services.db.IDatabaseCache;
import edu.wpi.cs3733.d20.teamL.services.db.IDatabaseService;
import edu.wpi.cs3733.d20.teamL.services.db.SQLEntry;
import edu.wpi.cs3733.d20.teamL.services.users.ILoginManager;
import edu.wpi.cs3733.d20.teamL.util.FXMLLoaderHelper;
import edu.wpi.cs3733.d20.teamL.util.io.DBTableFormatter;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuButton;
import javafx.scene.paint.Color;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;

@Slf4j
public class AddUserController implements Initializable {

    DBTableFormatter formatter = new DBTableFormatter();
    private FXMLLoaderHelper loaderHelper = new FXMLLoaderHelper();

    @FXML
    JFXTextField docID, fName, lName, services, username, password;
    @FXML
    MenuButton role;
    @FXML
    Label lblConfirmation;
    @Inject
    private IDatabaseService db;
    @Inject
    private IDatabaseCache cache;
//    @Inject
//    private ILoginManager loginManager;

    @FXML
    private void submitClicked(){
    String firstName = fName.getText();
    String lastName = lName.getText();
    String service = services.getText();
    String un = username.getText();
    String pw = password.getText();
    String doctorID = docID.getText();
    String type = role.getText();

    switch(type) {
        case "Nurse":
            type = "1";
            break;
        case "Doctor":
            type = "2";
            break;
        case "Admin":
            type = "3";
            break;
    }

        formatter.reportQueryResults(db.executeQuery(new SQLEntry(DBConstants.SELECT_ALL_DOCTORS)));

        int rows = 0;
    if(firstName != null){
        rows = db.executeUpdate(new SQLEntry(DBConstants.ADD_USER, new ArrayList<>(Arrays.asList(firstName, lastName, un, pw, type, service))));
        formatter.reportQueryResults(db.executeQuery(new SQLEntry(DBConstants.SELECT_ALL_USERS)));
    if(docID.getText() != null && !(docID.getText().isEmpty())) {
        rows = db.executeUpdate(new SQLEntry(DBConstants.UPDATE_DOCTOR_USERNAME, new ArrayList<>(Arrays.asList(un,doctorID ))));
    }
        if (rows == 0) {
            lblConfirmation.setTextFill(Color.RED);
            lblConfirmation.setText("Submission failed");
        } else if (rows == 1) {
            lblConfirmation.setTextFill(Color.BLACK);
            lblConfirmation.setText("Medication Request Sent");
            lName.setText("");
            fName.setText("");
            services.setText("");
            username.setText("");
            password.setText("");
            docID.setText("");


        } else {
            log.error("SQL update affected more than 1 row.");
        }
        loaderHelper.showAndFade(lblConfirmation);
    }

    }
@FXML
    private void closeClicked(){
        loaderHelper.goBack();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        formatter.reportQueryResults(db.executeQuery(new SQLEntry(DBConstants.SELECT_ALL_DOCTORS)));

    }
    @FXML
    private void nurseSelected(){
        role.setText("Nurse");
        docID.setVisible(false);
        docID.setDisable(true);
    }
    @FXML
    private void doctorSelected(){
        role.setText("Doctor");
        docID.setVisible(true);
        docID.setDisable(false);
    }
    @FXML
    private void adminSelected(){
        role.setText("Admin");
        docID.setVisible(false);
        docID.setDisable(true);
    }
}
