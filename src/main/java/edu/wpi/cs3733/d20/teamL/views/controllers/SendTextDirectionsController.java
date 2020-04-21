package edu.wpi.cs3733.d20.teamL.views.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import edu.wpi.cs3733.d20.teamL.util.SendEmailDirections;
import edu.wpi.cs3733.d20.teamL.util.SendTextDirections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.stage.Stage;

import java.util.List;
import java.util.Set;

public class SendTextDirectionsController {

    List<String> directions;

//    public SendTextDirectionsController(List<String> directions){
//        this.directions = directions;
//    }

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

//    @FXML
//    void onSendEmailClicked(ActionEvent event) {
//        if(emailField.getText().equals("")){
//            return;
//        }else{
//            String email = emailField.getText();
//            SendEmailDirectionsThread SEDT = new SendEmailDirectionsThread(this.directions,email);
//            SEDT.start();
//
//        }
//
//    }

    @FXML
    void handleSend(ActionEvent event) {
        if(event.getSource() == btnText){
            System.out.println("YUH");
            if(carrierSelector.getText().equals("Select Carrier for Number")){
                return;
            }else{
                String carrier = carrierSelector.getText();
                String number = phoneNumberField.getText();
                SendTextDirections STDT = new SendTextDirections("This way",number,carrier);
                STDT.start();
            }
        }else if(event.getSource() == btnEmail){
            if(emailField.getText().equals("")){
            return;
        }else{
            String email = emailField.getText();
            SendEmailDirections SEDT = new SendEmailDirections("Heyo Baller",email);
            SEDT.start();

        }
        }
    }

    @FXML
    void onVerizon(ActionEvent event) {
        carrierSelector.setText("Verizon");
    }

}
