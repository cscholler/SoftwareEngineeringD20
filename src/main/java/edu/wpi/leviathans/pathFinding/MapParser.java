package edu.wpi.leviathans.pathFinding;

import edu.wpi.leviathans.pathFinding.graph.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MapParser {

    public static final class DATA_LABELS {
        public static final String X = "x";
        public static final String Y = "y";
        public static final String NODE_TYPE = "nodeType";
        public static final String SHORT_NAME = "shortName";
        public static final String LONG_NAME = "longName";
    }

    public static final class NODE_TYPES {
        public static final String CONFERENCE = "CONF";
        public static final String HALL = "HALL";
        public static final String DEPARTMENT = "DEPT";
        public static final String INFO = "INFO";
        public static final String LAB = "LABS";
        public static final String RESTROOM = "REST";
    }

    /**
     * Writes a graph to two csv files and returns an array containing both of them.
     * @param graph
     * @return
     */
    public static List<File> parseGraphToMap(Graph graph) {
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
                newNode.data.put(DATA_LABELS.X, Integer.parseInt(data[1]));
                newNode.data.put(DATA_LABELS.Y, Integer.parseInt(data[2]));
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
                    int x1 = (int) source.data.get(DATA_LABELS.X);
                    int y1 = (int) source.data.get(DATA_LABELS.Y);
                    int x2 = (int) destination.data.get(DATA_LABELS.X);
                    int y2 = (int) destination.data.get(DATA_LABELS.Y);

                    int length = (int) Math.round(Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2)));

                    source.addEdgeTwoWay(new Edge(destination, length));
                }
            }

            return newGraph;

        } catch (FileNotFoundException e) {
            log.error("File not found"); //Should never get here
        } catch (IOException e) {
            log.error("Error in reading file");
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
}
