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
    if (args.length >= 1) {
      username = args[0];
      if (args.length >= 2) {
        password = args[1];
      } else {
        System.out.println("Please enter your password: ");
        password = scanner.next();
      }
    } else {
      System.out.print("Please enter your username: ");
      username = scanner.next();
      System.out.print("\nPlease enter your password: ");
      password = scanner.next();
    }

    Properties props = new Properties();
    props.put("user", username);
    props.put("password", password);

    DatabaseService dbService = new DatabaseService(props);
    dbService.buildTestDB();
  }
}
