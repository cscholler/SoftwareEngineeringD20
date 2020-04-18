package edu.wpi.cs3733.d20.teamL.views.controllers;

import com.jfoenix.controls.JFXAutoCompletePopup;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.events.JFXAutoCompleteEvent;
import edu.wpi.cs3733.d20.teamL.services.navSearch.SearchFields;
import edu.wpi.cs3733.d20.teamL.util.io.CSVReader;
import edu.wpi.cs3733.d20.teamL.util.io.DBCache;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.skin.TextFieldSkin;

import java.io.IOException;
import java.util.ArrayList;

public class NavigationController {

    @FXML private JFXButton btnLogin;
    @FXML private JFXButton btnMap;
    @FXML private JFXButton btnServices;
    @FXML private JFXButton btnHelp;
    @FXML private JFXTextField searchBox;

    public void handleButtonAction(ActionEvent actionEvent) throws IOException {

        if (actionEvent.getSource() == btnLogin) {


        } else if (actionEvent.getSource() == btnMap) {


        } else if (actionEvent.getSource() == btnHelp) {

        } else {


        }
    }
    public void inputHandler() {
        SearchFields sf = SearchFields.getSearchFields();
        sf.populateSearchFields();

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
