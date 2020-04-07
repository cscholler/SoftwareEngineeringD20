package edu.wpi.leviathans.services.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
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

	public ResultSet executeQuery(String sqlQuery, Object... values) {
		try {
			PreparedStatement statement = connection.prepareStatement(sqlQuery);
			for (int i = 0; i < values.length; i++) {
				statement.setObject(i, values[i]);
			}
			return statement.executeQuery();
		} catch (SQLException ex) {
			log.error("SQLException", ex);
			return null;
		}
	}

	public Collection<ResultSet> executeQueries(
			Collection<String> sqlQueries, Collection<Object>... valuesList) {
		Collection<ResultSet> resultSets = new ArrayList<>();
		ResultSet rs;
		int i = 0;
		for (String query : sqlQueries) {
			rs = executeQuery(query, valuesList[i]);
			resultSets.add(rs);
			i++;
		}
		return resultSets;
	}

	public int executeUpdate(String sqlUpdate, Object... values) {
		try {
			PreparedStatement statement = connection.prepareStatement(sqlUpdate);
			for (int i = 0; i < values.length; i++) {
				statement.setObject(i, values[i]);
			}
			return statement.executeUpdate();
		} catch (SQLException ex) {
			log.error("SQLException", ex);
			return 0;
		}
	}

	public Collection<Integer> executeUpdates(
			Collection<String> sqlUpdates, Collection<Object>... valuesList) {
		Collection<Integer> totalAffectedRows = new ArrayList<>();
		int currentAffectedRows = 0;
		int j = 0;

		for (String update : sqlUpdates) {
			currentAffectedRows = executeUpdate(update, valuesList[j]);
			totalAffectedRows.add(currentAffectedRows);
			j++;
		}
		return totalAffectedRows;
	}

	public void disconnect() {
		try {
			connection.commit();
			connection.close();
		} catch (SQLException ex) {
			log.error("SQLException", ex);
		}
	}

	public void buildTestDB() {
		Collection<String> testTables = new ArrayList<>();
		testTables.add(DBConstants.createMuseumsTable);
		testTables.add(DBConstants.createPaintingsTable);
		Collection<String> addMuseums = new ArrayList<>();
		for (int i = 0; i < 5; i++) {
			addMuseums.add(DBConstants.addMuseum);
		}
		Collection<String> addPaintings = new ArrayList<>();
		for (int i = 0; i < 20; i++) {
			addPaintings.add(DBConstants.addPainting);
		}
		testTables.add(DBConstants.addMuseum);
		executeUpdates(testTables, null);
		log.info("Created tables");
	}

	public void reportMuseumInfo() {
	}

	public void reportPaintingInfo() {
	}

	public void setPhoneNumber(String museumName) {
		executeUpdate(DBConstants.updateMuseumPhone);
	}
}
