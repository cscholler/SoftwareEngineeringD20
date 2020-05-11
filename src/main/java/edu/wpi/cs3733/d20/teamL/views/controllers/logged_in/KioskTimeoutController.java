package edu.wpi.cs3733.d20.teamL.views.controllers.logged_in;

import java.util.ArrayList;
import java.util.Arrays;

import edu.wpi.cs3733.d20.teamL.util.FXMLLoaderFactory;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.paint.Color;

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
    @FXML
    private Spinner logoutTimeoutText, idleCacheTimeoutText, forceCacheTimeoutText, screenSaverTimeoutText;
	@FXML
	private JFXButton btnClose;
	//@FXML
	//private Label confirmationLabel;
	@Inject
	private IDatabaseService db;
    @Inject
	private IDatabaseCache cache;

    @FXML
    private void initialize() {
		// Hard coded to only kiosk in database. Could be changed to find kiosk by ip.
    	currentKiosk = cache.getKioskCache().get(0);
    	ArrayList<String> results = db.getTableFromResultSet(db.executeQuery(new SQLEntry(DBConstants.SELECT_ALL_KIOSK_SETTINGS))).get(0);
    	logoutTimeoutText.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(30,300,Integer.parseInt(results.get(2))));
    	idleCacheTimeoutText.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(15,120,Integer.parseInt(results.get(3))));
    	forceCacheTimeoutText.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(30,300,Integer.parseInt(results.get(4))));
    	screenSaverTimeoutText.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(30,600,Integer.parseInt(results.get(5))));
    	//TODO: show current values somewhere
	}

	@FXML
	private void btnClosePressed() {
		Stage stage = (Stage) btnClose.getScene().getWindow();
		stage.close();
	}

	@FXML
	private void btnSavePressed() {
		String logoutTimeoutPeriod = (String) logoutTimeoutText.getValue();
		String idleCacheTimeoutPeriod = (String) idleCacheTimeoutText.getValue();
		String forceCacheTimeoutPeriod = (String) forceCacheTimeoutText.getValue();
		String screenSaverTimeoutPeriod = (String) screenSaverTimeoutText.getValue();
		boolean isLogoutTimeoutValid = !logoutTimeoutPeriod.isEmpty() && StringUtils.isNumeric(logoutTimeoutPeriod) && Long.parseLong(logoutTimeoutPeriod) * 1000 >= 300000;
		boolean isIdleCacheTimeoutValid = !idleCacheTimeoutPeriod.isEmpty() && StringUtils.isNumeric(idleCacheTimeoutPeriod) && Long.parseLong(logoutTimeoutPeriod) * 1000 >= 150000;
		boolean isForceCacheTimeoutValid = !forceCacheTimeoutPeriod.isEmpty() && StringUtils.isNumeric(forceCacheTimeoutPeriod) && Long.parseLong(logoutTimeoutPeriod) * 1000 >= 300000;
		boolean isScreenSaverTimeoutValid = !screenSaverTimeoutPeriod.isEmpty() && StringUtils.isNumeric(screenSaverTimeoutPeriod)  && Long.parseLong(logoutTimeoutPeriod) * 1000 >= 300000;;
		int rows = db.executeUpdate(new SQLEntry(DBConstants.UPDATE_KIOSK_TIMEOUTS, new ArrayList<>(Arrays.asList(isLogoutTimeoutValid ? String.valueOf(Long.parseLong(logoutTimeoutPeriod) * 1000) :
				String.valueOf(currentKiosk.getLogoutTimeoutPeriod()), isIdleCacheTimeoutValid ? String.valueOf(Long.parseLong(idleCacheTimeoutPeriod) * 1000) : String.valueOf(currentKiosk.getIdleCacheTimeout()),
				isForceCacheTimeoutValid ? String.valueOf(Long.parseLong(forceCacheTimeoutPeriod)) : String.valueOf(currentKiosk.getForceCacheTimout()), isScreenSaverTimeoutValid ?
				String.valueOf(Long.parseLong(screenSaverTimeoutPeriod)) : String.valueOf(currentKiosk.getScreenSaverTimeout()), currentKiosk.getID()))));
		if (rows == 0) {
			//confirmationLabel.setTextFill(Color.RED);
			//confirmationLabel.setText("Submission failed");
		} else if (rows == 1) {
			//confirmationLabel.setTextFill(Color.BLACK);
			//confirmationLabel.setText("Timeouts updated");
			cache.cacheKiosksFromDB();
		} else {
			log.error("SQL update affected more than 1 row.");
		}
		//loaderFactory.showAndFade(confirmationLabel);
	}
}
