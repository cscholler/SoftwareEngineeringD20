package edu.wpi.cs3733.d20.teamL.views.controllers.requests;

import com.google.inject.Inject;
import com.jfoenix.controls.JFXAutoCompletePopup;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import edu.wpi.cs3733.d20.teamL.services.db.DBConstants;
import edu.wpi.cs3733.d20.teamL.services.db.IDatabaseCache;
import edu.wpi.cs3733.d20.teamL.services.db.IDatabaseService;
import edu.wpi.cs3733.d20.teamL.services.db.SQLEntry;
import edu.wpi.cs3733.d20.teamL.util.FXMLLoaderHelper;
import edu.wpi.cs3733.d20.teamL.util.search.SearchFields;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.ResourceBundle;

public class MaintenanceRequestController implements Initializable {
    @FXML
    private JFXComboBox urgency, type;
    @FXML
    private JFXTextField location, description;
    @FXML
    private Label error;

    private JFXAutoCompletePopup<String> autoCompletePopup = new JFXAutoCompletePopup<>();

    private FXMLLoaderHelper loaderHelper = new FXMLLoaderHelper();
    private SearchFields searchFields;

    @Inject
    private IDatabaseService dbService;
    @Inject
    private IDatabaseCache dbCache;

    private String loggedInUsername;

    @FXML
    public void initialize(URL location, ResourceBundle resources) {
        loggedInUsername = "maintenance_requester"; // TODO implement a system to check who is logged in

        // Setup autocomplete
        searchFields = new SearchFields(dbCache.getNodeCache());
        searchFields.getFields().add(SearchFields.Field.nodeID);
        searchFields.populateSearchFields();
        autoCompletePopup.getSuggestions().addAll(searchFields.getSuggestions());

        urgency.getItems().addAll(
                "Minor",
                "Within the day",
                "Urgent"
        );

        type.getItems().addAll(
                "Plumbing",
                "Medical Equipment",
                "Electrical",
                "IT",
                "Other"
        );
    }

    @FXML
    private void submit() {
        // TODO Make a user to make maintenance requests to
        if (fieldsFilled()) {
            String notes = urgency.getSelectionModel().getSelectedItem().toString() + "|" + description.getText();
            String dateAndTime = new SimpleDateFormat("MM-dd-yyyy hh:mm:ss").format(new Date());

            ArrayList<String> params = new ArrayList<>(Arrays.asList(null, loggedInUsername, "maintenance_user", location.getText(),
                    "maintenance", type.getSelectionModel().getSelectedItem().toString(), notes, "pending", dateAndTime));
            int rows = dbService.executeUpdate(new SQLEntry(DBConstants.ADD_SERVICE_REQUEST, params));

            if (rows == 0) {
                error.setText("Submission failed");
                loaderHelper.showAndFade(error);
            }
        } else {
            error.setText("Please fill in all fields");
            loaderHelper.showAndFade(error);
        }
    }

    @FXML
    private void back() {
        loaderHelper.goBack();
    }

    @FXML
    private void autocomplete() {
        searchFields.applyAutocomplete(location, autoCompletePopup);
    }

    /**
     * Checks if all the fields of the form are filled.
     *
     * @return {true} If and only if all fields are filled {false} Otherwise
     */
    private boolean fieldsFilled() {
        return location.getText().length() > 0 && description.getText().length() > 0 && urgency.getSelectionModel().getSelectedIndex() > 0 && type.getSelectionModel().getSelectedIndex() > 0;
    }
}
