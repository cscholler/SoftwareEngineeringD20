package edu.wpi.cs3733.d20.teamL.views.controllers.requests;

import com.google.inject.Inject;
import com.jfoenix.controls.JFXAutoCompletePopup;
import com.jfoenix.controls.JFXComboBox;
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
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.ResourceBundle;


public class ExternalTransportController implements Initializable {


    ObservableList<String> transportOptions = FXCollections.observableArrayList("Taxi", "Bus", "Uber", "Lift");

    private SearchFields sf;
    private JFXAutoCompletePopup<String> autoCompletePopup;
    private FXMLLoaderHelper loaderHelper = new FXMLLoaderHelper();
    @FXML
    JFXComboBox transportSelector;
    @FXML
    JFXTextField startLoc, endLoc;
    @FXML
    Label confirmation;
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
        sf.populateWithExits();
        autoCompletePopup = new JFXAutoCompletePopup<>();
        autoCompletePopup.getSuggestions().addAll(sf.getSuggestions());

        transportSelector.setItems(transportOptions);
    }

    @FXML
    private void autocompleteS() {
        sf.applyAutocomplete(startLoc, autoCompletePopup);
    }

    @FXML
    private void submitClicked() {
        String start = startLoc.getText();
        String end = endLoc.getText();
        String type = (String) transportSelector.getValue();

        String status = "0";
        String dateAndTime = new SimpleDateFormat("M/dd/yy | h:mm aa").format(new Date());
        int rows = 0;
        if (!(start.isEmpty() || end.isEmpty() || type.isEmpty())) {
            rows = db.executeUpdate(new SQLEntry(DBConstants.ADD_SERVICE_REQUEST, new ArrayList<>(Arrays.asList(null, manager.getCurrentUser().getUsername(), null, start, "external transportation", type, end, status, dateAndTime))));
        }

        if (rows == 0) {
            confirmation.setTextFill(Color.RED);
            confirmation.setText("Request failed");
        } else {
            confirmation.setTextFill(Color.WHITE);
            confirmation.setText("Transport Request Sent");

            startLoc.setText("");
            endLoc.setText("");
            transportSelector.setValue("Choose Type pf Transport");
        }

        loaderHelper.showAndFade(confirmation);
    }

    @FXML
    private void closeClicked() {
        loaderHelper.goBack();
    }
}




