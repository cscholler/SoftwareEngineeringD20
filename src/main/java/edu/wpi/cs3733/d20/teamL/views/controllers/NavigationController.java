package edu.wpi.cs3733.d20.teamL.views.controllers;

import com.jfoenix.controls.JFXAutoCompletePopup;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import edu.wpi.cs3733.d20.teamL.App;
import edu.wpi.cs3733.d20.teamL.entities.Node;
import edu.wpi.cs3733.d20.teamL.services.navSearch.SearchFields;
import edu.wpi.cs3733.d20.teamL.services.db.IDBCache;

import edu.wpi.cs3733.d20.teamL.util.FXMLLoaderHelper;
import edu.wpi.cs3733.d20.teamL.views.controllers.map.MapViewerController;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.stage.Screen;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
public class NavigationController implements Initializable {

    @FXML
    private ImageView iHome;
    @FXML
    private JFXButton btnMap;
    @FXML
    private JFXTextField searchBox;
    @FXML
    private Label timeLabel;
    @Inject
    private IDBCache cache;
    private FXMLLoaderHelper loaderHelper = new FXMLLoaderHelper();
    private JFXAutoCompletePopup<String> autoCompletePopup;

    private SearchFields sf;

    @FXML
    public void initialize(URL location, ResourceBundle resources) {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> timeLabel.setText(new SimpleDateFormat("h:mm aa").format(new Date())));
            }
        }, 0, 1000);

        cache.cacheAllFromDB();
        sf = new SearchFields(cache.getNodeCache());
        sf.getFields().addAll(Arrays.asList(SearchFields.Field.shortName, SearchFields.Field.longName));
        sf.populateSearchFields();
        autoCompletePopup = new JFXAutoCompletePopup<>();
        autoCompletePopup.getSuggestions().addAll(sf.getSuggestions());
        //sets picture on home
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        iHome.setFitHeight(screenBounds.getHeight());
        iHome.setFitWidth(screenBounds.getWidth());

        searchBox.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.ENTER))
                searchMap();
        });
    }

    @FXML
    private void searchMap() {
        try {
            Stage stage = (Stage) btnMap.getScene().getWindow();
            FXMLLoader loader = loaderHelper.getFXMLLoader("MapViewer");
            Parent root = loader.load();
            Scene scene = new Scene(root);

            stage.setScene(scene);
            stage.setMaximized(true);
            stage.show();
            MapViewerController controller = loader.getController();
            controller.getMap().recalculatePositions();
            controller.setDestination(searchBox.getText());
            controller.navigate();

            stage.setWidth(App.SCREEN_WIDTH);
            stage.setHeight(App.SCREEN_HEIGHT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void loginBtnClicked() {
        try {
            Parent root = loaderHelper.getFXMLLoader("LoginPage").load();
            loaderHelper.setupPopup(new Stage(), new Scene(root));
        } catch (IOException e) {
            log.error("Encountered IOException", e);
        }
    }

    @FXML
    private void mapBtnClicked() {
        try {
            Parent root = loaderHelper.getFXMLLoader("MapViewer").load();
            loaderHelper.setupScene(new Scene(root));
        } catch (IOException e) {
            log.error("Encountered IOException", e);
        }
    }

    @FXML
    private void helpBtnClicked() {
        try {
            Parent root = loaderHelper.getFXMLLoader("Help").load();
            loaderHelper.setupScene(new Scene(root));
        } catch (IOException e) {
            log.error("Encountered IOException", e);
        }
    }

    @FXML
    private void servicesBtnClicked() {
        /*try {
            Parent root = loaderHelper.getFXMLLoader("Services").load();
            loaderHelper.setupScene(new Scene(root));
        } catch (IOException e) {
            log.error("Encountered IOException", e);
        }*/
    }

    /**
     * Supports autocompletion for user when typing in a specific word
     */
    @FXML
    private void autocomplete() {
        sf.applyAutocomplete(searchBox, autoCompletePopup);
    }

    public Label getTimeLabel() {
        return timeLabel;
    }

    public void setTimeLabel(Label timeLabel) {
        this.timeLabel = timeLabel;
    }
}
