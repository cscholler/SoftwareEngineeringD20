package edu.wpi.leviathans.services.db;

public class DBConstants {
	public static final String addMuseumsTable =
			"CREATE TABLE Museums( "
					+ "id INT NOT NULL GENERATED ALWAYS AS IDENTITY, "
					+ "name VARCHAR(64), "
					+ "location VARCHAR(64), "
					+ "PRIMARY KEY (id))";

	public static final String addPaintingsTable =
			"CREATE TABLE Paintings( "
					+ "id INT NOT NULL GENERATED ALWAYS AS IDENTITY, "
					+ "name VARCHAR(64), "
					+ "location VARCHAR(64), "
					+ "PRIMARY KEY (id))";
}
