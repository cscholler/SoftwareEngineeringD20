package edu.wpi.leviathans;

import edu.wpi.leviathans.services.db.DatabaseService;
import javafx.application.Application;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class App extends Application {

	@Override
	public void init() {
		log.info("Starting Up");
	}

	@Override
	public void start(Stage primaryStage) {
		DatabaseService dbService = new DatabaseService();
		dbService.executeQuery(DatabaseService.DBQueryConstants.addMuseumsTable);
		dbService.executeQuery(DatabaseService.DBQueryConstants.addPaintingsTable);
	}

	@Override
	public void stop() {
		log.info("Shutting Down");
	}
}
