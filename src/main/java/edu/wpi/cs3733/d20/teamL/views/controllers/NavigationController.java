package edu.wpi.cs3733.d20.teamL.views.controllers;

import com.jfoenix.controls.JFXAutoCompletePopup;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import edu.wpi.cs3733.d20.teamL.services.navSearch.SearchFields;
import edu.wpi.cs3733.d20.teamL.services.db.DBCache;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class NavigationController implements Initializable {

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

	public ArrayList<ArrayList<String>> getNodeCache() {
		return cache.getNodeCache();
	}

    public void handleButtonAction(ActionEvent actionEvent) throws IOException {

        if (actionEvent.getSource() == btnLogin) {


        } else if (actionEvent.getSource() == btnMap) {


        } else if (actionEvent.getSource() == btnHelp) {

        } else {


        }
    }

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
