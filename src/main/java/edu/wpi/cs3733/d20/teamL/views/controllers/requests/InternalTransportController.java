package edu.wpi.cs3733.d20.teamL.views.controllers.requests;

import com.google.inject.Inject;
import com.jfoenix.controls.JFXAutoCompletePopup;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextField;
import edu.wpi.cs3733.d20.teamL.services.db.DBConstants;
import edu.wpi.cs3733.d20.teamL.services.db.IDatabaseCache;
import edu.wpi.cs3733.d20.teamL.services.db.IDatabaseService;
import edu.wpi.cs3733.d20.teamL.services.db.SQLEntry;
import edu.wpi.cs3733.d20.teamL.services.users.ILoginManager;
import edu.wpi.cs3733.d20.teamL.util.FXMLLoaderHelper;
import edu.wpi.cs3733.d20.teamL.util.search.SearchFields;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.ResourceBundle;

public class InternalTransportController implements Initializable {

    ObservableList<String> transportOptions = FXCollections.observableArrayList("Wheelchair w/ Operator", "Wheelchair w/o Operator", "Crutches", "Walker", "Gurney");
    ObservableList<String> timeOptions = FXCollections.observableArrayList("AM", "PM");
    private SearchFields sf;
    private JFXAutoCompletePopup<String> autoCompletePopup;
    private FXMLLoaderHelper loaderHelper = new FXMLLoaderHelper();
    @FXML
    JFXComboBox transportSelector, AMPM;
    @FXML
    JFXTextField patient, startLoc, endLoc, hour, minutes;
    @FXML
    Label confirmation;
    @FXML
    JFXDatePicker date;
    @FXML
    BorderPane borderPane;
    @FXML
    StackPane stackPane;
    @FXML
    ImageView requestReceived;
    @Inject
    private IDatabaseService db;
    @Inject
    private IDatabaseCache dbCache;
    @Inject
    private ILoginManager manager;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        sf = new SearchFields(dbCache.getNodeCache());
        sf.getFields().add(SearchFields.Field.nodeID);
        sf.populateSearchFields();
        autoCompletePopup = new JFXAutoCompletePopup<>();
        autoCompletePopup.getSuggestions().addAll(sf.getSuggestions());
        //transportSelector.setPromptText("Select Equipment");
        transportSelector.setItems(transportOptions);
        AMPM.setItems(timeOptions);


        hour.addEventFilter(KeyEvent.KEY_TYPED, keyEvent -> {
            if (!"0123456789".contains(keyEvent.getCharacter())) {
                keyEvent.consume();
            }
        });

        minutes.addEventFilter(KeyEvent.KEY_TYPED, keyEvent -> {
            if (!"0123456789".contains(keyEvent.getCharacter())) {
                keyEvent.consume();
            }
        });

        borderPane.prefWidthProperty().bind(stackPane.widthProperty());
        borderPane.prefHeightProperty().bind(stackPane.heightProperty());
    }

    @FXML
    private void autocompleteS() {
        sf.applyAutocomplete(startLoc, autoCompletePopup);
    }

    @FXML
    private void autocompleteE() {
        sf.applyAutocomplete(endLoc, autoCompletePopup);
    }

   @FXML
    private boolean timeIsValid() {
        if (Integer.parseInt(hour.getText()) < 13 || (Integer.parseInt(minutes.getText()) < 60)) {
            return true;
        }
        return false;
    }


    @FXML
    private void submitClicked() {
        String start = startLoc.getText();
        String end = endLoc.getText();
        String type = (String) transportSelector.getValue();
        String dateNeeded = date.getId();
        String hourNeeded = hour.getText();
        String minNeeded = minutes.getText();
        String patientID = patient.getText();


        String status = "0";
        String dateAndTime = new SimpleDateFormat("M/dd/yy | h:mm:aa").format(new Date());
        String concatenatedNotes = end + dateNeeded + "\n" + hourNeeded + " : " + minNeeded;
        int rows = 0;
        if (!(start.isEmpty() || end.isEmpty() || type.isEmpty()) && timeIsValid()) {
            rows = db.executeUpdate(new SQLEntry(DBConstants.ADD_SERVICE_REQUEST, new ArrayList<>(Arrays.asList(patientID, manager.getCurrentUser().getUsername(), null, start, "internal transportation", type, concatenatedNotes, status, dateAndTime))));
        }

        if (rows == 0) {
            confirmation.setTextFill(Color.RED);
            confirmation.setText("Request failed");
        } else {
            confirmation.setTextFill(Color.WHITE);
            confirmation.setText("");

            startLoc.setText("");
            endLoc.setText("");
            transportSelector.setPromptText("Choose Type of Equipment");
            date.setId("");
            hour.setText("");
            minutes.setText("");
            patient.setText("");
            loaderHelper.showAndFade(requestReceived);
        }

        loaderHelper.showAndFade(confirmation);
    }

    @FXML
    private void closeClicked() {
        loaderHelper.goBack();
    }


}

