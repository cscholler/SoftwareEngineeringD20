package edu.wpi.cs3733.d20.teamL.services.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Properties;

public interface IDatabaseService {

	void connect(Properties props);

	ResultSet executeQuery(SQLEntry query);

	ArrayList<ResultSet> executeQueries(ArrayList<SQLEntry> queries);

	int executeUpdate(SQLEntry update);

	ArrayList<Integer> executeUpdates(ArrayList<SQLEntry> updates);

	PreparedStatement fillPreparedStatement(SQLEntry entry);

	void rebuildDatabase();

	void populateFromCSV(String csvFile, String update);

	void dropTables();

	ArrayList<String> getColumnNames(ResultSet resSet);

	ArrayList<ArrayList<String>> getTableFromResultSet(ResultSet resSet);
}
