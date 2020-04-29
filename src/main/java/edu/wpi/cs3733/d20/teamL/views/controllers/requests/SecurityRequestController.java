package edu.wpi.cs3733.d20.teamL.views.controllers.requests;

import com.jfoenix.controls.JFXAutoCompletePopup;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import edu.wpi.cs3733.d20.teamL.services.db.DBConstants;
import edu.wpi.cs3733.d20.teamL.services.db.IDatabaseCache;
import edu.wpi.cs3733.d20.teamL.services.db.IDatabaseService;
import edu.wpi.cs3733.d20.teamL.services.db.SQLEntry;
import edu.wpi.cs3733.d20.teamL.services.users.ILoginManager;
import edu.wpi.cs3733.d20.teamL.util.FXMLLoaderHelper;
import edu.wpi.cs3733.d20.teamL.util.search.SearchFields;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;

import javax.inject.Inject;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.ResourceBundle;

public class SecurityRequestController {

    @FXML
    JFXTextField patientIDText, locationText;
    @FXML
    JFXTextArea reasonText, notesText;
    @FXML
    JFXButton btnBack, btnSubmit;
    @FXML
    Label lblSubmitted;
    @Inject
    private IDatabaseService db;
    @Inject
    private IDatabaseCache dbCache;
    private FXMLLoaderHelper loaderHelper = new FXMLLoaderHelper();
    private SearchFields sf;
    private JFXAutoCompletePopup<String> autoCompletePopup;
    @Inject
    private ILoginManager manager;

    @FXML
    public void initialize(){
        dbCache.cacheAllFromDB();

        sf = new SearchFields(dbCache.getNodeCache());
        sf.getFields().add(SearchFields.Field.nodeID);
        sf.populateSearchFields();
        autoCompletePopup = new JFXAutoCompletePopup<>();
        autoCompletePopup.getSuggestions().addAll(sf.getSuggestions());
    }

    /**
     * shows autocomplete options when searching for a location
     */
    @FXML
    private void autocomplete() {
        sf.applyAutocomplete(locationText, autoCompletePopup);
    }

    /**
     * handles clicking of back button
     * @throws IOException
     */
    @FXML
    private void handleBack() throws IOException {
        loaderHelper.goBack();
    }

    /**
     * handles clicking of submit button
     * @throws IOException
     */
    @FXML
    private void handleSubmit() throws IOException {
        String id = patientIDText.getText();
        String location = locationText.getText();
        String reason = reasonText.getText();
        String notes = notesText.getText();

        String status = "0";
        String dateAndTime = new SimpleDateFormat("MM-dd-yyyy hh:mm:ss").format(new Date());

        String concatenatedNotes = "Reason: " + reason + "\nAdditional Notes: " + notes;

        if(id.isEmpty() || location.isEmpty() || reason.isEmpty()) {
            lblSubmitted.setTextFill(Color.RED);
            lblSubmitted.setText("Patient ID, Location, and Reason required.");
        } else {

            int rows = db.executeUpdate((new SQLEntry(DBConstants.ADD_SERVICE_REQUEST,
                    new ArrayList<>(Arrays.asList(null, null, manager.getCurrentUser().getUsername(), location, "Security", null, concatenatedNotes, status, dateAndTime)))));

            if(rows == 0) {
                lblSubmitted.setText("Submission Failed!                        ");
                lblSubmitted.setTextFill(Color.RED);
            } else {
                lblSubmitted.setText("Submitted!                                ");
                lblSubmitted.setTextFill(Color.WHITE);
                patientIDText.setText("");
                locationText.setText("");
                reasonText.setText("");
                notesText.setText("");
            }
        }

        loaderHelper.showAndFade(lblSubmitted);
    }

}
