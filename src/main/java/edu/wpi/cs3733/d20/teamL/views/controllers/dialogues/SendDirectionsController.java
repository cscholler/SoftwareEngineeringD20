package edu.wpi.cs3733.d20.teamL.views.controllers.dialogues;

import com.google.inject.Inject;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import edu.wpi.cs3733.d20.teamL.services.IMessengerService;
import javafx.fxml.FXML;
import javafx.stage.Stage;

public class SendDirectionsController {
	@Inject
	private IMessengerService messenger;
    @FXML
    private JFXTextField emailField;
    @FXML
    private JFXButton btnCancel;
    @FXML
    private JFXTextField phoneNumberField;


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
	void sendText(){
        String phoneNumber = phoneNumberField.getText();
        //TODO: replace with Twilio implementation
        messenger.sendText(messenger.getDirections(), phoneNumber);
    }

    @FXML
    void sendEmail(){
        //TODO: replace with SendGrid implementation
        String emailAddress = emailField.getText();
        messenger.sendEmail(messenger.getDirections(), emailAddress);
    }


}
