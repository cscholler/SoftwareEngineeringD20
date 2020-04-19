package edu.wpi.cs3733.d20.teamL.entities;

import edu.wpi.cs3733.d20.teamL.services.graph.Graph;
import javafx.geometry.Point2D;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class Node {

    private Graph graph;
    private Point2D position;
    private String id;
    private String shortName;
    private String longName;
    private String building;
    private String type;
    private int floor;

    public HashMap<String, Object> data = new HashMap<>(); //TODO remove Hashmap and add NodeGUI field

    private Collection<Edge> edges = new ArrayList<>();

    public Node(String id, Point2D position, int floor, String building, String type, String longName, String shortName) {
        this.id = id;
        this.position = position;
        this.floor = floor;
        this.building = building;
        this.type = type;
        this.longName = longName;
        this.shortName = shortName;
    }

    public Node(String id, Point2D position) {
        this.id = id;
        this.position = position;
    }

    public Graph getGraph() {
        return graph;
    }

    public void setGraph(Graph graph) {
        this.graph = graph;
    }

    public void setEdges(Collection<Edge> edges) {
        this.edges = edges;
    }

    public Collection<Edge> getEdges() {
        return edges;
    }

    /**
     * Finds the edge associated with the given node, the node must be pointed to by one of the edges
     * of this node.
     *
     * @param otherNode A node that is pointed to by one of the edges of this node
     * @return The edge associated with the given node, null if it is not found.
     */
    public Edge getEdge(Node otherNode) {
        for (Edge edge : edges) {
            if (edge.getDestination().equals(otherNode)) return edge;
        }
        return null;
    }

    /**
     * Gets a list of the Nodes this Nodes Edges point to.
     *
     * @return A collection of neighboring Nodes
     */
    public Collection<Node> getNeighbors() {
        Collection<Node> neighbors = new ArrayList<>();

        for (Edge edge : edges) {
            neighbors.add(edge.getDestination());
        }

        return neighbors;
    }

    /**
     * Sets the name to the given string.
     *
     * @param newName String containing the new name
     */
    public void setId(String newName) {
        // Remove and re-add the node so its hash is updated in the graph
        if (graph != null) graph.removeNode(this);

        id = newName;

        if (graph != null) graph.addNode(this);
    }

    /**
     * @return String of the name of this node
     */
    public String getID() {
        return id;
    }

    /**
     * Adds a new edge to the Node.
     *
     * @param newEdge The edge to add
     */
    public void addEdge(Edge newEdge) {
        newEdge.setSource(this);
    }

    /**
     * Adds a new edge to the Node and one to the destination node to this one.
     *
     * @param newEdge The edge to add
     */
    public void addEdgeTwoWay(Edge newEdge) {
        addEdge(newEdge);

        Edge otherEdge = new Edge(newEdge.getId(), newEdge.getDestination(), this);

        newEdge.getDestination().addEdge(otherEdge);
    }

    /**
     * Adds a collection of new edges to this node.
     *
     * @param newEdges Collection of new edges
     */
    public void addAllEdges(Collection<Edge> newEdges) {
        for (Edge edge : newEdges) {
            addEdge(edge);
        }
    }

    /**
     * Removes specified edge
     *
     * @param toRemove The edge that will be removed
     */
    public void removeEdge(Edge toRemove) {
        toRemove.setSource(null);
    }

    /**
     * Finds an edge in this Node that leads to a specified Node.
     *
     * @return the Edge that leads to the specified Node, null if it is not found.
     */
    public Edge edgeFromDest(Node otherNode) {
        Edge result = null;
        for (Edge edge : edges) {
            if (edge.getDestination().equals(otherNode)) result = edge;
        }
        return result;
    }

    public Point2D getPosition() {
        return position;
    }

    public void setPosition(Point2D position) {
        this.position = position;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getLongName() {
        return longName;
    }

    public void setLongName(String longName) {
        this.longName = longName;
    }

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }
}