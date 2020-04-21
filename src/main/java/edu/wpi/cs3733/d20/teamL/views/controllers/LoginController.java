package edu.wpi.cs3733.d20.teamL.views.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;

import java.io.IOException;

import edu.wpi.cs3733.d20.teamL.App;
import edu.wpi.cs3733.d20.teamL.util.FXMLLoaderHelper;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

public class LoginController {
	FXMLLoaderHelper loaderHelper = new FXMLLoaderHelper();
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
                root = loaderHelper.getFXMLLoader("StaffView").load();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.setMaximized(true);
                stage.show();

                stage.setWidth(App.SCREEN_WIDTH);
                stage.setHeight(App.SCREEN_HEIGHT);
            }
            else if (user.equals("Nurse") && password.equals("Nurse")) {
                System.out.println("Nurse");
                status = "Nurse";
                stage = (Stage) login.getScene().getWindow();

                stage.close();
                stage = (Stage) stage.getOwner();

				root = loaderHelper.getFXMLLoader("StaffView").load();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.setMaximized(true);
                stage.show();

                stage.setWidth(App.SCREEN_WIDTH);
                stage.setHeight(App.SCREEN_HEIGHT);
            }

            else if (user.equals("Admin") && password.equals("Admin")) {
                System.out.println("Admin");
                status = "Admin";
                stage = (Stage) login.getScene().getWindow();

                stage.close();
                stage = (Stage) stage.getOwner();

				root = loaderHelper.getFXMLLoader("AdminView").load();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.setMaximized(true);
                stage.show();

                stage.setWidth(App.SCREEN_WIDTH);
                stage.setHeight(App.SCREEN_HEIGHT);
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
}
