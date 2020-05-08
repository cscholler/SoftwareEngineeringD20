package edu.wpi.cs3733.d20.teamL.views.controllers.logged_in;

import edu.wpi.cs3733.d20.teamL.util.FXMLLoaderFactory;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class SideBarController {

    private FXMLLoaderFactory loaderFactory = new FXMLLoaderFactory();

    @FXML
    private void addUserClicked() {
        try {
            Parent root = loaderFactory.getFXMLLoader("admin/AddPerson").load();
            loaderFactory.setupPopup(new Stage(), new Scene(root));
        } catch (IOException ex) {
            log.error("Encountered IOException", ex);
        }
    }

    @FXML
    private void changePasswordClicked() {
        try {
            Parent root = loaderFactory.getFXMLLoader("admin/ChangePassword").load();
            loaderFactory.setupPopup(new Stage(), new Scene(root));
        } catch (IOException ex) {
            log.error("Encountered IOException", ex);
        }
    }

    @FXML
    public void btnImportClicked() {
        try {
            Parent root = loaderFactory.getFXMLLoader("dialogues/ImportDialogue").load();
            loaderFactory.setupPopup(new Stage(), new Scene(root));
        } catch (IOException ex) {
            log.error("Encountered IOException", ex);
        }
    }

    @FXML
    public void btnExportClicked() {
        try {
            Parent root = loaderFactory.getFXMLLoader("dialogues/ExportDialogue").load();
            loaderFactory.setupPopup(new Stage(), new Scene(root));
        } catch (IOException ex) {
            log.error("Encountered IOException", ex);
        }
    }






}
