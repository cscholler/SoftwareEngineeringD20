package edu.wpi.cs3733.d20.teamL.entities;

import java.util.HashMap;

public class Edge {

  //TODO make private & getters/setters
  public int length;
  public Node destination;
  public HashMap<String, Object> data = new HashMap<>();

  private String ID;

  Node source;

  public Edge(Node dest, int len) {
    destination = dest;
    length = len;
  }

  /**
   * Getter for source, the node this edge comes from.
   *
   * @return The Node object that this edge comes from
   */
  public Node getSource() {
    return source;
  }

  /**
   * Setter for source
   *
   * @param newSource The Node that is to be the new source Node for this Edge
   */
  public void setSource(Node newSource) {
    if (source != null) source.removeEdge(this);
    if (newSource != null) newSource.addEdge(this);
  }
}
