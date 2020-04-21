package edu.wpi.cs3733.d20.teamL.views.controllers;

import com.jfoenix.controls.JFXButton;
import edu.wpi.cs3733.d20.teamL.util.FXMLLoaderHelper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class EmergencyController {
	FXMLLoaderHelper loaderHelper = new FXMLLoaderHelper();
    @FXML
	private JFXButton btnCancel;

    /**
     * Goes back to the Home page when user clicks Cancel
     *
     * @param actionEvent The action taken by the user (pressing the button)
     * @throws IOException
     */
    public void handleCancel(ActionEvent actionEvent) throws IOException {
        Stage stage;
        Parent root;
        //Goes back to the Home page
        if (actionEvent.getSource() == btnCancel) {
            stage = (Stage) btnCancel.getScene().getWindow();
            root = loaderHelper.getFXMLLoader("Home").load();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setFullScreen(true);
            stage.show();
        }
    }
}
