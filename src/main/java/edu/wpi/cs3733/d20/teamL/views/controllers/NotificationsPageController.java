package edu.wpi.cs3733.d20.teamL.views.controllers;

import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class NotificationsPageController {
    @FXML
    JFXButton btnBack;


    @FXML
    public void handleButtonAction(ActionEvent e) throws IOException {
        Stage stage;
        Parent root;

        if (e.getSource() == btnBack) {
            stage = (Stage) btnBack.getScene().getWindow();
            //stage = new Stage();
            root = FXMLLoader.load(getClass().getResource("/edu/wpi/cs3733/d20/teamL/views/StaffView.fxml"));
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setFullScreen(true);
            stage.show();
        }
    }
}
