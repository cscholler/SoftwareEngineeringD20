package edu.wpi.cs3733.d20.teamL.views.controllers;

import com.jfoenix.controls.JFXAutoCompletePopup;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import edu.wpi.cs3733.d20.teamL.util.io.DBCache;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;

public class NavigationController {

    @FXML private JFXButton btnLogin;
    @FXML private JFXButton btnMap;
    @FXML private JFXButton btnServices;
    @FXML private JFXButton btnHelp;
    @FXML private JFXTextField searchBox;

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
            stage.show();

        //Displays the map of the hospital
        } else if (actionEvent.getSource() == btnMap) {

            stage = (Stage) btnMap.getScene().getWindow();
            root = FXMLLoader.load(getClass().getResource("/edu/wpi/cs3733/d20/teamL/views/MapViewer.fxml"));
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setFullScreen(true);
            stage.show();

        //Displays a popup window that help is on the way
        } else if (actionEvent.getSource() == btnHelp) {

            stage = new Stage();
            root = FXMLLoader.load(getClass().getResource("/edu/wpi/cs3733/d20/teamL/views/Help.fxml"));
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(btnHelp.getScene().getWindow());
            stage.setFullScreen(true);
            stage.show();

        //Goes to Service display screen
        } else {


        }

    }

    /**
     * Supports autocompletion for user when typing in a specific word
     *
     */
    public void inputHandler() {

        ArrayList<String> suggestions = new ArrayList<String>();
        DBCache dbCache = DBCache.getCache();
        for(ArrayList<String> nodes: dbCache.getNodeCache()) {
            suggestions.add(nodes.get(6));
            System.out.println("Nodes: " + nodes.get(6));
        }
        JFXAutoCompletePopup<String> autoCompletePopup = new JFXAutoCompletePopup<>();
        autoCompletePopup.getSuggestions().addAll(suggestions);
        autoCompletePopup.setSelectionHandler(event -> {
            searchBox.setText(event.getObject());
        });
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
