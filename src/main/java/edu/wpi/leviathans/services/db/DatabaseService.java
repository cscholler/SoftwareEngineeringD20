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

  public ResultSet executeQuery(String sqlQuery) {
    try {
      PreparedStatement statement = connection.prepareStatement(sqlQuery);
      return statement.executeQuery();
    } catch (SQLException ex) {
      log.error("SQLException", ex);
      return null;
    }
  }

  public Collection<ResultSet> executeQueries(Collection<String> sqlQueries) {
    Collection<ResultSet> resultSets = new ArrayList<>();
    ResultSet rs;
    for (String query : sqlQueries) {
      rs = executeQuery(query);
      resultSets.add(rs);
    }
    return resultSets;
  }

  public int executeUpdate(String sqlUpdate) {
    try {
      PreparedStatement statement = connection.prepareStatement(sqlUpdate);
      return statement.executeUpdate();
    } catch (SQLException ex) {
      log.error("SQLException", ex);
      return 0;
    }
  }

  public Collection<Integer> executeUpdates(Collection<String> sqlUpdates) {
    Collection<Integer> totalAffectedRows = new ArrayList<>();
    int currentAffectedRows = 0;
    for (String update : sqlUpdates) {
      currentAffectedRows = executeUpdate(update);
      totalAffectedRows.add(currentAffectedRows);
    }
    return totalAffectedRows;
  }

  public void disconnect() {
    try {
      connection.close();
    } catch (SQLException ex) {
      log.error("SQLException", ex);
    }
  }

  public void buildTestDB() {
    Collection<String> testUpdates = new ArrayList<>();
    testUpdates.add(DBConstants.createMuseumsTable);
    testUpdates.add(DBConstants.createPaintingsTable);
    executeUpdates(testUpdates);
    log.info("Created tables");
  }
}
