package edu.wpi.cs3733.d20.teamL.views.controllers.dialogues;

import com.jfoenix.controls.JFXButton;
import com.sun.javafx.fxml.FXMLLoaderHelper;
import edu.wpi.cs3733.d20.teamL.services.db.IDatabaseService;
import edu.wpi.cs3733.d20.teamL.services.users.ILoginManager;
import edu.wpi.cs3733.d20.teamL.util.AsyncTaskManager;
import edu.wpi.cs3733.d20.teamL.util.FXMLLoaderFactory;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.util.concurrent.Executor;
import java.util.concurrent.FutureTask;

@Slf4j
public class RebuildDatabasePopupController {

    FXMLLoaderFactory loaderHelper = new FXMLLoaderFactory();

    @Inject
    ILoginManager login;

    @Inject
    IDatabaseService db;

    @FXML
    private JFXButton noBtn;

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