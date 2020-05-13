package edu.wpi.cs3733.d20.teamL.views.controllers.requests;

import com.jfoenix.controls.*;
import edu.wpi.cs3733.d20.teamL.services.db.DBConstants;
import edu.wpi.cs3733.d20.teamL.services.db.IDatabaseCache;
import edu.wpi.cs3733.d20.teamL.services.db.IDatabaseService;
import edu.wpi.cs3733.d20.teamL.services.db.SQLEntry;
import edu.wpi.cs3733.d20.teamL.services.users.ILoginManager;
import edu.wpi.cs3733.d20.teamL.util.FXMLLoaderFactory;
import edu.wpi.cs3733.d20.teamL.util.SearchFields;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import javax.inject.Inject;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class SanitationPaneController {
    private FXMLLoaderFactory loaderHelper = new FXMLLoaderFactory();
    private SearchFields sf;
    private JFXAutoCompletePopup<String> autoCompletePopup = new JFXAutoCompletePopup<>();
    @Inject
    private IDatabaseService db;
    @Inject
    private IDatabaseCache dbCache;
    @Inject
    private ILoginManager loginManager;
    @FXML
    final ToggleGroup priorityGroup = new ToggleGroup();
    @FXML
    private JFXRadioButton highPriorityBox, nonEmergencyBox, lowPriorityBox;
    @FXML
    private JFXTextField incidentLocationText;
    @FXML
    private JFXCheckBox bioHazardCheckBox, spillCheckBox;
    @FXML
    private JFXTextArea additionalNotesText;
    @FXML
    private StackPane serviceStackPane;
    @FXML
    private ImageView requestReceived;
    @FXML
    private Label confirmation, tagTxt, priorityTxt;

    @FXML
    private VBox fieldsVBox;

    @FXML
    public void initialize(){
        fieldsVBox.setBackground(new Background(new BackgroundImage(new Image("/edu/wpi/cs3733/d20/teamL/assets/hexagons.png", 1000, 0, true, true, true), BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,BackgroundSize.DEFAULT)));

        sf = new SearchFields(dbCache.getNodeCache());
        sf.getFields().add(SearchFields.Field.nodeID);
        sf.populateSearchFields();
        autoCompletePopup.getSuggestions().addAll(sf.getSuggestions());

        serviceStackPane.setPickOnBounds(false);
    }

    /**
     * shows autocomplete options when searching for a location
     */
    @FXML
    private void autocomplete() {sf.applyAutocomplete(incidentLocationText, autoCompletePopup);}

    public void submitServiceRequest() throws IOException {
        String incidentLocation = incidentLocationText.getText() != null ? sf.getNode(incidentLocationText.getText()).getID() : null;
        String serviceTags = "";
        if (bioHazardCheckBox.isSelected()) serviceTags += "BioHazard, ";
        if (spillCheckBox.isSelected()) serviceTags += "Spill, ";
        String priorityLevel = "";
        if (highPriorityBox.isSelected()) priorityLevel = "High-Priority";
        if (nonEmergencyBox.isSelected()) priorityLevel = "Non-Emergency";
        if (lowPriorityBox.isSelected()) priorityLevel = "Low-Priority";
        String additionalNotes = additionalNotesText.getText();

        String status = "0";
        String dateAndTime = new SimpleDateFormat("MM-dd-yyyy hh:mm:ss").format(new Date());

        boolean validFields = true;

        if(serviceTags.equals("")) {
            tagTxt.setStyle("-fx-text-fill: RED");
            validFields = false;
        } else tagTxt.setStyle("-fx-text-fill: GRAY");
        if(priorityLevel.equals("")) {
            priorityTxt.setStyle("-fx-text-fill: RED");
            validFields = false;
        } else priorityTxt.setStyle("-fx-text-fill: GRAY");
        if(incidentLocation == null || incidentLocation.length() == 0) {
            incidentLocationText.setStyle("-fx-prompt-text-fill: RED");
            validFields = false;
        } else incidentLocationText.setStyle("-fx-prompt-text-fill: GRAY");

        int rows = 0;
        if(validFields) rows = db.executeUpdate((new SQLEntry(DBConstants.ADD_SERVICE_REQUEST,
                new ArrayList<>(Arrays.asList(null, loginManager.getCurrentUser().getUsername(),
                        null, null, "Sanitation", priorityLevel, serviceTags + additionalNotes, status, dateAndTime)))));

        if(rows == 0) {
            confirmation.setVisible(true);
            confirmation.setTextFill(Color.RED);
            confirmation.setText("Submission failed");
        } else {
            confirmation.setVisible(true);
            confirmation.setTextFill(Color.WHITE);
            confirmation.setText("");

            bioHazardCheckBox.setSelected(false);
            spillCheckBox.setSelected(false);

            highPriorityBox.setSelected(false);
            nonEmergencyBox.setSelected(false);
            lowPriorityBox.setSelected(false);

            incidentLocationText.setText("");
            additionalNotesText.setText("");

            loaderHelper.showAndFade(requestReceived);
        }

        loaderHelper.showAndFade(confirmation);

    }
}
