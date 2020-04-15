package edu.wpi.leviathans.views;

import com.google.inject.Inject;

import edu.wpi.leviathans.util.pathfinding.MapParser;
import edu.wpi.leviathans.util.pathfinding.Path;
import edu.wpi.leviathans.util.pathfinding.PathFinder;
import edu.wpi.leviathans.util.pathfinding.graph.Edge;
import edu.wpi.leviathans.util.pathfinding.graph.Graph;
import edu.wpi.leviathans.util.pathfinding.graph.Node;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.ResourceBundle;

import edu.wpi.leviathans.services.db.DBConstants;
import edu.wpi.leviathans.services.db.DatabaseService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PathfinderController {
	PathFinder pathfinder = new PathFinder();
	Graph newGraph = new Graph();
	//@Inject
	DatabaseService db = new DatabaseService();

	public Graph initialize() {
		try {
			generateGraph();
			return newGraph;
		} catch (SQLException ex) {
			log.error("Encountered SQLException.", ex);
		}
		return null;
	}

	private void generateGraph() throws SQLException {
		ResultSet rs = db.executeQuery(DBConstants.selectAllNodes);

		while (rs.next()) {
			Node newNode = new Node(rs.getString(1));
			newNode.position = new Point2D(Double.parseDouble(rs.getString(2)), Double.parseDouble(rs.getString(3)));
			newNode.data.put(MapParser.DATA_LABELS.NODE_TYPE, rs.getString(6));
			newNode.data.put(MapParser.DATA_LABELS.LONG_NAME, rs.getString(7));
			newNode.data.put(MapParser.DATA_LABELS.SHORT_NAME, rs.getString(8));

			newGraph.addNode(newNode);
		}

		rs = db.executeQuery(DBConstants.selectAllEdges);

		while (rs.next()) {
			Node source = newGraph.getNode(rs.getString(2));
			Node destination = newGraph.getNode(rs.getString(3));

			if (source != null && destination != null) {
				double x1 = source.position.getX();
				double y1 = source.position.getY();
				double x2 = destination.position.getX();
				double y2 = destination.position.getY();

				int length = (int) Math.round(Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2)));

				source.addEdgeTwoWay(new Edge(destination, length));
			}
		}
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
