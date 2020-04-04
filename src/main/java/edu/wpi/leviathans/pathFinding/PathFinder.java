package edu.wpi.leviathans.pathFinding;

import edu.wpi.leviathans.pathFinding.graph.*;
import java.util.*;

public class PathFinder {

  class NodeEntry {
    public Node node;
    public Node parent;
    public int shortestPath = Integer.MAX_VALUE;
    public int distFromDest = 0;

    public NodeEntry(Node p_node) {
      node = p_node;
    }

    public NodeEntry(Node p_node, int p_shortestPath) {
      node = p_node;
      shortestPath = p_shortestPath;
    }

    public int absoluteDist() {
      return shortestPath + distFromDest;
    }
  }

  private Comparator<NodeEntry> NodeEntrySorter = Comparator.comparing(NodeEntry::absoluteDist);
  private PriorityQueue<NodeEntry> priorityQueue = new PriorityQueue<>(NodeEntrySorter);

  /**
   * Uses the A-Star algorithm to get a path between the source and destination node. Throws error
   * if there is no path.
   *
   * @param source The Node to start with
   * @param destination The Node to pathfind to
   * @return a list of Nodes in representing the path between source and destination (inclusive).
   */
  public LinkedList<Node> aStarPathFind(Graph graph, Node source, Node destination) {
    // TODO: Implement

    // Initialize all nodes in the priority queue to infinity but don't add the source
    for (Node node : graph.getNodes()) {
      if (!node.equals(source)) {
        NodeEntry newEntry = new NodeEntry(node);
        priorityQueue.add(newEntry);
      }
    }
    priorityQueue.add(new NodeEntry(source,0));
    // Start from the source Node and recursively update the priority Queue
    NodeEntry destNode = aStarPathFindHelper(source, destination);

    return null;
  }

  private static NodeEntry aStarPathFindHelper(Node source, Node destination) {
    // TODO: Implement
    // Return null if there is no path between source and destination.
    return null;
  }

  /**
   * Converts a NodeEntry's chain of parents into a list representing the path
   *
   * @param entry
   * @return
   */
  private static LinkedList<Node> entryToList(NodeEntry entry) {
    // TODO: Impliment
    return null;
  }
}
