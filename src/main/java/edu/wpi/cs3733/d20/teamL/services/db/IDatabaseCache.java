package edu.wpi.cs3733.d20.teamL.services.db;

import edu.wpi.cs3733.d20.teamL.entities.Edge;
import edu.wpi.cs3733.d20.teamL.entities.Gift;
import edu.wpi.cs3733.d20.teamL.entities.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface IDatabaseCache {
	void cacheAllFromDB();

	void cacheNodes(ArrayList<Node> newNodes, ArrayList<Node> editedNodes);

	void cacheEdges(ArrayList<Edge> newEdges);

	void updateDB();

	ArrayList<ArrayList<String>> convertNodesToValuesList(List<Node> nodes);

	ArrayList<ArrayList<String>> convertEdgesToValuesList(List<Edge> edges);

	void cacheNodesFromDB();

	void clearNodeCache();

	List<Node> getNodeCache();

	List<Edge> getEdgeCache();

	void cacheEdgesFromDB();

	Node searchNodeCache(String nodeID);

	void clearEdgeCache();

	void cacheGiftsFromDB();

	void clearGiftsCache();

	void cacheCart(Map<String,Integer> cart);

	Map<String,Integer> getCartCache();

	ArrayList<Gift> getGiftsCache();

	void clearCartCache();
}
