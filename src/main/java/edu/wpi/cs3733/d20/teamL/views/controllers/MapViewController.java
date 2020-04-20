package edu.wpi.cs3733.d20.teamL.views.controllers;

import edu.wpi.cs3733.d20.teamL.entities.Edge;
import edu.wpi.cs3733.d20.teamL.services.db.DBCache;
import edu.wpi.cs3733.d20.teamL.services.graph.Path;
import edu.wpi.cs3733.d20.teamL.services.graph.PathFinder;
import edu.wpi.cs3733.d20.teamL.util.io.CSVHelper;
import edu.wpi.cs3733.d20.teamL.views.components.*;
import edu.wpi.cs3733.d20.teamL.views.dialogues.DataDialogue;
import edu.wpi.cs3733.d20.teamL.services.graph.MapParser;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.BorderPane;

import javafx.geometry.Point2D;

import edu.wpi.cs3733.d20.teamL.entities.Node;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.util.*;

public class MapViewController {
    @FXML
    MenuItem saveToDB, saveToCSV, open, quit;

    @FXML
    MenuItem undo, redo;

    @FXML
    MenuItem node, edge;

    @FXML
    TextField startNode, endNode;

    @FXML
    Button pathFind;

    @FXML
    ToggleGroup tools;

    @FXML
    ToggleButton mouse, eraser;

    @FXML
    BorderPane root;
    @FXML
    MapPane map;

    private double zoomLevel = 1;
    private Scene scene;
    private DBCache dbCache = new DBCache(false);

    @FXML
    public void initialize() {
        scene = root.getScene();

        coreShortcuts();

        pathFind.setOnAction(event -> {
            Path path = PathFinder.aStarPathFind(map.getGraph(), map.getGraph().getNode(startNode.getText()), map.getGraph().getNode(endNode.getText()));

            Iterator<Node> nodeIterator = path.iterator();

            // Loop through each node in the path and select it as well as the edge pointing to the next node
            Node currentNode = nodeIterator.next();
            Node nextNode;
            while (nodeIterator.hasNext()) {
                nextNode = nodeIterator.next();

                NodeGUI nodeGUI = map.getNodeGUI(currentNode);
                EdgeGUI edgeGUI = map.getEdgeGUI(currentNode.getEdge(nextNode));

                map.getSelector().add(nodeGUI);
                map.getSelector().add(edgeGUI);

				currentNode = nextNode;
			}
			// The above loop does not highlight the last node, this does that
			NodeGUI nodeGUI = map.getNodeGUI(currentNode);
			map.getSelector().add(nodeGUI);
			nodeGUI.setHighlighted(true);
		});

        dbCache.cacheAllFromDB();

        openFromDB();

        map.setZoomLevel(0.6);
	}

    private void coreShortcuts() {
        // Instantiate key combinations with ancronyms as naming convention (cv --> Ctrl-V)
        KeyCombination cq = new KeyCodeCombination(KeyCode.Q, KeyCombination.CONTROL_DOWN);
        KeyCombination cs = new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN);
        KeyCombination css = new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN);
        KeyCombination cz = new KeyCodeCombination(KeyCode.Z, KeyCombination.CONTROL_DOWN);
        KeyCombination cy = new KeyCodeCombination(KeyCode.Y, KeyCombination.CONTROL_DOWN);
        KeyCombination co = new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN);

        // Add shortcut prompts to buttons
        quit.setAccelerator(cq);
        saveToDB.setAccelerator(cs);
        saveToCSV.setAccelerator(css);
        undo.setAccelerator(cz);
        redo.setAccelerator(cy);
        open.setAccelerator(co);
    }

    public double getZoomLevel() {
        return zoomLevel;
    }

    @FXML
    void quit() {
        dbCache.updateDB();
        Platform.exit();
    }

    @FXML
    void saveToDB() {
        ArrayList<Node> nodes = new ArrayList<>(map.getGraph().getNodes());
        ArrayList<Edge> edges = new ArrayList<>(map.getGraph().getEdges());

        dbCache.cacheNodes(nodes, map.getEditedNodes());
        dbCache.cacheEdges(edges);
        dbCache.updateDB();

        map.getEditedNodes().clear();
    }

    @FXML
    public void saveToCSV() {
    	DataDialogue data = new DataDialogue();
    	data.setSaving(true);
    	data.showDialogue(pathFind.getScene().getWindow());
    	String nodeFilePath = data.getNodeFile().getAbsolutePath();
		String edgeFilePath = data.getEdgeFile().getAbsolutePath();
		CSVHelper csvHelper = new CSVHelper();
		ArrayList<ArrayList<String>> nodeTable = new ArrayList<>();
		ArrayList<ArrayList<String>> edgeTable = new ArrayList<>();
		ArrayList<Node> nodes = new ArrayList<>(map.getGraph().getNodes());
		ArrayList<Edge> edges = new ArrayList<>(map.getGraph().getEdges());
		nodeTable.add(new ArrayList<>(Arrays.asList("nodeID", "xCoord", "yCoord", "floor", "building", "nodeType", "longName", "shortName")));
		for (Node node : nodes) {
			nodeTable.add(node.toArrayList());
		}
		edgeTable.add(new ArrayList<>(Arrays.asList("edgeID", "startNode", "endNode")));
		for (Edge edge : edges) {
			edgeTable.add(edge.toArrayList());
		}
		csvHelper.writeToCSV(nodeFilePath, nodeTable);
		csvHelper.writeToCSV(edgeFilePath, edgeTable);
    }

    @FXML
    public void open() {
        DataDialogue data = new DataDialogue();
        data.showDialogue(pathFind.getScene().getWindow());
        map.setGraph(MapParser.parseMapToGraph(data.getNodeFile(), data.getEdgeFile()));
    }

    @FXML
    void openFromDB() {
        dbCache.cacheAllFromDB();
        map.setGraph(MapParser.getGraphFromCache(dbCache.getNodeCache()));
    }

    @FXML
    private void insertNode() {
        Node node = new Node("1", new Point2D(100,100)); //TODO CHANGE TO UNIQUE ID
        map.addNode(node);
    }

    @FXML
    private void handleTools(ActionEvent event) {
        if (event.getSource() == eraser) {
            map.setErasing(true);
        } else {
            map.setErasing(false);
        }
    }

    @FXML
    private void backToMain() {
        try {
            Stage stage = (Stage) pathFind.getScene().getWindow();
            Parent newRoot = FXMLLoader.load(getClass().getResource("/edu/wpi/cs3733/d20/teamL/views/Home.fxml"));
            Scene newScene = new Scene(newRoot);
            stage.setScene(newScene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
