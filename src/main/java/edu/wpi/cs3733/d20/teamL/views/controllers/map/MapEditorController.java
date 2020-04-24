package edu.wpi.cs3733.d20.teamL.views.controllers.map;

import com.jfoenix.controls.JFXTextField;
import edu.wpi.cs3733.d20.teamL.entities.Edge;
import edu.wpi.cs3733.d20.teamL.services.db.IDBCache;
import edu.wpi.cs3733.d20.teamL.services.graph.Path;
import edu.wpi.cs3733.d20.teamL.services.graph.PathFinder;
import edu.wpi.cs3733.d20.teamL.util.FXMLLoaderHelper;
import edu.wpi.cs3733.d20.teamL.util.io.CSVHelper;
import edu.wpi.cs3733.d20.teamL.views.components.*;
import edu.wpi.cs3733.d20.teamL.views.controllers.dialogues.DataDialogue;
import edu.wpi.cs3733.d20.teamL.services.graph.MapParser;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.BorderPane;

import javafx.geometry.Point2D;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import javax.inject.Inject;

import javafx.scene.layout.VBox;


import edu.wpi.cs3733.d20.teamL.entities.Node;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MapEditorController {
    @FXML
    MenuItem saveToDB, saveToCSV, open, quit;
    @FXML
    MenuItem undo, redo;
    @FXML
    MenuItem node, edge;
    @FXML
    TextField startNode, endNode;
    @FXML
    Button pathFind, btnCancel, btnSave, btnEditConnections, btnOpenEditor;
    @FXML
    ToggleGroup tools;
    @FXML
    ToggleButton mouse, eraser;
    @FXML
    BorderPane root;
    @FXML
    MapPane map;
    @FXML
    JFXTextField nodeIDText, xCoordText, yCoordText, buildingText, nodeTypeText, shortNameText, longNameText;
    @FXML
    VBox editor;
	@Inject
	private IDBCache cache;

    private Scene scene;
    private FXMLLoaderHelper loaderHelper = new FXMLLoaderHelper();

    @FXML
    public void initialize() {
        scene = root.getScene();

        coreShortcuts();

        pathFind.setOnAction(event -> {
            Path path = PathFinder.aStarPathFind(map.getGraph(), map.getGraph().getNode(startNode.getText()), map.getGraph().getNode(endNode.getText()));
            System.out.println(path.generateTextMessage());

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

        cache.cacheAllFromDB();

        map.setEditable(true);
        map.init();
        openFromDB();

        map.setZoomLevel(1);

        //Hides the node editor VBox
        editor.setPrefWidth(0);
        editor.setVisible(false);

        map.recalculatePositions();
	}

    /**
     * Creates and allows keyboard shortcuts on the map Editor
     */
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

    /**
     * @return a map pane that is used for both map editor and map viewer
     */
    public MapPane getMap() {
        return map;
    }


    @FXML
    void quit() {
        cache.updateDB();
        Platform.exit();
    }

    @FXML
    void saveToDB() {
        ArrayList<Node> nodes = new ArrayList<>(map.getGraph().getNodes());
        ArrayList<Edge> blackList = new ArrayList<>();
        ArrayList<Edge> newEdges = new ArrayList<>();

        for (Node node : nodes) {
            for (Edge edge : node.getEdges()) {
                if (!newEdges.contains(edge) && blackList.contains(edge)) newEdges.add(edge);
                if (edge.getDestination().getNeighbors().contains(node))
                    blackList.add(edge.getDestination().getEdge(node));
            }
        }

        cache.cacheNodes(nodes, map.getEditedNodes());
        cache.cacheEdges(newEdges);
        cache.updateDB();

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
        boolean confirmed = data.showDialogue(pathFind.getScene().getWindow());
        if (confirmed) {
            map.setGraph(MapParser.parseMapToGraph(data.getNodeFile(), data.getEdgeFile()));
        }

    }

    @FXML
    void openFromDB() {
        cache.cacheAllFromDB();
        map.setGraph(MapParser.getGraphFromCache(cache.getNodeCache()));
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
			Parent root = loaderHelper.getFXMLLoader("AdminView").load();
            loaderHelper.setupScene(new Scene(root));
        } catch (Exception ex) {
            log.error("Encountered Exception.", ex);
        }
    }

    @FXML
    private void myCustomAction(MouseEvent event) {
        Node selectedNode = map.getSelectedNode();

        if(selectedNode == null) {
            editor.setPrefWidth(0);
            editor.setVisible(false);
        } else {
            editor.setPrefWidth(200);
            editor.setVisible(true);

            nodeIDText.setText(selectedNode.getID());
            Double x = selectedNode.getPosition().getX();
            Double y = selectedNode.getPosition().getY();
            xCoordText.setText(x.toString());
            yCoordText.setText(y.toString());
            buildingText.setText(selectedNode.getBuilding());
            nodeTypeText.setText(selectedNode.getType());
            shortNameText.setText(selectedNode.getShortName());
            longNameText.setText(selectedNode.getLongName());
        }
    }

    @FXML
    private void updateNode() {
        Node selectedNode = map.getSelectedNode();
        NodeGUI selectedNodeGUI = map.getNodeGUI(selectedNode);
        Collection<Node> neighbors = selectedNode.getNeighbors();

        selectedNode.setId(nodeIDText.getText());
        double x = Double.parseDouble(xCoordText.getText());
        double y = Double.parseDouble(yCoordText.getText());
        selectedNode.setPosition(new Point2D(x, y));
        selectedNode.setBuilding(buildingText.getText());
        selectedNode.setType(nodeTypeText.getText());
        selectedNode.setShortName(shortNameText.getText());
        selectedNode.setLongName(longNameText.getText());

        map.removeNode(selectedNodeGUI);
        map.addNode(selectedNode);
        for(Node neighbor : neighbors) {
            Edge edge = new Edge(selectedNode, neighbor);
            selectedNode.addEdgeTwoWay(edge);
            map.addEdge(edge);
        }
    }
}
