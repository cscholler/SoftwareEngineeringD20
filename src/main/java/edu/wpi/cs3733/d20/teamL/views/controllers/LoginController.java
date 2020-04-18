package edu.wpi.cs3733.d20.teamL.views.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;


public class LoginController implements Initializable {


    @FXML
    private JFXTextField username;

    @FXML
    private JFXPasswordField pass;

    @FXML
    private JFXButton login, btnCancel;


    @FXML
    private String handleLogin(ActionEvent e) throws IOException {
        String user = username.getText();
        String password = pass.getText();
        String status = "Incorrect username or password";

        if (user.equals("Doctor") && password.equals("Doctor")) {
            System.out.println("Doctor");
            status = "Doctor";
        } else if (user.equals("Nurse") && password.equals("Nurse")) {
            System.out.println("Nurse");
            status = "Nurse";
        } else if (user.equals("Admin") && password.equals("Admin")) {
            System.out.println("Admin");
            status = "Admin";
        }
        System.out.println(status);
        pass.clear();
        username.clear();

        return status;
    }

//    private void handleCancel(ActionEvent e) throws IOException {
//        Stage stage = (Stage) btnCancel.getScene().getWindow();
//        Parent root = FXMLLoader.load(getClass().getResource("edu/wpi/cs3733/d20/teamL/views/MapViewer.fxml"));
//        stage.close();
//    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

}
