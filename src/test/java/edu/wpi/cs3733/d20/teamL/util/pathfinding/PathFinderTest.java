package edu.wpi.cs3733.d20.teamL.util.pathfinding;

import static org.junit.jupiter.api.Assertions.assertEquals;

import edu.wpi.cs3733.d20.teamL.entities.Edge;
import edu.wpi.cs3733.d20.teamL.entities.Node;
import edu.wpi.cs3733.d20.teamL.services.graph.Graph;
import edu.wpi.cs3733.d20.teamL.services.graph.Path;
import edu.wpi.cs3733.d20.teamL.services.graph.PathFinder;
import javafx.geometry.Point2D;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class PathFinderTest {
    @Test
    public void createGraph() throws Exception {
        PathFinder pathfinder = new PathFinder();
        Graph newGraph = new Graph();
        Node n1 = new Node("n1", new Point2D(1, 2));
        Node n2 = new Node("n2", new Point2D(2, 1));
        Node n3 = new Node("n3", new Point2D(2, -1));
        Node n4 = new Node("n4", new Point2D(1, -2));
        Node n5 = new Node("n5", new Point2D(-1, -2));
        Node n6 = new Node("n6", new Point2D(-2, -1));
        Node n7 = new Node("n7", new Point2D(-2, 1));
        Node n8 = new Node("n8", new Point2D(-1, 2));
        Node n9 = new Node("n9", new Point2D(0, 0));

        newGraph.addNode(n1);
        newGraph.addNode(n2);
        newGraph.addNode(n3);
        newGraph.addNode(n4);
        newGraph.addNode(n5);
        newGraph.addNode(n6);
        newGraph.addNode(n7);
        newGraph.addNode(n8);
        newGraph.addNode(n9);

        n1.addEdgeTwoWay(new Edge("new_edge", n1, n8));
        n1.addEdgeTwoWay(new Edge("new_edge", n1, n9));
        n2.addEdgeTwoWay(new Edge("new_edge", n2, n9));
        n3.addEdge(new Edge("new_edge", n3, n2));
        n3.addEdgeTwoWay(new Edge("new_edge", n3, n4));
        n4.addEdgeTwoWay(new Edge("new_edge", n4, n9));
        n4.addEdge(new Edge("new_edge", n4, n5));
        n5.addEdgeTwoWay(new Edge("new_edge", n5, n6));
        n6.addEdge(new Edge("new_edge", n6, n9));
        n6.addEdgeTwoWay(new Edge("new_edge", n6, n7));
        n7.addEdge(new Edge("new_edge", n7, n8));

        assertEquals(newGraph.getNode("n1").getID(), "n1");
        assertEquals(newGraph.getNode("n2").getNeighbors(), new ArrayList<Node>(Arrays.asList(n9)));
        assertEquals(newGraph.getNode("n9").getNeighbors(), new ArrayList<Node>(Arrays.asList(n1, n2, n4)));

        assertEquals(2, pathfinder.aStarPathFind(newGraph, n8, n1).getLength());
        assertEquals(new ArrayList<Node>(Arrays.asList(n8, n1)), pathfinder.aStarPathFind(newGraph, n8, n1).getPathNodes());

        assertEquals(4, pathfinder.aStarPathFind(newGraph, n6, n1).getLength());
        assertEquals(new ArrayList<>(Arrays.asList(n6, n9, n1)), pathfinder.aStarPathFind(newGraph, n6, n1).getPathNodes());
    }
}