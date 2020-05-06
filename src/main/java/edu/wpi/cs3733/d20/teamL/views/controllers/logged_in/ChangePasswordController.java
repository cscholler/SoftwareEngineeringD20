package edu.wpi.cs3733.d20.teamL.views.controllers.logged_in;


import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ChangePasswordController {

    @FXML
    private JFXButton btnCancel, btnConfirm;
    @FXML
    private JFXTextField usernameText, oldPasswordText, newPasswordText, confirmPasswordText;
    @FXML
    private Label incorrectPassword;

}
