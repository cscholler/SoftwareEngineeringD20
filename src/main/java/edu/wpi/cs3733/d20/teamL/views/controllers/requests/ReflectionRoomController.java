package edu.wpi.cs3733.d20.teamL.views.controllers.requests;

import com.jfoenix.controls.*;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import edu.wpi.cs3733.d20.teamL.services.db.DBConstants;
import edu.wpi.cs3733.d20.teamL.services.db.IDatabaseCache;
import edu.wpi.cs3733.d20.teamL.services.db.IDatabaseService;
import edu.wpi.cs3733.d20.teamL.services.db.SQLEntry;
import edu.wpi.cs3733.d20.teamL.services.users.ILoginManager;
import edu.wpi.cs3733.d20.teamL.util.FXMLLoaderFactory;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javax.inject.Inject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

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
    @FXML
    JFXButton btnLoadTimes;
    @FXML
    Label tableErrorLbl;
    private FXMLLoaderFactory loaderHelper = new FXMLLoaderFactory();
    @Inject
    private IDatabaseService db;
    @Inject
    private IDatabaseCache dbCache;
    @Inject
    private ILoginManager manager;

    private ObservableList<String> reflectionRooms = FXCollections.observableArrayList("Floor 1 Reflection Room", "Floor 3 Reflection Room", "Floor 4 Reflection Room");

    @FXML
    public void initialize() throws IOException {

        table.setVisible(false);
        btnLoadTimes.setVisible(true);
        tableErrorLbl.setVisible(false);

        rooms.setItems(reflectionRooms);

        borderPane.prefWidthProperty().bind(stackPane.widthProperty());
        borderPane.prefHeightProperty().bind(stackPane.heightProperty());
    }

    //TODO change values when room or date is changed, or make them uneditable

    @FXML
    private void loadTimes() {

        if (rooms.getValue() == null || date.getValue() == null) {
            loaderHelper.showAndFade(tableErrorLbl);
        } else {

            String r = (String) rooms.getValue();
            String d = date.getValue().toString();

            ArrayList<ArrayList<String>> requests = new ArrayList<>();
            requests = db.getTableFromResultSet(db.executeQuery(new SQLEntry(DBConstants.GET_ALL_ROOM_REQUESTS,  new ArrayList<>((Arrays.asList(r, d))))));

            JFXTreeTableColumn<TimeSlot, String> startTime = new JFXTreeTableColumn<>("Start Time");
            startTime.setCellValueFactory(param -> param.getValue().getValue().start);

            JFXTreeTableColumn<TimeSlot, String> endTime = new JFXTreeTableColumn<>("End Time");
            endTime.setCellValueFactory(param -> param.getValue().getValue().end);

            JFXTreeTableColumn<TimeSlot, String> availability = new JFXTreeTableColumn<>("Availability");
            availability.setCellValueFactory(param -> param.getValue().getValue().availability);

            ObservableList<TimeSlot> slots = FXCollections.observableArrayList();
            for (int i = 0; i < 24; i++) {
                String s = String.format("%02d", i);
                s = s + ":00";
                String e = String.format("%02d", ((i + 1) % 24));
                e = e + ":00";

                String availableText = "Open";

                for (ArrayList<String> entry : requests) {
                    if(entry.get(4).equals(s)){
                        availableText = "Reserved";
                    }
                }

                slots.add(new TimeSlot(s, e, availableText));
            }

            final TreeItem<TimeSlot> root = new RecursiveTreeItem<TimeSlot>(slots, RecursiveTreeObject::getChildren);
            table.getColumns().setAll(startTime, endTime, availability);
            table.setRoot(root);
            table.setShowRoot(false);

            table.setVisible(true);
            btnLoadTimes.setVisible(false);
            tableErrorLbl.setVisible(false);
        }
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

        int rows = db.executeUpdate((new SQLEntry(DBConstants.ADD_ROOM_REQUEST,
                new ArrayList<>(Arrays.asList(manager.getCurrentUser().getUsername(), room, dateChosen, startTime, endTime)))));

        //clear selected values
        rooms.setValue(null);
        date.setValue(null);
        table.getSelectionModel().clearSelection();

        loaderHelper.showAndFade(requestReceived);

        table.setVisible(false);
        btnLoadTimes.setVisible(true);
        tableErrorLbl.setVisible(false);
        table.getColumns().clear();

        requestReceived.toBack();
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
