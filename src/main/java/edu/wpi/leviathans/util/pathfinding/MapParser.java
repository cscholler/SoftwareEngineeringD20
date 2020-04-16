package edu.wpi.leviathans.util.pathfinding;

import edu.wpi.leviathans.services.db.DBConstants;
import edu.wpi.leviathans.services.db.DatabaseService;
import edu.wpi.leviathans.util.pathfinding.graph.*;
import javafx.geometry.Point2D;

import java.io.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class MapParser {

    public static class DATA_LABELS {
        public static String X = "x";
        public static String Y = "y";
        public static String NODE_TYPE = "nodeType";
        public static String SHORT_NAME = "shortName";
        public static String LONG_NAME = "longName";
    }

    public static class NODE_TYPES {
        public static String CONFERENCE = "CONF";
        public static String HALL = "HALL";
        public static String DEPARTMENT = "DEPT";
        public static String INFO = "INFO";
        public static String LAB = "LABS";
        public static String RESTROOM = "REST";
    }

    /**
     * Writes a graph to two csv files and returns an array containing both of them.
     * @param graph
     * @return
     */
    public static ArrayList<File> parseGraphToMap(Graph graph) {
        try {
            FileWriter nodesFile = new FileWriter("nodesFile.csv");
            FileWriter edgesFile = new FileWriter("edgesFile.csv");
        } catch (IOException e) {

        }

        return new ArrayList<>(2);
    }

    public static Graph parseMapToGraph(File nodesFile, File edgesFile) {
        if (nodesFile == null || edgesFile == null) return null;

        try {
            BufferedReader nodeReader = new BufferedReader(new FileReader(nodesFile));
            BufferedReader edgeReader = new BufferedReader(new FileReader(edgesFile));

            // Skip to the second row, the first row is just the labels for the data fields
            nodeReader.readLine();
            edgeReader.readLine();

            Graph newGraph = new Graph();

            String row = "";
            while ((row = nodeReader.readLine()) != null) {
                String[] data = row.split(",");

                Node newNode = new Node(data[0]);
                newNode.position = new Point2D(Double.parseDouble(data[1]), Double.parseDouble(data[2]));
                newNode.data.put(DATA_LABELS.NODE_TYPE, data[5]);
                newNode.data.put(DATA_LABELS.LONG_NAME, data[6]);
                newNode.data.put(DATA_LABELS.SHORT_NAME, data[7]);

                newGraph.addNode(newNode);
            }

            while ((row = edgeReader.readLine()) != null) {
                String[] data = row.split(",");

                Node source = newGraph.getNode(data[1]);
                Node destination = newGraph.getNode(data[2]);

                if (source != null && destination != null) {
                    double x1 = source.position.getX();
                    double y1 = source.position.getY();
                    double x2 = destination.position.getX();
                    double y2 = destination.position.getY();

                    int length = (int) Math.round(Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2)));

                    source.addEdgeTwoWay(new Edge(destination, length));
                }
            }

            return newGraph;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static Graph parseMapToGraph(String nodesPath, String edgesPath) {
        File nodesFile = new File(nodesPath);
        File edgesFile = new File(edgesPath);

        return parseMapToGraph(nodesFile, edgesFile);
    }

    public static Graph parseMapToGraph(String nodesPath, File edgesFile) {
        File nodesFile = new File(nodesPath);

        return parseMapToGraph(nodesFile, edgesFile);
    }

    public static Graph parseMapToGraph(File nodesFile, String edgesPath) {
        File edgesFile = new File(edgesPath);

        return parseMapToGraph(nodesFile, edgesFile);
    }

    public static Graph getGraphFromDatabase() {
        DatabaseService db = new DatabaseService();
        ResultSet rs = db.executeQuery(DBConstants.selectAllNodes);

        Graph newGraph = new Graph();

        try {
            while (rs.next()) {
                Node newNode = new Node(rs.getString(1));
                newNode.position = new Point2D(Double.parseDouble(rs.getString(2)), Double.parseDouble(rs.getString(3)));
                newNode.data.put(MapParser.DATA_LABELS.NODE_TYPE, rs.getString(6));
                newNode.data.put(MapParser.DATA_LABELS.LONG_NAME, rs.getString(7));
                newNode.data.put(MapParser.DATA_LABELS.SHORT_NAME, rs.getString(8));

                newGraph.addNode(newNode);
            }

            rs = db.executeQuery(DBConstants.selectAllEdges);

            while (rs.next()) {
                Node source = newGraph.getNode(rs.getString(2));
                Node destination = newGraph.getNode(rs.getString(3));

                if (source != null && destination != null) {
                    double x1 = source.position.getX();
                    double y1 = source.position.getY();
                    double x2 = destination.position.getX();
                    double y2 = destination.position.getY();

                    int length = (int) Math.round(Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2)));

                    source.addEdgeTwoWay(new Edge(destination, length));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return newGraph;
    }
}
