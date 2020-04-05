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
  private PriorityQueue<NodeEntry> priorityQueue = new PriorityQueue<NodeEntry>(NodeEntrySorter);
  private HashMap<Node, NodeEntry> priorityQueueKey = new HashMap<Node, NodeEntry>();

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
        newEntry.distFromDest = 1; // TODO: Distances
        priorityQueue.add(newEntry);
        priorityQueueKey.put(node, newEntry);
      }
    }

    NodeEntry sourceEntry = new NodeEntry(source, 0);
    sourceEntry.distFromDest = 1;

    priorityQueue.add(sourceEntry);
    priorityQueueKey.put(source, sourceEntry);

    // Start from the source Node and recursively update the priority Queue
    NodeEntry destNode = aStarPathFindHelper(source, destination);

    return entryToList(destNode);
  }

  private NodeEntry aStarPathFindHelper(Node source, Node destination) {
    // TODO: Implement
    while (!priorityQueue.isEmpty()) {
      NodeEntry currentNode = priorityQueue.poll();

      if (currentNode.node.equals(destination)) {
        return currentNode;
      }

      for (Edge edge : currentNode.node.getEdges()) {
        Node otherNode = edge.destination;

        // Set otherNode NodeEntry shortestPath to currentNode.shortestPath + edge.length
        NodeEntry otherNodeEntry = priorityQueueKey.get(otherNode);
        if (currentNode.shortestPath + edge.length < otherNodeEntry.shortestPath) {
          otherNodeEntry.shortestPath = currentNode.shortestPath + edge.length;
          otherNodeEntry.parent = currentNode.node;
          // otherNodeEntry.distFromDest += edge.length;
        }
      }
    }

    // Return null if there is no path between source and destination.
    return null;
  }

  /**
   * Converts a NodeEntry's chain of parents into a list representing the path
   *
   * @param entry
   * @return
   */
  private LinkedList<Node> entryToList(NodeEntry entry) {
    // TODO: Impliment
    LinkedList<Node> path = new LinkedList<Node>();
    NodeEntry current = entry;
    while (current.shortestPath != 0) {
      path.addFirst(current.node);
      System.out.println(current.node.getName());
      current = priorityQueueKey.get(current.parent);
    }
    System.out.println(current.node.getName());
    path.addFirst(current.node);
    return path;
  }
}
