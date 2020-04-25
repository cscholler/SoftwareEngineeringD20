package edu.wpi.cs3733.d20.teamL.services.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
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

	@Override
	protected void startService() {
		if (connection == null) {
			connect(props);
		}
		// Uncomment if database needs to be rebuilt
		rebuildDatabase();
	}

	@Override
	public void stopService() {
		disconnect();
	}

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

	@Override
	public ArrayList<ResultSet> executeQueries(ArrayList<SQLEntry> queries) {
		ArrayList<ResultSet> resSets = new ArrayList<>();
		for (SQLEntry query : queries) {
			resSets.add(executeQuery(query));
		}
		return resSets;
	}

	@Override
	public int executeUpdate(SQLEntry update) {
		int numRowsAffected = 0;
		try {
			if (update.getValues().size() == 0) {
				Statement stmt;
				stmt = connection.createStatement();
				numRowsAffected = stmt.executeUpdate(update.getStatement());
			} else {
				PreparedStatement pStmt = fillPreparedStatement(update);
				System.out.println(update.getStatement() + "\n" + update.getValues());
				numRowsAffected = pStmt.executeUpdate();
			}
		} catch (SQLException ex) {
			log.error("Encountered SQLException.", ex);
		}
		return numRowsAffected;
	}

	@Override
	public ArrayList<Integer> executeUpdates(ArrayList<SQLEntry> updates) {
		ArrayList<Integer> affectedRows = new ArrayList<>();
		for (SQLEntry update : updates) {
			affectedRows.add(executeUpdate(update));
		}
		return affectedRows;
	}

	@Override
	public PreparedStatement fillPreparedStatement(SQLEntry entry) {
		PreparedStatement pStmt = null;
		try {
			pStmt = connection.prepareStatement(entry.getStatement());
			for (int i = 0; i < entry.getValues().size(); i++) {
				pStmt.setString(i + 1, entry.getValues().get(i));
			}
		} catch (SQLException ex) {
			log.error("Encountered SQLException.", ex);
		}
		return pStmt;
	}

	@Override
	public void rebuildDatabase() {
		ArrayList<SQLEntry> updates = new ArrayList<>();
		updates.add(new SQLEntry(DBConstants.createNodeTable, new ArrayList<>()));
		updates.add(new SQLEntry(DBConstants.createEdgeTable, new ArrayList<>()));
		updates.add(new SQLEntry(DBConstants.createDoctorTable, new ArrayList<>()));
		updates.add(new SQLEntry(DBConstants.createPatientTable, new ArrayList<>()));
		updates.add(new SQLEntry(DBConstants.createMedicationRequestTable, new ArrayList<>()));
		updates.add(new SQLEntry(DBConstants.createUserTable, new ArrayList<>()));
		executeUpdates(updates);
		dropTables();
		populateFromCSV("MapLnodesFloor2", DBConstants.addNode);
		populateFromCSV("MapLedgesFloor2", DBConstants.addEdge);
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

	@Override
	public void dropTables() {
		ResultSet resSet;
		ArrayList<String> dropTableUpdates = new ArrayList<>();
		ArrayList<String> tablesToDrop = new ArrayList<>();
		ArrayList<SQLEntry> updates = new ArrayList<>();
		dropTableUpdates.add(DBConstants.dropNodeTable);
		dropTableUpdates.add(DBConstants.dropEdgeTable);
		dropTableUpdates.add(DBConstants.dropDoctorTable);
		dropTableUpdates.add(DBConstants.dropPatientTable);
		dropTableUpdates.add(DBConstants.dropMedicationRequestTable);
		dropTableUpdates.add(DBConstants.dropUserTable);
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