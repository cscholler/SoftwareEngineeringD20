package edu.wpi.leviathans.services.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Properties;

import edu.wpi.leviathans.util.io.CSVParser;
import lombok.extern.slf4j.Slf4j;

import edu.wpi.leviathans.services.Service;

@Slf4j
public class DatabaseService extends Service {
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
		buildDatabase();
	}

	@Override
	public void stopService() {
		disconnect();
	}

	private void connect(Properties props) {
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

	private void disconnect() {
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
				//connection = null;
			}
			log.info("Connection closed.");
		} catch (SQLException ex) {
			log.error("SQL Exception", ex);
		}
	}

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

	public ResultSet executeQuery(String query) {
		return executeQuery(query, new ArrayList<>());
	}

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

	public boolean executeUpdate(String update, ArrayList<String> values) {
		boolean isSuccess = false;
		try {
			if (values.size() == 0) {
				Statement stmt;
				stmt = connection.createStatement();
				isSuccess = stmt.execute(update);
			} else {
				PreparedStatement pStmt = fillPreparedStatement(update, values);
				isSuccess = pStmt.execute();
			}
		} catch (SQLException ex) {
			log.error("Encountered SQLException.", ex);
		}
		return isSuccess;
	}

	public boolean executeUpdate(String update) {
		return executeUpdate(update, new ArrayList<>());
	}

	public boolean executeUpdates(ArrayList<String> updates, ArrayList<ArrayList<String>> valuesList) {
		boolean isSuccess = true;
		boolean isPreparedStmt = !valuesList.isEmpty();
		if (isPreparedStmt && updates.size() != valuesList.size()) {
			throw new IllegalArgumentException();
		}
		for (int i = 0; i < updates.size(); i++) {
			if (!executeUpdate(updates.get(i),isPreparedStmt ? valuesList.get(i) : new ArrayList<>())) {
				isSuccess = false;
			}
		}
		return isSuccess;
	}

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

	private void dropTables() {
		ArrayList<String> dropTables = new ArrayList<>();

		dropTables.add(DBConstants.dropNodeTable);
		dropTables.add(DBConstants.dropEdgeTable);
		dropTables.add(DBConstants.dropDoctorTable);
		dropTables.add(DBConstants.dropPatientTable);
		dropTables.add(DBConstants.dropMedicationRequestTable);
		dropTables.add(DBConstants.dropUserTable);

		try {
			executeUpdates(dropTables, new ArrayList<>());
		} catch (Exception ex) {
			log.debug("Table(s) do not exist.");
		}
	}

	private boolean buildDatabase() {
		ArrayList<String> createTables = new ArrayList<>();

		//dropTables(); //drop tables doesn't work if tables ain't there

		createTables.add(DBConstants.createNodeTable);
		createTables.add(DBConstants.createEdgeTable);
		createTables.add(DBConstants.createDoctorTable);
		createTables.add(DBConstants.createPatientTable);
		createTables.add(DBConstants.createMedicationRequestTable);
		createTables.add(DBConstants.createUserTable);

		CSVParser parser = new CSVParser();
		ArrayList<String> populateNodes = new ArrayList<>();
		ArrayList<ArrayList<String>> nodeData = parser.readCSVFile();

		for (int i = 0; i < nodeData.size(); i++) {
			populateNodes.add(DBConstants.addNode);
		}

		parser = new CSVParser("MapLedgesFloor2.csv");
		ArrayList<String> populateEdges = new ArrayList<>();
		ArrayList<ArrayList<String>> edgeData = parser.readCSVFile();

		for (int i = 0; i < edgeData.size(); i++) {
			populateEdges.add(DBConstants.addEdge);
		}

		boolean createdTables = executeUpdates(createTables, new ArrayList<>());
		boolean populatedNodes = executeUpdates(populateNodes, nodeData);
		boolean populatedEdges = executeUpdates(populateEdges, edgeData);

		return createdTables && populatedNodes && populatedEdges;
	}

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

	public void collectUsedResultSet(ResultSet resSet) {
		usedResSets.add(resSet);
	}

	public void collectUsedResultSets(ArrayList<ResultSet> resSets) {
		usedResSets.addAll(resSets);
	}

	public void collectUsedStatement(Statement stmt) {
		usedStmts.add(stmt);
	}

	public void collectUsedStatements(ArrayList<Statement> stmt) {
		usedStmts.addAll(stmt);
	}
}