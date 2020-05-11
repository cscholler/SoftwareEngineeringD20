package edu.wpi.cs3733.d20.teamL.views.controllers.requests;

import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableView;
import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;

public class ConferenceRoomController {

    //iterate when previous/next weeks are clicked
    private int weeks = 0;

    @FXML
    private JFXTreeTableView<TimeSlot> table;

    @FXML
    public void initialize() {
        loadTable();
    }

    /**
     * moves the calendar back one week
     */
    @FXML
    private void lastWeekClicked() {
        weeks--;
        loadTable();
    }

    /**
     * moves the calendar forward one week
     */
    @FXML
    private void nextWeekClicked() {
        weeks++;
        loadTable();
    }

    /**
     * moves the calendar to the current week
     */
    @FXML
    private void todayClicked() {
        weeks = 0;
        loadTable();
    }

    /**
     * loads the data into the calendar for the correct week
     */
    @FXML
    private void loadTable() {

        LocalDate now = new LocalDate();

        LocalDate mon = now.withDayOfWeek(DateTimeConstants.MONDAY);
        //account for week on table
        mon = mon.plusWeeks(weeks);

        LocalDate tue = now.withDayOfWeek(DateTimeConstants.TUESDAY);
        //account for week on table
        tue = tue.plusWeeks(weeks);

        LocalDate wed = now.withDayOfWeek(DateTimeConstants.WEDNESDAY);
        //account for week on table
        wed = wed.plusWeeks(weeks);

        LocalDate thu = now.withDayOfWeek(DateTimeConstants.THURSDAY);
        //account for week on table
        thu = thu.plusWeeks(weeks);

        LocalDate fri = now.withDayOfWeek(DateTimeConstants.FRIDAY);
        //account for week on table
        fri = fri.plusWeeks(weeks);

        LocalDate sat = now.withDayOfWeek(DateTimeConstants.SATURDAY);
        //account for week on table
        sat = sat.plusWeeks(weeks);

        LocalDate sun = now.withDayOfWeek(DateTimeConstants.SUNDAY);
        //account for week on table
        sun = sun.plusWeeks(weeks);

        String su = sun.toString();
        su = su.substring(5);

        String mo = mon.toString();
        mo = mo.substring(5);

        String tu = tue.toString();
        tu = tu.substring(5);

        String we = wed.toString();
        we = we.substring(5);

        String th = thu.toString();
        th = th.substring(5);

        String fr = fri.toString();
        fr = fr.substring(5);

        String sa = sat.toString();
        sa = sa.substring(5);


        JFXTreeTableColumn<TimeSlot, String> time = new JFXTreeTableColumn<>("Time Slot");
        time.setCellValueFactory(param -> param.getValue().getValue().time);
        time.setPrefWidth(100);

        JFXTreeTableColumn<TimeSlot, String> monday = new JFXTreeTableColumn<>("Monday\n" + mo);
        monday.setCellValueFactory(param -> param.getValue().getValue().monday);
        monday.setPrefWidth(100);

        JFXTreeTableColumn<TimeSlot, String> tuesday = new JFXTreeTableColumn<>("Tuesday\n" + tu);
        tuesday.setCellValueFactory(param -> param.getValue().getValue().tuesday);
        tuesday.setPrefWidth(100);

        JFXTreeTableColumn<TimeSlot, String> wednesday = new JFXTreeTableColumn<>("Wednesday\n" + we);
        wednesday.setCellValueFactory(param -> param.getValue().getValue().wednesday);
        wednesday.setPrefWidth(100);

        JFXTreeTableColumn<TimeSlot, String> thursday = new JFXTreeTableColumn<>("Thursday\n" + th);
        thursday.setCellValueFactory(param -> param.getValue().getValue().thursday);
        thursday.setPrefWidth(100);

        JFXTreeTableColumn<TimeSlot, String> friday = new JFXTreeTableColumn<>("Friday\n" + fr);
        friday.setCellValueFactory(param -> param.getValue().getValue().friday);
        friday.setPrefWidth(100);

        JFXTreeTableColumn<TimeSlot, String> saturday = new JFXTreeTableColumn<>("Saturday\n" + sa);
        saturday.setCellValueFactory(param -> param.getValue().getValue().saturday);
        saturday.setPrefWidth(100);

        JFXTreeTableColumn<TimeSlot, String> sunday = new JFXTreeTableColumn<>("Sunday\n" + su);
        sunday.setCellValueFactory(param -> param.getValue().getValue().sunday);
        sunday.setPrefWidth(100);

        ObservableList<TimeSlot> slots = FXCollections.observableArrayList();
        String end = " AM";
        for (int i = 0; i < 24; i++) {
            String s = "";
            String e = "";

            if (i == 0) {
                s = "12:00" + end;
            } else if(i < 12) {
                s = String.format("%02d", i);
                s = s + ":00" + end;
            } else if (i == 12) {
                end = " PM";
                s = String.format("%02d", i);
                s = s + ":00" + end;
            } else {
                s = String.format("%02d", i % 12);
                s = s + ":00" + end;
            }

            if(i < 11) {
                e = String.format("%02d", i + 1);
                e = e + ":00" + end;
            } else if (i == 11) {
                e = "12:00 PM";
            } else if (i < 23){
                e = String.format("%02d", (i + 1) % 12);
                e = e + ":00" + end;
            } else {
                e = "12:00 AM";
            }






            //s = String.format("%02d", i);
            //s = s + ":00";
            //String e = String.format("%02d", ((i + 1) % 24));
            //e = e + ":00";

            String timeString = s + "-" + e;

            slots.add(new TimeSlot(timeString, "open", "open", "open", "open", "open", "open", "open"));
        }

        TreeTableView.TreeTableViewSelectionModel<TimeSlot> selection = table.getSelectionModel();
        selection.setSelectionMode(SelectionMode.MULTIPLE);

        final TreeItem<TimeSlot> root = new RecursiveTreeItem<>(slots, RecursiveTreeObject::getChildren);
        table.getColumns().setAll(time, monday, tuesday, wednesday, thursday, friday, saturday, sunday);
        table.setRoot(root);
        table.setShowRoot(false);
    }

    @FXML
    private void handleSubmit() {

    }

    class TimeSlot extends RecursiveTreeObject<TimeSlot> {
        StringProperty time;
        StringProperty monday;
        StringProperty tuesday;
        StringProperty wednesday;
        StringProperty thursday;
        StringProperty friday;
        StringProperty saturday;
        StringProperty sunday;

        public TimeSlot(String time, String monday, String tuesday, String wednesday, String thursday, String friday, String saturday, String sunday) {
            this.time = new SimpleStringProperty(time);
            this.monday = new SimpleStringProperty(monday);
            this.tuesday = new SimpleStringProperty(tuesday);
            this.wednesday = new SimpleStringProperty(wednesday);
            this.thursday = new SimpleStringProperty(thursday);
            this.friday = new SimpleStringProperty(friday);
            this.saturday = new SimpleStringProperty(saturday);
            this.sunday = new SimpleStringProperty(sunday);
        }
    }
}
