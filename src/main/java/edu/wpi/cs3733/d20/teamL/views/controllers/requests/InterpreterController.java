package edu.wpi.cs3733.d20.teamL.views.controllers.requests;

import com.google.inject.Inject;
import com.jfoenix.controls.JFXAutoCompletePopup;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import edu.wpi.cs3733.d20.teamL.services.db.DBConstants;
import edu.wpi.cs3733.d20.teamL.services.db.IDatabaseCache;
import edu.wpi.cs3733.d20.teamL.services.db.IDatabaseService;
import edu.wpi.cs3733.d20.teamL.services.db.SQLEntry;
import edu.wpi.cs3733.d20.teamL.services.users.ILoginManager;
import edu.wpi.cs3733.d20.teamL.util.FXMLLoaderHelper;
import edu.wpi.cs3733.d20.teamL.util.io.DBTableFormatter;
import edu.wpi.cs3733.d20.teamL.util.search.SearchFields;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.ResourceBundle;

public class InterpreterController implements Initializable {
    public JFXButton btnSpanish;
    public JFXButton btnFrench;
    public JFXButton btnItalian;
    public JFXButton btnASL;
    public JFXButton btnChinese;

    DBTableFormatter formatter = new DBTableFormatter();
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
    private Label confirmation, interpType;
    @FXML
    private JFXButton btnBack, btnSubmit;
    @FXML
    private JFXTextField pNameText, roomNumText, userIDText, assignedToText, additionalText;

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
     * shows autocomplete options when searching for a room
     */
    @FXML
    private void autocomplete() {
        sf.applyAutocomplete(roomNumText, autoCompletePopup);
    }

    /**
     * handles buttons "cancel" and "submit" when clicked in a interpreter service request
     * @param e tracks when button is pressed
     * @throws IOException
     */
    @FXML
    public void handleButtonAction(ActionEvent e) throws IOException {
        //goe back to Staff View
        if (e.getSource() == btnBack) {
            Parent root = loaderHelper.getFXMLLoader("StaffView").load();
            loaderHelper.setupScene(new Scene(root));

            //sumbits request
        } else if (e.getSource() == btnSubmit){
            String interpreterType = interpType.getText();
            String patientName = pNameText.getText();
            String roomNumber = roomNumText.getText();
            String userID = userIDText.getText();
            String assignedTo = assignedToText.getText();
            String additionalInfo = additionalText.getText();


            // Status codes-- 0: pending, 1: approved, 2: denied
            String status = "0";
            String dateAndTime = new SimpleDateFormat("MM-dd-yyyy hh:mm:ss").format(new Date());
            String user = loginManager.getCurrentUser().getUsername();
            // Adds request info to database

            String concatenatedNotes = interpreterType + "\n" + patientName + "\n" + roomNumber + "\n" + userID + "\n" + assignedTo + "\n" +additionalInfo;
            int rows = db.executeUpdate((new SQLEntry(DBConstants.ADD_SERVICE_REQUEST,
                    new ArrayList<>(Arrays.asList(interpreterType, patientName, roomNumber, user, assignedTo, additionalInfo, concatenatedNotes, dateAndTime, status)))));

            if (rows == 0) {
                confirmation.setTextFill(Color.RED);
                confirmation.setText("Submission failed");
            } else {
                confirmation.setTextFill(Color.WHITE);
                confirmation.setText("Request Sent");

                interpType.setText("");
                pNameText.setText("");
                roomNumText.setText("");
                userIDText.setText("");
                assignedToText.setText("");
                additionalText.setText("");
            }

            loaderHelper.showAndFade(confirmation);
        }

    }
    @FXML
    private void autoFillLanguage(ActionEvent e) {
        if(e.getSource() == btnASL){
            interpType.setText("Interpreter Type: ASL");
        }else if(e.getSource() == btnChinese){
            interpType.setText("Interpreter Type: Chinese");
        }else if(e.getSource() == btnFrench){
            interpType.setText("Interpreter Type: French");
        }else if(e.getSource() == btnItalian){
            interpType.setText("Interpreter Type: Italian");
        }else{
            interpType.setText("Interpreter Type: Spanish");
        }


    }

    @FXML
    private void backClicked(){
        loaderHelper.goBack();
    }
}
