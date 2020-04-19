package edu.wpi.cs3733.d20.teamL.views.controllers;

import edu.wpi.cs3733.d20.teamL.services.graph.Path;
import edu.wpi.cs3733.d20.teamL.services.graph.PathFinder;
import edu.wpi.cs3733.d20.teamL.views.components.*;
import edu.wpi.cs3733.d20.teamL.views.dialogues.DataDialogue;
import edu.wpi.cs3733.d20.teamL.services.graph.MapParser;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import javafx.geometry.Point2D;

import edu.wpi.cs3733.d20.teamL.entities.Edge;
import edu.wpi.cs3733.d20.teamL.services.graph.Graph;
import edu.wpi.cs3733.d20.teamL.entities.Node;
import java.util.*;

public class MapViewController {
    @FXML
    MenuItem save, saveAs, open, quit;

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
        save.setAccelerator(cs);
        saveAs.setAccelerator(css);
        undo.setAccelerator(cz);
        redo.setAccelerator(cy);
        open.setAccelerator(co);
    }

    public double getZoomLevel() {
        return zoomLevel;
    }

    @FXML
    void quit() {
        Platform.exit();
    }

    @FXML
    void save() {
        Alert saveAlert = new Alert(Alert.AlertType.WARNING);
        saveAlert.setTitle("Cannot complete operation");
        saveAlert.setContentText("Nothing to save");

        saveAlert.showAndWait();
    }

    @FXML
    public void saveAs() {
        save();
    }

    @FXML
    public void open() {
        DataDialogue data = new DataDialogue();

        data.showDialogue(pathFind.getScene().getWindow());

        map.setGraph(MapParser.parseMapToGraph(data.getNodeFile(), data.getEdgeFile()));
    }

    @FXML
    void openFromDB() {
        map.setGraph(MapParser.getGraphFromDatabase());
    }

    @FXML
    private void insertNode() {
        Node node = new Node("1");
        node.setPosition(new Point2D(100,100));

        map.addNode(node);
    }

    @FXML
    private void handleTools(ActionEvent event) {
        if(event.getSource() == eraser) {
            map.setErasing(true);
        } else {
            map.setErasing(false);
        }
    }

}
