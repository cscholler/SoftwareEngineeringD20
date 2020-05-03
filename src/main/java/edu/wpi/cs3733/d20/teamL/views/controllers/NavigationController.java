package edu.wpi.cs3733.d20.teamL.views.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.stage.Screen;
import javafx.stage.Stage;

import com.jfoenix.controls.JFXAutoCompletePopup;
import com.jfoenix.controls.JFXTextField;

import lombok.extern.slf4j.Slf4j;

import edu.wpi.cs3733.d20.teamL.util.search.SearchFields;
import edu.wpi.cs3733.d20.teamL.util.FXMLLoaderFactory;
import edu.wpi.cs3733.d20.teamL.views.controllers.map.MapViewerController;

@Slf4j
public class NavigationController implements Initializable {
    @FXML
    private ImageView backgroundImage;
    @FXML
    private JFXTextField searchBox;
    @FXML
    private Label timeLabel;
    private final FXMLLoaderFactory loaderHelper = new FXMLLoaderFactory();
    private JFXAutoCompletePopup<String> autoCompletePopup;
    private SearchFields sf;

    @FXML
    public void initialize(URL location, ResourceBundle resources) {
		// This should be the only place where the cache is rebuilt
        //cache.cacheAllFromDB();
        //sf = new SearchFields(cache.getNodeCache());
        sf.getFields().addAll(Arrays.asList(SearchFields.Field.shortName, SearchFields.Field.longName));
        sf.populateSearchFields();
        autoCompletePopup = new JFXAutoCompletePopup<>();
        autoCompletePopup.getSuggestions().addAll(sf.getSuggestions());
        //Sets home screen picture
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
		backgroundImage.setFitWidth(screenBounds.getWidth());
        backgroundImage.setFitHeight(screenBounds.getHeight());

        searchBox.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.ENTER))
                searchMap();
        });
    }

    @FXML
    private void clearOnClick(MouseEvent e) {
        if(e.getSource() == searchBox){
            searchBox.setPromptText("");
        }
    }
    /**
     * goes to map viewer page when a room is searched in the search bar
     */
    @FXML
    private void searchMap() {
        try {
            FXMLLoader loader = loaderHelper.getFXMLLoader("map_viewer/MapViewer");
            loaderHelper.setupScene(new Scene(loader.load()));
            MapViewerController controller = loader.getController();
            controller.setDestination(searchBox.getText());
            controller.navigate();
        } catch (IOException ex) {
            log.error("Encountered IOException.", ex);
        }
    }

    /**
     * login pops up when login button is clicked
     */
    @FXML
    private void loginBtnClicked() {
        try {
            Parent root = loaderHelper.getFXMLLoader("staff/LoginPage").load();
            loaderHelper.setupPopup(new Stage(), new Scene(root));
        } catch (IOException ex) {
            log.error("Encountered IOException", ex);
        }
    }

    /**
     * goes to map viewer when map button is clicked
     */
    @FXML
    private void mapBtnClicked() {
        try {
            Parent root = loaderHelper.getFXMLLoader("/Map Viewer/MapViewer").load();
            loaderHelper.setupScene(new Scene(root));
        } catch (IOException ex) {
            log.error("Encountered IOException", ex);
        }
    }

    /**
     * goes to emergency page when button is clicked
     */
    @FXML
    private void helpBtnClicked() {
        try {
            Parent root = loaderHelper.getFXMLLoader("Help").load();
            loaderHelper.setupScene(new Scene(root));
        } catch (IOException ex) {
            log.error("Encountered IOException", ex);
        }
    }

    /**
     * goes to a service page when services button is clicked
     */
    @FXML
    private void servicesBtnClicked() {
        /*try {
            Parent root = loaderHelper.getFXMLLoader("requests/MaintenanceRequest").load();
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
}
