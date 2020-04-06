package edu.wpi.leviathans.pathFinding;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import edu.wpi.leviathans.pathFinding.graph.Edge;
import edu.wpi.leviathans.pathFinding.graph.Graph;
import edu.wpi.leviathans.pathFinding.graph.Node;
import org.junit.jupiter.api.Test;

class PathFinderTest {
  @Test
  public void createGraph() throws Exception {
    PathFinder pathfinder = new PathFinder();
    Graph newGraph = new Graph();
    Node n1 = new Node("one");
    Node n2 = new Node("two");
    Node n3 = new Node("three");
    Node n4 = new Node("four");

    newGraph.addNode(n1);
    newGraph.addNode(n2);
    newGraph.addNode(n3);
    newGraph.addNode(n4);

    n1.addEdgeTwoWay(new Edge(n2, 3));
    n2.addEdgeTwoWay(new Edge(n3, 5));
    n3.addEdgeTwoWay(new Edge(n4, 2));
    n4.addEdgeTwoWay(new Edge(n1, 7));
    n1.addEdgeTwoWay(new Edge(n3, 348));
    n2.addEdgeTwoWay(new Edge(n4, 6));

    assertEquals(1, 1);
    // assertEquals(newGraph.getNode("one").getName(), "one");

    assertEquals(3, pathfinder.aStarPathFind(newGraph, n1, n3).size());
  }
}
