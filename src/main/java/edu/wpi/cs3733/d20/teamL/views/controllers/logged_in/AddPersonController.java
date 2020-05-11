package edu.wpi.cs3733.d20.teamL.views.controllers.logged_in;

import com.fasterxml.jackson.databind.util.JSONPObject;
import com.github.sarxos.webcam.Webcam;
import com.google.inject.Inject;
import com.jfoenix.controls.*;
import com.squareup.okhttp.*;
import edu.wpi.cs3733.d20.teamL.services.HTTPClientService;
import edu.wpi.cs3733.d20.teamL.services.db.DBConstants;
import edu.wpi.cs3733.d20.teamL.services.db.IDatabaseCache;
import edu.wpi.cs3733.d20.teamL.services.db.IDatabaseService;
import edu.wpi.cs3733.d20.teamL.services.db.SQLEntry;
import edu.wpi.cs3733.d20.teamL.services.users.PasswordManager;
import edu.wpi.cs3733.d20.teamL.util.FXMLLoaderFactory;
import edu.wpi.cs3733.d20.teamL.util.io.DBTableFormatter;
import edu.wpi.cs3733.d20.teamL.util.search.SearchFields;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import netscape.javascript.JSObject;
import org.apache.commons.io.FileUtils;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.ResourceBundle;
//import javax.ws.rs.client.Client;
//import javax.ws.rs.client.ClientBuilder;
//import javax.ws.rs.client.Entity;
//import javax.ws.rs.core.MediaType;

@Slf4j
public class AddPersonController implements Initializable {
    //For doctor
    private SearchFields sf;
    private JFXAutoCompletePopup<String> autoCompletePopup;
    @Inject
    private IDatabaseService db;
    @Inject
    private IDatabaseCache cache;
    @FXML
    private Label lblConfirmation, confirmation;
    @FXML
    private JFXTextArea addlInfoText;
    @FXML
    private JFXTextField doctorFN, doctorLN, docID, officeText;

    //for user
    ObservableList<String> serviceOptions = FXCollections.observableArrayList("Security", "Internal Transport", "External Transport", "Sanitation", "Maintenance", "Gift Shop", "Interpreter", "Information Technology");
    ObservableList<String> userOptions = FXCollections.observableArrayList("Staff", "Nurse", "Doctor", "Admin");
    ObservableList<String> languageOptions = FXCollections.observableArrayList("Spanish", "Italian", "Chinese", "ASL", "French");


    private final FXMLLoaderFactory loaderHelper = new FXMLLoaderFactory();

    @FXML
    JFXTextField doctorIDText, doctorUn,  sFName, sLName, sUn, mFName, mLName, mUn, nFName, nLName, nUn, aFName, aLName, aUn;
    @FXML
    JFXPasswordField doctorPw, sPw, mPw, nPw, aPw;
    @FXML
    private JFXComboBox<String> serviceCombo, languages;
    @FXML
    private JFXCheckBox securityBox, inTransportBox, exTransportBox, maintenanceBox, sanitationBox, pharmacistBox, giftShopBox, itBox, interpreterBox, managerBox;
    @FXML
    private VBox boxOService;
    @FXML
    private JFXButton btnCancel;
    @Inject
    private HTTPClientService client;

    @FXML
    private void setBtnCancel() {
        Stage stage;
        stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        serviceCombo.setItems(serviceOptions);
        languages.setItems(languageOptions);

        sf = new SearchFields(cache.getNodeCache());
        sf.getFields().add(SearchFields.Field.nodeID);
        sf.populateSearchFields();
        autoCompletePopup = new JFXAutoCompletePopup<>();
        autoCompletePopup.getSuggestions().addAll(sf.getSuggestions());
    }

    //Add doctor
    @FXML
    private void autocomplete() {
        sf.applyAutocomplete(officeText, autoCompletePopup);
    }

    /**
     * Handles UI portion of submit being clicked giving confirmation when it succeeds
     */

    //Nurse
    @FXML
    private void submitNurse() {
        int rows = 0;

        String firstName = nFName.getText();
        String lastName = nLName.getText();
        String username = nUn.getText();
        String password = nPw.getText();
        String type = "1";
        String services = null;
        String manager = null;

        rows = db.executeUpdate(new SQLEntry(DBConstants.ADD_USER, new ArrayList<>(Arrays.asList(firstName, lastName, username, PasswordManager.hashPassword(password), type, services, manager))));
        confirm(rows);
    }

    //Admin
    @FXML
    private void submitAdmin() {
        int rows = 0;

        String firstName = aFName.getText();
        String lastName = aLName.getText();
        String username = aUn.getText();
        String password = aPw.getText();
        String type = "3";
        String services = null;
        String manager = null;

        rows = db.executeUpdate(new SQLEntry(DBConstants.ADD_USER, new ArrayList<>(Arrays.asList(firstName, lastName, username, PasswordManager.hashPassword(password), type, services, manager))));
        confirm(rows);
    }

