package edu.wpi.cs3733.d20.teamL.services.db;

import edu.wpi.cs3733.d20.teamL.entities.Edge;
import edu.wpi.cs3733.d20.teamL.entities.Node;
import edu.wpi.cs3733.d20.teamL.services.graph.Graph;
import javafx.geometry.Point2D;

import javax.inject.Inject;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class DBCache implements IDBCache {
    private Graph graphCache = new Graph();
    private ArrayList<Node> nodeCache = new ArrayList<>();
    private ArrayList<Edge> edgeCache = new ArrayList<>();

    private ArrayList<Node> editedNodes = new ArrayList<>();

    private ArrayList<Node> addedNodes = new ArrayList<>();
    private ArrayList<Edge> addedEdges = new ArrayList<>();

    private ArrayList<Node> deletedNodes = new ArrayList<>();
    private ArrayList<Edge> deletedEdges = new ArrayList<>();

    @Inject
    IDatabaseService db;

	@Override
	public void cacheAllFromDB() {
		cacheNodesFromDB();
		cacheEdgesFromDB();
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

        Iterator<Node> iterator = editedNodes.iterator();
        while (iterator.hasNext()) {
            Node node = iterator.next();
            if (deletedNodes.contains(node)) iterator.remove();
        }

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
        ArrayList<String> updates = new ArrayList<>();

        // Concatenate the node and edges lists for one update on the cache
        ArrayList<ArrayList<String>> valuesList = new ArrayList<>();

        // Add nodes
        ArrayList<ArrayList<String>> addValues = convertNodesToValuesList(addedNodes);
        for (ArrayList<String> nodeString : addValues) {
            updates.add(DBConstants.addNode);
        }
        valuesList.addAll(addValues);

        // Add edges
        addValues = convertEdgesToValuesList(addedEdges);
        for (ArrayList<String> edgeString : addValues) {
            updates.add(DBConstants.addEdge);
        }
        valuesList.addAll(addValues);

        // Delete edges
        for (Edge edge : deletedEdges) {
            updates.add(DBConstants.removeEdge);
            valuesList.add(new ArrayList<>(Arrays.asList(edge.getID())));
        }

        // Edit nodes
        ArrayList<ArrayList<String>> editValues = convertNodesToValuesList(editedNodes);
        for (int i = 0; i < editValues.size(); i++) {
            updates.add(DBConstants.updateNode);
            ArrayList<String> currentNode = editValues.get(i);
            String nodeID = currentNode.get(0);
            currentNode.remove(0);
            currentNode.add(nodeID);
            valuesList.add(currentNode);
        }

        // Delete nodes
        ArrayList<ArrayList<String>> deleteValues = convertNodesToValuesList(deletedNodes);
        for (ArrayList<String> nodeString : deleteValues) {
            updates.add(DBConstants.removeNode);
            valuesList.add(new ArrayList<>(Arrays.asList(nodeString.get(0))));
        }

        db.executeUpdates(updates, valuesList); // TODO: Fix SQL error by preventing from adding duplicate nodes

        addedEdges.clear();
        addedEdges.clear();
        editedNodes.clear();
        deletedEdges.clear();
        deletedNodes.clear();
    }

    @Override
	public ArrayList<ArrayList<String>> convertNodesToValuesList(List<Node> nodes) {
        ArrayList<ArrayList<String>> valuesList = new ArrayList<>();

        for (Node node : nodes) {
            valuesList.add(node.toArrayList());
        }

        return valuesList;
    }

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
        ResultSet resSet = db.executeQuery(DBConstants.selectAllNodes);
        clearNodeCache();
        ArrayList<ArrayList<String>> nodeData = db.getTableFromResultSet(resSet);

        for(ArrayList<String> row : nodeData) {
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
        ResultSet resSet = db.executeQuery(DBConstants.selectAllEdges);
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

	@Override
	public void disconnectDB() {
		db.stopService();
	}
}
