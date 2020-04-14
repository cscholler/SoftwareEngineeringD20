package edu.wpi.leviathans;

import edu.wpi.leviathans.services.db.DatabaseService;
import edu.wpi.leviathans.views.mapViewer.MapApp;

public class Main {

	public static void main(String[] args) {
		MapApp.launch(MapApp.class, args);
		DatabaseService dbService = new DatabaseService();
		dbService.stopService();
	}
}
