package edu.wpi.leviathans.services.db;

public class DBConstants {
  // Create museums table in test database
  public static final String createMuseumsTable =
      "CREATE TABLE Museums( "
          + "id INT NOT NULL GENERATED ALWAYS AS IDENTITY, "
          + "name VARCHAR(64), "
          + "location VARCHAR(64), "
          + "phone_number VARCHAR(24), "
          + "year_established VARCHAR(4), "
          + "PRIMARY KEY (id))";

  // Create paintings table in test database
  public static final String createPaintingsTable =
      "CREATE TABLE Paintings( "
          + "id INT NOT NULL GENERATED ALWAYS AS IDENTITY, "
          + "name VARCHAR(64), "
          + "museum_name VARCHAR, "
          + "artist VARCHAR(64), "
          + "year_painted VARCHAR(4), "
          + "PRIMARY KEY (id))";

  // Add new museum to test database
  public static final String addMuseum =
      "INSERT INTO Museums(name, location, phone_number, year_established)" + "VALUES(?, ?, ?, ?)";

  // Add new painting to test database
  public static final String addPainting =
      "INSERT INTO Paintings(name, museum_name, artist, year_painted)" + "VALUES(?, ?, ?, ?)";

  // Select all museums in the table
  public static final String selectAllMuseums = "SELECT *" + "FROM Museums";

  // Select all paintings in the table
  public static final String selectAllPaintings = "SELECT * " + "FROM Paintings";

  // Upyear the phone number of a given museum
  public static final String updateMuseumPhone =
      "INSERT INTO Museums(phone_number)" + "VALUES(?)" + "WHERE name = ?";
}
