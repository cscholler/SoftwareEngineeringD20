package edu.wpi.cs3733.d20.teamL.services.db;

import edu.wpi.cs3733.d20.teamL.entities.Edge;
import edu.wpi.cs3733.d20.teamL.entities.Node;
import edu.wpi.cs3733.d20.teamL.services.graph.Graph;
import javafx.geometry.Point2D;

import java.sql.ResultSet;
import java.util.ArrayList;

public class DBCache {
	private static DBCache cache;

	//Tables that can be cached
	private Graph graphCache = new Graph();
	private ArrayList<Node> nodeCache = new ArrayList<>();
	private ArrayList<Edge> edgeCache = new ArrayList<>();

	DatabaseService db = new DatabaseService(true); //this should be changes eventually

	public void cacheAll() {
		cacheNodes();
		cacheEdges();
		disconnectDB();
	}

	/**
	 * cacheNode: Populates the node cache with nodes from the Database
	 */
	public void cacheNodes() {
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
	 * cacheEdges: Populates the edge cache with edges from the Database
	 */
	public void cacheEdges() {
		ResultSet resSet = db.executeQuery(DBConstants.selectAllEdges);
		clearEdgeCache();
		ArrayList<ArrayList<String>> edgeData = db.getTableFromResultSet(resSet);

		for (ArrayList<String> row : edgeData) {
			edgeCache.add(new Edge(row.get(0), searchNodeCache(row.get(1)), searchNodeCache(row.get(2))));
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
