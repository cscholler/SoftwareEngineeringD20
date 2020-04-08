package edu.wpi.leviathans.services.db;

import java.sql.*;
import java.util.Properties;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DatabaseService {
	private Connection connection;

	public DatabaseService(Properties props) {
		connect(props);
	}

	private void connect(Properties props) {
		try {
			Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
		} catch (ClassNotFoundException ex) {
			log.error("ClassNotFoundException", ex);
			return;
		}

		try {
			connection = DriverManager.getConnection("jdbc:derby:memory:myDB;create=true", props);
		} catch (SQLException ex) {
			log.error("SQLException", ex);
		}
	}

	public void disconnect() {
		try {
			if (connection != null) {
				connection.commit();
				connection.close();
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}

	public void buildTestDB() {
		String query;
		ResultSet rs;
		try {
			Statement stmt = connection.createStatement();
			query = DBConstants.createMuseumsTable;
			stmt.execute(query);
			query = DBConstants.createPaintingsTable;
			stmt.execute(query);
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}

	public void handleUserRequest(int programMode, String museumName, String newPhoneNumber) {
		switch (programMode) {
			case 1: {
				reportMuseumInfo();
			}
			break;
			case 2: {
				reportPaintingInfo();
			}
			break;
			case 3: {
				setPhoneNumber(museumName, newPhoneNumber);
			}
			break;
			case 4:
			default: {
				disconnect();
				System.exit(0);
			}
		}
	}

	private void reportMuseumInfo() {
	}

	private void reportPaintingInfo() {
	}

	private void setPhoneNumber(String museumName, String newPhoneNumber) {
	}
}
