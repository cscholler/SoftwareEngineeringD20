package edu.wpi.cs3733.d20.teamL.views.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import edu.wpi.cs3733.d20.teamL.entities.MedicineRequest;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
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
    JFXListView<MedicineRequest> notifications;


    @FXML
    private void loadData(){
        list.removeAll(list);
        MedicineRequest test = new MedicineRequest("Josh Hoy", "1234", "Joan", "Aspirin", "2cc", "305");
        MedicineRequest test1 = new MedicineRequest("Luke Bodwell", "3333", "Joy", "Oxicotin", "20cc", "430");
        MedicineRequest test2 = new MedicineRequest("Conrad Tulig", "5454", "Jill", "Cough Syrup", "5cc", "100");
        list.addAll(test, test1, test2);
        notifications.getItems().addAll(list);

    }
@FXML
private void displaySelected(MouseEvent e){
    String message = notifications.getSelectionModel().getSelectedItem().getPatientName();
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

        notifications.setCellFactory(param -> new ListCell<MedicineRequest>() {
                @Override
                protected void updateItem(MedicineRequest medReq, boolean empty) {
                    super.updateItem(medReq, empty);
                    if (medReq != null) {
                        setText(medReq.getDose() +  " of " + medReq.getMedType() + " for " + medReq.getPatientName());
                    }
            }
        });
    }
}
