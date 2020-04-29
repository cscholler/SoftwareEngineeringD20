package edu.wpi.cs3733.d20.teamL.services.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;

import lombok.extern.slf4j.Slf4j;

import edu.wpi.cs3733.d20.teamL.util.io.CSVHelper;
import edu.wpi.cs3733.d20.teamL.services.Service;

@Slf4j
public class DatabaseService extends Service implements IDatabaseService {
	private Connection connection;
	private Properties props = null;

	public DatabaseService(Properties props) {
		super();
		this.serviceName = DBConstants.SERVICE_NAME;
		this.props = props;
	}

	public DatabaseService() {
		super();
		this.serviceName = DBConstants.SERVICE_NAME;
	}

	/**
	 * Starts the database service
	 */
	@Override
	public void startService() {
		if (connection == null) {
			connect(props);
		}
		// Uncomment if database needs to be rebuilt
		rebuildDatabase();
	}

	/**
	 * Stops the database service
	 */
	@Override
	public void stopService() {
		disconnect();
	}

	/**
	 * Connects to the database with optional username and password
	 *
	 * @param props Username and password properties
	 */
	@Override
	public void connect(Properties props) {
		try {
			Class.forName(DBConstants.DB_DRIVER);
		} catch (ClassNotFoundException ex) {
			log.error("ClassNotFoundException", ex);
			return;
		}
		try {
			if (props != null) {
				connection = DriverManager.getConnection(DBConstants.DB_URL, props);
			} else {
				connection = DriverManager.getConnection(DBConstants.DB_URL);
			}
			log.info("Connection established.");
		} catch (SQLException ex) {
			log.error("Encountered SQLException.", ex);
		}
	}

	/**
	 * Disconnects from the database if currently connected
	 */
	protected void disconnect() {
		try {
			if (connection != null) {
				connection.commit();
				connection.close();
				connection = null;
			}
			log.info("Connection closed.");
		} catch (SQLException ex) {
			log.error("SQL Exception", ex);
		}
	}

	/**
	 * Executes an SQL query
	 *
	 * @param query The query to be executed, including its values if the query is a prepared statement
	 * @return A result set containing the results of the query
	 */
	@Override
	public ResultSet executeQuery(SQLEntry query) {
		ResultSet resSet = null;
		try {
			if (query.getValues().size() == 0) {
				Statement stmt;
				stmt = connection.createStatement();
				resSet = stmt.executeQuery(query.getStatement());
			} else {
				PreparedStatement pStmt = fillPreparedStatement(query);
				resSet = pStmt.executeQuery();
			}
		} catch (SQLException ex) {
			log.error("Encountered SQLException.", ex);
		}
		return resSet;
	}

	/**
	 * Executes a list of SQL queries
	 *
	 * @param queries A list of queries to be executed, including their values if they are prepared statements
	 * @return A list of result sets containing the results of each query
	 */
	@Override
	public ArrayList<ResultSet> executeQueries(ArrayList<SQLEntry> queries) {
		ArrayList<ResultSet> resSets = new ArrayList<>();
		for (SQLEntry query : queries) {
			resSets.add(executeQuery(query));
		}
		return resSets;
	}

	/**
	 * Executes an SQL update
	 *
	 * @param update The update to be executed, including its values if the update is a prepared statement
	 * @return The number of rows affected by the update
	 */
	@Override
	public int executeUpdate(SQLEntry update) {
		int numRowsAffected = 0;
		try {
			if (update.getValues().size() == 0) {
				Statement stmt;
				stmt = connection.createStatement();
				numRowsAffected = stmt.executeUpdate(update.getStatement());
				stmt.close();
			} else {
				PreparedStatement pStmt = fillPreparedStatement(update);
				numRowsAffected = pStmt.executeUpdate();
				pStmt.close();
			}
		} catch (SQLException ex) {
			log.error("Encountered SQLException.", ex);
		}
		return numRowsAffected;
	}

	/**
	 * Executes a list of SQL updates
	 *
	 * @param updates A list of updates to be executed, including their values if they are prepared statements
	 * @return A list of the number of rows affected by each update in the list
	 */
	@Override
	public ArrayList<Integer> executeUpdates(ArrayList<SQLEntry> updates) {
		ArrayList<Integer> affectedRows = new ArrayList<>();
		for (SQLEntry update : updates) {
			affectedRows.add(executeUpdate(update));
		}
		return affectedRows;
	}

