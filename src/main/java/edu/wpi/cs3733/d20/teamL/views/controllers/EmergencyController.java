package edu.wpi.cs3733.d20.teamL.views.controllers;

import edu.wpi.cs3733.d20.teamL.util.FXMLLoaderHelper;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class EmergencyController {
    FXMLLoaderHelper loaderHelper = new FXMLLoaderHelper();

    /**
     * Goes back to the Home page when user clicks Cancel
     * @throws IOException
     */
    @FXML
    private void cancelClicked() {
        try {
            Parent root = loaderHelper.getFXMLLoader("Home").load();
            loaderHelper.setupScene(new Scene(root));
        } catch (IOException e) {
            log.error("Encountered IOException", e);
        }

    }

}

