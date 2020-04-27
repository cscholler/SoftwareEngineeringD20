package edu.wpi.cs3733.d20.teamL.views.controllers.dialogues;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.stage.Stage;

import javax.inject.Inject;
import java.util.List;

public class SendTextDirectionsController {

    List<String> directions;

    @FXML
    private JFXTextField emailField;
    @FXML
    private JFXButton btnCancel, btnEmail, btnText;
    @FXML
    private MenuButton carrierSelector;
    @FXML
    private JFXTextField phoneNumberField;
    @FXML
    void onSprint(ActionEvent event) {
        carrierSelector.setText("Sprint");
    }
    @FXML
    void onTMobile(ActionEvent event) {
        carrierSelector.setText("T-Mobile");
    }
    @FXML
    void onATT(ActionEvent event) {
        carrierSelector.setText("AT&T");
    }
    @FXML
    void onVerizon(ActionEvent event) { carrierSelector.setText("Verizon");}

    /**
     * closes text directions window
     * @param event tracks when button is clicked
     */
    @FXML
    void handleButtonCancel(ActionEvent event) {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }

    /**
     * sends either a text or an email
     * @param event tracks which button is clicked
     */
	@FXML
	void handleSend(ActionEvent event) {
	    //sends text
		if (event.getSource() == btnText) {
			if (!carrierSelector.getText().equals("Select Carrier for Number")) {
				String carrier = carrierSelector.getText();
				String number = phoneNumberField.getText();
				//TODO: replace with Twilio implementation
				/*mailer.setCarrier(carrier);
				mailer.setPhoneNumber(number);
				mailer.setIsText(true);
				mailer.sendTextToCarrier();*/
			}
		// sends email
		} else if (event.getSource() == btnEmail) {
			if (!emailField.getText().equals("")) {
				//TODO: replace with SendGrid implementation
				String email = emailField.getText();
				/*mailer.setEmailAddress(email);
				mailer.setIsText(false);
				mailer.sendMail();*/
			}
		}
	}
}
