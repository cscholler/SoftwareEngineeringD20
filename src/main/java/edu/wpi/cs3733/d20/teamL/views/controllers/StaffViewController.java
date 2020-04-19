package edu.wpi.cs3733.d20.teamL.views.controllers;

import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

public class StaffViewController {

    @FXML
    private JFXButton btnLogout;
    @FXML
    private JFXButton btnNotif;
    @FXML
    private JFXButton btnMeds;
    @FXML
    private JFXButton btnFindOpen;
    @FXML
    private JFXButton btnMR;
    @FXML
    private JFXButton btnChangeR;
    @FXML
    private JFXButton btnMap;
    @FXML
    private Label lblName;

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
            root = FXMLLoader.load(getClass().getResource("/edu/wpi/cs3733/d20/teamL/views/NotificationsPage.fxml"));
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setFullScreen(true);
            stage.show();
            //opens med request
        } else if (event.getSource() == btnMeds) {
            stage = (Stage) btnMeds.getScene().getWindow();
            root = FXMLLoader.load(getClass().getResource("/edu/wpi/cs3733/d20/teamL/views/MedicationRequest.fxml"));
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setFullScreen(true);
            stage.show();
            //opens mapView
        } else if (event.getSource() == btnMap) {
            stage = (Stage) btnMap.getScene().getWindow();
            root = FXMLLoader.load(getClass().getResource("/edu/wpi/cs3733/d20/teamL/views/MapViewer.fxml"));
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setFullScreen(true);
            stage.show();
        } else if (event.getSource() == btnLogout) {
            stage = (Stage) btnLogout.getScene().getWindow();
            root = FXMLLoader.load(getClass().getResource("/edu/wpi/cs3733/d20/teamL/views/Home.fxml"));
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setFullScreen(true);
            stage.show();
        }
    }
}

