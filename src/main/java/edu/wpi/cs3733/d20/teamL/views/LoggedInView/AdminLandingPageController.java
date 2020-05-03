package edu.wpi.cs3733.d20.teamL.views.LoggedInView;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXListView;
import edu.wpi.cs3733.d20.teamL.util.FXMLLoaderHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import edu.wpi.cs3733.d20.teamL.services.users.ILoginManager;
import edu.wpi.cs3733.d20.teamL.util.FXMLLoaderHelper;

import javax.inject.Inject;

@Slf4j
public class AdminLandingPageController {

    @FXML
    private JFXComboBox selectDatabase;
    ObservableList<String> databaseOptions = FXCollections.observableArrayList("Map Nodes", "Map Edges", "Gift Inventory", "Users");

    @FXML
    private JFXListView listData;

    @Inject
    ILoginManager login;


    FXMLLoaderHelper loaderHelper = new FXMLLoaderHelper();

    @FXML
    public void logoutBtn() {
        login.logOut(true);
        try {
            Parent root = loaderHelper.getFXMLLoader("map_viewer/MapViewer").load();
            loaderHelper.setupScene(new Scene(root));
        } catch (IOException ex) {
            log.error("Encountered IOException", ex);
        }
    }

    @FXML
    private void btnMapClicked() {
        try {
            Parent root = loaderHelper.getFXMLLoader("Admin/MapEditor").load();
            loaderHelper.setupPopup(new Stage(), new Scene(root));
        } catch (IOException ex) {
            log.error("Encountered IOException", ex);
        }
    }


}
