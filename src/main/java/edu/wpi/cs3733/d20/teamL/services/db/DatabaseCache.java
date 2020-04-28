package edu.wpi.cs3733.d20.teamL.services.db;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javafx.geometry.Point2D;

import com.google.inject.Inject;

import edu.wpi.cs3733.d20.teamL.entities.Edge;
import edu.wpi.cs3733.d20.teamL.entities.Node;

public class DatabaseCache implements IDatabaseCache {
    private ArrayList<Node> nodeCache = new ArrayList<>();
    private ArrayList<Edge> edgeCache = new ArrayList<>();
    private ArrayList<Node> editedNodes = new ArrayList<>();
    private ArrayList<Node> addedNodes = new ArrayList<>();
    private ArrayList<Edge> addedEdges = new ArrayList<>();
    private ArrayList<Node> deletedNodes = new ArrayList<>();
    private ArrayList<Edge> deletedEdges = new ArrayList<>();
    @Inject
    private IDatabaseService db;

    @Override
    public void cacheAllFromDB() {
        cacheNodesFromDB();
        cacheEdgesFromDB();

        // Search for nodes connected between floors and set each chain of connected nodes to a separate shaft value.
        // Didn't want to store shaft in the database so this is the solution.
        int currentShaft = 0;
        boolean foundShaft = false;

        for (Node node : nodeCache) {
            if (node.getType().equals("ELEV") || node.getType().equals("STAI")) {

                for (Node neighbor : node.getNeighbors()) {
                    if (neighbor.getFloor() != node.getFloor()) {
                        if (!foundShaft) {
                            foundShaft = true;
                            currentShaft++;
                        }
                        neighbor.setShaft(currentShaft);
                    }
                }

                if (foundShaft) {
                    node.setShaft(currentShaft);
                    foundShaft = false;
                }
            }
        }

    }

    /**
     * Sets the nodes cache to the given ArrayList of nodes. This will overwrite the cache and eventually overwrite the database, use this when you are sure you want to make changes to the map.
     *
     * @param newNodes A new list of nodes to set the nodes cache to.
     */
    @Override
    public void cacheNodes(ArrayList<Node> newNodes, ArrayList<Node> editedNodes) {
        for (Node node : newNodes) {
            if (!nodeCache.contains(node)) addedNodes.add(node);
        }
        for (Node node : nodeCache) {
            if (!newNodes.contains(node)) deletedNodes.add(node);
        }
        editedNodes.removeIf(node -> deletedNodes.contains(node));
        this.editedNodes = editedNodes;
        this.nodeCache = newNodes;
    }

    /**
     * Sets the edges cache to the given ArrayList of edges. This will overwrite the cache and eventually overwrite the database, use this when you are sure you want to make changes to the map.
     *
     * @param newEdges A new list of edges to set the edges cache to.
     */
    @Override
    public void cacheEdges(ArrayList<Edge> newEdges) {
        for (Edge edge : newEdges) {
            if (!edgeCache.contains(edge)) {
                addedEdges.add(edge);
            }
        }

        for (Edge edge : edgeCache) if (!newEdges.contains(edge)) deletedEdges.add(edge);

        this.edgeCache = newEdges;
    }

