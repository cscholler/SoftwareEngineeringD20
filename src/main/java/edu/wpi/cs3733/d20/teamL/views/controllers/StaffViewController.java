package edu.wpi.cs3733.d20.teamL.views.controllers;

import com.jfoenix.controls.JFXButton;
import edu.wpi.cs3733.d20.teamL.App;
import edu.wpi.cs3733.d20.teamL.util.FXMLLoaderHelper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

public class StaffViewController {
	@FXML
	private Label userLabel;
	@FXML
    private JFXButton btnLogout, btnNotif,btnMeds ,btnMe, btnMR, btnChangeR, btnMap, btnAddPatient;
    @FXML
    private Label lblName;
    private FXMLLoaderHelper loaderHelper = new FXMLLoaderHelper();

    /**
     * Controls staff view page after they log in
     *
     * @param event tracks which button was pressed
     * @throws IOException
     */
    @FXML
    public void handleCircleButton(ActionEvent event) throws IOException {
        Stage stage;
        Parent root;

        //open notifications
        if (event.getSource() == btnNotif) {
            stage = (Stage) btnNotif.getScene().getWindow();
            root = loaderHelper.getFXMLLoader("NotificationsPage").load();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.show();

            stage.setWidth(App.SCREEN_WIDTH);
            stage.setHeight(App.SCREEN_HEIGHT);
            //opens med request
        } else if (event.getSource() == btnMeds) {
            stage = (Stage) btnMeds.getScene().getWindow();
            root = loaderHelper.getFXMLLoader("MedicationRequest").load();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.hide();
            stage.isMaximized();
            stage.show();

            stage.setWidth(App.SCREEN_WIDTH);
            stage.setHeight(App.SCREEN_HEIGHT);
            //opens mapView
        } else if (event.getSource() == btnMap) {
            stage = (Stage) btnMap.getScene().getWindow();
            FXMLLoader fxmlLoader = loaderHelper.getFXMLLoader("MapViewer");
            root = fxmlLoader.load();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.show();

            MapViewerController controller = fxmlLoader.getController();
            controller.getMap().recalculatePositions();

            stage.setWidth(App.SCREEN_WIDTH);
            stage.setHeight(App.SCREEN_HEIGHT);
            //returns back to home
        } else if (event.getSource() == btnLogout) {
            stage = (Stage) btnLogout.getScene().getWindow();
            root = loaderHelper.getFXMLLoader("Home").load();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.hide();
            stage.setMaximized(true);
            stage.show();
            //adds a patient
        } else if (event.getSource() == btnAddPatient) {
            stage = (Stage) btnAddPatient.getScene().getWindow();
            root = loaderHelper.getFXMLLoader("AddPatient").load();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.hide();
            stage.setMaximized(true);
            stage.show();

            stage.setWidth(App.SCREEN_WIDTH);
            stage.setHeight(App.SCREEN_HEIGHT);
        }
    }

	public Label getUserLabel() {
		return userLabel;
	}

	public void setUserLabel(Label userLabel) {
		this.userLabel = userLabel;
	}
}
