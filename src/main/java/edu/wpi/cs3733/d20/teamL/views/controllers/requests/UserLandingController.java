package edu.wpi.cs3733.d20.teamL.views.controllers.requests;

import com.jfoenix.controls.JFXButton;
import edu.wpi.cs3733.d20.teamL.util.FXMLLoaderHelper;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Collection;

@Slf4j
public class UserLandingController {
    FXMLLoaderHelper loaderHelper = new FXMLLoaderHelper();

    @FXML
    private Pane servicePane;

    @FXML
    private JFXButton btnGift, btnSecurity, btnSanitation, btnMaintenance, btnIT, btnInternal, btnExternal, btnInterpreter, btnMedication;

    @FXML
    public void initialize(){

    }

    @FXML
    public void securitySelected() throws IOException{
        try {
            servicePane.getChildren().setAll((Collection<? extends Node>) loaderHelper.getFXMLLoader("SecurityRequest").load());
        } catch (IOException ex) {
            log.error("Encountered IOException", ex);
        }
    }
}


