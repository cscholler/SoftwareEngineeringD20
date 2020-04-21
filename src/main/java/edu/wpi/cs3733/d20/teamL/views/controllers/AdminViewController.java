package edu.wpi.cs3733.d20.teamL.views.controllers;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import com.jfoenix.controls.JFXButton;

import edu.wpi.cs3733.d20.teamL.util.FXMLLoaderHelper;

public class AdminViewController {

    @FXML
    private JFXButton btnLogout;
    @FXML
    private JFXButton btnNotif;
    @FXML
    private JFXButton btnAddDoctor;
    @FXML
    private JFXButton btnFindOpen;
    @FXML
    private JFXButton btnMR;
    @FXML
    private JFXButton btnChangeR;
    @FXML
    private JFXButton btnMap;
    @FXML
    private JFXButton btnAddPatient;
    @FXML
    private Label lblName;

    FXMLLoaderHelper loaderHelper = new FXMLLoaderHelper();

    /**
     * Controls staff view page after they log in
     *
     * @param event tracks which button was pressed
     * @throws IOException
     */
    @FXML
    public void handleCircleButton(ActionEvent event) throws IOException {
        Stage stage;
        Parent root;

        //open notifications
        if (event.getSource() == btnNotif) {
            stage = (Stage) btnNotif.getScene().getWindow();
            root = loaderHelper.getFXMLLoader("NotificationsPage").load();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.show();
            //opens med request
        } else if (event.getSource() == btnAddDoctor) {
            stage = (Stage) btnAddDoctor.getScene().getWindow();
            root = loaderHelper.getFXMLLoader("AddDoctor").load();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.show();
            //opens mapView
        } else if (event.getSource() == btnMap) {
            stage = (Stage) btnMap.getScene().getWindow();
			root = loaderHelper.getFXMLLoader("MapViewController").load();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.show();
        } else if (event.getSource() == btnLogout) {
            stage = (Stage) btnLogout.getScene().getWindow();
			root = loaderHelper.getFXMLLoader("Home").load();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.show();
        } else if (event.getSource() == btnAddPatient) {
            stage = (Stage) btnAddPatient.getScene().getWindow();
			root = loaderHelper.getFXMLLoader("AddPatient").load();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.show();
        }
    }
}
