package edu.wpi.cs3733.d20.teamL.views.controllers.requests;

import com.jfoenix.controls.JFXButton;
import edu.wpi.cs3733.d20.teamL.services.users.ILoginManager;
import edu.wpi.cs3733.d20.teamL.util.FXMLLoaderHelper;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.io.IOException;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

@Slf4j
public class UserLandingController {
    FXMLLoaderHelper loaderHelper = new FXMLLoaderHelper();

    @FXML
    private Label timeLabel;
    @FXML
    private Pane servicePane;
    @FXML
    private JFXButton btnGift, btnSecurity, btnSanitation, btnMaintenance, btnIT, btnInternal, btnExternal, btnInterpreter, btnMedication;
    @Inject
    ILoginManager login;

    @FXML
    public void initialize(){
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> timeLabel.setText(new SimpleDateFormat("h:mm aa").format(new Date())));
            }
        }, 0, 1000);
    }

    @FXML
    public void launchSecurityPane() throws IOException{
        try {
            servicePane.getChildren().clear();
            servicePane.getChildren().add(loaderHelper.getFXMLLoader("requests/SecurityPane").load());
            resetButtons();
            btnSecurity.setStyle("-fx-background-color: #DCDCDC;");
        } catch (IOException ex) {
            log.error("Encountered IOException", ex);
        }
    }

    @FXML
    public void launchSanitationPane(ActionEvent actionEvent) {
        resetButtons();
        btnSanitation.setStyle("-fx-background-color: #DCDCDC;");
        System.out.println("Sanitation Request");
    }

    @FXML
    public void launchGiftPane(ActionEvent actionEvent) {
        resetButtons();
        btnGift.setStyle("-fx-background-color: #DCDCDC;");
        System.out.println("Gift Request");
    }

    @FXML
    public void launchMaintenancePane(ActionEvent actionEvent) {
        resetButtons();
        btnMaintenance.setStyle("-fx-background-color: #DCDCDC;");
        System.out.println("Maintenance Request");
    }

    @FXML
    public void launchMedicationPane(ActionEvent actionEvent) {
        resetButtons();
        btnMedication.setStyle("-fx-background-color: #DCDCDC;");
        System.out.println("Medication Request");
    }

    @FXML
    public void launchInternalPane(ActionEvent actionEvent) {
        resetButtons();
        btnInternal.setStyle("-fx-background-color: #DCDCDC;");
        System.out.println("Internal Transport");
    }

    @FXML
    public void launchExternalPane(ActionEvent actionEvent) {
        resetButtons();
        btnExternal.setStyle("-fx-background-color: #DCDCDC;");
        System.out.println("External Transport");
    }

    @FXML
    public void launchITPane(ActionEvent actionEvent) {
        resetButtons();
        btnIT.setStyle("-fx-background-color: #DCDCDC;");
        System.out.println("IT Request");
    }

    @FXML
    public void launchInterpreterPane(ActionEvent actionEvent) {
        resetButtons();
        btnInterpreter.setStyle("-fx-background-color: #DCDCDC;");
        System.out.println("Interpreter Request");
    }

    private void resetButtons() {
        JFXButton[] allButtons = new JFXButton[]{btnGift, btnSecurity, btnSanitation, btnMaintenance, btnIT, btnInternal, btnExternal, btnInterpreter, btnMedication};
        for (JFXButton currButton:allButtons) {
            currButton.setStyle("-fx-background-color: white;");
        }
    }

    @FXML
    public void launchNotifPage(ActionEvent actionEvent) {
        try {
            Parent root = loaderHelper.getFXMLLoader("Staff/NotificationsPage").load();
            loaderHelper.setupScene(new Scene(root));
        } catch (IOException ex) {
            log.error("Encountered IOException", ex);
        }
    }

    public void logoutBtn(ActionEvent actionEvent) {
        log.info("here");
        login.logOut();
        try {
            Parent root = loaderHelper.getFXMLLoader("Home").load();
            loaderHelper.setupScene(new Scene(root));
        } catch (IOException ex) {
            log.error("Encountered IOException", ex);
        }
    }
}


