package edu.wpi.cs3733.d20.teamL.views.controllers;

import edu.wpi.cs3733.d20.teamL.util.FXMLLoaderHelper;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class StaffViewController {

    private FXMLLoaderHelper loaderHelper = new FXMLLoaderHelper();

    @FXML
    private void notifsClicked(){
        try {
            Parent root = loaderHelper.getFXMLLoader("NotificationsPage").load();
            loaderHelper.setupPopup(new Stage(), new Scene(root));
        } catch (IOException e) {
            log.error("Encountered IOException", e);
        }
    }
    @FXML
    private void medsClicked(){
        try {
            Parent root = loaderHelper.getFXMLLoader("MedicationRequest").load();
            loaderHelper.setupPopup(new Stage(), new Scene(root));
        } catch (IOException e) {
            log.error("Encountered IOException", e);
        }
    }
    @FXML
    private void mapClicked(){
        try {
            Parent root = loaderHelper.getFXMLLoader("MapViewer").load();
            loaderHelper.setupPopup(new Stage(), new Scene(root));
        } catch (IOException e) {
            log.error("Encountered IOException", e);
        }
    }

    @FXML
    private void logoutClicked(){
        try {
            Parent root = loaderHelper.getFXMLLoader("Home").load();
            loaderHelper.setupPopup(new Stage(), new Scene(root));
        } catch (IOException e) {
            log.error("Encountered IOException", e);
        }
    }

    @FXML
    private void addPatientClicked(){
        try {
            Parent root = loaderHelper.getFXMLLoader("AddPatient").load();
            loaderHelper.setupPopup(new Stage(), new Scene(root));
        } catch (IOException e) {
            log.error("Encountered IOException", e);
        }
    }


}
