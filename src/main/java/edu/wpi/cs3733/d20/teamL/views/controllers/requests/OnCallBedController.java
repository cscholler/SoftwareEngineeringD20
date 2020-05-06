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
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javax.inject.Inject;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;

public class OnCallBedController {

    @FXML
    private JFXTreeTableView<TimeSlot> table;
    @FXML
    private JFXComboBox beds;
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
    @Inject
    private IDatabaseService db;
    @Inject
    private IDatabaseCache dbCache;
    @Inject
    private ILoginManager manager;

    private FXMLLoaderFactory loaderHelper = new FXMLLoaderFactory();

    private ObservableList<String> bedsList = FXCollections.observableArrayList("Bed 1", "Bed 2", "Bed 3", "Bed 4", "Bed 5", "Bed 6", "Bed 7");

    @FXML
    public void initialize() throws IOException {

        table.setVisible(false);

        beds.setItems(bedsList);

        borderPane.prefWidthProperty().bind(stackPane.widthProperty());
        borderPane.prefHeightProperty().bind(stackPane.heightProperty());

        //load dates when bed and date are changed
        date.valueProperty().addListener((observable, oldDate, newDate)->{
            if(date.getValue() != null && beds.getValue() != null) {
                loadTimes();
            }
        });

        beds.valueProperty().addListener((observable, oldBed, newBed) -> {
            if(date.getValue() != null && beds.getValue() != null) {
                loadTimes();
            }
        });
    }

    //TODO change values when bed or date is changed, or make them uneditable

    @FXML
    private void loadTimes() {

            String b = (String) beds.getValue();
            String d = date.getValue().toString();

            ArrayList<ArrayList<String>> requests = new ArrayList<>();
            requests = db.getTableFromResultSet(db.executeQuery(new SQLEntry(DBConstants.GET_ALL_ROOM_REQUESTS,  new ArrayList<>((Arrays.asList(b, d))))));

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

            TreeTableView.TreeTableViewSelectionModel<TimeSlot> selection = table.getSelectionModel();
            selection.setSelectionMode(SelectionMode.MULTIPLE);

            final TreeItem<TimeSlot> root = new RecursiveTreeItem<>(slots, RecursiveTreeObject::getChildren);
            table.getColumns().setAll(startTime, endTime, availability);
            table.setRoot(root);
            table.setShowRoot(false);

            table.setVisible(true);
    }

    @FXML
    private void handleSubmit() {

        //check for null values
        if(beds.getValue() == null || date.getValue() == null || table.getSelectionModel().isEmpty()) {
            confirmation.setText("Select a Valid Bed, Date, and Time");
            loaderHelper.showAndFade(confirmation);
        } else {

            //check if date is valid
            LocalDate d = date.getValue();
            if(d.isBefore(LocalDate.now())) {
                confirmation.setText("Select a Valid Date");
                loaderHelper.showAndFade(confirmation);
            } else {

                ObservableList<TreeItem<TimeSlot>> rows = table.getSelectionModel().getSelectedItems();
                String bed = (String) beds.getValue();
                String dateChosen = date.getValue().toString();

                ArrayList<String> availabilities = new ArrayList<>();

                for (TreeItem<TimeSlot> ti : rows) {
                    TimeSlot t = ti.getValue();
                    String a = t.availability.getValue();
                    availabilities.add(a);
                }

                //check if time slot is reserved
                if (availabilities.contains("Reserved")) {
                    confirmation.setText("Select an Open Time");
                    loaderHelper.showAndFade(confirmation);
                } else {

                    boolean failed = false;

                    //add each hour to the database
                    for (TreeItem<TimeSlot> ti : rows) {
                        TimeSlot t = ti.getValue();
                        String startTime = t.start.getValue();
                        String endTime = t.end.getValue();
                        String availability = t.availability.getValue();

                        int r = db.executeUpdate((new SQLEntry(DBConstants.ADD_ROOM_REQUEST,
                                new ArrayList<>(Arrays.asList(manager.getCurrentUser().getUsername(), bed, dateChosen, startTime, endTime)))));

                        if(r == 0) {
                            confirmation.setText("Submission Failed");
                            loaderHelper.showAndFade(confirmation);
                            failed = true;
                            break;
                        }
                    }

                    if (!failed) {
                        //clear selected values
                        beds.setValue(null);
                        date.setValue(null);
                        table.getSelectionModel().clearSelection();

                        requestReceived.toFront();

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
