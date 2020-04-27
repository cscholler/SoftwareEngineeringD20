package edu.wpi.cs3733.d20.teamL.views.controllers.requests;

import com.jfoenix.controls.JFXAutoCompletePopup;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import edu.wpi.cs3733.d20.teamL.services.db.DBConstants;
import edu.wpi.cs3733.d20.teamL.services.db.IDatabaseCache;
import edu.wpi.cs3733.d20.teamL.services.db.IDatabaseService;
import edu.wpi.cs3733.d20.teamL.services.db.SQLEntry;
import edu.wpi.cs3733.d20.teamL.services.search.SearchFields;
import edu.wpi.cs3733.d20.teamL.services.users.ILoginManager;
import edu.wpi.cs3733.d20.teamL.util.FXMLLoaderHelper;
import edu.wpi.cs3733.d20.teamL.util.io.DBTableFormatter;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.ResourceBundle;

@Slf4j
public class ITServiceController implements Initializable {

    private FXMLLoaderHelper loaderHelper = new FXMLLoaderHelper();
    private SearchFields sf;
    private JFXAutoCompletePopup<String> autoCompletePopup;

    @Inject
    private IDatabaseService db;
    @Inject
    private IDatabaseCache dbCache;
    @Inject
    private ILoginManager loginManager;
    @FXML
    private Label confirmation;
    @FXML
    private JFXButton btnBack, btnSubmit;
    @FXML
    private JFXTextField locationText, typeText, notesText;

    @FXML
    public void initialize(URL location, ResourceBundle resources) {

        dbCache.cacheAllFromDB();
        sf = new SearchFields(dbCache.getNodeCache());
        sf.getFields().add(SearchFields.Field.nodeID);
        sf.populateSearchFields();
        autoCompletePopup = new JFXAutoCompletePopup<>();
        autoCompletePopup.getSuggestions().addAll(sf.getSuggestions());

    }

    /**
     * Does autocomplete text for the destination for the service to go to
     *
     */
    @FXML
    private void autoComplete() {
        sf.applyAutocomplete(locationText, autoCompletePopup);
    }

    /**
     * Goes back to the Staff page when back button is clicked
     *
     */
    @FXML
    private void backClicked() {

        loaderHelper.goBack();
    }

    /**
     * When clicked, the UI will either show that the request is made.
     * Or that the request had failed.
     *
     */
    @FXML
    private void submitClicked() {
        String userName = loginManager.getCurrentUser().getUsername();
        String location = locationText.getText();
        String type = typeText.getText();
        String notes = notesText.getText();

        String status = "0";
        String dateAndTime = new SimpleDateFormat("MM-dd-yyyy hh:mm:ss").format(new Date());

        int rows = db.executeUpdate(new SQLEntry(DBConstants.ADD_SERVICE_REQUEST,
                new ArrayList<>(Arrays.asList(null, userName, null, location, "IT Service", type, notes, status, dateAndTime))));

        if (rows == 0) {
            confirmation.setTextFill(Color.RED);
            confirmation.setText("Submission failed");
        } else {
            confirmation.setTextFill(Color.BLACK);
            confirmation.setText("IT Request Sent");

            locationText.setText("");
            typeText.setText("");
            notesText.setText("");
        }
        loaderHelper.showAndFade(confirmation);
    }
}
