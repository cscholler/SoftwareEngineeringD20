package edu.wpi.leviathans.pathFinding.graph;

public class Edge {

  public int length;
  public Node destination;

  Node source;

  public Edge(Node dest, int len) {
    destination = dest;
    length = len;
  }
}
