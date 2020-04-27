package edu.wpi.cs3733.d20.teamL.services.pathfinding.graph;

import static org.junit.jupiter.api.Assertions.*;

import edu.wpi.cs3733.d20.teamL.entities.Edge;
import edu.wpi.cs3733.d20.teamL.entities.Node;
import edu.wpi.cs3733.d20.teamL.entities.Graph;
import javafx.geometry.Point2D;
import org.junit.jupiter.api.Test;

class GraphTest {
  @Test
  public void createGraph() throws Exception {
    Graph newGraph = new Graph();
    Node test_1 = new Node("test_1", new Point2D(0, 0), 1, "A");
    Node test_2 = new Node("test_2", new Point2D(0, 0), 1, "A");
    newGraph.addNode(test_1);
    newGraph.addNode(test_2);

    test_1.addEdgeTwoWay(new Edge(test_1, test_2));

    assertEquals("test_1", test_2.getEdge(test_1).getDestination().getID());
  }
}
