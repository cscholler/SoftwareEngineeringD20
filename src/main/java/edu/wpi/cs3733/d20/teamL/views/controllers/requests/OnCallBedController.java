package edu.wpi.cs3733.d20.teamL.views.controllers.requests;

import com.jfoenix.controls.*;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
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

import java.io.IOException;

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
    JFXButton btnLoadTimes;
    @FXML
    Label tableErrorLbl;

    private FXMLLoaderFactory loaderHelper = new FXMLLoaderFactory();

    private ObservableList<String> bedsList = FXCollections.observableArrayList("Bed 1", "Bed 2", "Bed 3", "Bed 4", "Bed 5", "Bed 6", "Bed 7");

    @FXML
    public void initialize() throws IOException {

        table.setVisible(false);
        btnLoadTimes.setVisible(true);
        tableErrorLbl.setVisible(false);

        beds.setItems(bedsList);

        borderPane.prefWidthProperty().bind(stackPane.widthProperty());
        borderPane.prefHeightProperty().bind(stackPane.heightProperty());
    }

    @FXML
    private void loadTimes() {
        if (beds.getValue() == null || date.getValue() == null) {
            tableErrorLbl.setVisible(true);
        } else {
            JFXTreeTableColumn<TimeSlot, String> startTime = new JFXTreeTableColumn<>("Start Time");
            startTime.setCellValueFactory(param -> param.getValue().getValue().start);

            JFXTreeTableColumn<TimeSlot, String> endTime = new JFXTreeTableColumn<>("End Time");
            endTime.setCellValueFactory(param -> param.getValue().getValue().end);

            JFXTreeTableColumn<TimeSlot, String> availability = new JFXTreeTableColumn<>("Availability");
            availability.setCellValueFactory(param -> param.getValue().getValue().availability);

            //TODO fill with actual times from database when date and room selected
            //TODO maybe have the database just contain requests, and don't show those when room + date is entered
            ObservableList<TimeSlot> slots = FXCollections.observableArrayList();
            for (int i = 0; i < 24; i++) {
                String s = String.format("%02d", i);
                s = s + ":00";
                String e = String.format("%02d", ((i + 1) % 24));
                e = e + ":00";
                slots.add(new TimeSlot(s, e, "Open"));
            }

            TreeTableView.TreeTableViewSelectionModel<TimeSlot> selection = table.getSelectionModel();
            selection.setSelectionMode(SelectionMode.MULTIPLE);

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
        ObservableList<TreeItem<TimeSlot>> rows = table.getSelectionModel().getSelectedItems();
        //TimeSlot t = row.getValue();
        //String startTime = t.start.getValue();
        //String endTime = t.end.getValue();
        //String availability = t.availability.getValue();
        String bed = (String) beds.getValue();
        String dateChosen = date.getValue().toString();
        //TODO check values for null, past date, time

        System.out.println(rows.size() + " rows are selected.");

        for (TreeItem<TimeSlot> ti : rows) {
            TimeSlot t = ti.getValue();
            String startTime = t.start.getValue();
            String endTime = t.end.getValue();
            String availability = t.availability.getValue();
            System.out.println(bed + " " + dateChosen + " " + startTime + " to " + endTime);
        }



        //System.out.println(bed + " " + dateChosen + " " + startTime + " to " + endTime);

        //TODO enter request into database

        //clear selected values
        beds.setValue(null);
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
