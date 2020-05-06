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
import edu.wpi.cs3733.d20.teamL.util.FXMLLoaderFactory;
import edu.wpi.cs3733.d20.teamL.util.search.SearchFields;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

import javax.inject.Inject;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
    @FXML
    private ImageView requestReceived;
    @Inject
    private IDatabaseService db;
    @Inject
    private IDatabaseCache dbCache;
    private FXMLLoaderFactory loaderHelper = new FXMLLoaderFactory();
    private SearchFields sf;
    private JFXAutoCompletePopup<String> autoCompletePopup;
    @Inject
    private ILoginManager manager;
    @FXML
    StackPane stackPane;
    @FXML
    BorderPane borderPane;
    @FXML
    private Label confirmation, urgencyLbl;

    public void initialize() throws IOException {
        // restrict key input to numerals on personnel needed textfield
        personnelText.addEventFilter(KeyEvent.KEY_TYPED, keyEvent -> {
            if (!"0123456789".contains(keyEvent.getCharacter())) {
                keyEvent.consume();
            }
        });

        sf = new SearchFields(dbCache.getNodeCache());
        sf.getFields().add(SearchFields.Field.longName);
        sf.getFields().add(SearchFields.Field.shortName);
        sf.populateSearchFields();
        autoCompletePopup = new JFXAutoCompletePopup<>();
        autoCompletePopup.getSuggestions().addAll(sf.getSuggestions());

        borderPane.prefWidthProperty().bind(stackPane.widthProperty());
        borderPane.prefHeightProperty().bind(stackPane.heightProperty());
    }

    /**
     * shows autocomplete options when searching for a location
     */
    @FXML
    private void autocomplete() {
        sf.applyAutocomplete(locationText, autoCompletePopup); }

    /**
     * handles clicking of submit button
     * @throws IOException
     */
    @FXML
    private void handleSubmit() throws IOException {
        String id = patientIDText.getText();
        String location = locationText.getText() == null ? sf.getNode(locationText.getText()).getID() : null;
        String reason = reasonText.getText();
        String notes = notesText.getText();
        String personnel = personnelText.getText();
        String urgencyText = null;

        RadioButton rb = (RadioButton)urgency.getSelectedToggle();
        if(rb != null) {
            urgencyText = rb.getText();
        }

        String status = "0";
        String dateAndTime = new SimpleDateFormat("M/dd/yy | h:mm aa").format(new Date());
        String concatenatedNotes = "Urgency: " + urgencyText + "\nPersonnel Needed: " + personnel + "\nReason: " + reason + "\nAdditional Notes: " + notes;


            //TODO invalid input window
            boolean validFields = true;

            if(reason == null || reason.length() == 0) {
                reasonText.setStyle("-fx-text-fill: RED");
                validFields = false;
            } else reasonText.setStyle("-fx-text-fill: GRAY");
            if(db.getTableFromResultSet(db.executeQuery(new SQLEntry(DBConstants.GET_PATIENT_NAME, new ArrayList<>(Collections.singletonList(id))))).size() == 0) {
                patientIDText.setStyle("-fx-prompt-text-fill: RED");
                validFields = false;
            } else patientIDText.setStyle("-fx-text-fill: GRAY");
            if(location == null || location.length() == 0) {
                locationText.setStyle("-fx-prompt-text-fill: RED");
                validFields = false;
            } else locationText.setStyle("-fx-text-fill: GRAY");
            if(rb == null) {
                urgencyLbl.setStyle("-fx-text-fill: RED");
                validFields = false;
            } else urgencyLbl.setStyle("-fx-text-fill: GRAY");
            if(personnel == null || personnel.length() == 0) {
                personnelText.setStyle("-fx-prompt-text-fill: RED");
                validFields = false;
            } else personnelText.setStyle("-fx-text-fill: GRAY");

            if(validFields){

            int rows = db.executeUpdate((new SQLEntry(DBConstants.ADD_SERVICE_REQUEST,
                    new ArrayList<>(Arrays.asList(null, manager.getCurrentUser().getUsername(), null, location, "security", null, concatenatedNotes, status, dateAndTime)))));

            if(rows == 0) {
                //TODO database error window
                confirmation.setVisible(true);
                confirmation.setTextFill(Color.RED);
                confirmation.setText("Submission failed");
            } else {
                confirmation.setVisible(true);
                confirmation.setTextFill(Color.WHITE);
                confirmation.setText("");

                patientIDText.setText("");
                locationText.setText("");
                reasonText.setText("");
                notesText.setText("");
                personnelText.setText("");
                rb.setSelected(false);

                loaderHelper.showAndFade(requestReceived);
            }
            loaderHelper.showAndFade(confirmation);
        }
    }
}
