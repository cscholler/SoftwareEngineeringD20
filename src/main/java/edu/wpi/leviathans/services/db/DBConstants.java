package edu.wpi.leviathans.services.db;

public class DBConstants {
  // Create museums table in test database
  public static final String createMuseumsTable =
      "CREATE TABLE Museums( "
          + "id INT NOT NULL GENERATED ALWAYS AS IDENTITY, "
          + "name VARCHAR(64), "
          + "location VARCHAR(64), "
          + "phone_number VARCHAR(16), "
          + "data_established DATE, "
          + "PRIMARY KEY (id))";

  // Create paintings table in test database
  public static final String createPaintingsTable =
      "CREATE TABLE Paintings( "
          + "id INT NOT NULL GENERATED ALWAYS AS IDENTITY, "
          + "name VARCHAR(64), "
          // TODO: make foreign key
          + "museum_id INT, "
          + "artist VARCHAR(64), "
          + "date_painted DATE, "
          + "PRIMARY KEY (id))";

  // Add new museum to test database
  public static final String addMuseum =
      "INSERT INTO Museums(name, location, phone_number, date_established)" + "VALUES(?, ?, ?, ?)";

  // Add new painting to test database
  public static final String addPainting =
      "INSERT INTO Paintings(name, museum_id, artist, date_painted)" + "VALUES(?, ?, ?, ?)";
}