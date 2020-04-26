package edu.wpi.cs3733.d20.teamL.views.controllers.dialogues;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;

import java.io.IOException;

import edu.wpi.cs3733.d20.teamL.services.users.LoginManager;
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

import javax.inject.Inject;

public class LoginController {
	FXMLLoaderHelper loaderHelper = new FXMLLoaderHelper();
    @FXML
    private JFXTextField usernameField;
    @FXML
    private JFXPasswordField passwordField;
    @FXML
    private JFXButton login, btnCancel;
    @FXML
    private Text incorrectText;
    @FXML
    private AnchorPane anchorPane;
    @Inject
	private LoginManager loginManager;

    /**
     * logs the user in when the enter key is pressed
     * @param event Tracks which key is pressed
     */
    @FXML
    private void enterHandle(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            //"presses" the login button
            login.fire();
        }
    }

    /**
     * Dummy function to allow enter to be pressed from the password box
     * @param e the key that is pressed
     * @throws
     */
    @FXML
    private void bugfix(ActionEvent e) {}

    /**
     * Controls the login feature setting usernames and passwords and only accepting correct usernames and passwords
     * @param event Tracks which button is pressed
     * @throws IOException
     */
    @FXML
    private void handleLogin(ActionEvent event) throws IOException {
        Stage stage;

        String username = usernameField.getText();
        String password = passwordField.getText();
        incorrectText.setVisible(false);

        //set up flashing text
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(200), anchorPane);
        fadeTransition.setFromValue(0.5);
        fadeTransition.setToValue(1.0);
        fadeTransition.setCycleCount(1);

        //closes login popup
        if (event.getSource() == btnCancel) {
            stage = (Stage) btnCancel.getScene().getWindow();
            stage.close();
        } else if (event.getSource() == login) {
            loginManager.logIn(username, password);
            if (loginManager.isAuthenticated()) {
				((Stage) login.getScene().getWindow()).close();
				String view;
            	switch (loginManager.getCurrentUser().getAcctType()) {
					default:
					case "0":
					case "1":
					case "2": {
						view = "StaffView";
					}
					break;
					case "3": {
						view = "AdminView";
					}
				}
				loaderHelper.setupScene(new Scene(loaderHelper.getFXMLLoader(view).load()));
			} else {
				incorrectText.setVisible(true);
				fadeTransition.play();
			}
            usernameField.clear();
			passwordField.clear();
        }
    }
}
