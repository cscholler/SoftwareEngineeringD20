package edu.wpi.cs3733.d20.teamL.views.controllers;

import com.google.inject.Inject;
import com.jfoenix.controls.JFXButton;
import edu.wpi.cs3733.d20.teamL.services.db.IDatabaseCache;
import edu.wpi.cs3733.d20.teamL.services.db.IDatabaseService;
import edu.wpi.cs3733.d20.teamL.services.users.ILoginManager;
import edu.wpi.cs3733.d20.teamL.util.FXMLLoaderHelper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

@Slf4j
public class LoggedInViewController implements Initializable{
    @FXML
    JFXButton btnAddUser, btnAddDoctor, btnAddPatient, btnServiceRequest, btnMapEditor;
    @FXML
    HBox buttonBox;
    @FXML
    Label lblName, lblMap;
    @FXML
    private VBox vMap, vPatient, vDoc, vService, vUser;
    @Inject
	IDatabaseService db;
    @Inject
    ILoginManager loginManager;
    String map;
    FXMLLoaderHelper loaderHelper = new FXMLLoaderHelper();


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        lblName.setText(loginManager.getCurrentUser().getFName());
        if (loginManager.getCurrentUser().getAcctType().equals("3")) {
            buttonBox.getChildren().remove(vPatient);
            buttonBox.getChildren().remove(vService);
            map = "MapEditor";
            lblMap.setText("Map Editor");
        } else {
            buttonBox.getChildren().remove(vUser);
            buttonBox.getChildren().remove(vDoc);
            map = "MapViewer";
            lblMap.setText("Map Viewer");
        }
    }

    /**
     * goes to notifications page when button is clicked
     */
    @FXML
    private void notifsClicked() {
        try {
            Parent root = loaderHelper.getFXMLLoader("NotificationsPage").load();
            loaderHelper.setupScene(new Scene(root));
        } catch (IOException ex) {
            log.error("Encountered IOException", ex);
        }
    }

    @FXML
    private void addUserClicked(){
        try {
            System.out.println("Got here");
            Parent root = loaderHelper.getFXMLLoader("AddUser").load();
            loaderHelper.setupScene(new Scene(root));
        } catch (IOException e) {
            log.error("Encountered IOException", e);
        }
    }

    @FXML
    private void addDoctorClicked(){
        try {
            Parent root = loaderHelper.getFXMLLoader("AddDoctor").load();
            loaderHelper.setupScene(new Scene(root));
        } catch (IOException e) {
            log.error("Encountered IOException", e);
        }
    }

    @FXML
    private void addPatientClicked(){
        try {
            Parent root = loaderHelper.getFXMLLoader("AddPatient").load();
            loaderHelper.setupScene(new Scene(root));
        } catch (IOException e) {
            log.error("Encountered IOException", e);
        }
    }

    @FXML
    private void mapEditorClicked(){
        try {
            Parent root = loaderHelper.getFXMLLoader(map).load();
            loaderHelper.setupScene(new Scene(root));
        } catch (IOException ex) {
            log.error("Encountered IOException", ex);
        }
    }

    @FXML
    private void serviceRequestsClicked(){
        try {
            Parent root = loaderHelper.getFXMLLoader("ServiceRequestsPage").load();
            loaderHelper.setupScene(new Scene(root));
        } catch (IOException ex) {
            log.error("Encountered IOException", ex);
        }
    }

    /**
     * goes back to homepage when logout button is clicked
     */
    @FXML
    private void logoutClicked() {
        log.info("here");
        loginManager.logOut();
        try {
            Parent root = loaderHelper.getFXMLLoader("Home").load();
            loaderHelper.setupScene(new Scene(root));
        } catch (IOException ex) {
            log.error("Encountered IOException", ex);
        }
    }

    @FXML
	public void importClicked(ActionEvent actionEvent) {

	}

	@FXML
	public void exportClicked(ActionEvent actionEvent) {

	}

	@FXML
	public void clearClicked() {
    	log.warn("Rebuilding database");
    	db.rebuildDatabase();
	}
}
