package edu.wpi.leviathans;

import edu.wpi.leviathans.services.db.DatabaseService;

public class Main {

	public static void main(String[] args) {
		App.launch(App.class, args);
		DatabaseService dbService = new DatabaseService();
		dbService.stopService();
	}
}
