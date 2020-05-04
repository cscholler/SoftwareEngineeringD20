package edu.wpi.cs3733.d20.teamL.views.controllers.requests;

import com.jfoenix.controls.*;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import edu.wpi.cs3733.d20.teamL.util.FXMLLoaderHelper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import java.io.IOException;

public class ReflectionRoomController {

    @FXML
    private JFXTreeTableView<TimeSlot> table;
    @FXML
    private JFXComboBox rooms;
    @FXML
    private JFXDatePicker date;
    @FXML
    private ImageView requestReceived;
    @FXML
    BorderPane borderPane;
    @FXML
    StackPane stackPane;
    private FXMLLoaderHelper loaderHelper = new FXMLLoaderHelper();

    private ObservableList<String> reflectionRooms = FXCollections.observableArrayList("Floor 1 Reflection Room", "Floor 3 Reflection Room", "Floor 4 Reflection Room");

    @FXML
    public void initialize() throws IOException {


        JFXTreeTableColumn<TimeSlot, String> startTime = new JFXTreeTableColumn<>("Start Time");
        startTime.setCellValueFactory(param -> param.getValue().getValue().start);

        JFXTreeTableColumn<TimeSlot, String> endTime = new JFXTreeTableColumn<>("End Time");
        endTime.setCellValueFactory(param -> param.getValue().getValue().end);

        JFXTreeTableColumn<TimeSlot, String> availability = new JFXTreeTableColumn<>("Availability");
        availability.setCellValueFactory(param -> param.getValue().getValue().availability);

        //TODO fill with actual times from database when date and room selected
        //TODO maybe have the database just contain requests, and don't show those when room + date is entered
        ObservableList<TimeSlot> slots = FXCollections.observableArrayList();
        for(int i = 0; i<24; i++) {
            String s = String.format("%02d", i);
            s = s + ":00";
            String e = String.format("%02d", ((i + 1 ) % 24));
            e = e + ":00";
            slots.add(new TimeSlot(s, e, "Open"));
        }

        final TreeItem<TimeSlot> root = new RecursiveTreeItem<TimeSlot>(slots, RecursiveTreeObject::getChildren);
        table.getColumns().setAll(startTime, endTime, availability);
        table.setRoot(root);
        table.setShowRoot(false);

        rooms.setItems(reflectionRooms);

        borderPane.prefWidthProperty().bind(stackPane.widthProperty());
        borderPane.prefHeightProperty().bind(stackPane.heightProperty());
    }

    @FXML
    private void handleSubmit() {
        TreeItem<TimeSlot> row = table.getSelectionModel().getSelectedItem();
        TimeSlot t = row.getValue();
        String startTime = t.start.getValue();
        String endTime = t.end.getValue();
        String availability = t.availability.getValue();
        String room = (String) rooms.getValue();
        String dateChosen = date.getValue().toString();
        //TODO check values for null, past date, time

        System.out.println(room + " " + dateChosen + " " + startTime + " to " + endTime);

        //TODO enter request into database

        //clear selected values
        rooms.setValue(null);
        date.setValue(null);
        table.getSelectionModel().clearSelection();

        loaderHelper.showAndFade(requestReceived);
    }

    class TimeSlot extends RecursiveTreeObject<TimeSlot> {
        StringProperty start;
        StringProperty end;
        StringProperty availability;

        public TimeSlot(String start,String end, String availability)
        {
            this.start = new SimpleStringProperty(start);
            this.end  = new SimpleStringProperty(end);
            this.availability = new SimpleStringProperty(availability);
        }
    }
}
