package edu.wpi.cs3733.d20.teamL.views.controllers.requests;

import com.google.inject.Inject;
import com.jfoenix.controls.*;
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
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import lombok.extern.slf4j.Slf4j;


import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.ResourceBundle;

@Slf4j
public class ITPaneController implements Initializable {

    @FXML
    private ImageView requestReceived;
    @FXML
    private BorderPane borderPane;
    @FXML
    private StackPane stackPane;

    ObservableList<String> options = FXCollections.observableArrayList("General Help", "Data Backup", "Hardware/Software Issues", "Cyber Attacks");

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
    private Label confirmation;
    @FXML
    private JFXButton btnSubmit;
    @FXML
    private JFXTextField locationText;
    @FXML
    private JFXTextArea notesText;
    @FXML
    private JFXComboBox<String> typeBox;

	private SearchFields searchFields;
    @FXML
    public void initialize(URL location, ResourceBundle resources) {
        dbCache.cacheAllFromDB();
        sf = new SearchFields(dbCache.getNodeCache());
        sf.getFields().add(SearchFields.Field.longName);
        sf.getFields().add(SearchFields.Field.shortName);
        sf.populateSearchFields();
		searchFields = new SearchFields(dbCache.getNodeCache());
		searchFields.getFields().add(SearchFields.Field.longName);
		searchFields.getFields().add(SearchFields.Field.shortName);
		searchFields.populateSearchFields();
		autoCompletePopup.getSuggestions().addAll(searchFields.getSuggestions());

        typeBox.setPromptText("Request Type:");
        typeBox.setItems(options);
        borderPane.prefWidthProperty().bind(stackPane.widthProperty());
        borderPane.prefHeightProperty().bind(stackPane.heightProperty());
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
     * When clicked, the UI will either show that the request is made.
     * Or that the request had failed.
     *
     */
    @FXML
    private void submitClicked() {
        String userName = loginManager.getCurrentUser().getUsername();
        String location = locationText.getText();
        String type = typeBox.getValue();
        String notes = notesText.getText();

        String status = "0";
        String dateAndTime = new SimpleDateFormat("M/dd/yy | h:mm aa").format(new Date());

        int rows = db.executeUpdate(new SQLEntry(DBConstants.ADD_SERVICE_REQUEST,
                new ArrayList<>(Arrays.asList(null, userName, null, searchFields.getNode(location).getID(), "information technology", type, notes, status, dateAndTime))));

        if (rows == 0) {
            confirmation.setTextFill(Color.RED);
            confirmation.setText("Submission failed");
        } else {
            locationText.setText("");
            typeBox.setValue(null);
            notesText.setText("");
            loaderHelper.showAndFade(requestReceived);
            confirmation.setText("");
        }
        loaderHelper.showAndFade(confirmation);
    }
}
