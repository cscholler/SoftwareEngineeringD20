package edu.wpi.cs3733.d20.teamL.views.controllers.logged_in;

import com.github.sarxos.webcam.Webcam;
import com.google.inject.Inject;
import com.jfoenix.controls.*;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import edu.wpi.cs3733.d20.teamL.services.IHTTPClientService;
import edu.wpi.cs3733.d20.teamL.services.db.DBConstants;
import edu.wpi.cs3733.d20.teamL.services.db.IDatabaseCache;
import edu.wpi.cs3733.d20.teamL.services.db.IDatabaseService;
import edu.wpi.cs3733.d20.teamL.services.db.SQLEntry;
import edu.wpi.cs3733.d20.teamL.services.users.PasswordManager;
import edu.wpi.cs3733.d20.teamL.util.FXMLLoaderFactory;
import edu.wpi.cs3733.d20.teamL.util.SearchFields;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.ResourceBundle;

@Slf4j
public class AddPersonController implements Initializable {
    //For doctor
    private SearchFields sf;
    private JFXAutoCompletePopup<String> autoCompletePopup;
    @Inject
    private IDatabaseService db;
    @Inject
    private IDatabaseCache cache;
    @FXML
    private Label lblConfirmation;
    @FXML
    private JFXTextArea addlInfoText;
    @FXML
    private JFXTextField doctorFN, doctorLN, officeText;
    @FXML
    private ImageView face;
    @FXML
    JFXTextField doctorIDText, doctorUn, sFName, sLName, sUn, mFName, mLName, mUn, nFName, nLName, nUn, aFName, aLName, aUn;
    @FXML
    JFXPasswordField doctorPw, sPw, mPw, nPw, aPw;
    @FXML
    private JFXComboBox<String> serviceCombo, languages;
    @FXML
    private JFXCheckBox securityBox, inTransportBox, exTransportBox, maintenanceBox, sanitationBox, pharmacistBox, giftShopBox, itBox, interpreterBox, managerBox;
    @FXML
    private VBox boxOService;
    @FXML
    private JFXButton btnCancel;
    private BufferedImage image;

    //for user
    ObservableList<String> serviceOptions = FXCollections.observableArrayList("Security", "Internal Transport", "External Transport", "Sanitation", "Maintenance", "Gift Shop", "Interpreter", "Information Technology");
    ObservableList<String> languageOptions = FXCollections.observableArrayList("Spanish", "Italian", "Chinese", "ASL", "French");


    private final FXMLLoaderFactory loaderHelper = new FXMLLoaderFactory();


    @Inject
    private IHTTPClientService client;
    boolean pictureTaken = false;


    @FXML
    private void setBtnCancel() {
        Stage stage;
        stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        serviceCombo.setItems(serviceOptions);
        languages.setItems(languageOptions);
        sf = new SearchFields(cache.getNodeCache());
        sf.getFields().add(SearchFields.Field.nodeID);
        sf.populateSearchFields();
        autoCompletePopup = new JFXAutoCompletePopup<>();
        autoCompletePopup.getSuggestions().addAll(sf.getSuggestions());
    }

    //Add doctor
    @FXML
    private void autocomplete() {
        sf.applyAutocomplete(officeText, autoCompletePopup);
    }

    /**
     * Handles UI portion of submit being clicked giving confirmation when it succeeds
     */

    //Nurse
    @FXML
    private void submitNurse() throws IOException {
        int rows = 0;

        String firstName = nFName.getText();
        String lastName = nLName.getText();
        String username = nUn.getText();
        String password = nPw.getText();
        String type = "1";
        String services = null;
        String manager = null;

        rows = db.executeUpdate(new SQLEntry(DBConstants.ADD_USER, new ArrayList<>(Arrays.asList(firstName, lastName, username, PasswordManager.hashPassword(password), type, services, manager))));
        confirm(rows);
        if(rows == 1 && pictureTaken){
            sendFace(username);
            delete();
        }
    }

    //Admin
    @FXML
    private void submitAdmin() throws IOException {
        int rows = 0;

        String firstName = aFName.getText();
        String lastName = aLName.getText();
        String username = aUn.getText();
        String password = aPw.getText();
        String type = "3";
        String services = null;
        String manager = null;

        rows = db.executeUpdate(new SQLEntry(DBConstants.ADD_USER, new ArrayList<>(Arrays.asList(firstName, lastName, username, PasswordManager.hashPassword(password), type, services, manager))));
        confirm(rows);
        if(rows == 1 && pictureTaken){
            sendFace(username);
            delete();
        }
    }

    //Manager
    @FXML
    private void submitManager() throws IOException {
        int rows = 0;

        String firstName = mFName.getText();
        String lastName = mLName.getText();
        String username = mUn.getText();
        String password = mPw.getText();
        String type = "0";
        String services = null;
        String manager = serviceCombo.getValue();

        rows = db.executeUpdate(new SQLEntry(DBConstants.ADD_USER, new ArrayList<>(Arrays.asList(firstName, lastName, username, PasswordManager.hashPassword(password), type, services, manager))));
        confirm(rows);
        if(rows == 1 && pictureTaken){
            sendFace(username);
            delete();
        }
    }

