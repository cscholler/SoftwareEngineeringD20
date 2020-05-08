package edu.wpi.cs3733.d20.teamL.views.controllers.requests;

import com.google.api.client.auth.oauth2.Credential;
import com.jfoenix.controls.JFXButton;
import com.uber.sdk.core.auth.OAuth2Credentials;
import com.uber.sdk.core.auth.Scope;
import com.uber.sdk.core.client.CredentialsSession;
import com.uber.sdk.core.client.ServerTokenSession;
import com.uber.sdk.core.client.SessionConfiguration;
import com.uber.sdk.rides.client.UberRidesApi;
import com.uber.sdk.rides.client.model.Product;
import com.uber.sdk.rides.client.services.RidesService;
import edu.wpi.cs3733.d20.teamL.services.users.ILoginManager;
import edu.wpi.cs3733.d20.teamL.util.FXMLLoaderFactory;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import lombok.extern.slf4j.Slf4j;
import retrofit2.Response;

import javax.inject.Inject;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
public class UserLandingController {
    SessionConfiguration config = new SessionConfiguration.Builder()
            .setClientId("U2kFfAINgolQSICDl4d_-mVHFXX1ha13")
            .setClientSecret("<CLIENT_SECRET>")
            .setEnvironment(SessionConfiguration.Environment.SANDBOX)
            .setScopes(Arrays.asList(Scope.PROFILE, Scope.REQUEST))
            .setRedirectUri("<REDIRECT_URI>")
            .build();

    OAuth2Credentials credentials = new OAuth2Credentials.Builder()
            .setSessionConfiguration(config)
            .build();

    Credential credential = credentials.authenticate(authorizationCode, userId);
    CredentialsSession session = new CredentialsSession(config, credential)
    RidesService service = UberRidesApi.with(session).createService();

    String authorizationUrl = credentials.getAuthorizationUrl();


    ServerTokenSession session = new ServerTokenSession(config);
    public ImageView btnClose;
    public JFXButton btnAddPatient;
    FXMLLoaderFactory loaderHelper = new FXMLLoaderFactory();
    @FXML
    private Label timeLabel, requestLabel, userLabel;
    @FXML
    private Pane servicePane;
    @FXML
    private JFXButton btnGift, btnSecurity, btnSanitation, btnMaintenance, btnIT, btnInternal, btnExternal, btnInterpreter,
            btnMedication, btnReflectionRoom, btnOnCallBed, btnLaunchAPI;
    @Inject
    ILoginManager login;

    public UserLandingController() throws UnsupportedEncodingException {
    }