    /**
     * Pushes all caches to the database.
     */
    @Override
    public void updateDB() {
        // Concatenate the node and edges lists for one update on the cache
        ArrayList<SQLEntry> updates = new ArrayList<>();
        // Add nodes
        for (ArrayList<String> nodeInfo : convertNodesToValuesList(addedNodes)) {
            updates.add(new SQLEntry(DBConstants.ADD_NODE, nodeInfo));
        }
        // Add edges
        for (ArrayList<String> edgeInfo : convertEdgesToValuesList(addedEdges)) {
            updates.add(new SQLEntry(DBConstants.ADD_EDGE, edgeInfo));
        }
        // Delete edges
        for (Edge edge : deletedEdges) {
            updates.add(new SQLEntry(DBConstants.REMOVE_EDGE, new ArrayList<>(Collections.singletonList(edge.getID()))));
        }
        // Edit nodes
        for (ArrayList<String> currentNode : convertNodesToValuesList(editedNodes)) {
            String nodeID = currentNode.get(0);
            currentNode.remove(0);
            currentNode.add(nodeID);
            updates.add(new SQLEntry(DBConstants.UPDATE_NODE, currentNode));
        }
        // Delete nodes
        for (ArrayList<String> currentNode : convertNodesToValuesList(deletedNodes)) {
            updates.add(new SQLEntry(DBConstants.REMOVE_NODE, new ArrayList<>(Collections.singletonList(currentNode.get(0)))));
        }
        db.executeUpdates(updates); // TODO: Fix SQL error by preventing from adding duplicate nodes
        // Clear added, edited, and deleted nodes from cache
        addedNodes.clear();
        addedEdges.clear();
        editedNodes.clear();
        deletedEdges.clear();
        deletedNodes.clear();
    }

    /**
     * Converts a list of nodes into a list of all the nodes' fields
     *
     * @param nodes This list of Nodes to be converted
     * @return A list of lists of entries representing individual nodes
     */
    @Override
    public ArrayList<ArrayList<String>> convertNodesToValuesList(List<Node> nodes) {
        ArrayList<ArrayList<String>> valuesList = new ArrayList<>();
        for (Node node : nodes) {
            valuesList.add(node.toArrayList());
        }
        return valuesList;
    }

    /**
     * Converts a list of edges into a list of all the edges' fields
     *
     * @param edges The list of edges to be converted
     * @return A list of lists of entries representing individual edges
     */

    @Override
    public ArrayList<ArrayList<String>> convertEdgesToValuesList(List<Edge> edges) {
        ArrayList<ArrayList<String>> valuesList = new ArrayList<>();

        for (Edge edge : edges) {
            valuesList.add(edge.toArrayList());
        }

        return valuesList;
    }

    /**
     * cacheNode: Populates the node cache with nodes from the Database
     */
    @Override
    public void cacheNodesFromDB() {
        ResultSet resSet = db.executeQuery(new SQLEntry(DBConstants.SELECT_ALL_NODES));
        clearNodeCache();
        ArrayList<ArrayList<String>> nodeData = db.getTableFromResultSet(resSet);

        for (ArrayList<String> row : nodeData) {
            nodeCache.add(new Node(row.get(0),
                    new Point2D(Double.parseDouble(row.get(1)), Double.parseDouble(row.get(2))),
                    Integer.parseInt(row.get(3)), row.get(4), row.get(5), row.get(6), row.get(7)));
        }
    }

    /**
     * clearNodeCache: Clears the Nodes cache
     */
    @Override
    public void clearNodeCache() {
        nodeCache.clear();
    }

    @Override
    public ArrayList<Node> getNodeCache() {
        return nodeCache;
    }

    /**
     * cacheEdgesFromDB: Populates the edge cache with edges from the Database
     */
    @Override
    public void cacheEdgesFromDB() {
        ResultSet resSet = db.executeQuery(new SQLEntry(DBConstants.SELECT_ALL_EDGES));
        clearEdgeCache();
        ArrayList<ArrayList<String>> edgeData = db.getTableFromResultSet(resSet);

        for (ArrayList<String> row : edgeData) {
            Edge newEdge = new Edge(searchNodeCache(row.get(1)), searchNodeCache(row.get(2)));
            newEdge.getSource().addEdgeTwoWay(newEdge);
            edgeCache.add(newEdge);
        }
    }

    /**
     * Searches the nodeCache for a node with a particular nodeID.
     *
     * @param nodeID The nodeID to search for.
     * @return The node with the associated nodeID. {null} If the nodeID is not found.
     */
    @Override
    public Node searchNodeCache(String nodeID) {
        for (Node node : nodeCache) {
            if (node.getID().equals(nodeID)) return node;
        }
        System.out.println("Did not find node");
        return null;
    }

    /**
     * clearEdgeCache: Clears the Edges cache
     */
    @Override
    public void clearEdgeCache() {
        edgeCache.clear();
    }
}
