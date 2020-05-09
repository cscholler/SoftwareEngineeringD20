package edu.wpi.cs3733.d20.teamL.views.controllers.dialogues;

import com.jfoenix.controls.JFXButton;
import edu.wpi.cs3733.d20.teamL.services.db.IDatabaseService;
import edu.wpi.cs3733.d20.teamL.util.AsyncTaskManager;
import javafx.fxml.FXML;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;

@Slf4j
public class RebuildDatabasePopupController {
	@FXML
	private JFXButton btnNo;
	@Inject
	private IDatabaseService db;

	@FXML
	public void btnYesClicked() {
		Stage stage = (Stage) btnNo.getScene().getWindow();
		stage.close();
		log.warn("Rebuilding database");
		AsyncTaskManager.startTaskWithPopup(db::rebuildDatabase, "Rebuilding database...", "Finished rebuilding database");
	}

	public void btnNoClicked() {
		Stage stage = (Stage) btnNo.getScene().getWindow();
		stage.close();
	}
}