    @FXML
    public void initialize() throws IOException {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> timeLabel.setText(new SimpleDateFormat("E, MMM d | h:mm aa").format(new Date())));
            }
        }, 0, 1000);

        userLabel.setText("Hello, " + login.getCurrentUser().getFName());


        launchDefaultPane();

        if (login.getCurrentUser().getAcctType().equals("1")) {
            btnAddPatient.setVisible(true);
        }
    }

    @FXML
    private void launchAPIntment() throws Exception {
        Response<List<Product>> response = service.getProducts(37.79f, -122.39f).execute();
        List<Product> products = response.body();
        String productId = products.get(0).getProductId();
//        AppointmentRequest app = new AppointmentRequest();
//        app.run(0, 0, 1280,720,"/edu/wpi/cs3733/d20/teamL/css/GlobalStyleSheet.css", null, null);
    }


    @FXML
    public void launchDefaultPane() throws IOException {
        resetAndLoadPane("DefaultPane", " ");
        requestLabel.setVisible(false);
        btnClose.setVisible(false);
    }

    @FXML
    public void launchSecurityPane() throws IOException {
        resetAndLoadPane("SecurityPane", "Security Request");
        btnSecurity.setStyle("-fx-background-color: #DCDCDC");
    }

    @FXML
    public void launchSanitationPane() throws IOException {
        resetAndLoadPane("SanitationPane", "Sanitation Request");
        btnSanitation.setStyle("-fx-background-color: #DCDCDC");
    }

    @FXML
    public void launchGiftPane() throws IOException {
        resetAndLoadPane("GiftCartPane", "Gift Request");
        btnGift.setStyle("-fx-background-color: #DCDCDC");
    }

    @FXML
    public void launchMaintenancePane() throws IOException {
        resetAndLoadPane("MaintenancePane", "Maintenance Request");
        btnMaintenance.setStyle("-fx-background-color: #DCDCDC");
    }

    @FXML
    public void launchMedicationPane() throws IOException {
        resetAndLoadPane("MedicationPane", "Medication Request");
        btnMedication.setStyle("-fx-background-color: #DCDCDC");
    }

    @FXML
    public void launchInternalPane() throws IOException {
        resetAndLoadPane("InternalPane", "Internal Transport");
        btnInternal.setStyle("-fx-background-color: #DCDCDC");
    }

    @FXML
    public void launchExternalPane() throws IOException {
        resetAndLoadPane("ExternalPane", "External Transport");
        btnExternal.setStyle("-fx-background-color: #DCDCDC");
    }

    @FXML
    public void launchITPane() throws IOException {
        resetAndLoadPane("ITPane", "IT Request");
        btnIT.setStyle("-fx-background-color: #DCDCDC");
    }

    @FXML
    public void launchInterpreterPane() throws IOException {
        resetAndLoadPane("InterpreterPane", "Interpreter Request");
        btnInterpreter.setStyle("-fx-background-color: #DCDCDC");
    }

    @FXML
    private void launchReflectionRoomPane() throws IOException {
        resetAndLoadPane("ReflectionRoomPane", "Reserve Reflection Room");
        btnReflectionRoom.setStyle("-fx-background-color: #DCDCDC");
    }

    @FXML
    private void launchOnCallBedPane() throws IOException {
        resetAndLoadPane("OnCallBedPane", "Reserve On-Call Bed");
        btnOnCallBed.setStyle("-fx-background-color: #DCDCDC");
    }

    @FXML
    private void launchAddPatient() throws IOException {
        resetAndLoadPane("AddPatient", "Add Patient");
        btnAddPatient.setStyle("-fx-background-color: #DCDCDC");
    }

    private void resetAndLoadPane(String regionName, String labelText) throws IOException {
        JFXButton[] allButtons = new JFXButton[]{
                btnGift, btnSecurity, btnSanitation, btnMaintenance, btnIT, btnInternal, btnExternal, btnInterpreter, btnMedication, btnOnCallBed, btnReflectionRoom, btnAddPatient
        };
        for (JFXButton currButton : allButtons) {
            currButton.setStyle("-fx-background-color: white;");
        }

        try {
            servicePane.getChildren().clear();
            Region n = loaderHelper.getFXMLLoader("requests/" + regionName).load();

            servicePane.getChildren().add(n);
            n.prefWidthProperty().bind(servicePane.widthProperty());
            n.prefHeightProperty().bind(servicePane.heightProperty());
        } catch (IOException ex) {
            log.error("Encountered IOException", ex);
            launchDefaultPane();
        }

        requestLabel.setText(labelText);
        requestLabel.setVisible(true);

        btnClose.setVisible(true);
    }

    @FXML
    public void launchNotifPage() {
        try {
            Parent root = loaderHelper.getFXMLLoader("staff/NotificationsPage").load();
            loaderHelper.setupScene(new Scene(root));
        } catch (IOException ex) {
            log.error("Encountered IOException", ex);
        }
    }

    public void logoutBtn() {
        login.logOut(true);
        try {
            Parent root = loaderHelper.getFXMLLoader("map_viewer/MapViewer").load();
            loaderHelper.setupScene(new Scene(root));
        } catch (IOException ex) {
            log.error("Encountered IOException", ex);
        }
    }

}


