package edu.wpi.leviathans.util.pathfinding.graph;

import java.util.HashMap;

public class Edge {

    public int length;
    public Node destination;
    public HashMap<String, Object> data;

    Node source;

    public Edge(Node p_destination, int p_length) {
        destination = p_destination;
        length = p_length;
    }
}
