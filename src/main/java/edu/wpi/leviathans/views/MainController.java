package edu.wpi.leviathans.views;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import java.io.IOException;

public class MainController {

    @FXML private Button btnBack;
    @FXML private Button btnDisplay;
    @FXML private Button btnModify;
    @FXML private Button btnDownload;
    @FXML private Button btnDemonstration;

    @FXML
    private void handleButtonAction(ActionEvent e) throws IOException {

        Stage stage;
        Parent root;

        if (e.getSource() == btnDisplay) {

            stage = (Stage) btnDisplay.getScene().getWindow();
            root = FXMLLoader.load(getClass().getResource("Display.fxml"));

        } else if (e.getSource() == btnModify) {

            stage = (Stage) btnModify.getScene().getWindow();
            root = FXMLLoader.load(getClass().getResource("Modify.fxml"));


        } else if (e.getSource() == btnDownload) {

            stage = (Stage) btnDownload.getScene().getWindow();
            root = FXMLLoader.load(getClass().getResource("Download.fxml"));

        } else if (e.getSource() == btnDemonstration) {

            stage = (Stage) btnDemonstration.getScene().getWindow();
            root = FXMLLoader.load(getClass().getResource("Demonstration.fxml"));

        } else {

            stage = (Stage) btnBack.getScene().getWindow();
            root = FXMLLoader.load(getClass().getResource("Display.fxml"));

        }

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();

    }


}
