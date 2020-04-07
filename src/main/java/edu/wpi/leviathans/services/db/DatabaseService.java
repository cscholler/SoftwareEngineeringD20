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

    public ResultSet executeQuery(String sqlQuery, Object... values) {
        try {
            PreparedStatement statement = connection.prepareStatement(sqlQuery);
            for (int i = 0; i < values.length; i++) {
                statement.setObject(i, values[i]);
            }
            return statement.executeQuery();
        } catch (SQLException ex) {
            log.error("SQLException", ex);
            return null;
        }
    }

    public Collection<ResultSet> executeQueries(Collection<String> sqlQueries, Collection<ArrayList<Object>> valuesList) {
        Collection<ResultSet> resultSets = new ArrayList<>();
        ResultSet rs;
        for (int i = 0; i < sqlQueries.size(); i++) {
            //TODO: fix this
            rs = executeQuery(sqlQueries[i], valuesList[i]);
            resultSets.add(rs);
            i++;
        }
        return resultSets;
    }

    public int executeUpdate(String sqlUpdate, Object... values) {
        try {
            PreparedStatement statement = connection.prepareStatement(sqlUpdate);
            for (int i = 0; i < values.length; i++) {
                statement.setObject(i, values[i]);
            }
            return statement.executeUpdate();
        } catch (SQLException ex) {
            log.error("SQLException", ex);
            return 0;
        }
    }

    public Collection<Integer> executeUpdates(Collection<String> sqlUpdates, Collection<ArrayList<Object>> valuesList) {
        Collection<Integer> totalAffectedRows = new ArrayList<>();
        int currentAffectedRows = 0;
        for (int i = 0; i < sqlUpdates.size(); i++) {
            // TODO: fix this
            currentAffectedRows = executeUpdate(sqlUpdates[i], valuesList[i]);
            totalAffectedRows.add(currentAffectedRows);
        }
        return totalAffectedRows;
    }

    public void disconnect() {
        try {
            connection.commit();
            connection.close();
        } catch (SQLException ex) {
            log.error("SQLException", ex);
        }
    }
    public void handleUserRequest(int programMode, String museumName) {
        switch (programMode) {
            case 1: {
                reportMuseumInfo();
            }
            break;
            case 2: {
                reportPaintingInfo();
            }
            break;
            case 3: {
                setPhoneNumber(museumName);
            }
            break;
            case 4:
            default: {
                System.exit(0);
            }
        }
    }


    public void buildTestDB() {
        Collection<String> tablesToAdd = new ArrayList<>();
        tablesToAdd.add(DBConstants.createMuseumsTable);
        tablesToAdd.add(DBConstants.createPaintingsTable);
        executeUpdates(tablesToAdd, null);
        log.info("Created tables");
        Collection<String> museumsToAdd = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            museumsToAdd.add(DBConstants.addMuseum);
        }
        Collection<ArrayList<Object>> museumsInfo = new ArrayList<>();
        executeUpdates(museumsToAdd, museumsInfo);
        log.info("Added museums");
        // TODO: fill prepared statements
        Collection<String> paintingsToAdd = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            paintingsToAdd.add(DBConstants.addPainting);
        }
        Collection<ArrayList<Object>> paintingsInfo = new ArrayList<>();
        executeUpdates(paintingsToAdd, paintingsInfo);
        log.info("Added paintings");
        // TODO: fill prepared statements
    }

    private void reportMuseumInfo() {
        ResultSet resultSet = executeQuery(DBConstants.selectAllMuseums);
        processResults(resultSet);
    }

    public void reportPaintingInfo() {
        ResultSet resultSet = executeQuery(DBConstants.selectAllPaintings);
        processResults(resultSet);
    }

    public void setPhoneNumber(String museumName) {
        Collection<Object> values = new ArrayList<>();
        values.add(museumName);
        executeUpdate(DBConstants.updateMuseumPhone, values);
    }

    private void processResults(ResultSet resultSet) {
        //TODO: implement
    }
}
