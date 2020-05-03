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
import edu.wpi.cs3733.d20.teamL.util.FXMLLoaderFactory;
import edu.wpi.cs3733.d20.teamL.util.search.SearchFields;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.util.Callback;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.ResourceBundle;

public class InternalTransportController implements Initializable {

    ObservableList<String> transportOptions = FXCollections.observableArrayList("Wheelchair w/ Operator", "Wheelchair w/o Operator", "Crutches", "Walker", "Gurney");

    private SearchFields sf;
    private JFXAutoCompletePopup<String> autoCompletePopup;
    private FXMLLoaderFactory loaderHelper = new FXMLLoaderFactory();
    @FXML
    JFXComboBox transportSelector;
    @FXML
    JFXTextField startLoc, endLoc, hour, minutes;
    @FXML
    Label confirmation;
    @FXML
    JFXDatePicker date;
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

        transportSelector.setValue("Type of Transport");
        transportSelector.setItems(transportOptions);

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

    }

    @FXML
    private void autocompleteS() {
        sf.applyAutocomplete(startLoc, autoCompletePopup);
    }

    @FXML
    private void autocompleteE() {
        sf.applyAutocomplete(endLoc, autoCompletePopup);
    }

   /* @FXML
    private boolean timeIsValid() {
        if (Integer.parseInt(hour.getText()) < 13 || (Integer.parseInt(minutes.getText()) < 60)) {
            return true;
        }
        return false;
    }*/


    @FXML
    private void submitClicked() {
        String start = startLoc.getText();
        String end = endLoc.getText();
        String type = (String) transportSelector.getValue();
        Callback<DatePicker, DateCell> dateNeeded = date.getDayCellFactory();
        String hourNeeded = hour.getText();
        String minNeeded = minutes.getText();


        String status = "0";
        String dateAndTime = new SimpleDateFormat("M/dd/yy | h:mm:aa").format(new Date());
        String concatenatedNotes = dateNeeded + "\n" + hourNeeded + " : " + minNeeded;
        int rows = 0;
        if (!(start.isEmpty() || end.isEmpty() || type.isEmpty())) {
            rows = db.executeUpdate(new SQLEntry(DBConstants.ADD_SERVICE_REQUEST, new ArrayList<>(Arrays.asList(null, manager.getCurrentUser().getUsername(), null, start, "internal transportation", type, end, concatenatedNotes, status, dateAndTime))));
        }

        if (rows == 0) {
            confirmation.setTextFill(Color.RED);
            confirmation.setText("Request failed");
        } else {
            confirmation.setTextFill(Color.WHITE);
            confirmation.setText("Transport Request Sent");

            startLoc.setText("");
            endLoc.setText("");
            transportSelector.setValue("Choose Type of Equipment");
        }

        loaderHelper.showAndFade(confirmation);
    }

    @FXML
    private void closeClicked() {
        loaderHelper.goBack();
    }


}

