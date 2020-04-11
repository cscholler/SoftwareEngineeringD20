package edu.wpi.leviathans;

import edu.wpi.leviathans.services.db.DatabaseService;

import java.util.Properties;
import java.util.Scanner;

public class Main {

	public static void main(String[] args) {
		// Uncomment for use with JavaFX
		App.launch(App.class, args);

		Scanner scanner = new Scanner(System.in);
		String username = "";
		String password = "";
		String museumName = "";
		String newPhoneNumber = "";
		int programMode = 4;

		if (args.length >= 1) {
			username = args[0];
			if (args.length >= 2) {
				password = args[1];
				if (args.length >= 3) {
					programMode = Integer.parseInt(args[2]);
				} else {
					System.out.println("Please enter your password:");
					password = scanner.nextLine();
				}
			} else {
				System.out.println("Please enter your password:");
				password = scanner.nextLine();
				System.out.println("Please enter the number indicating what mode you'd like the program to run in:");
				programMode = scanner.nextInt();
			}
		} else {
			System.out.println("Please enter your username:");
			username = scanner.nextLine();
			System.out.println("Please enter your password:");
			password = scanner.nextLine();
			System.out.println("Please enter the number indicating what mode you'd like the program to run in:");
			programMode = scanner.nextInt();
		}
		System.out.println(username);
		System.out.println(password);
		System.out.println(programMode);
		if (programMode == 3) {
			System.out.println("Please enter the name of the museum to change the phone number of:");
			museumName = scanner.nextLine();
			System.out.println("Please enter the new phone number:");
			newPhoneNumber = scanner.nextLine();
		}

		Properties props = new Properties();
		props.put("user", username);
		props.put("password", password);
		DatabaseService dbService = new DatabaseService(props);
		dbService.buildTestDB();
		dbService.handleUserRequest(programMode, museumName, newPhoneNumber);
		dbService.stopService();
	}
}
