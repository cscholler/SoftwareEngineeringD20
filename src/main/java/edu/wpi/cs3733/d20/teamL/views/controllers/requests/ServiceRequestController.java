package edu.wpi.cs3733.d20.teamL.views.controllers.requests;

import edu.wpi.cs3733.d20.teamL.util.FXMLLoaderFactory;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class ServiceRequestController {
    FXMLLoaderFactory loaderHelper = new FXMLLoaderFactory();

    @FXML
    private void backClicked() {
        loaderHelper.goBack();
    }

    @FXML
    private void securityClicked() {
        try {
            Parent root = loaderHelper.getFXMLLoader("SecurityRequest").load();
            loaderHelper.setupScene(new Scene(root));
        } catch (IOException ex) {
            log.error("Encountered IOException", ex);
        }
    }

    @FXML
    private void outTransClicked() {
        try {
            Parent root = loaderHelper.getFXMLLoader("requests/ExternalPane").load();
            loaderHelper.setupScene(new Scene(root));
        } catch (IOException ex) {
            log.error("Encountered IOException", ex);
        }
    }

    @FXML
    private void translateClicked() {
        try {
            Parent root = loaderHelper.getFXMLLoader("Interpreter").load();
            loaderHelper.setupScene(new Scene(root));
        } catch (IOException ex) {
            log.error("Encountered IOException", ex);
        }
    }

    @FXML
    private void inTransClicked() {
        try {
            Parent root = loaderHelper.getFXMLLoader("requests/InternalPane").load();
            loaderHelper.setupScene(new Scene(root));
        } catch (IOException ex) {
            log.error("Encountered IOException", ex);
        }
    }

    @FXML
    private void itClicked() {
        try {
            Parent root = loaderHelper.getFXMLLoader("ITService").load();
            loaderHelper.setupScene(new Scene(root));
        } catch (IOException ex) {
            log.error("Encountered IOException", ex);
        }
    }

    @FXML
    private void maintainanceClicked() {
        try {
            Parent root = loaderHelper.getFXMLLoader("requests/MaintenanceRequest").load();
            loaderHelper.setupScene(new Scene(root));
        } catch (IOException ex) {
            log.error("Encountered IOException", ex);
        }
    }

    @FXML
    private void sanitationClicked() {
        try {
            Parent root = loaderHelper.getFXMLLoader("SanitationServicesRequest").load();
            loaderHelper.setupScene(new Scene(root));
        } catch (IOException ex) {
            log.error("Encountered IOException", ex);
        }
    }

    @FXML
    private void giftRequestClicked() {
        try {
            Parent root = loaderHelper.getFXMLLoader("GiftCart").load();
            loaderHelper.setupScene(new Scene(root));
        } catch (IOException ex) {
            log.error("Encountered IOException", ex);
        }
    }

    @FXML
    private void medRequestClicked() {
        try {
            Parent root = loaderHelper.getFXMLLoader("MedicationRequest").load();
            loaderHelper.setupScene(new Scene(root));
        } catch (IOException ex) {
            log.error("Encountered IOException", ex);
        }
    }
}
