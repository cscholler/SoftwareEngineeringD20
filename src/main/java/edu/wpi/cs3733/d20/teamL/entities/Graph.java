package edu.wpi.cs3733.d20.teamL.entities;

import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Graph implements Iterable<Node> {

    protected Map<String, Node> nodes = new ConcurrentHashMap<>();

    public static Graph graphFromCache(Collection<Node> nodes, Collection<Edge> edges) {
        Graph newGraph = new Graph();
        for (Node node : nodes) {
            newGraph.addNode(node);
        }

        for (Edge edge : edges) {
            edge.getSource().addEdgeTwoWay(edge);
        }

        // Search for nodes connected between floors and set each chain of connected nodes to a separate shaft value.
        // Didn't want to store shaft in the database so this is the solution.
        int currentElev = 0;
        int currentStair = 0;

        for (Node node : nodes) {
            if (node.getType().equals("ELEV")) {
                currentElev = stairOrElev(node, currentElev);
            } else if (node.getType().equals("STAI")) {
                currentStair = stairOrElev(node, currentStair);
            }
        }

        return newGraph;
    }

    private static int stairOrElev(Node node, int currentShaft) {
        boolean foundShaft = false;
        if(node.getShaft() == 0) {
            for (Node neighbor : node.getNeighbors()) {
                if (neighbor.getFloor() != node.getFloor()) {
                    if (!foundShaft) {
                        foundShaft = true;
                        currentShaft++;
                    }
                    neighbor.setShaft(currentShaft);
                }
            }
        }

        if (foundShaft) {
            node.setShaft(currentShaft);
        }

        return currentShaft;
    }

    public int getMaxShaft(String type) {
        int maxShaft = 0;
        for (Node node : getNodes()) {
            if(node.getType().equals(type) && node.getShaft() > maxShaft)
                maxShaft = node.getShaft();
        }

        return maxShaft;
    }

    /**
     * Gets a collection of edges removing one from each pair of two-way-edges.
     *
     * @return A collection of edges with None that are inverses of each other
     */
    public Collection<Edge> getEdgesOneWay() {
        Collection<Edge> oneWayEdges = new ArrayList<>();

        for (Edge edge : getEdges()) {
            // Check the edges that have already been edges for an edge with the same source and destination but reversed
//            boolean otherWayExists = false;
//            for (Edge existingEdge : oneWayEdges) {
//                if (edge.getSource() == existingEdge.getDestination() && edge.getDestination() == existingEdge.getSource())
//                    otherWayExists = true;
//            }



            // Only add an edge if the source and destination nodes are ordered alphabetically
            if(edge.getSource().getID().compareTo(edge.getDestination().getID()) > 0) {
                oneWayEdges.add(edge);
            }
        }

        return oneWayEdges;
    }

    /**
     * Gets the collection of Nodes contained in this graph.
     *
     * @return The collection of Nodes in this graph
     */
    public Collection<Node> getNodes() {
        return nodes.values();
    }

    /**
     * Gets a list of all edges between nodes in this Graph
     *
     * @return An ArrayList of Edges
     */
    public List<Edge> getEdges() {
        ArrayList<Edge> edges = new ArrayList<>();

        for (Node node : getNodes()) {
            for (Edge edge : node.getEdges()) {
                // Check if the edges ID has already been added
                boolean alreadyAdded = false;
                for (Edge existingEdge : edges) {
                    if (existingEdge.getID().equals(edge.getID())) {
                        alreadyAdded = true;
                        break;
                    }
                }

                // Add the edge if it hasn't already been added
                if (!alreadyAdded)
                    edges.add(edge);
            }
        }

        return edges;
    }

    /**
     * Adds a new node to this graph.
     *
     * @param newNode The new node to add
     */
    public void addNode(Node newNode) {
        String name = newNode.getID();

        nodes.put(name, newNode);
    }

    /**
     * Adds all nodes in the given collection.
     *
     * @param newNodes Collection of nodes to add
     */
    public void addAllNodes(Iterable<Node> newNodes) {
        for (Node node : newNodes) {
            addNode(node);
        }
    }

    /**
     * Adds all nodes in the given collection.
     *
     * @param newNodes Collection of nodes to add
     */
    public void addAllNodes(Node ... newNodes) {
        for (Node node : newNodes) {
            addNode(node);
        }
    }

    /**
     * Gets a node within this graph based on its name
     *
     * @param nodeID
     */
    public Node getNode(String nodeID) {
        return nodes.get(nodeID);
    }

    /**
     * Gets a collection of all the edges pointing to a given node.
     *
     * @param node The node the edges all point to
     * @return a collection of edges pointing to the node
     */
    public Collection<Edge> getEdgesPointingTo(Node node) {
        Collection<Edge> pointingEdges = new ArrayList<>();

        for (Edge edge : getEdges()) {
            if (edge.getDestination().equals(node))
                pointingEdges.add(edge);
        }

        return pointingEdges;
    }

    /**
     * Removes a given node.
     *
     * @param node The node to remove
     */
    public void removeNode(Node node) {
        nodes.remove(node.getID());
    }

    /**
     * Removes a Node based on its name.
     *
     * @param name String containing the name of the node to remove
     */
    public void removeNode(String name) {
        nodes.remove(name);
    }

    public String getUniqueNodeID() { // TODO: require the user to put in a nodeID instead of generating it
        String id = "new_node1";
        int curr = 1;
        boolean unique = false;
        while(!unique) {
            if(this.getNode(id) == null) unique = true;
            else {
                curr ++;
                id = "new_node" + curr;
            }
        }
        return id;
    }

    /**
     * Gets a unique node ID based on the node's current type and floor
     *
     * @param node Node to generate unique ID for
     * @return String of unique ID
     */
    public String getUniqueNodeID(Node node) {
        String team = "L";
        String type = node.getType();
        String shaft = String.valueOf(node.getShaft());
        String floor = (node.getFloor() < 10 ? "0" : "") + node.getFloor();

        String id = "new_node";
        int curr = 0;
        boolean unique = false;
        while(!unique) {
            curr ++;
            String adjCurr = (curr < 10 ? "0" : "") + curr;
            id = team + type + shaft + adjCurr + floor;

            if(this.getNode(id) == null) unique = true;
        }
        return id;
    }

    @Override @NotNull
    public Iterator<Node> iterator() {
        return getNodes().iterator();
    }

}
