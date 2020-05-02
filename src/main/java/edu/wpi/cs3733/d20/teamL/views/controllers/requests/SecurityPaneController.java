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
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyEvent;

import javax.inject.Inject;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class SecurityPaneController {
    @FXML
    ToggleGroup urgency = new ToggleGroup();
    @FXML
    JFXTextField patientIDText, locationText, personnelText;
    @FXML
    JFXTextArea reasonText, notesText;
    @FXML
    JFXButton btnSubmit;
    @Inject
    private IDatabaseService db;
    @Inject
    private IDatabaseCache dbCache;
    private FXMLLoaderHelper loaderHelper = new FXMLLoaderHelper();
    private SearchFields sf;
    private JFXAutoCompletePopup<String> autoCompletePopup;
    @Inject
    private ILoginManager manager;

    public void initialize() throws IOException {
        // restrict key input to numerals on personnel needed textfield
        personnelText.addEventFilter(KeyEvent.KEY_TYPED, keyEvent -> {
            if (!"0123456789".contains(keyEvent.getCharacter())) {
                keyEvent.consume();
            }
        });

        dbCache.cacheAllFromDB();

        sf = new SearchFields(dbCache.getNodeCache());
        sf.getFields().add(SearchFields.Field.longName);
        sf.getFields().add(SearchFields.Field.shortName);
        sf.populateSearchFields();
        autoCompletePopup = new JFXAutoCompletePopup<>();
        autoCompletePopup.getSuggestions().addAll(sf.getSuggestions());
    }

    /**
     * shows autocomplete options when searching for a location
     */
    @FXML
    private void autocomplete() { sf.applyAutocomplete(locationText, autoCompletePopup); }

    /**
     * handles clicking of submit button
     * @throws IOException
     */
    @FXML
    private void handleSubmit() throws IOException {
        String id = patientIDText.getText();
        String location = sf.getNode(locationText.getText()).getID();
        String reason = reasonText.getText();
        String notes = notesText.getText();
        String personnel = personnelText.getText();

        RadioButton rb = (RadioButton)urgency.getSelectedToggle();
        String urgencyText = rb.getText();

        System.out.println(urgencyText);

        String status = "0";
        String dateAndTime = new SimpleDateFormat("M/dd/yy | h:mm aa").format(new Date());

        String concatenatedNotes = "Personnel Needed: " + personnel + "\nReason: " + reason + "\nAdditional Notes: " + notes;

        if(id.isEmpty() || location.isEmpty() || reason.isEmpty() || personnel.isEmpty()) {
            //TODO invalid input window
        } else {

            int rows = db.executeUpdate((new SQLEntry(DBConstants.ADD_SERVICE_REQUEST,
                    new ArrayList<>(Arrays.asList(null, manager.getCurrentUser().getUsername(), null, location, "security", null, concatenatedNotes, status, dateAndTime)))));

            if(rows == 0) {
                //TODO database error window
            } else {
                //TODO show successful window
                patientIDText.setText("");
                locationText.setText("");
                reasonText.setText("");
                notesText.setText("");
                personnelText.setText("");
            }
        }
    }
}
