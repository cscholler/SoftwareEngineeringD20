package edu.wpi.cs3733.d20.teamL.views.controllers;

import com.jfoenix.controls.JFXAutoCompletePopup;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import edu.wpi.cs3733.d20.teamL.App;
import edu.wpi.cs3733.d20.teamL.entities.Node;
import edu.wpi.cs3733.d20.teamL.services.navSearch.SearchFields;
import edu.wpi.cs3733.d20.teamL.services.db.IDBCache;

import edu.wpi.cs3733.d20.teamL.util.FXMLLoaderHelper;
import edu.wpi.cs3733.d20.teamL.util.io.SMSSender;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;

import javax.inject.Inject;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class NavigationController implements Initializable {

    @FXML private ImageView iHome;
    @FXML private JFXButton btnLogin;
    @FXML private JFXButton btnMap;
    @FXML private JFXButton btnServices;
    @FXML private JFXButton btnHelp;
    @FXML private JFXTextField searchBox;
    @Inject
	private IDBCache cache;
    private FXMLLoaderHelper loaderHelper = new FXMLLoaderHelper();
	private SearchFields sf;
	private JFXAutoCompletePopup<String> autoCompletePopup;

	@FXML
	public void initialize(URL location, ResourceBundle resources) {
		cache.cacheAllFromDB();
		sf = new SearchFields(getNodeCache());
		sf.populateSearchFields();
		autoCompletePopup = new JFXAutoCompletePopup<>();
		autoCompletePopup.getSuggestions().addAll(sf.getSuggestions());
		//sets picture on home
		Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
		iHome.setFitHeight(screenBounds.getHeight());
        iHome.setFitWidth(screenBounds.getWidth());

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
			root = loaderHelper.getFXMLLoader("LoginPage").load();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(btnLogin.getScene().getWindow());
            stage.showAndWait();
        //Displays the map of the hospital
        } else if (actionEvent.getSource() == btnMap) {
            stage = (Stage) btnMap.getScene().getWindow();
			FXMLLoader loader = loaderHelper.getFXMLLoader("MapViewer");
			root = loader.load();
            Scene scene = new Scene(root);
            stage.setScene(scene);

            stage.hide();

            stage.setMaximized(true);
            stage.show();
            MapViewerController controller = loader.getController();
            controller.getMap().recalculatePositions();

            stage.setWidth(App.SCREEN_WIDTH);
            stage.setHeight(App.SCREEN_HEIGHT);
        //Displays a popup window that help is on the way
        } else if (actionEvent.getSource() == btnHelp) {
            stage = (Stage) btnHelp.getScene().getWindow();
			root = loaderHelper.getFXMLLoader("Help").load();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.hide();
            stage.setMaximized(true);
            stage.show();

            stage.setWidth(App.SCREEN_WIDTH);
            stage.setHeight(App.SCREEN_HEIGHT);

        //Goes to Service display screen
        }
    }

    /**
     * Supports autocompletion for user when typing in a specific word
     *
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
}
