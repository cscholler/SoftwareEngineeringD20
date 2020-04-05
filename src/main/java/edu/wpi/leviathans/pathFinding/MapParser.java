package edu.wpi.leviathans.pathFinding;

import edu.wpi.leviathans.pathFinding.graph.*;

import java.io.*;

public class MapParser {

    public static String X_LABEL = "x";
    public static String Y_LABEL = "y";

    public static Graph parseMapToGraph(String nodesPath, String edgesPath) {
        try {
            BufferedReader nodeReader = new BufferedReader(new FileReader(nodesPath));
            BufferedReader edgeReader = new BufferedReader(new FileReader(edgesPath));

            // Skip to the second row, the first row is just the labels for the data fields
            nodeReader.readLine();
            edgeReader.readLine();

            Graph newGraph = new Graph();

            String row = "";
            while ((row = nodeReader.readLine()) != null) {
                String[] data = row.split(",");

                Node newNode = new Node(data[0]);
                newNode.data.put(X_LABEL, Integer.parseInt(data[1]));
                newNode.data.put(Y_LABEL, Integer.parseInt(data[2]));

                newGraph.addNode(newNode);
            }

            while ((row = edgeReader.readLine()) != null) {
                String[] data = row.split(",");

                Node source = newGraph.getNode(data[1]);
                Node destination = newGraph.getNode(data[2]);

                if (source != null && destination != null) {
                    int x1 = (int) source.data.get(X_LABEL);
                    int y1 = (int) source.data.get(Y_LABEL);
                    int x2 = (int) destination.data.get(X_LABEL);
                    int y2 = (int) destination.data.get(Y_LABEL);

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
}
