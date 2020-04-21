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
	private ArrayList<ResultSet> usedResSets = new ArrayList<>();
	private ArrayList<Statement> usedStmts = new ArrayList<>();

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
	public void startService() {
		if (connection == null) {
			connect(props);
		}
		// Uncomment if database needs to be rebuilt
		//buildDatabase();
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

	@Override
	public void disconnect() {
		try {
			for (ResultSet resSet : usedResSets) {
				resSet.close();
			}
			for (Statement stmt : usedStmts) {
				stmt.close();
			}
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
	public ResultSet executeQuery(String query, ArrayList<String> values) {
		ResultSet resSet = null;
		try {
			if (values.size() == 0) {
				Statement stmt;
				stmt = connection.createStatement();
				resSet = stmt.executeQuery(query);
			} else {
				PreparedStatement pStmt = fillPreparedStatement(query, values);
				resSet = pStmt.executeQuery();
			}
		} catch (SQLException ex) {
			log.error("Encountered SQLException.", ex);
		}
		return resSet;
	}

	@Override
	public ResultSet executeQuery(String query) {
		return executeQuery(query, new ArrayList<>());
	}

	@Override
	public ArrayList<ResultSet> executeQueries(ArrayList<String> queries, ArrayList<ArrayList<String>> valuesList) {
		ArrayList<ResultSet> resSets = new ArrayList<>();
		boolean isPreparedStmt = !valuesList.isEmpty();

		if (isPreparedStmt && queries.size() != valuesList.size()) {
			throw new IllegalArgumentException();
		}
		for (int i = 0; i < queries.size() + 1; i++) {
			resSets.add(executeQuery(queries.get(i), valuesList.get(i)));
		}
		return resSets;
	}

	@Override
	public ArrayList<ResultSet> executeQueries(ArrayList<String> queries) {
		return executeQueries(queries, new ArrayList<>());
	}

	@Override
	public int executeUpdate(String update, ArrayList<String> values) {
		int rows = 0;
		try {
			if (values.size() == 0) {
				Statement stmt;
				stmt = connection.createStatement();
				rows = stmt.executeUpdate(update);
			} else {
				PreparedStatement pStmt = fillPreparedStatement(update, values);
				rows = pStmt.executeUpdate();
			}
		} catch (SQLException ex) {
			log.error("Encountered SQLException.", ex);
		}
		return rows;
	}

	@Override
	public int executeUpdate(String update) {
		return executeUpdate(update, new ArrayList<>());
	}

	@Override
	public int executeUpdates(ArrayList<String> updates, ArrayList<ArrayList<String>> valuesList) {
		int totalRows = 0;
		boolean isPreparedStmt = !valuesList.isEmpty();
		if (isPreparedStmt && updates.size() != valuesList.size()) {
			throw new IllegalArgumentException();
		}
		for (int i = 0; i < updates.size(); i++) {
			totalRows += executeUpdate(updates.get(i), isPreparedStmt ? valuesList.get(i) : new ArrayList<>());
		}
		return totalRows;
	}

	@Override
	public int executeUpdates(ArrayList<String> updates) {
		return executeUpdates(updates, new ArrayList<>());
	}

	@Override
	public PreparedStatement fillPreparedStatement(String query, ArrayList<String> values) {
		PreparedStatement pStmt = null;
		try {
			pStmt = connection.prepareStatement(query);
			for (int i = 0; i < values.size(); i++) {
				pStmt.setString(i + 1, values.get(i));
			}
		} catch (SQLException ex) {
			log.error("Encountered SQLException.", ex);
		}
		return pStmt;
	}

	@Override
	public void buildDatabase() {
		ArrayList<String> createTables = new ArrayList<>();
		createTables.add(DBConstants.createNodeTable);
		createTables.add(DBConstants.createEdgeTable);
		createTables.add(DBConstants.createDoctorTable);
		createTables.add(DBConstants.createPatientTable);
		createTables.add(DBConstants.createMedicationRequestTable);
		createTables.add(DBConstants.createUserTable);
		dropTables();
		executeUpdates(createTables);
		populateFromCSV("MapLnodesFloor2", DBConstants.addNode);
		populateFromCSV("MapLedgesFloor2", DBConstants.addEdge);
	}

	@Override
	public void populateFromCSV(String csvFile, String update) {
		ArrayList<String> rowsToAdd = new ArrayList<>();
		ArrayList<ArrayList<String>> rowData = new ArrayList<>();
		CSVHelper csvReader = new CSVHelper();
		
		for (ArrayList<String> row : csvReader.readCSVFile(csvFile, true)) {
			rowsToAdd.add(update);
			rowData.add(new ArrayList<>(row));
		}

		executeUpdates(rowsToAdd, rowData);
	}

	@Override
	public void dropTables() {
		ResultSet resSet;
		ArrayList<String> droppableTables = new ArrayList<>();
		ArrayList<String> tablesToDrop = new ArrayList<>();
		droppableTables.add(DBConstants.dropNodeTable);
		droppableTables.add(DBConstants.dropEdgeTable);
		droppableTables.add(DBConstants.dropDoctorTable);
		droppableTables.add(DBConstants.dropPatientTable);
		droppableTables.add(DBConstants.dropMedicationRequestTable);
		droppableTables.add(DBConstants.dropUserTable);
		try {
			for (int i = 0; i < DBConstants.getTableNames().size(); i++) {
				resSet = connection.getMetaData().getTables(null, "APP", DBConstants.getTableNames().get(i).toUpperCase(), null);
				if (resSet.next()) {
					tablesToDrop.add(droppableTables.get((droppableTables.size() - 1) - i));
				}
			}
			executeUpdates(tablesToDrop, new ArrayList<>());
		} catch (SQLException ex) {
			log.error("Encountered SQLException.", ex);
		}
	}


	@Override
	public ArrayList<String> getColumnNames(ResultSet resSet) {
		ArrayList<String> colLabels = new ArrayList<>();
		try {
			ResultSetMetaData resSetMD = resSet.getMetaData();
			int totalCols = resSetMD.getColumnCount();
			for (int i = 0; i < totalCols; i++) {
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

	@Override
	public void collectUsedResultSet(ResultSet resSet) {
		usedResSets.add(resSet);
	}

	@Override
	public void collectUsedResultSets(ArrayList<ResultSet> resSets) {
		usedResSets.addAll(resSets);
	}

	@Override
	public void collectUsedStatement(Statement stmt) {
		usedStmts.add(stmt);
	}

	@Override
	public void collectUsedStatements(ArrayList<Statement> stmt) {
		usedStmts.addAll(stmt);
	}
}