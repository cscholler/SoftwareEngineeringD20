package edu.wpi.cs3733.d20.teamL.views.controllers.requests;

import com.google.inject.Inject;
import com.jfoenix.controls.JFXAutoCompletePopup;
import com.jfoenix.controls.JFXTextField;
import edu.wpi.cs3733.d20.teamL.entities.Gift;
import edu.wpi.cs3733.d20.teamL.services.db.DBConstants;
import edu.wpi.cs3733.d20.teamL.services.db.IDatabaseCache;
import edu.wpi.cs3733.d20.teamL.services.db.IDatabaseService;
import edu.wpi.cs3733.d20.teamL.services.db.SQLEntry;
import edu.wpi.cs3733.d20.teamL.services.users.ILoginManager;
import edu.wpi.cs3733.d20.teamL.util.FXMLLoaderFactory;
import edu.wpi.cs3733.d20.teamL.util.io.DBTableFormatter;
import edu.wpi.cs3733.d20.teamL.util.search.SearchFields;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
public class GiftCheckoutController implements Initializable {
    DBTableFormatter formatter = new DBTableFormatter();
    private FXMLLoaderFactory loaderHelper = new FXMLLoaderFactory();
    private SearchFields sf;
    private JFXAutoCompletePopup<String> autoCompletePopup;
    @Inject
    private IDatabaseService db;
    @Inject
    private IDatabaseCache cache;
    @Inject
    private ILoginManager loginManager;
    @FXML
    private Label lblConfirmation, cartLbl;
    @FXML
    private JFXTextField messageText, cartText, patFNameText, patLNameText, senderText, addInfoText;

    ArrayList<Gift> cart;

    @FXML
    public void initialize(URL location, ResourceBundle resources) {

        sf = new SearchFields(cache.getNodeCache());
        sf.getFields().add(SearchFields.Field.longName);
        sf.getFields().add(SearchFields.Field.shortName);
        sf.populateSearchFields();
        autoCompletePopup = new JFXAutoCompletePopup<>();
        autoCompletePopup.getSuggestions().addAll(sf.getSuggestions());
        formatter.reportQueryResults(db.executeQuery(new SQLEntry(DBConstants.SELECT_ALL_DOCTORS)));
        formatter.reportQueryResults(db.executeQuery(new SQLEntry(DBConstants.SELECT_ALL_PATIENTS)));

        StringBuilder sb = new StringBuilder(/*"Gift Service Request Cart:\r\n"*/);
        cart = cache.getCartCache();
        for(Gift gift : cart) {
            sb.append("1x " + gift.getSubtype() + ", of type " + gift.getType() + ".\r\n");
        }
        cartLbl.setText(sb.toString());
    }

    /**
     * Applies autocomplete to the room number field
     */
    @FXML
    private void autocomplete() {
        sf.applyAutocomplete(senderText, autoCompletePopup);
    }

    @FXML
    private void btnCancelClicked() throws IOException {
        loaderHelper.goBack();
    }

    @FXML
    private void btnSubmitClicked() throws IOException {
        String message = messageText.getText();
        String patientFName = patFNameText.getText();
        String patientLName = patLNameText.getText();
        String sender = senderText.getText();
        String additionalInfo = addInfoText.getText();

        // Status codes-- 0: pending, 1: approved, 2: delivered, 3: denied,
        String status = "0";
        String dateAndTime = new SimpleDateFormat("M/dd/yy | h:mm aa").format(new Date());

        cart = cache.getCartCacheNull();

        // Adds request info to database
        String patientID = db.getTableFromResultSet(db.executeQuery(new SQLEntry(DBConstants.GET_PATIENT_ID, new ArrayList<>(Arrays.asList(patientFName, patientLName))))).get(0).get(0);
        String patientRoomNum = db.getTableFromResultSet(db.executeQuery(new SQLEntry(DBConstants.GET_PATIENT_ROOM, new ArrayList<>(Collections.singletonList(patientID))))).get(0).get(0);
        //TODO: add more verification checks
        int rows = 0;

        rows = db.executeUpdate(new SQLEntry(DBConstants.ADD_GIFT_DELIVERY_REQUEST, new ArrayList<>(Arrays.asList(patientID, sender, loginManager.getCurrentUser().getUsername(), null,
                cart.get(0).getID(), cart.get(1) != null ? cart.get(1).getID() : null, cart.get(2) != null ? cart.get(2).getID() : null, message, additionalInfo, status, dateAndTime))));

        formatter.reportQueryResults(db.executeQuery(new SQLEntry(DBConstants.SELECT_ALL_GIFT_DELIVERY_REQUESTS)));
        if (rows == 0) {
            lblConfirmation.setTextFill(Color.RED);
            lblConfirmation.setText("Submission failed");
        } else if (rows == 1) {
            lblConfirmation.setTextFill(Color.BLACK);
            lblConfirmation.setText("Gift Delivery Service Request Sent");
            messageText.setText("");
            patFNameText.setText("");
            patLNameText.setText("");
            senderText.setText("");
            addInfoText.setText("");
            cache.clearCartCache();
            loaderHelper.goBack();
            loaderHelper.goBack();
        } else {
            log.error("SQL update affected more than 1 row.");
        }
        loaderHelper.showAndFade(lblConfirmation);
    }
}
