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
    ObservableList list = FXCollections.observableArrayList();


    @FXML
    JFXButton btnBack, btnCompleted, btnDecline;
    @FXML
    JFXListView<MedicineRequest> notifications;
    @FXML
    Label reqMessage, addInfo;

    /**
     * Loads data to the list view in the form of MedicineRequest Objects
     */
    @FXML
    private void loadData() {
        list.removeAll(list);
        MedicineRequest test = new MedicineRequest("Josh Hoy", "1234", "Joan", "Aspirin", "2cc", "305", "Nothing to report");
        MedicineRequest test1 = new MedicineRequest("Luke Bodwell", "3333", "Joy", "Oxicotin", "20cc", "430", "Four score and seven years ago our fathers brought forth on this continent, a new nation, conceived in Liberty, and dedicated to the proposition that all men are created equal.\n" +
                "\n" +
                "Now we are engaged in a great civil war, testing whether that nation, or any nation so conceived and so dedicated, can long endure. We are met on a great battle-field of that war. We have come to dedicate a portion of that field, as a final resting place for those who here gave their lives that that nation might live. It is altogether fitting and proper that we should do this.\n" +
                "\n" +
                "But, in a larger sense, we can not dedicate -- we can not consecrate -- we can not hallow -- this ground. The brave men, living and dead, who struggled here, have consecrated it, far above our poor power to add or detract. The world will little note, nor long remember what we say here, but it can never forget what they did here. It is for us the living, rather, to be dedicated here to the unfinished work which they who fought here have thus far so nobly advanced. It is rather for us to be here dedicated to the great task remaining before us -- that from these honored dead we take increased devotion to that cause for which they gave the last full measure of devotion -- that we here highly resolve that these dead shall not have died in vain -- that this nation, under God, shall have a new birth of freedom -- and that government of the people, by the people, for the people, shall not perish from the earth.\n" +
                "\n" +
                "Abraham Lincoln\n" +
                "November 19, 1863");
        MedicineRequest test2 = new MedicineRequest("Conrad Tulig", "5454", "Jill", "Cough Syrup", "5cc", "100", "Although it has been over 15 years since the first recorded use of a robot for a surgical procedure, the field of medical robotics is still an emerging one that has not yet reached a critical mass. Although robots have the potential to improve the precision and capabilities of physicians, the number of robots in clinical use is still very small. In this review article, we begin with a short historical review of medical robotics, followed by an overview of clinical applications where robots have been applied. The clinical applications are then discussed; they include neurosurgery, orthopedics, urology, maxillofacial surgery, radiosurgery, ophthalmology, and cardiac surgery. We conclude with a listing of technology challenges and research areas, including system architecture, software design, mechanical design, imaging compatible systems, user interface, and safety issues.");
        list.addAll(test, test1, test2);
        notifications.getItems().addAll(list);

    }

    /**
     * Checks for anyone clicking on the listView of notifications and opens them in the pane to the right
     */
    @FXML
    private void displaySelected() {
        MedicineRequest req = notifications.getSelectionModel().getSelectedItem();
        String message = notifications.getSelectionModel().getSelectedItem().getPatientName();
        if (message == null || message.isEmpty()) {
            System.out.println("Nothing");
        } else {
            reqMessage.setWrapText(true);
            reqMessage.setText(req.getNurseName() + " requests " + req.getDose() + " of " + req.getMedType() + " for " + req.getPatientName() + "(" + req.getPatientID() +")" + " in room " + req.getRoomNum() );
            addInfo.setWrapText(true);
            addInfo.setText(req.getAddInfo());
            System.out.println(message);
        }
    }

    /**
     * Handles buttons pressed on the notification screen back, decline, approve
     * @param e tracks which button is pressed
     * @throws IOException
     */
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

    /**
     * Calls loadData and sets up the cellFactory
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        loadData();

        notifications.setCellFactory(param -> new ListCell<MedicineRequest>() {
            @Override
            protected void updateItem(MedicineRequest medReq, boolean empty) {
                super.updateItem(medReq, empty);
                if (medReq != null) {
                    setText(medReq.getDose() + " of " + medReq.getMedType() + " for " + medReq.getPatientName());
                }
            }
        });
    }
}
