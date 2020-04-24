package edu.wpi.cs3733.d20.teamL.views.controllers;

import java.io.IOException;

import edu.wpi.cs3733.d20.teamL.views.controllers.map.MapEditorController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import com.jfoenix.controls.JFXButton;

import edu.wpi.cs3733.d20.teamL.util.FXMLLoaderHelper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AdminViewController {

    FXMLLoaderHelper loaderHelper = new FXMLLoaderHelper();

    /**
     * Controls staff view page after they log in
     *
     * @throws IOException
     */

    @FXML
    private void notificationsClicked() {
        try {
            Parent root = loaderHelper.getFXMLLoader("NotificationsPage").load();
            loaderHelper.setupScene(new Scene(root));
        } catch (IOException e) {
            log.error("Encountered IOException", e);
        }
        //opens med request
    }

    @FXML
    private void addDoctorClicked() {
        try {
            Parent root = loaderHelper.getFXMLLoader("AddDoctor").load();
            loaderHelper.setupScene(new Scene(root));
        } catch (IOException e) {
            log.error("Encountered IOException", e);
        }

    }

    @FXML
    private void logoutClicked() {
        try {
            loaderHelper.goBack();
        } catch (Exception e) {
            log.error("Encountered IOException", e);
        }

    }

    @FXML
    private void mapEditorClicked() {
        try {
            Parent root = loaderHelper.getFXMLLoader("MapEditor").load();
            loaderHelper.setupScene(new Scene(root));
        } catch (IOException e) {
            log.error("Encountered IOException", e);
        }

    }
}
