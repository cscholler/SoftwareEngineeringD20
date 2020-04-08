package edu.wpi.leviathans.pathFinding.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class Graph {

  private HashMap<String, Node> nodes;

  public Graph() {
    nodes = new HashMap<String, Node>();
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
  public ArrayList<Edge> getEdges() {
    ArrayList<Edge> edges = new ArrayList<>();

    for (Node node : getNodes()) {
      for (Edge edge : node.getEdges()) {
        if (!edges.contains(edge)) edges.add(edge);
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
    String name = newNode.getName();
    newNode.graph = this;

    nodes.put(name, newNode);
  }

  /**
   * Adds all nodes in the given collection.
   *
   * @param newNodes Collection of nodes to add
   */
  public void addAllNodes(Collection<Node> newNodes) {
    for (Node node : newNodes) {
      addNode(node);
    }
  }

  /**
   * Gets a node within this graph based on its name
   *
   * @param nodeName
   */
  public Node getNode(String nodeName) {
    return nodes.get(nodeName);
  }

  /**
   * Removes a given node.
   *
   * @param node The node to remove
   */
  public void removeNode(Node node) {
    nodes.remove(node.getName());
  }

  /**
   * Removes a Node based on its name.
   *
   * @param name String containing the name of the node to remove
   */
  public void removeNode(String name) {
    nodes.remove(name);
  }
}
