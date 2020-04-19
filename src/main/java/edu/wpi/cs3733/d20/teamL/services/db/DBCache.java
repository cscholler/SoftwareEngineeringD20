package edu.wpi.cs3733.d20.teamL.services.db;

import edu.wpi.cs3733.d20.teamL.entities.Edge;
import edu.wpi.cs3733.d20.teamL.entities.Node;
import edu.wpi.cs3733.d20.teamL.services.graph.Graph;
import javafx.geometry.Point2D;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DBCache {
    private static DBCache cache;

    public DBCache() {
        cacheNodesFromDB();
        cacheEdgesFromDB();
    }

    //Tables that can be cached
    private Graph graphCache = new Graph();
    private ArrayList<Node> nodeCache = new ArrayList<>();
    private ArrayList<Edge> edgeCache = new ArrayList<>();

    private ArrayList<Node> editedNodes = new ArrayList<>();
    private ArrayList<Edge> editedEdges = new ArrayList<>();

    private ArrayList<Node> addedNodes = new ArrayList<>();
    private ArrayList<Edge> addedEdges = new ArrayList<>();

    private ArrayList<Node> deletedNodes = new ArrayList<>();
    private ArrayList<Edge> deletedEdges = new ArrayList<>();

    DatabaseService db = new DatabaseService(); //this should be changes eventually

	public void cacheAllFromDB() {
		cacheNodesFromDB();
		cacheEdgesFromDB();
		disconnectDB();
	}

    /**
     * Sets the nodes cache to the given ArrayList of nodes. This will overwrite the cache and eventually overwrite the database, use this when you are sure you want to make changes to the map.
     *
     * @param nodes A new list of nodes to set the nodes cache to.
     */
	public void cacheNodes(ArrayList<Node> nodes) {
        for (Node node : nodes) {
            if (!nodeCache.contains(node)) addedNodes.add(node);
            else {
                for (Node prevNode : nodeCache) {
                    if (node.getID().equals(prevNode.getID()) && !node.equals(prevNode)) {
                        editedNodes.add(node);
                        break;
                    }
                }
            }
        }

        for (Node node : nodeCache) if (!nodes.contains(node)) deletedNodes.add(node);
	    
	    this.nodeCache = nodes;
    }

    /**
     * Sets the edges cache to the given ArrayList of edges. This will overwrite the cache and eventually overwrite the database, use this when you are sure you want to make changes to the map.
     *
     * @param edges A new list of edges to set the edges cache to.
     */
    public void cacheEdges(ArrayList<Edge> edges) {
        for (Edge edge : edges) {
            if (!edgeCache.contains(edge)) addedEdges.add(edge);
            else {
                for (Edge prevEdge : edgeCache) {
                    if (edge.getID().equals(prevEdge.getID()) && !edge.equals(prevEdge)) {
                        editedEdges.add(edge);
                        break;
                    }
                }
            }
        }

        for (Edge edge : edgeCache) if (!edges.contains(edge)) deletedEdges.add(edge);

        this.edgeCache = edges;
    }

    /**
     * Pushes all caches to the database.
     */
    public void updateDB() {
        ArrayList<String> updates = new ArrayList<>();

        // Concatenate the node and edges lists for one update on the cache
        ArrayList<ArrayList<String>> valuesList = new ArrayList<>();

        ArrayList<ArrayList<String>> addValues = convertNodesToValuesList(addedNodes);
        ArrayList<ArrayList<String>> editValues = convertNodesToValuesList(editedNodes);
        ArrayList<ArrayList<String>> deleteValues = convertNodesToValuesList(deletedNodes);

        for (ArrayList<String> nodeString : addValues) {
            updates.add(DBConstants.addNode);
        }
        valuesList.addAll(addValues);

        for (ArrayList<String> nodeString : editValues) {
            updates.add(DBConstants.updateNode);
        }
        valuesList.addAll(editValues);

        for (ArrayList<String> nodeString : deleteValues) {
            updates.add(DBConstants.removeNode);
            valuesList.add(new ArrayList<>(Arrays.asList(nodeString.get(0))));
        }

        addValues = convertEdgesToValuesList(addedEdges);
        editValues = convertEdgesToValuesList(editedEdges);
        deleteValues = convertEdgesToValuesList(deletedEdges);

        for (ArrayList<String> edgeString : addValues) {
            updates.add(DBConstants.addEdge);
        }
        valuesList.addAll(addValues);

        for (ArrayList<String> edgeString : editValues) {
            updates.add(DBConstants.updateEdge);
        }
        valuesList.addAll(editValues);

        for (ArrayList<String> edgeString : deleteValues) {
            updates.add(DBConstants.removeEdge);
            valuesList.add(new ArrayList<>(Arrays.asList(edgeString.get(0))));
        }

        db.executeUpdates(updates, valuesList);
    }

    private ArrayList<ArrayList<String>> convertNodesToValuesList(List<Node> nodes) {
        ArrayList<ArrayList<String>> valuesList = new ArrayList<>();

        for (Node node : nodes) {
            valuesList.add(node.toArrayList());
        }

        return valuesList;
    }

    private ArrayList<ArrayList<String>> convertEdgesToValuesList(List<Edge> edges) {
        ArrayList<ArrayList<String>> valuesList = new ArrayList<>();

        for (Edge edge : edges) {
            valuesList.add(edge.toArrayList());
        }

        return valuesList;
    }

    /**
     * cacheNode: Populates the node cache with nodes from the Database
     */
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
    public void clearNodeCache() {
        nodeCache.clear();
    }

    public ArrayList<Node> getNodeCache() {
        return nodeCache;
    }

    /**
     * cacheEdgesFromDB: Populates the edge cache with edges from the Database
     */
    public void cacheEdgesFromDB() {
        ResultSet resSet = db.executeQuery(DBConstants.selectAllEdges);
        clearEdgeCache();
        ArrayList<ArrayList<String>> edgeData = db.getTableFromResultSet(resSet);

        for (ArrayList<String> row : edgeData) {
            Edge newEdge = new Edge(row.get(0), searchNodeCache(row.get(1)), searchNodeCache(row.get(2)));
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
    public void clearEdgeCache() {
        edgeCache.clear();
    }

	public void disconnectDB() {
		db.stopService();
	}
}
