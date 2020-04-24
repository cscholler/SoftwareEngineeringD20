package edu.wpi.cs3733.d20.teamL.views.controllers.dialogues;

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
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
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
     * logs the user in when the enter key is pressed
     * @param e Tracks which key is pressed
     */
    @FXML
    private void enterHandle(KeyEvent e) {
        if(e.getCode() == KeyCode.ENTER) {
            //"presses" the login button
            login.fire();
        }
    }

    /**
     * Dummy function to allow enter to be pressed from the password box
     * @param e the key that is pressed
     * @throws IOException
     */
    @FXML
    private void bugfix(ActionEvent e) throws IOException {}

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
            stage = (Stage) btnCancel.getScene().getWindow();
            stage.close();
        } else if (e.getSource() == login) {
            if (user.equals("Doctor") && password.equals("Doctor")) {
                status = "Doctor";

                stage = (Stage) login.getScene().getWindow();
                stage.close();
                stage = (Stage) stage.getOwner(); //Gets the owner of the popup (AKA our homescreen) in order to set that as the stage

                //stage.close();
                root = loaderHelper.getFXMLLoader("StaffView").load();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.hide();
                stage.setMaximized(true);
                stage.show();

                stage.setWidth(App.SCREEN_WIDTH);
                stage.setHeight(App.SCREEN_HEIGHT);
            } else if (user.equals("Nurse") && password.equals("Nurse")) {
                status = "Nurse";
                stage = (Stage) login.getScene().getWindow();

                stage.close();
                stage = (Stage) stage.getOwner();

                System.out.println("Logging in to Nurse...");

				root = loaderHelper.getFXMLLoader("StaffView").load();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.hide();
                stage.setMaximized(true);
                stage.show();

                stage.setWidth(App.SCREEN_WIDTH);
                stage.setHeight(App.SCREEN_HEIGHT);
            } else if (user.equals("Admin") && password.equals("Admin")) {
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
            } else {
                incorrectText.setVisible(true);
                fadeTransition.play();
            }
            System.out.println(status);
            pass.clear();
            username.clear();
        }
    }
}
