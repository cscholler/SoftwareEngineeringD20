package edu.wpi.leviathans.pathFinding.mapViewer;

import edu.wpi.leviathans.pathFinding.MapParser;
import edu.wpi.leviathans.pathFinding.graph.*;

import edu.wpi.leviathans.pathFinding.mapViewer.dataDialogue.*;
import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import javafx.geometry.Point2D;
import javafx.scene.shape.Line;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MapViewer {
    @FXML
    MenuItem save;
    @FXML
    MenuItem saveAs;
    @FXML
    MenuItem open;
    @FXML
    MenuItem quit;

    @FXML
    MenuItem undo;
    @FXML
    MenuItem redo;

    @FXML
    MenuItem node;
    @FXML
    MenuItem edge;

    @FXML
    ToggleGroup tools;

    @FXML
    AnchorPane body;
    @FXML
    ScrollPane scroller;
    @FXML
    BorderPane root;
    @FXML
    Label position;

    Graph graph = new Graph();

    HashMap<Node, NodeGUI> nodes = new HashMap<>();
    HashMap<Edge, EdgeGUI> edges = new HashMap<>();

    private int circleRadius = 12;
    private Color nodeColor = Color.ORANGE;
    private double zoomLevel = 1;
    private Scene scene;

    public void init() {
        scene = body.getScene();

        coreShortcuts();

        position.setText(positionInfo());
        scroller.setPannable(true);

        scroller.setOnScroll(event -> {
            if(event.isControlDown()) {
                // Get the initial zoom level
                double prevZoomLevel = getZoomLevel();

                // Change the zoom level
                setZoomLevel(prevZoomLevel * (1 + event.getDeltaY() / 100));

                position.setText(positionInfo());
            }
        });

        scroller.setOnMouseDragged(event -> position.setText(positionInfo()));
    }

    private void coreShortcuts() {
        // Instantiate key combinations with ancronyms as naming convention (cv --> Ctrl-V)
        KeyCombination cq = new KeyCodeCombination(KeyCode.Q, KeyCombination.CONTROL_DOWN);
        KeyCombination cs = new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN);
        KeyCombination css = new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN);
        KeyCombination cz = new KeyCodeCombination(KeyCode.Z, KeyCombination.CONTROL_DOWN);
        KeyCombination cy = new KeyCodeCombination(KeyCode.Y, KeyCombination.CONTROL_DOWN);
        KeyCombination co = new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN);

        quit.setAccelerator(cq);
        save.setAccelerator(cs);
        saveAs.setAccelerator(css);
        undo.setAccelerator(cz);
        redo.setAccelerator(cy);
        open.setAccelerator(co);

        scene.getAccelerators().put(cq, () -> quit());
        scene.getAccelerators().put(cs, () -> save());
        scene.getAccelerators().put(css, () -> saveAs());
        scene.getAccelerators().put(co, () -> open());
    }

    public void setZoomLevel(double newZoomLevel) {
        newZoomLevel = Math.max(newZoomLevel, 0.01);

        for (NodeGUI nodeGUI : nodes.values()) {
            Point2D prevPos = new Point2D(nodeGUI.layoutX.get(), nodeGUI.layoutY.get());
            Point2D newPos = prevPos.multiply(newZoomLevel / zoomLevel);
            nodeGUI.setLayoutPos(newPos);
        }

        zoomLevel = newZoomLevel;
    }

    public void changeZoomLevelBy(double deltaZoomLevel) {
        setZoomLevel(zoomLevel + deltaZoomLevel);
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

        data.showDialogue(scene.getWindow());

        graph = MapParser.parseMapToGraph(data.getNodeFile(), data.getEdgeFile());

        if(graph != null) body.getChildren().addAll(paneFromGraph(graph).getChildren());
    }

    private String positionInfo() {
        return "+" + round(getZoomLevel(), 3) + "\n(" + scroller.getHvalue() + ", " + scroller.getVvalue() + ")";
    }

    double round(double num, int place) {
        return Math.round(num * Math.pow(10, place)) / Math.pow(10, place);
    }

    private AnchorPane paneFromGraph(Graph graph) {
        AnchorPane root = new AnchorPane();

        // Add nodes to the scene
        for (Node node : graph.getNodes()) {
            NodeGUI nodeGUI = new NodeGUI(node);

            nodeGUI.gui.setRadius(circleRadius);
            nodeGUI.gui.fillProperty().setValue(nodeColor);

            Point2D zoomedPos = new Point2D(nodeGUI.layoutX.get() * zoomLevel, nodeGUI.layoutY.get() * zoomLevel);
            nodeGUI.setLayoutPos(zoomedPos);

            nodes.put(node, nodeGUI);
            node.data.put("GUI", nodeGUI);
        }

        // Add lines to the scene
        for (Edge edge : graph.getEdges()) {
            EdgeGUI edgeGUI = new EdgeGUI(edge);
            edgeGUI.gui.strokeProperty().setValue(nodeColor);
            edgeGUI.gui.strokeWidthProperty().setValue(4);

            // Set start position of the line to the source node
            edgeGUI.startX.bind(nodes.get(edge.getSource()).layoutX);
            edgeGUI.startY.bind(nodes.get(edge.getSource()).layoutY);

            // Set end position of the line to the destination node
            edgeGUI.endX.bind(nodes.get(edge.destination).layoutX);
            edgeGUI.endY.bind(nodes.get(edge.destination).layoutY);

            root.getChildren().add(edgeGUI.gui);
            edges.put(edge, edgeGUI);
            edge.data.put("GUI", edgeGUI);
        }

        for(NodeGUI nodeGUI : nodes.values()) {
            root.getChildren().add(nodeGUI.gui);
        }

        return root;
    }

    private class NodeGUI {
        public Circle gui = new Circle();
        public SimpleDoubleProperty layoutX = new SimpleDoubleProperty();
        public SimpleDoubleProperty layoutY = new SimpleDoubleProperty();

        private Node node;

        public NodeGUI(Node initNode) {
            node = initNode;

            // Set initial x and y position
            layoutX.set((int) node.data.get(MapParser.DATA_LABELS.X));
            layoutY.set((int) node.data.get(MapParser.DATA_LABELS.Y));

            gui.centerXProperty().bindBidirectional(layoutX);
            gui.centerYProperty().bindBidirectional(layoutY);
        }

        public void setLayoutPos(Point2D newPos) {
            layoutX.set(newPos.getX());
            layoutY.set(newPos.getY());
        }
    }

    private class EdgeGUI {
        public Line gui = new Line();

        public SimpleDoubleProperty startX = new SimpleDoubleProperty();
        public SimpleDoubleProperty startY = new SimpleDoubleProperty();
        public SimpleDoubleProperty endX = new SimpleDoubleProperty();
        public SimpleDoubleProperty endY = new SimpleDoubleProperty();

        private Edge edge;

        public EdgeGUI(Edge initEdge) {
            edge = initEdge;

            // Set start position of the line to the source node
            startX.set((int) edge.getSource().data.get(MapParser.DATA_LABELS.X));
            startY.set((int) edge.getSource().data.get(MapParser.DATA_LABELS.Y));

            // Set end position of the line to the destination node
            endX.set((int) edge.destination.data.get(MapParser.DATA_LABELS.X));
            endY.set((int) edge.destination.data.get(MapParser.DATA_LABELS.Y));

            gui.startXProperty().bindBidirectional(startX);
            gui.startYProperty().bindBidirectional(startY);
            gui.endXProperty().bindBidirectional(endX);
            gui.endYProperty().bindBidirectional(endY);
        }

        public void setStartPos(Point2D newPos) {
            startX.set(newPos.getX());
            startY.set(newPos.getY());
        }

        public void setEndPos(Point2D newPos) {
            endX.set(newPos.getX());
            endY.set(newPos.getY());
        }
    }
}
