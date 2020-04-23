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
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;

import javax.inject.Inject;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

public class NavigationController implements Initializable {

    @FXML
    private ImageView iHome;
    @FXML
    private JFXButton btnLogin;
    @FXML
    private JFXButton btnMap;
    @FXML
    private JFXButton btnServices;
    @FXML
    private JFXButton btnHelp;
    @FXML
    private JFXTextField searchBox;
    @FXML
    private JFXButton btnSearch;
    @FXML
    private Label timeLabel;
    @Inject
    private IDBCache cache;
    private FXMLLoaderHelper loaderHelper = new FXMLLoaderHelper();
    private SearchFields sf;
    private JFXAutoCompletePopup<String> autoCompletePopup;

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
        sf = new SearchFields(getNodeCache());
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

            stage.hide();

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

    public ArrayList<Node> getNodeCache() {
        return cache.getNodeCache();
    }


    @FXML
    private void loginBtnClicked() {
        Parent root = loaderHelper.getFXMLLoader("LoginPage").load();
        loaderHelper.setupPopup(new Stage(), new Scene(root));
    }

    @FXML
    private void mapBtnClicked() {
        Parent root = loaderHelper.getFXMLLoader("MapViewer").load();
        loaderHelper.setupScene(new Scene(root));
    }

    @FXML
    private void helpBtnClicked() {
        Parent root = loaderHelper.getFXMLLoader("Help").load();
        loaderHelper.setupScene(new Scene(root));
    }

    /**
     * Supports autocompletion for user when typing in a specific word
     */
    public void inputHandler() {
        autoCompletePopup.setSelectionHandler(event -> searchBox.setText(event.getObject()));
        searchBox.textProperty().addListener(observable -> {
            autoCompletePopup.filter(string ->
                    string.toLowerCase().contains(searchBox.getText().toLowerCase()));
            if (autoCompletePopup.getFilteredSuggestions().isEmpty() ||
                    searchBox.getText().isEmpty()) {
                autoCompletePopup.hide();
            } else {
                autoCompletePopup.show(searchBox);
            }
        });
    }

    public Label getTimeLabel() {
        return timeLabel;
    }

    public void setTimeLabel(Label timeLabel) {
        this.timeLabel = timeLabel;
    }
}
