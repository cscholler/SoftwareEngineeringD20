package main.java.edu.wpi.leviathans.aStar.graph;

import java.util.List;

public class Node {

    public class Edge {
        public int length;
        public Node destination;

        public Edge(Node dest, int len) {
            destination = dest;
            len = length;
        }
    }

    private String name;
    private List<Edge> edges;

    public Node(String name) {
        this.name = name;
    }



}
