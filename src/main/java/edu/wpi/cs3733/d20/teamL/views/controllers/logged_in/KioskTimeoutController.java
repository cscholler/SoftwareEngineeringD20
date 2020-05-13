package edu.wpi.cs3733.d20.teamL.views.controllers.logged_in;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import com.jfoenix.controls.JFXAutoCompletePopup;
import edu.wpi.cs3733.d20.teamL.App;
import edu.wpi.cs3733.d20.teamL.util.FXMLLoaderFactory;
import edu.wpi.cs3733.d20.teamL.util.SearchFields;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;

import com.google.inject.Inject;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;

import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;

import edu.wpi.cs3733.d20.teamL.entities.Kiosk;
import edu.wpi.cs3733.d20.teamL.services.db.DBConstants;
import edu.wpi.cs3733.d20.teamL.services.db.IDatabaseCache;
import edu.wpi.cs3733.d20.teamL.services.db.IDatabaseService;
import edu.wpi.cs3733.d20.teamL.services.db.SQLEntry;

@Slf4j
public class KioskTimeoutController {
	private final FXMLLoaderFactory loaderFactory = new FXMLLoaderFactory();
	private Kiosk currentKiosk;
	private SearchFields sf;
	private final JFXAutoCompletePopup<String> autoCompletePopup = new JFXAutoCompletePopup<>();
    @FXML
    private Spinner<Integer> logoutTimeoutText, idleCacheTimeoutText, forceCacheTimeoutText, screenSaverTimeoutText;
    @FXML
	private JFXTextField kioskLocationText;
	@FXML
	private JFXButton btnClose;
	@FXML
	private Label locationInvalid;
	@Inject
	private IDatabaseService db;
    @Inject
	private IDatabaseCache cache;

    @FXML
    private void initialize() {
		// Hard coded to only kiosk in database. Could be changed to find kiosk by ip.
    	currentKiosk = cache.getKioskCache().get(0);
    	ArrayList<String> results = db.getTableFromResultSet(db.executeQuery(new SQLEntry(DBConstants.SELECT_ALL_KIOSK_SETTINGS))).get(0);
		kioskLocationText.setText(cache.searchNodeCache(results.get(1)).getLongName());
    	logoutTimeoutText.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(30,300, Integer.parseInt(results.get(2)) / 1000));
    	idleCacheTimeoutText.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(15,120, Integer.parseInt(results.get(3)) / 1000));
    	forceCacheTimeoutText.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(30,300, Integer.parseInt(results.get(4)) / 1000));
    	screenSaverTimeoutText.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(30,600, Integer.parseInt(results.get(5)) / 1000));
    	logoutTimeoutText.setEditable(true);
    	idleCacheTimeoutText.setEditable(true);
    	forceCacheTimeoutText.setEditable(true);
    	screenSaverTimeoutText.setEditable(true);
		sf = new SearchFields(cache.getNodeCache());
		sf.getFields().add(SearchFields.Field.nodeID);
		sf.populateSearchFields();
		autoCompletePopup.getSuggestions().addAll(sf.getSuggestions());
	}

	@FXML
	private void btnClosePressed() {
		Stage stage = (Stage) btnClose.getScene().getWindow();
		stage.close();
	}

	@FXML
	private void btnSavePressed() {
    	String kioskLocation = sf.getNode(kioskLocationText.getText()).getID();
		String logoutTimeoutPeriod = String.valueOf(logoutTimeoutText.getValue());
		String idleCacheTimeoutPeriod = String.valueOf(idleCacheTimeoutText.getValue());
		String forceCacheTimeoutPeriod = String.valueOf(forceCacheTimeoutText.getValue());
		String screenSaverTimeoutPeriod = String.valueOf(screenSaverTimeoutText.getValue());
		boolean isLogoutTimeoutValid = !logoutTimeoutPeriod.isEmpty() && Long.parseLong(logoutTimeoutPeriod) * 1000 >= 30000;
		boolean isIdleCacheTimeoutValid = !idleCacheTimeoutPeriod.isEmpty() && Long.parseLong(idleCacheTimeoutPeriod) * 1000 >= 15000;
		boolean isForceCacheTimeoutValid = !forceCacheTimeoutPeriod.isEmpty() && Long.parseLong(forceCacheTimeoutPeriod) * 1000 >= 30000;
		boolean isScreenSaverTimeoutValid = !screenSaverTimeoutPeriod.isEmpty() && Long.parseLong(screenSaverTimeoutPeriod) * 1000 >= 30000;
		boolean isKioskLocationValid = db.getTableFromResultSet(db.executeQuery(new SQLEntry(DBConstants.GET_NODE, new ArrayList<>(Collections.singletonList(kioskLocation))))).size() == 1;
		int rows = 0;
		System.out.println(isKioskLocationValid && isIdleCacheTimeoutValid && isForceCacheTimeoutValid && isLogoutTimeoutValid && isScreenSaverTimeoutValid);
		if (isKioskLocationValid) {
			 rows = db.executeUpdate(new SQLEntry(DBConstants.UPDATE_KIOSK_TIMEOUTS, new ArrayList<>(Arrays.asList(kioskLocation, isLogoutTimeoutValid ? String.valueOf(Long.parseLong(logoutTimeoutPeriod) * 1000) :
					String.valueOf(currentKiosk.getLogoutTimeoutPeriod()), isIdleCacheTimeoutValid ? String.valueOf(Long.parseLong(idleCacheTimeoutPeriod) * 1000) : String.valueOf(currentKiosk.getIdleCacheTimeout()),
					isForceCacheTimeoutValid ? String.valueOf(Long.parseLong(forceCacheTimeoutPeriod) * 1000) : String.valueOf(currentKiosk.getForceCacheTimout()), isScreenSaverTimeoutValid ?
					String.valueOf(Long.parseLong(screenSaverTimeoutPeriod) * 1000) : String.valueOf(currentKiosk.getScreenSaverTimeout()), currentKiosk.getID()))));
			((Stage) btnClose.getScene().getWindow()).close();
		} else {
			locationInvalid.setVisible(true);
		}

		if (rows == 1) {
			cache.cacheKiosksFromDB();
			App.startLogoutTimer();
			App.startIdleTimer();
			App.startForceUpdateTimer();
			App.startScreenSaverTimer();
		} else if (rows != 0){
			log.error("SQL update affected more than 1 row.");
		}
	}

	@FXML
	private void autocomplete() {sf.applyAutocomplete(kioskLocationText, autoCompletePopup);}
}
