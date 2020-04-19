package edu.wpi.cs3733.d20.teamL.views.controllers;

import com.jfoenix.controls.JFXAutoCompletePopup;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.events.JFXAutoCompleteEvent;
import edu.wpi.cs3733.d20.teamL.util.io.DBCache;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.skin.TextFieldSkin;
import javafx.stage.Stage;

import javax.management.Notification;
import javax.swing.text.html.ImageView;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

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

