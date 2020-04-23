package edu.wpi.cs3733.d20.teamL.views.controllers.dialogues;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import edu.wpi.cs3733.d20.teamL.services.mail.IMailerService;
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
    private Label sendDirectionsLabel;

    @FXML
    private Label descLabel;

    @FXML
    private JFXTextField emailField;

    @FXML
    private JFXButton btnCancel;

    @FXML
    private MenuButton carrierSelector;

    @FXML
    private JFXTextField phoneNumberField;

    @FXML
    private JFXButton btnEmail;

    @FXML
    private JFXButton btnText;

    @Inject
	IMailerService mailer;

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
    void handleButtonCancel(ActionEvent event) {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }

	@FXML
	void handleSend(ActionEvent event) {
		if (event.getSource() == btnText) {
			System.out.println("YUH");
			if (!carrierSelector.getText().equals("Select Carrier for Number")) {
				String carrier = carrierSelector.getText();
				String number = phoneNumberField.getText();
				mailer.setCarrier(carrier);
				mailer.setPhoneNumber(number);
				mailer.setIsText(true);
				mailer.sendTextToCarrier();
			}
		} else if (event.getSource() == btnEmail) {
			if (!emailField.getText().equals("")) {
				String email = emailField.getText();
				mailer.setEmailAddress(email);
				mailer.setIsText(false);
				mailer.sendMail();
			}
		}
	}

    @FXML
    void onVerizon(ActionEvent event) {
        carrierSelector.setText("Verizon");
    }
}
