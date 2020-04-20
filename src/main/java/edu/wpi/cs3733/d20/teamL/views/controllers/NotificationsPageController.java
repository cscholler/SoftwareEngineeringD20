package edu.wpi.cs3733.d20.teamL.views.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class NotificationsPageController implements Initializable {
    ObservableList list= FXCollections.observableArrayList();


    @FXML
    JFXButton btnBack;
    @FXML
    JFXListView<String> notifications;


    @FXML
    private void loadData(){
        list.removeAll(list);
        String test = "Josh Hoy";
        String test1 = "JHoy";
        String test2 = "Hoyboy";
        list.addAll(test, test1, test2);
        notifications.getItems().addAll(list);

    }
@FXML
private void displaySelected(MouseEvent e){
    String message = notifications.getSelectionModel().getSelectedItem();
       if(message == null || message.isEmpty()){
           System.out.println("Nothing");
       }else{
           System.out.println(message);
       }
}
    @FXML
    public void handleButtonAction(ActionEvent e) throws IOException {
        Stage stage;
        Parent root;

        if (e.getSource() == btnBack) {
            stage = (Stage) btnBack.getScene().getWindow();
            //stage = new Stage();
            root = FXMLLoader.load(getClass().getResource("/edu/wpi/cs3733/d20/teamL/views/StaffView.fxml"));
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.show();
        }
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
     loadData();
    }
}