	/**
	 * Fills a prepared statement with the supplied values
	 *
	 * @param command The SQL command to be processed
	 * @return A fully processed prepared statement ready to be executed
	 */
	@Override
	public PreparedStatement fillPreparedStatement(SQLEntry command) {
		PreparedStatement pStmt = null;
		if (command.getValues().size() == 0) {
			// TODO: replace with custom exception
			throw new IllegalArgumentException();
		}
		try {
			pStmt = connection.prepareStatement(command.getStatement());
			for (int i = 0; i < command.getValues().size(); i++) {
				pStmt.setString(i + 1, command.getValues().get(i));
			}
		} catch (SQLException ex) {
			log.error("Encountered SQLException.", ex);
		}
		return pStmt;
	}

	/**
	 * Rebuilds the database by dropping and re-creating all tables then adding the default nodes and edges from CSV files
	 */
	@Override
	public void rebuildDatabase() {
		dropTables();
		ArrayList<SQLEntry> updates = new ArrayList<>();
		updates.add(new SQLEntry(DBConstants.CREATE_NODE_TABLE));
		updates.add(new SQLEntry(DBConstants.CREATE_EDGE_TABLE));
		updates.add(new SQLEntry(DBConstants.CREATE_USER_TABLE));
		updates.add(new SQLEntry(DBConstants.CREATE_DOCTOR_TABLE));
		updates.add(new SQLEntry(DBConstants.CREATE_PATIENT_TABLE));
		updates.add(new SQLEntry(DBConstants.CREATE_GIFT_TABLE));
		updates.add(new SQLEntry(DBConstants.CREATE_GIFT_DELIVERY_REQUEST_TABLE));
		updates.add(new SQLEntry(DBConstants.CREATE_MEDICATION_REQUEST_TABLE));
		updates.add(new SQLEntry(DBConstants.CREATE_SERVICE_REQUEST_TABLE));
		executeUpdates(updates);
		populateFromCSV("MapLAllNodes", DBConstants.ADD_NODE);
		populateFromCSV("MapLAllEdges", DBConstants.ADD_EDGE);

		executeUpdate(new SQLEntry(DBConstants.ADD_USER, new ArrayList<>(Arrays.asList("Admin", "Admin", "admin", "admin", "3", null, null))));
		executeUpdate(new SQLEntry(DBConstants.ADD_USER, new ArrayList<>(Arrays.asList("Nurse", "Joy", "nurse", "nurse", "1", null, null))));
		executeUpdate(new SQLEntry(DBConstants.ADD_USER, new ArrayList<>(Arrays.asList("Staff", "Member", "staff", "staff", "0", null, null))));
		executeUpdate(new SQLEntry(DBConstants.ADD_USER, new ArrayList<>(Arrays.asList("Wilson", "Wong", "doctor", "doctor", "2", null, null))));

		executeUpdate(new SQLEntry(DBConstants.ADD_DOCTOR, new ArrayList<>(Arrays.asList("123", "Wilson", "Wong", "doctor", null, null))));
		executeUpdate(new SQLEntry(DBConstants.ADD_PATIENT, new ArrayList<>(Arrays.asList("456", "Conrad", "Tulig", "123", null, null))));

		executeUpdate(new SQLEntry(DBConstants.ADD_GIFT, new ArrayList<>(Arrays.asList("Flower", "Roses", "A vase of 7 roses", "100"))));
		executeUpdate(new SQLEntry(DBConstants.ADD_GIFT, new ArrayList<>(Arrays.asList("Flower", "Tulips", "A vase of 10 tulip", "100"))));
		executeUpdate(new SQLEntry(DBConstants.ADD_GIFT, new ArrayList<>(Arrays.asList("Flower", "Dandelion", "A vase of 12 dandelions", "100"))));

		executeUpdate(new SQLEntry(DBConstants.ADD_GIFT, new ArrayList<>(Arrays.asList("Toys", "Building blocks", "A container with ", "10"))));
		executeUpdate(new SQLEntry(DBConstants.ADD_GIFT, new ArrayList<>(Arrays.asList("Toys", "Play-Do", "A package of 6 Play-do colors in different containers", "10"))));
		executeUpdate(new SQLEntry(DBConstants.ADD_GIFT, new ArrayList<>(Arrays.asList("Toys", "Hot Wheels", "A set of 10 Hot Wheels as well as a small race track.", "10"))));

		executeUpdate(new SQLEntry(DBConstants.ADD_GIFT, new ArrayList<>(Arrays.asList("Books", "LOTR", "The three Lord of the Rings books from the trilogy", "5"))));
		executeUpdate(new SQLEntry(DBConstants.ADD_GIFT, new ArrayList<>(Arrays.asList("Books", "Harry Potter", "The entire Harry Potter series", "3"))));
		executeUpdate(new SQLEntry(DBConstants.ADD_GIFT, new ArrayList<>(Arrays.asList("Books", "Inheritance", "The 4 books from the Inheritance cycle series", "1"))));

		executeUpdate(new SQLEntry(DBConstants.ADD_GIFT, new ArrayList<>(Arrays.asList("Movies", "LOTR", "The three Lord of the Rings movies from the trilogy", "100"))));
		executeUpdate(new SQLEntry(DBConstants.ADD_GIFT, new ArrayList<>(Arrays.asList("Movies", "Star Wars", "All 6 Canon Star Wars movies", "100"))));
		executeUpdate(new SQLEntry(DBConstants.ADD_GIFT, new ArrayList<>(Arrays.asList("Movies", "Pulp Fiction", "A copy of the movie Pulp Fiction", "3"))));

	}

