package edu.wpi.cs3733.d20.teamL.views.controllers.requests;

import com.fasterxml.jackson.databind.module.SimpleAbstractTypeResolver;
import com.jfoenix.controls.*;
import edu.wpi.cs3733.d20.teamL.services.db.DBConstants;
import edu.wpi.cs3733.d20.teamL.services.db.IDatabaseCache;
import edu.wpi.cs3733.d20.teamL.services.db.IDatabaseService;
import edu.wpi.cs3733.d20.teamL.services.db.SQLEntry;
import edu.wpi.cs3733.d20.teamL.services.users.ILoginManager;
import edu.wpi.cs3733.d20.teamL.util.FXMLLoaderHelper;
import edu.wpi.cs3733.d20.teamL.util.search.SearchFields;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import javax.inject.Inject;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class SanitationPaneController {
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
    public void initialize(){
        sf = new SearchFields(dbCache.getNodeCache());
        sf.getFields().add(SearchFields.Field.longName);
        sf.getFields().add(SearchFields.Field.shortName);
        sf.populateSearchFields();
        autoCompletePopup.getSuggestions().addAll(sf.getSuggestions());

        serviceStackPane.setPickOnBounds(false);
    }

    /**
     * shows autocomplete options when searching for a location
     */
    @FXML
    private void autocomplete() {sf.applyAutocomplete(incidentLocationText, autoCompletePopup);}

    public void submitServiceRequest(ActionEvent actionEvent) throws IOException {
        String incidentLocation = incidentLocationText.getText();
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

        int rows = db.executeUpdate((new SQLEntry(DBConstants.ADD_SERVICE_REQUEST,
                new ArrayList<>(Arrays.asList(null, loginManager.getCurrentUser().getUsername(),
                        null, null, "Sanitation", priorityLevel, serviceTags + additionalNotes, status, dateAndTime)))));

        requestReceived.setVisible((true));
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(2000), requestReceived);
        fadeTransition.setDelay(Duration.millis(2000));
        fadeTransition.setFromValue(1.0);
        fadeTransition.setToValue(0.0);
        fadeTransition.setCycleCount(1);
        fadeTransition.play();

    }
}