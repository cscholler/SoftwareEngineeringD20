package edu.wpi.cs3733.d20.teamL.views.controllers;

import com.google.inject.Inject;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXSpinner;
import edu.wpi.cs3733.d20.teamL.services.db.IDatabaseService;
import edu.wpi.cs3733.d20.teamL.services.users.ILoginManager;
import edu.wpi.cs3733.d20.teamL.util.AsyncTaskManager;
import edu.wpi.cs3733.d20.teamL.util.FXMLLoaderFactory;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.*;

@Slf4j
public class LoggedInViewController implements Initializable{
    @FXML
    private JFXButton btnAddUser, btnAddDoctor, btnAddPatient, btnServiceRequest, btnMapEditor;
    @FXML
    private HBox buttonBox;
    @FXML
    private Label lblName, lblMap;
    @FXML
    private VBox vMap, vPatient, vDoc, vService, vUser;
    @Inject
	IDatabaseService db;
    @Inject
    ILoginManager loginManager;
    String map;
    FXMLLoaderFactory loaderHelper = new FXMLLoaderFactory();


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        lblName.setText(loginManager.getCurrentUser().getFName());
        if (loginManager.getCurrentUser().getAcctType().equals("3")) {
            buttonBox.getChildren().remove(vPatient);
            buttonBox.getChildren().remove(vService);
            map = "admin/MapEditor";
            lblMap.setText("Map Editor");
        } else {
            buttonBox.getChildren().remove(vUser);
            buttonBox.getChildren().remove(vDoc);
            map = "map_viewer/MapViewer";
            lblMap.setText("Map Viewer");
        }
    }

    /**
     * goes to notifications page when button is clicked
     */
    @FXML
    private void notifsClicked() {
        try {
            Parent root = loaderHelper.getFXMLLoader("NotificationsPage").load();
            loaderHelper.setupScene(new Scene(root));
        } catch (IOException ex) {
            log.error("Encountered IOException", ex);
        }
    }

    @FXML
    private void addUserClicked(){
        try {
            System.out.println("Got here");
            Parent root = loaderHelper.getFXMLLoader("AddUser").load();
            loaderHelper.setupScene(new Scene(root));
        } catch (IOException e) {
            log.error("Encountered IOException", e);
        }
    }

    @FXML
    private void addDoctorClicked(){
        try {
            Parent root = loaderHelper.getFXMLLoader("AddDoctor").load();
            loaderHelper.setupScene(new Scene(root));
        } catch (IOException e) {
            log.error("Encountered IOException", e);
        }
    }

    @FXML
    private void addPatientClicked(){
        try {
            Parent root = loaderHelper.getFXMLLoader("AddPatient").load();
            loaderHelper.setupScene(new Scene(root));
        } catch (IOException e) {
            log.error("Encountered IOException", e);
        }
    }

    @FXML
    private void mapEditorClicked(){
        try {
            Parent root = loaderHelper.getFXMLLoader(map).load();
            loaderHelper.setupScene(new Scene(root));
        } catch (IOException ex) {
            log.error("Encountered IOException", ex);
        }
    }

    @FXML
    private void serviceRequestsClicked(){
        try {
            Parent root = loaderHelper.getFXMLLoader("ServiceRequestsPage").load();
            loaderHelper.setupScene(new Scene(root));
        } catch (IOException ex) {
            log.error("Encountered IOException", ex);
        }
    }

    /**
     * goes back to homepage when logout button is clicked
     */
    @FXML
    private void logoutClicked() {
        loginManager.logOut(true);
        try {
            Parent root = loaderHelper.getFXMLLoader("map_viewer/MapViewer").load();
            loaderHelper.setupScene(new Scene(root));
        } catch (IOException ex) {
            log.error("Encountered IOException", ex);
        }
    }

    @FXML
	public void importClicked(ActionEvent actionEvent) {
		try {
			Parent root = loaderHelper.getFXMLLoader("dialogues/ImportDialogue").load();
			loaderHelper.setupPopup(new Stage(), new Scene(root));
		} catch (IOException ex) {
			log.error("Encountered IOException", ex);
		}
	}

	@FXML
	public void exportClicked(ActionEvent actionEvent) {
        try {
            Parent root = loaderHelper.getFXMLLoader("dialogues/ExportDialogue").load();
            loaderHelper.setupPopup(new Stage(), new Scene(root));
        } catch (IOException ex) {
            log.error("Encountered IOException", ex);
        }
	}

	@FXML
	public void clearClicked() {
    	log.warn("Rebuilding database");

    	Executor uiExec = Platform::runLater;

        Alert loading = new Alert(Alert.AlertType.NONE);
        loading.setResult(ButtonType.OK);
//        JFXSpinner spinner = new JFXSpinner();
//        spinner.setRadius(10);
        ImageView spinner = new ImageView(new Image("edu/wpi/cs3733/d20/teamL/assets/spinner.gif"));
        spinner.setPreserveRatio(true);
        spinner.setFitWidth(40);
        loading.setGraphic(spinner);
        loading.setContentText("Rebuilding database...");

        // LOOK HOW SMART I AM
        Button btn = new Button("Start");
        btn.setOnAction(evt -> {
            btn.setDisable(true);

            // make alert appear / disappear
            Thread t = new Thread(() -> {
                boolean showing = false;
                while (true) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        log.error("{}", ex);
                    }
                    Runnable action = showing ? loading::close : loading::show;
                    Platform.runLater(action);
                    showing = !showing;
                }
            });
            t.setDaemon(true);
            t.start();
        });

        loading.show();

        AsyncTaskManager.newTask(() -> {
            db.rebuildDatabase();
            log.info("Finished rebuilding database");
            uiExec.execute(new FutureTask<>(() -> {
                loading.close();
                showDone();
                return null;
            }));
        });
	}

	private Boolean showDone() {
        log.info("showDone() Called");
        Alert done = new Alert(Alert.AlertType.INFORMATION);
        done.setContentText("Finished rebuilding database");
        done.showAndWait();

        return true;
    }
}
