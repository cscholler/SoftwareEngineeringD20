package edu.wpi.cs3733.d20.teamL.views.LoggedInView;


import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ChangePasswordController {

    @FXML
    private JFXButton btnCancel, btnConfirm;
    @FXML
    private JFXTextField usernameText, newPasswordText, confirmPasswordText;
    @FXML
    private Label incorrectPassword;

    @FXML
    public void handleChangePassword(ActionEvent event) {

        Stage stage;

        if (event.getSource() == btnCancel) {
            stage = (Stage) btnCancel.getScene().getWindow();
            stage.close();
        } else if (event.getSource() == btnConfirm) {

            stage = (Stage) btnConfirm.getScene().getWindow();
            stage.close();
        }
    }

}
