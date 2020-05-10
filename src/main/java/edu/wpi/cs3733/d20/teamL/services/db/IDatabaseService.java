package edu.wpi.cs3733.d20.teamL.services.db;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;

public interface IDatabaseService {
	void connect();

	ResultSet executeQuery(SQLEntry query);

	ArrayList<ResultSet> executeQueries(ArrayList<SQLEntry> queries);

	int executeUpdate(SQLEntry update);

	ArrayList<Integer> executeUpdates(ArrayList<SQLEntry> updates);

	PreparedStatement fillPreparedStatement(SQLEntry entry);

	void rebuildDatabase();

	void populateFromCSV(String csvFile, String tableName);

	void populateFromCSV(File csvFile, String tableName, boolean append) throws SQLException;

	void dropTables();

	ArrayList<String> getColumnNames(ResultSet resSet);

	ArrayList<ArrayList<String>> getTableFromResultSet(ResultSet resSet);

	Map<String, ArrayList<String>> getTableUpdateMappings();
}

