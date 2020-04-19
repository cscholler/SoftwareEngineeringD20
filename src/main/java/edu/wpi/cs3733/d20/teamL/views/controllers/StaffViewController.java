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
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;



public class StaffViewController {

    @FXML
    private ImageView iLogout;
    @FXML
    private ImageView iNotif;
    @FXML
    private ImageView iMeds;
    @FXML
    private ImageView iFindOpen;
    @FXML
    private ImageView iMR;
    @FXML
    private ImageView iChangeR;
    @FXML
    private ImageView iMap;
    @FXML
    private Label lblName;


    @FXML
    public void handleCircles(MouseEvent event) throws IOException {
        Stage stage;
        Parent root;
        if (event.getSource() == iNotif) {


        } else if (event.getSource() == iMeds) {
            /*stage = new Stage();
            root = FXMLLoader.load(getClass().getResource("/edu/wpi/cs3733/d20/teamL/views/AHHHHHHH.fxml"));
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();*/


        } else if (event.getSource() == iMap) {
            stage = new Stage();
            root = FXMLLoader.load(getClass().getResource("/edu/wpi/cs3733/d20/teamL/views/MapViewer.fxml"));
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();


        }
    }
}

