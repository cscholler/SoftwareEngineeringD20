package edu.wpi.cs3733.d20.teamL.views.controllers.requests;

import com.jfoenix.controls.JFXButton;
import edu.wpi.cs3733.d20.teamL.services.users.ILoginManager;
import edu.wpi.cs3733.d20.teamL.util.FXMLLoaderHelper;
import javafx.animation.FadeTransition;
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

import javax.inject.Inject;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

@Slf4j
public class UserLandingController {
    public ImageView btnClose;
    FXMLLoaderHelper loaderHelper = new FXMLLoaderHelper();
    @FXML
    private Label timeLabel, requestLabel;
    @FXML
    private Pane servicePane;
    @FXML
    private JFXButton btnGift, btnSecurity, btnSanitation, btnMaintenance, btnIT, btnInternal, btnExternal, btnInterpreter, btnMedication;
    @Inject
    ILoginManager login;

    @FXML
    public void initialize() throws IOException {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> timeLabel.setText(new SimpleDateFormat("h:mm aa").format(new Date())));
            }
        }, 0, 1000);

        launchDefaultPane();
    }

    @FXML
    public void launchDefaultPane() throws IOException{
        resetAndLoadPane("DefaultPane", " ");
        requestLabel.setVisible(false);
        btnClose.setVisible(false);
    }

    @FXML
    public void launchSecurityPane() throws IOException{
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
        resetAndLoadPane("GiftCart", "Gift Request");
        btnSanitation.setStyle("-fx-background-color: #DCDCDC");
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

    private void resetAndLoadPane(String regionName, String labelText) throws IOException {
        JFXButton[] allButtons = new JFXButton[]{btnGift, btnSecurity, btnSanitation, btnMaintenance, btnIT, btnInternal, btnExternal, btnInterpreter, btnMedication};
        for (JFXButton currButton:allButtons) {
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
            Parent root = loaderHelper.getFXMLLoader("Staff/NotificationsPage").load();
            loaderHelper.setupScene(new Scene(root));
        } catch (IOException ex) {
            log.error("Encountered IOException", ex);
        }
    }

    public void logoutBtn() {
        log.info("here");
        login.logOut();
        try {
            Parent root = loaderHelper.getFXMLLoader("MapViewer").load();
            loaderHelper.setupScene(new Scene(root));
        } catch (IOException ex) {
            log.error("Encountered IOException", ex);
        }
    }

}


