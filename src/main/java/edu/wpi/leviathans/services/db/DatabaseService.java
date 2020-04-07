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

    public ResultSet executeQuery(String sqlQuery, ArrayList<Object> values) {
        try {
            PreparedStatement statement = connection.prepareStatement(sqlQuery);
            for (int i = 0; i < values.size(); i++) {
                statement.setObject(i, values.get(i));
            }
            return statement.executeQuery();
        } catch (SQLException ex) {
            log.error("SQLException", ex);
            return null;
        }
    }

    public ArrayList<ResultSet> executeQueries(ArrayList<String> sqlQueries, ArrayList<ArrayList<Object>> valuesList) {
        ArrayList<ResultSet> resultSets = new ArrayList<>();
        ResultSet rs;
        for (int i = 0; i < sqlQueries.size(); i++) {
            rs = executeQuery(sqlQueries.get(i), valuesList.get(i));
            resultSets.add(rs);
            i++;
        }
        return resultSets;
    }

    public int executeUpdate(String sqlUpdate, ArrayList<Object> values) {
        try {
            PreparedStatement statement = connection.prepareStatement(sqlUpdate);
            for (int i = 0; i < values.size(); i++) {
                statement.setObject(i, values.get(i));
            }
            return statement.executeUpdate();
        } catch (SQLException ex) {
            log.error("SQLException", ex);
            return 0;
        }
    }

    public ArrayList<Integer> executeUpdates(ArrayList<String> sqlUpdates, ArrayList<ArrayList<Object>> valuesList) {
        ArrayList<Integer> totalAffectedRows = new ArrayList<>();
        int currentAffectedRows = 0;
        for (int i = 0; i < sqlUpdates.size(); i++) {
            currentAffectedRows = executeUpdate(sqlUpdates.get(i), valuesList.get(i));
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
        ArrayList<String> tablesToAdd = new ArrayList<>();
        tablesToAdd.add(DBConstants.createMuseumsTable);
        tablesToAdd.add(DBConstants.createPaintingsTable);
        executeUpdates(tablesToAdd, null);
        log.info("Created tables");
        ArrayList<String> museumsToAdd = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            museumsToAdd.add(DBConstants.addMuseum);
        }
        ArrayList<ArrayList<Object>> museumsInfo = new ArrayList<>();
        // TODO: populate museumsInfo to fill prepared statements
        executeUpdates(museumsToAdd, museumsInfo);
        log.info("Added museums");

        ArrayList<String> paintingsToAdd = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            paintingsToAdd.add(DBConstants.addPainting);
        }
        ArrayList<ArrayList<Object>> paintingsInfo = new ArrayList<>();
        // TODO: populate museumsInfo to fill prepared statements
        executeUpdates(paintingsToAdd, paintingsInfo);
        log.info("Added paintings");
    }

    private void reportMuseumInfo() {
        //TODO: fix warning (implement null check?)
        ResultSet resultSet = executeQuery(DBConstants.selectAllMuseums, null);
        processResults(resultSet);
    }

    public void reportPaintingInfo() {
        //TODO: fix warning (implement null check?)
        ResultSet resultSet = executeQuery(DBConstants.selectAllPaintings, null);
        processResults(resultSet);
    }

    public void setPhoneNumber(String museumName) {
        ArrayList<Object> values = new ArrayList<>();
        values.add(museumName);
        executeUpdate(DBConstants.updateMuseumPhone, values);
    }

    private void processResults(ResultSet resultSet) {
        //TODO: implement
    }
}
