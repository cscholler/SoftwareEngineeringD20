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
                    + "museum_id INT REFERENCES Museums(id), "
                    + "artist VARCHAR(64), "
                    + "date_painted DATE, "
                    + "PRIMARY KEY (id))";

    // Add new museum to test database
    public static final String addMuseum =
            "INSERT INTO Museums(name, location, phone_number, date_established)" + "VALUES(?, ?, ?, ?)";

    // Add new painting to test database
    public static final String addPainting =
            "INSERT INTO Paintings(name, museum_id, artist, date_painted)" + "VALUES(?, ?, ?, ?)";

    // Select all museums in the table
    public static final String selectAllMuseums =
            "SELECT *" + "FROM Museums";

    // Select all paintings in the table
    public static final String selectAllPaintings =
            "SELECT * " + "FROM Paintings";

    // Update the phone number of a given museum
    public static final String updateMuseumPhone =
            "INSERT INTO Museums(phone_number)" + "VALUES(?)" + "WHERE name = ?";
}
