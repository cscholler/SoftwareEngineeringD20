// This is a more fully featured implementation of the database service that is currently broken.
// Will fix later. Don't touch.

package edu.wpi.leviathans.services.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DatabaseService2 {
  private Connection connection;

  public DatabaseService2(Properties props) {
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

  public ResultSet executeQuery(String sqlQuery, ArrayList<String> values) {
    try {
      PreparedStatement statement = connection.prepareStatement(sqlQuery);
      if (!values.isEmpty()) {
        for (int i = 0; i < values.size(); i++) {
          if (values.get(i) != null) {
            statement.setString(i, values.get(i));
          }
        }
      }
      return statement.executeQuery();
    } catch (SQLException ex) {
      log.error("SQLException", ex);
      return null;
    }
  }

  public ArrayList<ResultSet> executeQueries(
      ArrayList<String> sqlQueries, ArrayList<ArrayList<String>> valuesList) {
    ArrayList<ResultSet> resultSets = new ArrayList<>();
    ResultSet rs;
    for (int i = 0; i < sqlQueries.size(); i++) {
      rs =
          executeQuery(
              sqlQueries.get(i), valuesList.isEmpty() ? new ArrayList<>() : valuesList.get(i));
      resultSets.add(rs);
    }
    return resultSets;
  }

  public int executeUpdate(String sqlUpdate, ArrayList<String> values) {
    try {
      PreparedStatement statement = connection.prepareStatement(sqlUpdate);
      if (!values.isEmpty()) {
        for (int i = 0; i < values.size(); i++) {
          statement.setString(i, values.get(i));
        }
      }
      return statement.executeUpdate();
    } catch (SQLException ex) {
      log.error("SQLException", ex);
      return 0;
    }
  }

  public ArrayList<Integer> executeUpdates(
      ArrayList<String> sqlUpdates, ArrayList<ArrayList<String>> valuesList) {
    ArrayList<Integer> affectedRowsList = new ArrayList<>();
    int currentAffectedRows = 0;
    for (int i = 0; i < sqlUpdates.size(); i++) {
      currentAffectedRows =
          executeUpdate(
              sqlUpdates.get(i), valuesList.isEmpty() ? new ArrayList<>() : valuesList.get(i));
      affectedRowsList.add(currentAffectedRows);
    }
    return affectedRowsList;
  }

  public void disconnect() {
    try {
      connection.commit();
      connection.close();
    } catch (SQLException ex) {
      log.error("SQLException", ex);
    }
  }

  public void handleUserRequest(int programMode, String museumName, String newPhoneNumber) {
    switch (programMode) {
      case 1:
        {
          reportMuseumInfo();
        }
        break;
      case 2:
        {
          reportPaintingInfo();
        }
        break;
      case 3:
        {
          setPhoneNumber(museumName, newPhoneNumber);
        }
        break;
      case 4:
      default:
        {
          System.exit(0);
        }
    }
  }

  public void buildTestDB() {
    ArrayList<String> tablesToAdd = new ArrayList<>();
    tablesToAdd.add(DBConstants.createMuseumsTable);
    tablesToAdd.add(DBConstants.createPaintingsTable);
    executeUpdates(tablesToAdd, new ArrayList<>());
    log.info("Created tables");
    ArrayList<String> museumsToAdd = new ArrayList<>();
    for (int i = 0; i < 5; i++) {
      museumsToAdd.add(DBConstants.addMuseum);
    }
    ArrayList<ArrayList<String>> museumsInfo = new ArrayList<>();
    // TODO: populate museumsInfo to fill prepared statements
    executeUpdates(museumsToAdd, museumsInfo);
    log.info("Added museums");

    ArrayList<String> paintingsToAdd = new ArrayList<>();
    for (int i = 0; i < 20; i++) {
      paintingsToAdd.add(DBConstants.addPainting);
    }
    ArrayList<ArrayList<String>> paintingsInfo = new ArrayList<>();
    // TODO: populate museumsInfo to fill prepared statements
    executeUpdates(paintingsToAdd, paintingsInfo);
    log.info("Added paintings");
  }

  private void reportMuseumInfo() {
    ResultSet resultSet = executeQuery(DBConstants.selectAllMuseums, new ArrayList<>());
    processResults(resultSet);
  }

  public void reportPaintingInfo() {
    ResultSet resultSet = executeQuery(DBConstants.selectAllPaintings, new ArrayList<>());
    processResults(resultSet);
  }

  public void setPhoneNumber(String museumName, String newPhoneNumber) {
    ArrayList<String> values = new ArrayList<>();
    values.add(newPhoneNumber);
    values.add(museumName);
    executeUpdate(DBConstants.updateMuseumPhone, values);
  }

  private void processResults(ResultSet resultSet) {
    // TODO: finish
    ArrayList<String> results = new ArrayList<>();
    try {
      while (resultSet.next()) {
        results.add(String.valueOf(resultSet.next()));
      }
    } catch (SQLException ex) {
      log.error("SQLException", ex);
    }
  }
}
