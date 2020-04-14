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
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PathfinderController {
	PathFinder pathfinder = new PathFinder();
	Graph newGraph = new Graph();
	@Inject
	DatabaseService db;

	public static final class DATA_LABELS {
		public static final String X = "x";
		public static final String Y = "y";
		public static final String NODE_TYPE = "nodeType";
		public static final String SHORT_NAME = "shortName";
		public static final String LONG_NAME = "longName";
	}

	public static final class NODE_TYPES {
		public static final String CONFERENCE = "CONF";
		public static final String HALL = "HALL";
		public static final String DEPARTMENT = "DEPT";
		public static final String INFO = "INFO";
		public static final String LAB = "LABS";
		public static final String RESTROOM = "REST";
	}

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
			newNode.data.put(MapParser.DATA_LABELS.X, Integer.parseInt(rs.getString(2)));
			newNode.data.put(MapParser.DATA_LABELS.Y, Integer.parseInt(rs.getString(3)));
			newNode.data.put(MapParser.DATA_LABELS.NODE_TYPE, rs.getString(4));
			newNode.data.put(MapParser.DATA_LABELS.LONG_NAME, rs.getString(5));
			newNode.data.put(MapParser.DATA_LABELS.SHORT_NAME, rs.getString(6));

			newGraph.addNode(newNode);
		}

		rs = db.executeQuery(DBConstants.selectAllEdges);

		while (rs.next()) {
			Node source = newGraph.getNode(rs.getString(2));
			Node destination = newGraph.getNode(rs.getString(3));

			if (source != null && destination != null) {
				int x1 = (int) source.data.get(MapParser.DATA_LABELS.X);
				int y1 = (int) source.data.get(MapParser.DATA_LABELS.Y);
				int x2 = (int) destination.data.get(MapParser.DATA_LABELS.X);
				int y2 = (int) destination.data.get(MapParser.DATA_LABELS.Y);

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
