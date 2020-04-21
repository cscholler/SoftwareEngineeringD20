package edu.wpi.cs3733.d20.teamL.views.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;


public class LoginController implements Initializable {


    @FXML
    private JFXTextField username;

    @FXML
    private JFXPasswordField pass;

    @FXML
    private JFXButton login, btnCancel;

    @FXML
    private Text incorrectText;

    @FXML
    AnchorPane anchorPane;

    /**
     * Controls the login feature setting
     * @param e Tracks which button is pressed
     * @throws IOException
     */
    @FXML
    private void handleLogin(ActionEvent e) throws IOException {
        Stage stage;
        Parent root;

        String user = username.getText();
        String password = pass.getText();
        incorrectText.setVisible(false);
        String status = "Incorrect username or password";

        //set up flashing text
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(200), anchorPane);
        fadeTransition.setFromValue(0.5);
        fadeTransition.setToValue(1.0);
        fadeTransition.setCycleCount(3);

        if (e.getSource() == btnCancel) {
            System.out.println("Cancel");
            stage = (Stage) btnCancel.getScene().getWindow();
            stage.close();

        } else if (e.getSource() == login) {

            if (user.equals("Doctor") && password.equals("Doctor")) {
                System.out.println("Doctor");
                status = "Doctor";

                stage = (Stage) login.getScene().getWindow();
                stage.close();
                stage = (Stage) stage.getOwner(); //Gets the owner of the popup (AKA our homescreen) in order to set that as the stage

                //stage.close();
                root = FXMLLoader.load(getClass().getResource("/edu/wpi/cs3733/d20/teamL/views/StaffView.fxml"));
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.hide();
                stage.setMaximized(true);
                stage.show();

            }
            else if (user.equals("Nurse") && password.equals("Nurse")) {
                System.out.println("Nurse");
                status = "Nurse";
                stage = (Stage) login.getScene().getWindow();

                stage.close();
                stage = (Stage) stage.getOwner();

                root = FXMLLoader.load(getClass().getResource("/edu/wpi/cs3733/d20/teamL/views/StaffView.fxml"));
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.hide();
                stage.setMaximized(true);
                stage.show();
            }

            else if (user.equals("Admin") && password.equals("Admin")) {
                System.out.println("Admin");
                status = "Admin";
                stage = (Stage) login.getScene().getWindow();

                stage.close();
                stage = (Stage) stage.getOwner();

                root = FXMLLoader.load(getClass().getResource("/edu/wpi/cs3733/d20/teamL/views/AdminView.fxml"));
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.setMaximized(true);
                stage.show();
            }
            else {
                incorrectText.setVisible(true);
                fadeTransition.play();
            }
            System.out.println(status);
            pass.clear();
            username.clear();

            //return status;
        }
    }
//    private void handleCancel(ActionEvent e) throws IOException {
//        Stage stage = (Stage) btnCancel.getScene().getWindow();
//        Parent root = FXMLLoader.load(getClass().getResource("edu/wpi/cs3733/d20/teamL/views/MapViewer.fxml"));
//        stage.close();
//    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

}
