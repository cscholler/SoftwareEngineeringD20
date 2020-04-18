package edu.wpi.cs3733.d20.teamL.views.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;

/**
 *
 * @author villan
 */
public class LoginController implements Initializable {


    @FXML
    private AnchorPane anchorPane;

    @FXML
    private JFXTextField username;

    @FXML
    private JFXPasswordField pass;

    @FXML
    private JFXButton login;

    @FXML
    void makeLogin(ActionEvent event) {
        if(username.getText().equals("genuine")&&pass.getText().equals("coder"))
        {
            System.out.println("Welcome");
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

}
