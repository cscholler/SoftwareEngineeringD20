package edu.wpi.leviathans.pathFinding.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class Node {

    public Graph graph;
    public HashMap<String, Object> data = new HashMap<>();

    private Collection<Edge> edges = new ArrayList<>();
    private String name;

    public Node(String p_name) {
        name = p_name;
    }

    public Node(String p_name, Collection<Edge> p_edges) {
        name = p_name;
        edges = p_edges;
    }

    public Collection<Edge> getEdges() {
        return edges;
    }

    /**
     * Finds the edge associated with the given node, the node must be pointed to by one of the edges of this node.
     *
     * @param otherNode A node that is pointed to by one of the edges of this node
     * @return The edge associated with the given node, null if it is not found.
     */
    public Edge getEdge(Node otherNode) {
        for (Edge edge : edges) {
            if (edge.destination.equals(otherNode)) return edge;
        }
        return null;
    }

    /**
     * Sets the name to the given string.
     *
     * @param newName String containing the new name
     */
    void setName(String newName) {
        // Remove and re-add the node so its hash is updated in the graph
        if (graph != null) graph.removeNode(this);

        name = newName;

        if (graph != null) graph.addNode(this);
    }

    /**
     * @return String of the name of this node
     */
    public String getName() {
        return name;
    }

    /**
     * Adds a new edge to the Node.
     *
     * @param newEdge The edge to add
     */
    public void addEdge(Edge newEdge) {
        edges.add(newEdge);
        newEdge.source = this;
    }

    /**
     * Adds a new edge to the Node and one to the destination node to this one.
     *
     * @param newEdge The edge to add
     */
    public void addEdgeTwoWay(Edge newEdge) {
        addEdge(newEdge);

        Edge otherEdge = new Edge(this, newEdge.length);

        newEdge.destination.addEdge(otherEdge);
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
  /*public <T extends Number> T getNumberData(String key) {
      return (T) (Number) Double.parseDouble(data.get(key));
  }

  /**
   * Adds an number value to the nodes data with the given label
   *
   * @param key   The name of the data field
   * @param value The number value to add (could be integer, double, float, etc.)
   */
  /*public void putNumberData(String key, Number value) {
      data.put(key, value.toString());
  }*/

    /**
     * Finds an edge in this Node that leads to a specified Node.
     *
     * @return the Edge that leads to the specified Node, null if it is not found.
     */
    public Edge edgeFromDest(Node otherNode) {
        Edge result = null;
        for (Edge edge : edges) {
            if (edge.destination.equals(otherNode)) result = edge;
        }
        return result;
    }
}
