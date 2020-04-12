package edu.wpi.leviathans.views;

import com.google.inject.Inject;
import edu.wpi.leviathans.pathFinding.Path;
import edu.wpi.leviathans.pathFinding.PathFinder;
import edu.wpi.leviathans.pathFinding.graph.Edge;
import edu.wpi.leviathans.pathFinding.graph.Graph;
import edu.wpi.leviathans.pathFinding.graph.Node;

import java.util.Iterator;

import edu.wpi.leviathans.services.db.DatabaseService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class PathfinderController {
	PathFinder pathfinder = new PathFinder();
	Graph newGraph = new Graph();
	@Inject
	DatabaseService db;

	Node n1 = new Node("n1");
	Node n2 = new Node("n2");
	Node n3 = new Node("n3");
	Node n4 = new Node("n4");
	Node n5 = new Node("n5");
	Node n6 = new Node("n6");
	Node n7 = new Node("n7");
	Node n8 = new Node("n8");
	Node n9 = new Node("n9");

	private boolean setupGraph = false;

	@FXML
	private TextField end, start;

	@FXML
	private Button btn;

	@FXML
	private TextArea txt;

	private void generateGraph() {
		newGraph.addNode(n1);
		newGraph.addNode(n2);
		newGraph.addNode(n3);
		newGraph.addNode(n4);
		newGraph.addNode(n5);
		newGraph.addNode(n6);
		newGraph.addNode(n7);
		newGraph.addNode(n8);
		newGraph.addNode(n9);

		n1.addEdgeTwoWay(new Edge(n8, 3));
		n1.addEdge(new Edge(n9, 5));
		n2.addEdgeTwoWay(new Edge(n9, 4));
		n3.addEdge(new Edge(n2, 3));
		n3.addEdgeTwoWay(new Edge(n4, 2));
		n4.addEdgeTwoWay(new Edge(n9, 5));
		n4.addEdge(new Edge(n5, 4));
		n5.addEdgeTwoWay(new Edge(n6, 2));
		n6.addEdge(new Edge(n9, 6));
		n6.addEdgeTwoWay(new Edge(n7, 1));
		n7.addEdge(new Edge(n8, 3));
	}

	@FXML
	private void generatePath(ActionEvent event) {

		if (!setupGraph) {
			setupGraph = true;
			generateGraph();
		}

		Node startNode = newGraph.getNode(start.getText());
		Node endNode = newGraph.getNode(end.getText());

		Path path = pathfinder.aStarPathFind(newGraph, startNode, endNode);

		txt.setText(pathToString(path));
	}

	private String pathToString(Path path) {

		String newString = "Path:\n\t";

		Iterator<Node> nodes = path.iterator();

		while (nodes.hasNext()) {
			Node currentNode = nodes.next();
			newString += currentNode.getName();
			if (nodes.hasNext()) newString += " --> ";
		}

		newString += "\n\nPath Length:\n\t" + path.getLength();

		return newString;
	}
}
