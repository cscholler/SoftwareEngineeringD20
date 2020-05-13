package edu.wpi.cs3733.d20.teamL.views.controllers.requests;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import edu.wpi.cs3733.d20.teamL.entities.Reservation;
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
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;

public class ConferenceRoomController {

    //iterate when previous/next weeks are clicked
    private int weeks = 0;

    @FXML
    private JFXTreeTableView<TimeSlot> table;
    @FXML
    private JFXComboBox rooms;
    @FXML
    private BorderPane borderPane;
    @FXML
    private StackPane stackPane;
    @FXML
    private ImageView requestReceived;
    @FXML
    private Label confirmation;
    private FXMLLoaderFactory loaderHelper = new FXMLLoaderFactory();
    @Inject
    private IDatabaseService db;
    @Inject
    private IDatabaseCache dbCache;
    @Inject
    private ILoginManager manager;

    private ObservableList<String> conferenceRooms = FXCollections.observableArrayList("Conference Room 1");
    private ArrayList<String> days = new ArrayList<>(Arrays.asList("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"));

    @FXML
    public void initialize() {

        rooms.setItems(conferenceRooms);
        rooms.getSelectionModel().selectFirst();
        loadTable();

        borderPane.prefWidthProperty().bind(stackPane.widthProperty());
        borderPane.prefHeightProperty().bind(stackPane.heightProperty());

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

        String room = (String) rooms.getValue();

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

        String su = sun.toString();
        su = su.substring(5);

        ArrayList<String> weekDates = new ArrayList<>(Arrays.asList("Monday " + mo, "Tuesday " + tu, "Wednesday " + we, "Thursday " + th, "Friday " + fr, "Saturday " + sa, "Sunday " + su));

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

        ArrayList<Reservation> requests = dbCache.getReservations();

        ObservableList<TimeSlot> slots = FXCollections.observableArrayList();
        String end = " AM";
        for (int i = 0; i < 24; i++) {
            String s = "";
            String e = "";

            if (i == 0) {
                s = "12:00" + end;
            } else if(i < 12) {
                s = i + ":00" + end;
            } else if (i == 12) {
                end = " PM";
                s = i + ":00" + end;
            } else {
                s = (i % 12) + ":00" + end;
            }

            if(i < 11) {
                e = i + 1 + ":00" + end;
            } else if (i == 11) {
                e = "12:00 PM";
            } else if (i < 23){
                e = ((i + 1) % 12) + ":00" + end;
            } else {
                e = "12:00 AM";
            }

            ArrayList<String> openValues = new ArrayList<>(Arrays.asList("", "", "", "", "", "", ""));

            for (int j = 0; j < 7; j++) {
                String d = weekDates.get(j);

                for (Reservation res : requests) {
                    if(res.getPlace().equals(room) && res.getDate().equals(d) && res.getStartTime().equals(s)) {
                        openValues.set(j, "Reserved");
                    }
                }

            }

            String timeString = s + "-" + e;

            slots.add(new TimeSlot(timeString, openValues.get(0), openValues.get(1), openValues.get(2), openValues.get(3), openValues.get(4), openValues.get(5), openValues.get(6)));
        }

        TreeTableView.TreeTableViewSelectionModel<TimeSlot> selection = table.getSelectionModel();
        selection.setCellSelectionEnabled(true);

        final TreeItem<TimeSlot> root = new RecursiveTreeItem<>(slots, RecursiveTreeObject::getChildren);
        table.getColumns().setAll(time, monday, tuesday, wednesday, thursday, friday, saturday, sunday);
        table.setRoot(root);
        table.setShowRoot(false);

    }

    @FXML
    private void handleSubmit() {

        //check for null values
        if(rooms.getValue() == null || table.getSelectionModel().isEmpty()) {
            confirmation.setText("Select a Valid Room and Time");
            loaderHelper.showAndFade(confirmation);
        } else {

            //check if date is valid
            final ObservableList<TreeTablePosition<TimeSlot, ?>> selectedCells = table.getSelectionModel().getSelectedCells();

            TreeTablePosition<TimeSlot, ?> pos = selectedCells.get(0);

            int col = pos.getColumn();

            TreeItem<TimeSlot> ti = pos.getTreeItem();
            TimeSlot t = ti.getValue();

            String room = (String) rooms.getValue();

            String date = table.getColumns().get(col).getText();
            date = date.replaceAll("\n", " ");

            LocalDate chosenDate = new LocalDate();

            chosenDate = chosenDate.withDayOfWeek(DateTimeConstants.MONDAY);
            chosenDate = chosenDate.plusWeeks(weeks);
            chosenDate = chosenDate.plusDays(col-1);

            if(chosenDate.isBefore(LocalDate.now())) {
                confirmation.setText("Select a Valid Date");
                loaderHelper.showAndFade(confirmation);
            } else {

                //check if room is available
                if (t.availabilities.get(col).equals("Reserved")) {
                    confirmation.setText("Select an Open Time Slot");
                    loaderHelper.showAndFade(confirmation);
                } else {

                    //the time slot
                    String time = t.availabilities.get(0);
                    String[] times = time.split("-");
                    String st = times[0];
                    String et = times[1];

                    int rows = db.executeUpdate((new SQLEntry(DBConstants.ADD_ROOM_REQUEST,
                            new ArrayList<>(Arrays.asList(manager.getCurrentUser().getUsername(), room, date, st, et)))));

                    if(rows == 0) {
                        confirmation.setText("Submission Failed");
                        loaderHelper.showAndFade(confirmation);
                    } else {
                        loaderHelper.showAndFade(requestReceived);
                        requestReceived.toBack();
                        dbCache.cacheReservationsFromDB();
                        loadTable();
                    }
                }
            }
        }

        table.getSelectionModel().clearSelection();

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
        ArrayList<String> availabilities;

        public TimeSlot(String time, String monday, String tuesday, String wednesday, String thursday, String friday, String saturday, String sunday) {
            this.time = new SimpleStringProperty(time);
            this.monday = new SimpleStringProperty(monday);
            this.tuesday = new SimpleStringProperty(tuesday);
            this.wednesday = new SimpleStringProperty(wednesday);
            this.thursday = new SimpleStringProperty(thursday);
            this.friday = new SimpleStringProperty(friday);
            this.saturday = new SimpleStringProperty(saturday);
            this.sunday = new SimpleStringProperty(sunday);

            availabilities = new ArrayList<>(Arrays.asList(time, monday, tuesday, wednesday, thursday, friday, saturday, sunday));
        }
    }
}
