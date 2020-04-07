package edu.wpi.leviathans;

import edu.wpi.leviathans.services.db.DatabaseService;

import java.util.Properties;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        // Uncomment for use with JavaFX
        // App.launch(App.class, args);

        // TODO: fix scanner not accepting input
        Scanner scanner = new Scanner(System.in);
        String username = "";
        String password = "";
        String museumName = "";
        String newPhoneNumber = "";
        int programMode = 4;
        boolean foo = true;
        if (args.length >= 1) {
            username = args[0];
            if (args.length >= 2) {
                password = args[1];
                if (args.length >= 3) {
                    museumName = args[2];
                } else {
                    System.out.println("Please enter your password:");
                    password = scanner.nextLine();
                }
            } else {
                System.out.println("Please enter your password:");
                password = scanner.nextLine();
                System.out.print(
                        "\nPlease enter the number indicating what mode you'd like the program to run the program in:");
                programMode = Integer.parseInt(scanner.nextLine());
            }
        } else {
            System.out.print("Please enter your username:");
            username = scanner.nextLine();
            System.out.println("Please enter your password:");
            password = scanner.nextLine();
            System.out.print("Please enter the number indicating what mode you'd like the program to run the program in:");
            programMode = Integer.parseInt(scanner.nextLine());
        }

        if (programMode == 3) {
            System.out.println("Please enter the name of the museum to change the phone number of:");
            museumName = scanner.nextLine();
        }
        Properties props = new Properties();
        props.put("user", username);
        props.put("password", password);

        DatabaseService dbService = new DatabaseService(props);
        dbService.buildTestDB();
        handleUserRequest(programMode, dbService, museumName);
        dbService.disconnect();
    }

    private static void handleUserRequest(
            int programMode, DatabaseService dbService, String museumName) {
        switch (programMode) {
            case 1: {
                dbService.reportMuseumInfo();
            }
            break;
            case 2: {
                dbService.reportPaintingInfo();
            }
            break;
            case 3: {
                dbService.setPhoneNumber(museumName);
            }
            break;
            case 4:
            default: {
                System.exit(0);
            }
        }
    }
}
