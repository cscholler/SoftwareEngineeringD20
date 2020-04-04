package edu.wpi.leviathans.pathFinding.graph;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class Node {

  public Graph graph;
  public HashMap<String, String> data;

  private List<Edge> edges;
  private String name; // This is deliberate, I want Graph to be able to access this.

  public Node(String p_name) {
    name = p_name;
  }

  public Node(String p_name, List<Edge> p_edges) {
    name = p_name;
    edges = p_edges;
  }

  /**
   * Sets the name to the given string.
   *
   * @param newName String containing the new name
   */
  void setName(String newName) {
    // Remove and re-add the node so its hash is updated in the graph
    graph.removeNode(this);
    name = newName;

    graph.addNode(this);
  }

  /** @return String of the name of this node */
  public String getName() {
    return name;
  }

  /**
   * Adds a new edge to the Node
   *
   * @param newEdge The edge to add
   */
  public void addEdge(Edge newEdge) {
    edges.add(newEdge);
    newEdge.source = this;
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
    edges.remove(toRemove);
    toRemove.source = null;
  }

  /**
   * Gets number data from the node
   *
   * @param key The name of the data field
   * @return the number associated with the given key (could be integer, double, float, etc.)
   */
  public <T extends Number> T getNumberData(String key) {
    return (T) (Number) Double.parseDouble(data.get(key));
  }

  /**
   * Adds an number value to the nodes data with the given label
   *
   * @param key The name of the data field
   * @param value The number value to add (could be integer, double, float, etc.)
   */
  public void putNumberData(String key, Number value) {
    data.put(key, value.toString());
  }
}
