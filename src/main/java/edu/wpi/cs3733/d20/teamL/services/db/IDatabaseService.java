package edu.wpi.cs3733.d20.teamL.services.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Properties;

public interface IDatabaseService {
	void startService();

	void stopService();

	void connect(Properties props);

	void disconnect();

	ResultSet executeQuery(String query, ArrayList<String> values);

	ResultSet executeQuery(String query);

	ArrayList<ResultSet> executeQueries(ArrayList<String> queries, ArrayList<ArrayList<String>> valuesList);

	ArrayList<ResultSet> executeQueries(ArrayList<String> queries);

	int executeUpdate(String update, ArrayList<String> values);

	int executeUpdate(String update);

	int executeUpdates(ArrayList<String> updates, ArrayList<ArrayList<String>> valuesList);

	int executeUpdates(ArrayList<String> updates);

	PreparedStatement fillPreparedStatement(String query, ArrayList<String> values);

	void buildDatabase();

	void populateFromCSV(String csvFile, String update);

	void dropTables();

	ArrayList<String> getColumnNames(ResultSet resSet);

	ArrayList<ArrayList<String>> getTableFromResultSet(ResultSet resSet);

	void collectUsedResultSet(ResultSet resSet);

	void collectUsedResultSets(ArrayList<ResultSet> resSets);

	void collectUsedStatement(Statement stmt);

	void collectUsedStatements(ArrayList<Statement> stmt);
}
