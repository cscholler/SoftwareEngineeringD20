package edu.wpi.cs3733.d20.teamL.views.controllers.requests;

import com.jfoenix.controls.JFXAutoCompletePopup;
import com.jfoenix.controls.JFXComboBox;
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
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;

import javax.inject.Inject;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.ResourceBundle;

public class SanitationRequestController {
    private FXMLLoaderHelper loaderHelper = new FXMLLoaderHelper();
    private SearchFields sf;
    private JFXAutoCompletePopup<String> autoCompletePopup = new JFXAutoCompletePopup<>();
    @Inject
    private IDatabaseService db;
    @Inject
    private IDatabaseCache dbCache;
    @Inject
    private ILoginManager loginManager;
    @FXML
    private Label sanitationConfirmation;
    @FXML
    private JFXTextField subjectText, locationText;
    @FXML
    private JFXComboBox reqTypeText;
    @FXML
    private JFXTextArea addNotesText;

    @FXML
    public void initialize(){
        sf = new SearchFields(dbCache.getNodeCache());
        sf.getFields().add(SearchFields.Field.longName);
        sf.getFields().add(SearchFields.Field.shortName);
        sf.populateSearchFields();
        autoCompletePopup.getSuggestions().addAll(sf.getSuggestions());
    }

    /**
     * shows autocomplete options when searching for a location
     */
    @FXML
    private void autocomplete() {sf.applyAutocomplete(locationText, autoCompletePopup);}

    /**
     * handles buttons "back" and "submit" when clicked in sanitation service request
     * @throws IOException
     */
    @FXML
    public void handleBackButton() throws IOException {
        loaderHelper.goBack();
    }

    @FXML
    public void handleSubmitButton() throws IOException {
        String subject = subjectText.getText();
        String location = sf.getNode(locationText.getText()).getID();
        String requestType = reqTypeText.getPromptText();
        String additionalNotes = addNotesText.getText();

        String status = "0";
        String dateAndTime = new SimpleDateFormat("M-dd-yyyy | h:mm aa").format(new Date());
        String concatenatedNotes = location + "\n" + subject + "\n" + additionalNotes;
        // TODO: Get name of nurse from current user
        int rows = db.executeUpdate((new SQLEntry(DBConstants.ADD_SERVICE_REQUEST,
                new ArrayList<>(Arrays.asList(null, loginManager.getCurrentUser().getUsername(), null, null, "sanitation", requestType, concatenatedNotes, status, dateAndTime)))));

        if (rows == 0) sanitationConfirmation.setText("*Please fill out all above fields");
        if (rows == 1) {
            sanitationConfirmation.setText(("Sanitation Request Sent"));
            subjectText.setText("");
            locationText.setText("");
            reqTypeText.setPromptText("Select Request Type");
            addNotesText.setText("");
        }

        loaderHelper.showAndFade(sanitationConfirmation);

    }
}
