package edu.wpi.cs3733.d20.teamL.util.pathfinding.graph;

import static org.junit.jupiter.api.Assertions.*;

import edu.wpi.cs3733.d20.teamL.entities.Edge;
import edu.wpi.cs3733.d20.teamL.entities.Node;
import edu.wpi.cs3733.d20.teamL.services.graph.Graph;
import org.junit.jupiter.api.Test;

class GraphTest {
  @Test
  public void createGraph() throws Exception {
    Graph newGraph = new Graph();
    Node test_1 = new Node("test_1");
    Node test_2 = new Node("test_2");
    newGraph.addNode(test_1);
    newGraph.addNode(test_2);

    test_1.addEdgeTwoWay(new Edge(test_2, 3));

    assertEquals("test_1", test_2.getEdge(test_1).destination.getID());
  }
}