	@Override
	public void populateFromCSV(String csvFile, String statement) {
		ArrayList<SQLEntry> updates = new ArrayList<>();
		CSVHelper csvReader = new CSVHelper();
		for (ArrayList<String> row : csvReader.readCSVFile(csvFile, true)) {
			updates.add(new SQLEntry(statement, new ArrayList<>(row)));
		}
		executeUpdates(updates);
	}

	/**
	 * Drops all the tables in the database if they currently exist
	 */
	@Override
	public void dropTables() {
		ResultSet resSet;
		ArrayList<String> dropTableUpdates = new ArrayList<>();
		ArrayList<String> tablesToDrop = new ArrayList<>();
		ArrayList<SQLEntry> updates = new ArrayList<>();
		dropTableUpdates.add(DBConstants.DROP_NODE_TABLE);
		dropTableUpdates.add(DBConstants.DROP_EDGE_TABLE);
		dropTableUpdates.add(DBConstants.DROP_USER_TABLE);
		dropTableUpdates.add(DBConstants.DROP_DOCTOR_TABLE);
		dropTableUpdates.add(DBConstants.DROP_PATIENT_TABLE);
		dropTableUpdates.add(DBConstants.DROP_GIFT_TABLE);
		dropTableUpdates.add(DBConstants.DROP_GIFT_DELIVER_REQUEST_TABLE);
		dropTableUpdates.add(DBConstants.DROP_MEDICATION_REQUEST_TABLE);
		dropTableUpdates.add(DBConstants.DROP_SERVICE_REQUEST_TABLE);
		try {
			for (int i = 0; i < DBConstants.GET_TABLE_NAMES().size(); i++) {
				resSet = connection.getMetaData().getTables(null, "APP", DBConstants.GET_TABLE_NAMES().get(i).toUpperCase(), null);
				if (resSet.next()) {
					tablesToDrop.add(dropTableUpdates.get((dropTableUpdates.size() - 1) - i));
				}
			}
			for (String entry : tablesToDrop) {
				updates.add(new SQLEntry(entry));
			}
			executeUpdates(updates);
		} catch (SQLException ex) {
			log.error("Encountered SQLException.", ex);
		}
	}

	/**
	 * Determines the titles of the columns of a table based on a result set
	 *
	 * @param resSet The result set that determines the table to find the column titles of
	 * @return A list of the titles of the columns in the table
	 */
	@Override
	public ArrayList<String> getColumnNames(ResultSet resSet) {
		ArrayList<String> colLabels = new ArrayList<>();
		try {
			ResultSetMetaData resSetMD = resSet.getMetaData();
			for (int i = 0; i < resSetMD.getColumnCount(); i++) {
				colLabels.set(i, resSetMD.getColumnLabel(i + 1));
			}
		} catch (SQLException ex) {
			log.error("Encountered SQLException.", ex);
		}
		return colLabels;
	}

	/**
	 * Converts a result set into a table
	 *
	 * @param resSet The result set to build the table from
	 * @return The table represented by a list of lists of strings
	 */
	@Override
	public ArrayList<ArrayList<String>> getTableFromResultSet(ResultSet resSet) {
		ArrayList<ArrayList<String>> table = new ArrayList<>();
		try {
			while (resSet.next()) {
				ResultSetMetaData resSetMD = resSet.getMetaData();
				int totalCols = resSetMD.getColumnCount();
				ArrayList<String> row = new ArrayList<>();
				for (int i = 0; i < totalCols; i++) {
					row.add(resSet.getString(i + 1));
				}
				table.add(row);
			}
		} catch (SQLException ex) {
			log.error("Encountered SQLException.", ex);
		}
		return table;
	}
}