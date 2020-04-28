package edu.wpi.cs3733.d20.teamL.views.controllers;

import com.google.inject.Inject;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import edu.wpi.cs3733.d20.teamL.services.db.DBConstants;
import edu.wpi.cs3733.d20.teamL.services.db.IDatabaseCache;
import edu.wpi.cs3733.d20.teamL.services.db.IDatabaseService;
import edu.wpi.cs3733.d20.teamL.services.db.SQLEntry;
import edu.wpi.cs3733.d20.teamL.util.FXMLLoaderHelper;
import edu.wpi.cs3733.d20.teamL.util.io.DBTableFormatter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;

@Slf4j
public class AddUserController implements Initializable {
    ObservableList<String> serviceOptions = FXCollections.observableArrayList("Security", "Internal Transport", "External Transport", "Sanitation", "Maintenance", "Pharmacist", "Gift Shop", "Interpreter", "Information Technology");
    ObservableList<String> userOptions = FXCollections.observableArrayList("Doctor", "Nurse", "Admin", "Staff");

    DBTableFormatter formatter = new DBTableFormatter();
    private FXMLLoaderHelper loaderHelper = new FXMLLoaderHelper();

    @FXML
    JFXTextField doctorIDText, fNameText, lNameText, servicesText, usernameText, passwordText, languages;
    @FXML
    MenuButton role;
    @FXML
    Label lblConfirmation;
    @FXML
    private JFXComboBox serviceCombo, userCombo;
    @FXML
    private JFXCheckBox securityBox, inTransportBox, exTransportBox, interpreterBox, giftShopBox, pharmacistBox, managerBox, itBox, maintenanceBox, sanitationBox;
    @FXML
    private VBox boxOService;
    @Inject
    private IDatabaseService db;
    @Inject
    private IDatabaseCache cache;
//    @Inject
//    private ILoginManager loginManager;
    String user;

    @FXML
    private void submitClicked() {
		String firstName = fNameText.getText();
		String lastName = lNameText.getText();
		String service = servicesText.getText();
		String username = usernameText.getText();
		String password = passwordText.getText();
		String doctorID = doctorIDText.getText();
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
		if (firstName != null) {
			rows = db.executeUpdate(new SQLEntry(DBConstants.ADD_USER, new ArrayList<>(Arrays.asList(firstName, lastName, username, password, type, service))));
			formatter.reportQueryResults(db.executeQuery(new SQLEntry(DBConstants.SELECT_ALL_USERS)));
			if (doctorIDText.getText() != null && !(doctorIDText.getText().isEmpty())) {
				rows = db.executeUpdate(new SQLEntry(DBConstants.UPDATE_DOCTOR_USERNAME, new ArrayList<>(Arrays.asList(username, doctorID))));
			}
			if (rows == 0) {
				lblConfirmation.setTextFill(Color.RED);
				lblConfirmation.setText("Submission failed");
			} else if (rows == 1) {
				lblConfirmation.setTextFill(Color.BLACK);
				lblConfirmation.setText("Medication Request Sent");
				lNameText.setText("");
				fNameText.setText("");
				servicesText.setText("");
				usernameText.setText("");
				passwordText.setText("");
				doctorIDText.setText("");
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

        serviceCombo.setItems(serviceOptions);
        userCombo.setItems(userOptions);
    }

    @FXML
    private void userSelected(){
        user = (String) userCombo.getValue();
        if(!(user == "Admin")){
            managerBox.setVisible(true);
            managerBox.setDisable(false);
            if(managerBox.isSelected()){
                serviceCombo.setVisible(true);
                serviceCombo.setDisable(false);
                boxOService.setVisible(false);
                boxOService.setDisable(true);
                languages.setVisible(false);
                languages.setDisable(true);
            } else {
                System.out.println("Services");
                serviceCombo.setVisible(false);
                serviceCombo.setDisable(true);
                boxOService.setVisible(true);
                boxOService.setDisable(false);
                if(interpreterBox.isSelected()){
                    languages.setVisible(true);
                    languages.setDisable(false);
                } else{
                    languages.setVisible(false);
                    languages.setDisable(true);
                }
            }
        } else{
            managerBox.setDisable(true);
            managerBox.setVisible(false);
            languages.setVisible(false);
            languages.setDisable(true);
            boxOService.setVisible(false);
            boxOService.setDisable(true);
        }

        if(user == "Doctor"){
            doctorIDText.setVisible(true);
            doctorIDText.setDisable(false);
        } else{
            doctorIDText.setVisible(false);
            doctorIDText.setDisable(true);
        }
    }


}
