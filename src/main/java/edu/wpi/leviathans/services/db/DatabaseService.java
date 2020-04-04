package edu.wpi.leviathans.services.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DatabaseService {
	private Connection connection;

	public DatabaseService() {
		connect();
	}

	private void connect() {
		try {
			Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
		} catch (ClassNotFoundException ex) {
			log.error("ClassNotFoundException", ex);
			return;
		}
		try {
			connection = DriverManager.getConnection("jdbc:derby:memory:myDB;create=true");
		} catch (SQLException ex) {
			log.error("SQLException", ex);
		}
	}

	public ResultSet executeQuery(String query) {
		try {
			PreparedStatement statement = connection.prepareStatement(query);
			return statement.executeQuery();

		} catch (SQLException ex) {
			log.error("SQLException", ex);
			return null;
		}
	}

	public void disconnect() {
		try {
			connection.close();
		} catch (SQLException ex) {
			log.error("SQLException", ex);
		}
	}

	public static class DBQueryConstants {
		public static final String addMuseumsTable =
				"CREATE TABLE Museums( "
						+ "id INT NOT NULL GENERATED ALWAYS AS IDENTITY"
						+ "name VARCHAR(64)"
						+ "location VARCHAR(64)"
						+ ""
						+ "PRIMARY KEY (id))";

		public static final String addPaintingsTable =
				"CREATE TABLE Paintings( "
						+ "id INT NOT NULL GENERATED ALWAYS AS IDENTITY"
						+ "name VARCHAR(64)"
						+ "location VARCHAR(64)"
						+ ""
						+ "PRIMARY KEY (id))";
	}
}
