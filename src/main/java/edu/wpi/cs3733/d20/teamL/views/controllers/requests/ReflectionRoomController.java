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
import java.time.LocalDate;
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
    Label confirmation;
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

        rooms.setItems(reflectionRooms);

        borderPane.prefWidthProperty().bind(stackPane.widthProperty());
        borderPane.prefHeightProperty().bind(stackPane.heightProperty());

        //load dates when room and date are changed
        date.valueProperty().addListener((observable, oldDate, newDate)->{
            if(date.getValue() != null && rooms.getValue() != null) {
                loadTimes();
            }
        });

        rooms.valueProperty().addListener((observable, oldRoom, newRoom) -> {
            if(date.getValue() != null && rooms.getValue() != null) {
                loadTimes();
            }
        });
    }

    @FXML
    private void loadTimes() {

            String r = (String) rooms.getValue();
            String d = date.getValue().toString();

            ArrayList<ArrayList<String>> requests = new ArrayList<>();
            requests = db.getTableFromResultSet(db.executeQuery(new SQLEntry(DBConstants.GET_ALL_ROOM_REQUESTS,  new ArrayList<>((Arrays.asList(r, d))))));

            JFXTreeTableColumn<TimeSlot, String> startTime = new JFXTreeTableColumn<>("Start Time");
            startTime.setCellValueFactory(param -> param.getValue().getValue().start);
            startTime.setPrefWidth(150);

            JFXTreeTableColumn<TimeSlot, String> endTime = new JFXTreeTableColumn<>("End Time");
            endTime.setCellValueFactory(param -> param.getValue().getValue().end);
            endTime.setPrefWidth(150);

            JFXTreeTableColumn<TimeSlot, String> availability = new JFXTreeTableColumn<>("Availability");
            availability.setCellValueFactory(param -> param.getValue().getValue().availability);
            availability.setPrefWidth(150);

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
    }

    @FXML
    private void handleSubmit() {

        //check for null values
        if(rooms.getValue() == null || date.getValue() == null || table.getSelectionModel().isEmpty()) {
            confirmation.setText("Select a Valid Room, Date, and Time");
            loaderHelper.showAndFade(confirmation);
        } else {

            //check if date is valid
            LocalDate d = date.getValue();
            if(d.isBefore(LocalDate.now())) {
                confirmation.setText("Select a Valid Date");
                loaderHelper.showAndFade(confirmation);
            } else {

                TreeItem<TimeSlot> row = table.getSelectionModel().getSelectedItem();
                TimeSlot t = row.getValue();
                String startTime = t.start.getValue();
                String endTime = t.end.getValue();
                String availability = t.availability.getValue();
                String room = (String) rooms.getValue();
                String dateChosen = date.getValue().toString();

                //check if room is available
                if(availability.equals("Reserved")){
                    confirmation.setText("Select an Open Time Slot");
                    loaderHelper.showAndFade(confirmation);
                } else {

                    int rows = db.executeUpdate((new SQLEntry(DBConstants.ADD_ROOM_REQUEST,
                            new ArrayList<>(Arrays.asList(manager.getCurrentUser().getUsername(), room, dateChosen, startTime, endTime)))));

                    if(rows == 0) {
                        confirmation.setText("Submission Failed");
                        loaderHelper.showAndFade(confirmation);
                    } else {

                        //clear selected values
                        rooms.setValue(null);
                        date.setValue(null);
                        table.getSelectionModel().clearSelection();

                        loaderHelper.showAndFade(requestReceived);

                        table.setVisible(false);
                        table.getColumns().clear();

                        requestReceived.toBack();
                    }
                }
            }
        }
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
