package edu.wpi.cs3733.d20.teamL.views.controllers.logged_in;

import com.google.inject.Inject;
import com.jfoenix.controls.*;
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

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;

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
    JFXTextField doctorIDText, fNameText, lNameText, usernameText;
    @FXML
    JFXPasswordField passwordText;
    @FXML
    private JFXComboBox<String> serviceCombo, userCombo, languages;
    @FXML
    private JFXCheckBox securityBox, inTransportBox, exTransportBox, maintenanceBox, sanitationBox, pharmacistBox, giftShopBox, itBox, interpreterBox, managerBox;
    @FXML
    private VBox boxOService;
    @FXML
    private JFXButton btnCancel;

    @FXML
    private void setBtnCancel() {
        Stage stage;
        stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        serviceCombo.setItems(serviceOptions);
        userCombo.setItems(userOptions);
        languages.setItems(languageOptions);

        sf = new SearchFields(cache.getNodeCache());
        sf.getFields().add(SearchFields.Field.nodeID);
        sf.populateSearchFields();
        autoCompletePopup = new JFXAutoCompletePopup<>();
        autoCompletePopup.getSuggestions().addAll(sf.getSuggestions());
    }

    //for user
    @FXML
    private void submitClicked() {
        String firstName = fNameText.getText();
        String lastName = lNameText.getText();
        String username = usernameText.getText();
        String password = passwordText.getText();
        String doctorID = doctorIDText.getText();
        String type;
        String services;
        String manager;

        switch (userCombo.getValue()) {
            default:
            case "Staff":
                type = "0";
                break;
            case "Nurse":
                type = "1";
                break;
            case "Doctor":
                type = "2";
                break;
            case "Admin":
                type = "3";
        }
        int rows = 0;

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
            manager = managerBox.isSelected() ? serviceCombo.getSelectionModel().getSelectedItem().toLowerCase().replace(" ", "_") : null;

            services = managerBox.isSelected() ? null : servicesList.toString();
            if (type.equals("1") || type.equals("2")) {
                services = "pharmacy";
            }
            if (!(firstName.isBlank() || lastName.isBlank() || username.isBlank() || password.isBlank() || userCombo.getValue().isBlank())) {
                rows = db.executeUpdate(new SQLEntry(DBConstants.ADD_USER, new ArrayList<>(Arrays.asList(firstName, lastName, username, PasswordManager.hashPassword(password), type, services, manager))));
            }
            if (doctorIDText.getText() != null && !(doctorIDText.getText().isEmpty())) {
				rows = db.executeUpdate(new SQLEntry(DBConstants.UPDATE_DOCTOR_USERNAME, new ArrayList<>(Arrays.asList(username, doctorID))));
			}
            if (rows == 0) {
                lblConfirmation.setTextFill(Color.RED);
                lblConfirmation.setText("Submission failed");
            } else if (rows == 1) {
                lblConfirmation.setTextFill(Color.BLACK);
                lblConfirmation.setText("User added");
                lNameText.setText("");
                fNameText.setText("");
                usernameText.setText("");
                passwordText.setText("");
                doctorIDText.setText("");
                languages.setValue(null);
                managerBox.setSelected(false);
                managerBox.setVisible(false);
                serviceCombo.setVisible(false);
                serviceCombo.setDisable(false);
                boxOService.setVisible(false);
                boxOService.setDisable(false);
                languages.setVisible(false);
                languages.setDisable(false);
                doctorIDText.setVisible(false);
                doctorIDText.setDisable(false);
                userCombo.setValue("");
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
    }

    @FXML
    private void userSelected() {
        String user = userCombo.getValue();
        if (user.equals("Staff")) {
            managerBox.setVisible(true);
            managerBox.setDisable(false);
            if (managerBox.isSelected()) {
                serviceCombo.setVisible(true);
                serviceCombo.setDisable(false);
                boxOService.setVisible(false);
                boxOService.setDisable(true);
                languages.setVisible(false);
                languages.setDisable(true);
            } else {
                System.out.println("services");
                serviceCombo.setVisible(false);
                serviceCombo.setDisable(true);
                boxOService.setVisible(true);
                boxOService.setDisable(false);
                if (interpreterBox.isSelected()) {
                    languages.setVisible(true);
                    languages.setDisable(false);
                } else {
                    languages.setVisible(false);
                    languages.setDisable(true);
                }
            }
        } else {
            managerBox.setDisable(true);
            managerBox.setVisible(false);
            languages.setVisible(false);
            languages.setDisable(true);
            boxOService.setVisible(false);
            boxOService.setDisable(true);
        }

        if (user.equals("Doctor")) {
            doctorIDText.setVisible(true);
            doctorIDText.setDisable(false);
        } else {
            doctorIDText.setVisible(false);
            doctorIDText.setDisable(true);
        }
    }

    //Add doctor
    @FXML
    private void autocomplete() {
        sf.applyAutocomplete(officeText, autoCompletePopup);
    }

    /**
     * Handles UI portion of submit being clicked giving confirmation when it succeeds
     */

    @FXML
    private void btnSubmitClicked() {
        String doctorID = docID.getText();
        String fName = doctorFN.getText();
        String lName = doctorLN.getText();
        String roomNum = officeText.getText();
        String additionalInfo = addlInfoText.getText();
        int rows = db.executeUpdate(new SQLEntry(DBConstants.ADD_DOCTOR, new ArrayList<>(Arrays.asList(doctorID, fName, lName, null, roomNum, additionalInfo))));
        if (rows == 0) {
            confirmation.setTextFill(Color.RED);
            confirmation.setText("Submission failed");
        } else if (rows == 1) {
            confirmation.setTextFill(Color.BLACK);
            confirmation.setText("Doctor Added");
            doctorFN.setText("");
            doctorLN.setText("");
            doctorIDText.setText("");
            officeText.setText("");
            addlInfoText.setText("");
		} else {
			log.error("SQL update affected more than 1 row.");
		}
        loaderHelper.showAndFade(confirmation);
		cache.cacheDoctorsFromDB();
    }
}

