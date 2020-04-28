package edu.wpi.cs3733.d20.teamL.views.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import edu.wpi.cs3733.d20.teamL.util.FXMLLoaderHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class AssignPopupController implements Initializable {
    ObservableList<String> users;


    @FXML
    private JFXComboBox userSelector;

    @FXML
    private JFXButton btnBack;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        userSelector.setItems(users);
    }

    @FXML
    private void submitClicked(){

    }

    @FXML
    private void backClicked(){
        Stage stage = (Stage) btnBack.getScene().getWindow();
            stage.close();
    }
}
