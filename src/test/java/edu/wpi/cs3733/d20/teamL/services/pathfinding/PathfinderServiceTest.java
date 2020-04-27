package edu.wpi.cs3733.d20.teamL.services.pathfinding;

import static org.junit.jupiter.api.Assertions.assertEquals;

import edu.wpi.cs3733.d20.teamL.entities.Edge;
import edu.wpi.cs3733.d20.teamL.entities.Node;
import edu.wpi.cs3733.d20.teamL.entities.Graph;
import edu.wpi.cs3733.d20.teamL.entities.Path;
import javafx.geometry.Point2D;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

class PathfinderServiceTest {
    @Test
    public void createGraph() throws Exception {
        PathfinderService pathfinderService = new PathfinderService();
        pathfinderService.setPathfindingMethod(PathfinderService.PathfindingMethod.Astar);
        Graph newGraph = new Graph();
        Node n1 = new Node("n1", new Point2D(1, 2), 1, "A");
        Node n2 = new Node("n2", new Point2D(2, 1), 1, "A");
        Node n3 = new Node("n3", new Point2D(2, -1), 1, "A");
        Node n4 = new Node("n4", new Point2D(1, -2), 1, "A");
        Node n5 = new Node("n5", new Point2D(-1, -2), 1, "A");
        Node n6 = new Node("n6", new Point2D(-2, -1), 1, "A");
        Node n7 = new Node("n7", new Point2D(-2, 1), 1, "A");
        Node n8 = new Node("n8", new Point2D(-1, 2), 1, "A");
        Node n9 = new Node("n9", new Point2D(0, 0), 1, "A");

        newGraph.addNode(n1);
        newGraph.addNode(n2);
        newGraph.addNode(n3);
        newGraph.addNode(n4);
        newGraph.addNode(n5);
        newGraph.addNode(n6);
        newGraph.addNode(n7);
        newGraph.addNode(n8);
        newGraph.addNode(n9);

        n1.addEdgeTwoWay(n8);
        n1.addEdgeTwoWay(n9);
        n2.addEdgeTwoWay(n9);
        n3.addEdge(n2);
        n3.addEdgeTwoWay(n4);
        n4.addEdgeTwoWay(n9);
        n4.addEdge(n5);
        n5.addEdgeTwoWay(n6);
        n6.addEdge(n9);
        n6.addEdgeTwoWay(n7);
        n7.addEdge(n8);

        assertEquals(newGraph.getNode("n1").getID(), "n1");
        assertEquals(newGraph.getNode("n2").getNeighbors(), new ArrayList<Node>(Arrays.asList(n9)));
        assertEquals(newGraph.getNode("n9").getNeighbors(), new ArrayList<Node>(Arrays.asList(n1, n2, n4)));

        Path path1 = pathfinderService.pathfind(newGraph, n8, n1);
        assertEquals(2, path1.getLength());
        assertEquals(new ArrayList<Node>(Arrays.asList(n8, n1)), path1.getPathNodes());
        System.out.println(path1.generateTextMessage());

        Path path2 = pathfinderService.pathfind(newGraph, n6, n1);
        assertEquals(4, path2.getLength());
        assertEquals(new ArrayList<>(Arrays.asList(n6, n9, n1)), path2.getPathNodes());
        System.out.println(path2.generateTextMessage());
    }
}
