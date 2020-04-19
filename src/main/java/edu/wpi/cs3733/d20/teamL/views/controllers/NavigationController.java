package edu.wpi.cs3733.d20.teamL.views.controllers;

import com.jfoenix.controls.JFXAutoCompletePopup;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import edu.wpi.cs3733.d20.teamL.entities.Node;
import edu.wpi.cs3733.d20.teamL.services.navSearch.SearchFields;
import edu.wpi.cs3733.d20.teamL.services.db.DBCache;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class NavigationController {

    @FXML private JFXButton btnLogin;
    @FXML private JFXButton btnMap;
    @FXML private JFXButton btnServices;
    @FXML private JFXButton btnHelp;
    @FXML private JFXTextField searchBox;
	private DBCache cache;
	private SearchFields sf;

	@FXML
	public void initialize(URL location, ResourceBundle resources) {
		cache = new DBCache();
		cache.cacheNodes();
		cache.cacheEdges();
		sf = new SearchFields(getNodeCache());
		sf.populateSearchFields();
	}

	public ArrayList<Node> getNodeCache() {
		return cache.getNodeCache();
	}

    /**
     * Handles the user's action when pressing on a specific button and goes to a new page
     *
     * @param actionEvent The action taken by the user (pressing the button)
     * @throws IOException
     */
    public void handleButtonAction(ActionEvent actionEvent) throws IOException {

        Stage stage;
        Parent root;

        //Goes to the Login Page
        if (actionEvent.getSource() == btnLogin) {

            stage = new Stage();
            root = FXMLLoader.load(getClass().getResource("/edu/wpi/cs3733/d20/teamL/views/LoginPage.fxml"));
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(btnLogin.getScene().getWindow());
            stage.showAndWait();

        //Displays the map of the hospital
        } else if (actionEvent.getSource() == btnMap) {

            stage = (Stage) btnMap.getScene().getWindow();
            root = FXMLLoader.load(getClass().getResource("/edu/wpi/cs3733/d20/teamL/views/MapViewer.fxml"));
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

        //Displays a popup window that help is on the way
        } else if (actionEvent.getSource() == btnHelp) {

            stage = new Stage();
            root = FXMLLoader.load(getClass().getResource("/edu/wpi/cs3733/d20/teamL/views/Help.fxml"));
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(btnHelp.getScene().getWindow());
            stage.showAndWait();

        //Goes to Service display screen
        } else {


        }

    }


    /**
     * Supports autocompletion for user when typing in a specific word
     *
     */
    public void inputHandler() {
        JFXAutoCompletePopup<String> autoCompletePopup = new JFXAutoCompletePopup<>();
        autoCompletePopup.getSuggestions().addAll(sf.getSuggestions());
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
}
