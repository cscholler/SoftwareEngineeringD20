package edu.wpi.leviathans;

import edu.wpi.leviathans.services.db.DatabaseService;

import java.util.Properties;
import java.util.Scanner;

public class Main {

	public static void main(String[] args) {
		// Uncomment for use with JavaFX
		// App.launch(App.class, args);

		Scanner scanner = new Scanner(System.in);
		String username = "";
		String password = "";
		System.out.println("Please enter your username:");
		username = scanner.nextLine();
		System.out.println("Please enter your password:");
		password = scanner.nextLine();

		Properties props = new Properties();
		props.put("user", username);
		props.put("password", password);
		DatabaseService dbService = new DatabaseService(props);
		dbService.stopService();
	}
}
