package edu.wpi.cs3733.d20.teamL.util.pathfinding;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import edu.wpi.cs3733.d20.teamL.entities.Graph;
import edu.wpi.cs3733.d20.teamL.services.db.IDatabaseCache;
import javafx.geometry.Point2D;

import com.google.inject.Inject;

import edu.wpi.cs3733.d20.teamL.entities.Node;
import edu.wpi.cs3733.d20.teamL.entities.Edge;


public class MapParser {

    @Inject
    static IDatabaseCache dbCache;

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
     *
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

    public static Graph parseMapToGraph(File nodesFile, File edgesFile) { //TODO use csv parser
        if (nodesFile == null) return null;

        try {
            // Parse nodes
            BufferedReader nodeReader = new BufferedReader(new FileReader(nodesFile));

            // Skip to the second row, the first row is just the labels for the data fields
            nodeReader.readLine();

            Graph newGraph = new Graph();

            String row = "";
            while ((row = nodeReader.readLine()) != null) {
                String[] data = row.split(",");

                Node newNode = new Node(data[0], new Point2D(Double.parseDouble(data[1]), Double.parseDouble(data[2])),
                        Integer.parseInt(data[3]), data[4], data[5], data[6], data[7]);

                newGraph.addNode(newNode);
            }

            // Parse edges
            if (edgesFile != null) {
                BufferedReader edgeReader = new BufferedReader(new FileReader(edgesFile));
                // Skip the first row
                edgeReader.readLine();

                while ((row = edgeReader.readLine()) != null) {
                    String[] data = row.split(",");

                    Node source = newGraph.getNode(data[1]);
                    Node destination = newGraph.getNode(data[2]);

                    if (source != null && destination != null) {
                        double x1 = source.getPosition().getX();
                        double y1 = source.getPosition().getY();
                        double x2 = destination.getPosition().getX();
                        double y2 = destination.getPosition().getY();

                        int length = (int) Math.round(Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2)));

                        source.addEdgeTwoWay(new Edge(source, destination));
                    }
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
}
