package edu.wpi.cs3733.d20.teamL.views.controllers;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;

import com.google.inject.Inject;

import lombok.extern.slf4j.Slf4j;

import edu.wpi.cs3733.d20.teamL.util.FXMLLoaderHelper;
import edu.wpi.cs3733.d20.teamL.services.users.ILoginManager;

@Slf4j
public class StaffViewController {
	private FXMLLoaderHelper loaderHelper = new FXMLLoaderHelper();
	@Inject
	private ILoginManager loginManager;


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

    /**
     * goes to medication request page when button is clicked
     */
    @FXML
    private void medsClicked() {
        try {
            Parent root = loaderHelper.getFXMLLoader("MedicationRequest").load();
            loaderHelper.setupScene(new Scene(root));
        } catch (IOException ex) {
            log.error("Encountered IOException", ex);
        }
    }

    /**
     * goes to map viewer when map butno is clicked
     */
    @FXML
    private void mapClicked() {
        try {
            Parent root = loaderHelper.getFXMLLoader("MapViewer").load();
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

    /**
     * goes to add patient page when button is clicked
     */
    @FXML
    private void addPatientClicked() {
        try {
            Parent root = loaderHelper.getFXMLLoader("AddPatient").load();
            loaderHelper.setupScene(new Scene(root));
        } catch (IOException e) {
            log.error("Encountered IOException", e);
        }
    }
}
