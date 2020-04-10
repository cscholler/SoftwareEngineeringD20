package edu.wpi.leviathans.pathFinding;

import edu.wpi.leviathans.pathFinding.graph.*;

import java.io.*;

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
}