    //Manager
    @FXML
    private void submitManager() {
        int rows = 0;

        String firstName = mFName.getText();
        String lastName = mLName.getText();
        String username = mUn.getText();
        String password = mPw.getText();
        String type = "0";
        String services = null;
        String manager = serviceCombo.getValue();

        rows = db.executeUpdate(new SQLEntry(DBConstants.ADD_USER, new ArrayList<>(Arrays.asList(firstName, lastName, username, PasswordManager.hashPassword(password), type, services, manager))));
        confirm(rows);
    }

    //Doctor
    @FXML
    private void submitDoc() {
        int rows = 0;

        String firstName = doctorFN.getText();
        String lastName = doctorLN.getText();
        String username = doctorUn.getText();
        String password = doctorPw.getText();
        String doctorID = doctorIDText.getText();
        String roomNum = officeText.getText();
        String additionalInfo = addlInfoText.getText();
        String type = "2";
        String services = null;
        String manager = "pharmacist";

        rows = db.executeUpdate(new SQLEntry(DBConstants.ADD_USER, new ArrayList<>(Arrays.asList(firstName, lastName, username, PasswordManager.hashPassword(password), type, services, manager))));
        int rows1 = db.executeUpdate(new SQLEntry(DBConstants.ADD_DOCTOR, new ArrayList<>(Arrays.asList(doctorID, firstName, lastName, null, roomNum, additionalInfo))));
        confirm(rows);
    }

    //Staff
    @FXML
    private void submitStaff() {
        int rows = 0;

        String firstName = sFName.getText();
        String lastName = sLName.getText();
        String username = sUn.getText();
        String password = sPw.getText();
        String type = "0";
        String services = null;
        String manager = null;

        if (firstName != null) {
            StringBuilder servicesList = new StringBuilder();
            boolean anyServicesSelected = false;
            if (securityBox.isSelected()) {
                servicesList.append("security;");
                anyServicesSelected = true;
            }
            if (inTransportBox.isSelected()) {
                servicesList.append("internal_transportation;");
                anyServicesSelected = true;
            }
            if (exTransportBox.isSelected()) {
                servicesList.append("external_transportation;");
                anyServicesSelected = true;
            }
            if (maintenanceBox.isSelected()) {
                servicesList.append("maintenance;");
                anyServicesSelected = true;
            }
            if (sanitationBox.isSelected()) {
                servicesList.append("sanitation;");
                anyServicesSelected = true;
            }
            if (pharmacistBox.isSelected()) {
                servicesList.append("pharmacy;");
                anyServicesSelected = true;
            }
            if (giftShopBox.isSelected()) {
                servicesList.append("gift_shop;");
                anyServicesSelected = true;
            }
            if (itBox.isSelected()) {
                servicesList.append("information_technology;");
                anyServicesSelected = true;
            }
            if (interpreterBox.isSelected()) {
                servicesList.append("interpreter(").append(languages.getValue().replace(" ", "")).append(");");
                anyServicesSelected = true;
            }
            if (anyServicesSelected) {
                servicesList.setLength(servicesList.length() - 1);
            }

            services = servicesList.toString();
        }

        rows = db.executeUpdate(new SQLEntry(DBConstants.ADD_USER, new ArrayList<>(Arrays.asList(firstName, lastName, username, PasswordManager.hashPassword(password), type, services, manager))));
        confirm(rows);
    }

    private void confirm(int rows){
        if (rows == 0) {
            lblConfirmation.setTextFill(Color.RED);
            lblConfirmation.setText("Submission failed");
        } else if (rows == 1) {
            lblConfirmation.setTextFill(Color.BLACK);
            lblConfirmation.setText("User added");
            languages.setValue(null);
            serviceCombo.setVisible(false);
            serviceCombo.setDisable(false);
            boxOService.setVisible(false);
            boxOService.setDisable(false);
            languages.setVisible(false);
            languages.setDisable(false);
            doctorIDText.setVisible(false);
            doctorIDText.setDisable(false);
            securityBox.setSelected(false);
            inTransportBox.setSelected(false);
            exTransportBox.setSelected(false);
            maintenanceBox.setSelected(false);
            sanitationBox.setSelected(false);
            pharmacistBox.setSelected(false);
            giftShopBox.setSelected(false);
            itBox.setSelected(false);
            interpreterBox.setSelected(false);
        } else {
            log.error("SQL update affected more than 1 row.");
        }
        loaderHelper.showAndFade(lblConfirmation);
        cache.cacheUsersFromDB();
        cache.cacheDoctorsFromDB();
    }

    @FXML
    private void userSelected(){
       languages.setDisable(false);
    }
}



