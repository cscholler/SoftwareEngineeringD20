package edu.wpi.cs3733.d20.teamL.views.controllers.dialogues;

import com.google.inject.Inject;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import edu.wpi.cs3733.d20.teamL.services.IMessengerService;
import edu.wpi.cs3733.d20.teamL.util.FXMLLoaderHelper;
import javafx.fxml.FXML;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.control.Label;

import java.awt.*;

public class SendDirectionsController {
	@Inject
	private IMessengerService messenger;
    @FXML
    private JFXTextField emailField;
    @FXML
    private JFXButton btnCancel;
    @FXML
    private JFXTextField phoneNumberField;
    @FXML
    private Label confirmation;

    private FXMLLoaderHelper loaderHelper = new FXMLLoaderHelper();

    /**
     * closes text directions window
     */
    @FXML
    void cancelClicked() {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }

    /**
     * sends either a text or an email
     *
     */

    @FXML
	void sendText() {
        String phoneNumber = phoneNumberField.getText();
        confirmation.setText(messenger.sendText(messenger.getDirections(), phoneNumber));
        if (confirmation.getText().equals("Message Sent")) {
            confirmation.setTextFill(Color.WHITE);
            phoneNumberField.setText("");
            emailField.setText("");
        } else {
            confirmation.setTextFill(Color.RED);
        }
        loaderHelper.showAndFade(confirmation);
    }

    @FXML
    void sendEmail() {
        String emailAddress = emailField.getText();
        confirmation.setText(messenger.sendEmail(messenger.getDirections(), emailAddress));
        if (confirmation.getText().equals("Message Sent")) {
            confirmation.setTextFill(Color.WHITE);
            emailField.setText("");
            phoneNumberField.setText("");
        } else {
            confirmation.setTextFill(Color.RED);
        }
        loaderHelper.showAndFade(confirmation);
    }
}
