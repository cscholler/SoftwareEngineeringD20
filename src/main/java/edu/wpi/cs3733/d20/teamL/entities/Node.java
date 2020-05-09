package edu.wpi.cs3733.d20.teamL.entities;

import javafx.geometry.Point2D;

import java.util.*;

public class Node {

    private Graph graph;
    private Point2D position;
    private String id;
    private String shortName;
    private String longName;
    private String building;
    private String type;
    private int shaft = 0;
    private int floor;
    private int freq = 0;

    private HashMap<String, Object> data = new HashMap<>(); //TODO remove Hashmap and add NodeGUI Field

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

    public Node(String id, Point2D position, int floor, String building) {
        this.id = id;
        this.position = position;
        this.floor = floor;
        this.shortName = "Hall";
        this.longName = "Hallway Intersection X Level " + floor;
        this.type = "HALL L" + floor + "XXX";
        this.building = building;
    }

    public Node(String id, Point2D position, String floor, String building, String type, String longName, String shortName) {
        this(id, position, floorStringToInt(floor), building, type, longName, shortName);
    }

    public Node(String id, Point2D position, String floor, String building) {
        this(id, position, floorStringToInt(floor), building);
    }

    public static int floorStringToInt(String floor) {
        switch (floor) {
            case "G":
                return 0;
            case "L1":
                return -1;
            case "L2":
                return -2;
            default:
                return Integer.parseInt(floor);
        }
    }

    public static String floorIntToString(int floor) {
        switch (floor) {
            case 0:
                return "G";
            case -1:
                return "L1";
            case -2:
                return "L2";
            default:
                return String.valueOf(floor);
        }
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

        // Remove all duplicate edges
        Iterator<Edge> iterator = edges.iterator();
        while(iterator.hasNext()) {
            Edge edge = iterator.next();

            boolean foundDuplicate = false;
            for (Edge otherEdge : edges) {
                if (edge.getID().equals(otherEdge.getID()) && otherEdge != edge) {
                    foundDuplicate = true;
                    break;
                }
            }

            if (foundDuplicate)
                iterator.remove();
        }

        return edges;
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

    public String getFloorAsString() {
        return floorIntToString(getFloor());
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    public void setFloor(String floor) {
        this.floor = floorStringToInt(floor);
    }

    public int getShaft() {
        return shaft;
    }

    public void setShaft(String shaft) {
        this.shaft = Integer.parseInt(shaft);
    }

    public void setShaft(int shaft) {
        this.shaft = shaft;
    }

    public int getFreq() {
        return freq;
    }

    public void setFreq(int freq) {
        this.freq = freq;
    }

    /**
     * Adds a new edge to the Node.
     *
     * @param newEdge The edge to add
     */
    public void addEdge(Edge newEdge) {
        if (!getEdges().contains(newEdge)) newEdge.setSource(this);
        else newEdge.source = this;
    }

    /**
     * Adds a new Edge leading to the given node
     *
     * @param otherNode The node this edge leads to
     */
    public Edge addEdge(Node otherNode) {
        Edge newEdge = new Edge(this, otherNode);
        addEdge(newEdge);

        return getEdge(otherNode);
    }

    /**
     * Adds a new edge to the Node and one to the destination node to this one.
     *
     * @param newEdge The edge to add
     */
    public void addEdgeTwoWay(Edge newEdge) {
        addEdge(newEdge);

        newEdge.getDestination().addEdge(this);
    }

    /**
     * Adds a new edge to the Node and one to the destination node to this one.
     *
     * @param otherNode The node to add a two way edge to
     */
    public Edge addEdgeTwoWay(Node otherNode) {
        addEdge(otherNode);

        otherNode.addEdge(this);

        return getEdge(otherNode);
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
     * Removes specified edge
     *
     * @param dest The destination node of the edge to be removed
     */
    public void removeEdge(Node dest) {
        Edge toRemove = getEdge(dest);
        if (toRemove != null) removeEdge(toRemove);
    }

    /**
     * Removes a specified and the edge from the destination to this node if there is one.
     *
     * @param dest The destination node
     */
    public void removeEdgeTwoWay(Node dest) {
        removeEdge(dest);

        if(dest.getEdge(this) != null)
            dest.removeEdge(this);
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
     * Converts the Node fields into an ArrayList to be added to the Database
     *
     * @return ArrayList of Strings representing Node fields
     */
    public ArrayList<String> toArrayList() {
        return new ArrayList<>(Arrays.asList(getID(), String.valueOf(getPosition().getX()), String.valueOf(getPosition().getY()),
                String.valueOf(getFloor()), getBuilding(), getType(), getLongName(), getShortName(), String.valueOf(getFreq())));
    }

    /**
     * Place to dump/get data where the Node has no field for (ONLY fields not used in the Database)
     *
     * @return HashMap where key is the String of the object's name and the field is the object's value
     */
    public HashMap<String, Object> getData() {
        return data;
    }

    public double distanceTo(Node otherNode) {
        double length = getPosition().distance(otherNode.getPosition()) + (Math.abs(getFloor() - otherNode.getFloor()) * 100);
        if (!getBuilding().equals(otherNode.getBuilding())) length += 10000;
        return length;
    }

    /*
     * Checks if all the fields of this node are the same as the other node.
     *
     * @param obj
     * @return
     */
    /*@Override
    public boolean equals(Object obj) {
        if (obj instanceof Node) {
            Node otherNode = (Node) obj;

            if (!getID().equals(otherNode.getID())) return false;
            if (!getPosition().equals(otherNode.getPosition())) return false;
            if (getFloor() != (otherNode.getFloor())) return false;
            if (!getBuilding().equals(otherNode.getBuilding())) return false;
            if (!getType().equals(otherNode.getType())) return false;
            if (!getLongName().equals(otherNode.getLongName())) return false;
            if (!getShortName().equals(otherNode.getShortName())) return false;

            return true;
        } else  {
            throw new IllegalArgumentException("'equals()' must compare this with another Node.");
        }
    }*/
}
