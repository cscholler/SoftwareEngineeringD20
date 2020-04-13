package edu.wpi.leviathans.services.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Properties;

import lombok.extern.slf4j.Slf4j;

import edu.wpi.leviathans.services.Service;

@Slf4j
public class DatabaseService extends Service {
	private Connection connection = null;
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
				connection = null;
			}
			log.info("Connection closed.");
		} catch (SQLException ex) {
			log.error("SQL Exception", ex);
		}
	}

	public ResultSet executeQuery(String query, ArrayList<Object> values) {
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

	public ArrayList<ResultSet> executeQueries(ArrayList<String> queries, ArrayList<ArrayList<Object>> valuesList) {
		ArrayList<ResultSet> resSets = new ArrayList<>();
		if (queries.size() != valuesList.size()) {
			throw new IllegalArgumentException();
		}
		for (int i = 0; i < queries.size(); i++) {
			resSets.add(executeQuery(queries.get(i), valuesList.get(i)));
		}
		return resSets;
	}

	public boolean executeUpdate(String update, ArrayList<Object> values) {
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

	public boolean executeUpdates(ArrayList<String> updates, ArrayList<ArrayList<Object>> valuesList) {
		boolean isSuccess = true;
		if (updates.size() != valuesList.size()) {
			throw new IllegalArgumentException();
		}
		for (int i = 0; i < updates.size(); i++) {
			if (!executeUpdate(updates.get(i), valuesList.get(i))) {
				isSuccess = false;
			}
		}
		return isSuccess;
	}

	public PreparedStatement fillPreparedStatement(String query, ArrayList<Object> values) {
		PreparedStatement pStmt = null;
		try {
			pStmt = connection.prepareStatement(query);
			for (int i = 1; i < values.size() + 1; i++) {
				Object value = values.get(i);
				if (value instanceof String) {
					pStmt.setString(i, (String) value);
				} else if (value instanceof Integer) {
					pStmt.setInt(i, (Integer) value);
				}
			}
		} catch (SQLException ex) {
			log.error("Encountered SQLException.", ex);
		}
		return pStmt;
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