    //Doctor
    @FXML
    private void submitDoc() throws IOException {
        int rows = 0;
int rows1 = 0;

        String firstName = doctorFN.getText();
        String lastName = doctorLN.getText();
        String username = doctorUn.getText();
        String password = doctorPw.getText();
        String doctorID = doctorIDText.getText();
        String roomNum = officeText.getText() != null ? sf.getNode(officeText.getText()).getID() : null;
        String additionalInfo = addlInfoText.getText();
        String type = "2";
        String services = null;
        String manager = "pharmacy";

        rows = db.executeUpdate(new SQLEntry(DBConstants.ADD_USER, new ArrayList<>(Arrays.asList(firstName, lastName, username, PasswordManager.hashPassword(password), type, services, manager))));
        rows1 = db.executeUpdate(new SQLEntry(DBConstants.ADD_DOCTOR, new ArrayList<>(Arrays.asList(doctorID, firstName, lastName, username, roomNum, additionalInfo))));
        log.info(rows1+ "");
        confirm(rows);
        if(rows == 1 && pictureTaken){
            sendFace(username);
            delete();
        }
    }

    //Staff
    @FXML
    private void submitStaff() throws IOException {
        int rows = 0;

        String firstName = sFName.getText();
        String lastName = sLName.getText();
        String username = sUn.getText();
        String password = sPw.getText();
        String type = "0";
        String services = null;
        String manager = null;

        if (firstName != null) {
            StringBuilder servicesList = new StringBuilder();
            boolean anyServicesSelected = false;
            if (securityBox.isSelected()) {
                servicesList.append("security;");
                anyServicesSelected = true;
            }
            if (inTransportBox.isSelected()) {
                servicesList.append("internal_transportation;");
                anyServicesSelected = true;
            }
            if (exTransportBox.isSelected()) {
                servicesList.append("external_transportation;");
                anyServicesSelected = true;
            }
            if (maintenanceBox.isSelected()) {
                servicesList.append("maintenance;");
                anyServicesSelected = true;
            }
            if (sanitationBox.isSelected()) {
                servicesList.append("sanitation;");
                anyServicesSelected = true;
            }
            if (pharmacistBox.isSelected()) {
                servicesList.append("pharmacy;");
                anyServicesSelected = true;
            }
            if (giftShopBox.isSelected()) {
                servicesList.append("gift_shop;");
                anyServicesSelected = true;
            }
            if (itBox.isSelected()) {
                servicesList.append("information_technology;");
                anyServicesSelected = true;
            }
            if (interpreterBox.isSelected()) {
                servicesList.append("interpreter(").append(languages.getValue().replace(" ", "")).append(");");
                anyServicesSelected = true;
            }
            if (anyServicesSelected) {
                servicesList.setLength(servicesList.length() - 1);
            }
            services = servicesList.toString();
        }

        rows = db.executeUpdate(new SQLEntry(DBConstants.ADD_USER, new ArrayList<>(Arrays.asList(firstName, lastName, username, PasswordManager.hashPassword(password), type, services, manager))));
        confirm(rows);
        if(rows == 1 && pictureTaken){
            sendFace(username);
            delete();
        }
    }

    private void confirm(int rows) {
        if (rows == 0) {
            lblConfirmation.setTextFill(Color.RED);
            lblConfirmation.setText("Submission failed");
        } else if (rows == 1) {
            lblConfirmation.setTextFill(Color.BLACK);
            lblConfirmation.setText("User added");
            languages.setValue(null);
            serviceCombo.setVisible(false);
            serviceCombo.setDisable(false);
            boxOService.setVisible(false);
            boxOService.setDisable(false);
            languages.setVisible(false);
            languages.setDisable(false);
            doctorIDText.setVisible(false);
            doctorIDText.setDisable(false);
            securityBox.setSelected(false);
            inTransportBox.setSelected(false);
            exTransportBox.setSelected(false);
            maintenanceBox.setSelected(false);
            sanitationBox.setSelected(false);
            pharmacistBox.setSelected(false);
            giftShopBox.setSelected(false);
            itBox.setSelected(false);
            interpreterBox.setSelected(false);
        } else {
            log.error("SQL update affected more than 1 row.");
        }
        loaderHelper.showAndFade(lblConfirmation);
        cache.cacheUsersFromDB();
        cache.cacheDoctorsFromDB();
    }

    @FXML
    private void userSelected() {
        languages.setDisable(false);

    }


    @FXML
    private void capture() throws IOException {
        Webcam webcam = Webcam.getDefault();
        webcam.open();
        image = webcam.getImage();
        Image setimage = SwingFXUtils.toFXImage(image, null);
        face.setImage(setimage);
        webcam.close();
        pictureTaken = true;
    }

    private void sendFace(String username) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ImageIO.write(image, "PNG", bos);
        byte[] fileContent = bos.toByteArray();
        String encodedString = Base64.getEncoder().encodeToString(fileContent);

        JSONObject json = new JSONObject();
        json.put("image", encodedString);
        json.put("gallery_name", "users");
        json.put("subject_id", username);


        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, json.toString());
        log.info(json.toString());
        Request request = new Request.Builder()
                .url("https://kairosapi-karios-v1.p.rapidapi.com/enroll")
                .post(body)
                .addHeader("x-rapidapi-host", "kairosapi-karios-v1.p.rapidapi.com")
                .addHeader("x-rapidapi-key", "e9d19d8ab2mshee9ab7d6044378bp106222jsnc2e9a579d919")
                .addHeader("content-type", "application/json")
                .addHeader("accept", "application/json")
                .build();

        Response response = client.getClient().newCall(request).execute();
        log.info("" + response.code());
        log.info(response.body().string());
    }

    @FXML
    private void delete(){
        face.setImage(new Image("edu/wpi/cs3733/d20/teamL/assets/gift_delivery/noImage.png"));
        pictureTaken = false;
    }
}




