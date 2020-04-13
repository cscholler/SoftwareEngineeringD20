package edu.wpi.leviathans.util.pathfinding.mapViewer;

import edu.wpi.leviathans.util.pathfinding.mapViewer.dataDialogue.*;
import edu.wpi.leviathans.util.pathfinding.MapParser;
import edu.wpi.leviathans.util.pathfinding.graph.*;

import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;

import javafx.geometry.Point2D;
import javafx.scene.shape.Line;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class MapViewer {
    // TODO: reorganize
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

    private HashMap<NodeGUI, Point2D> selectedNodes = new HashMap();
    private boolean dragging = false;
    private boolean onSelectable = false;

    private int circleRadius = 12;
    private Color nodeColor = Color.ORANGE;
    private Paint highLightColor = Color.CYAN;
    private double highlightRadius = 2;

    private double zoomLevel = 1;
    private Scene scene;

    private void clearSelection() {
        for (NodeGUI nodeGUI : selectedNodes.keySet())
            nodeGUI.setHighlighted(false);
        selectedNodes.clear();
    }

    public void init() {
        scene = body.getScene();

        coreShortcuts();

        position.setText(positionInfo());
        scroller.setPannable(true);

        body.setOnScroll(event -> {
            if (event.isControlDown()) {
                // Get the initial zoom level
                double prevZoomLevel = getZoomLevel();

                // Change the zoom level
                setZoomLevel(prevZoomLevel * (1 + event.getDeltaY() / 100));

                position.setText(positionInfo());
            }
        });

        body.addEventHandler(MouseEvent.ANY, event -> {
            if (event.getButton() == MouseButton.PRIMARY && !event.isShortcutDown() && !event.isShiftDown() && !onSelectable)
                clearSelection();
            if (event.getButton() != MouseButton.MIDDLE) event.consume();
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

        // Add shortcut prompts to buttons
        quit.setAccelerator(cq);
        save.setAccelerator(cs);
        saveAs.setAccelerator(css);
        undo.setAccelerator(cz);
        redo.setAccelerator(cy);
        open.setAccelerator(co);

        // Give the shortcuts functionality within the scene TODO: the section above should do this but it doesn't for some reason
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

        if (graph != null) body.getChildren().addAll(paneFromGraph(graph).getChildren());
    }

    private String positionInfo() {
        return "+" + round(getZoomLevel(), 3) + "\n(" + scroller.getHvalue() + ", " + scroller.getVvalue() + ")";
    }

    double round(double num, int place) {
        return Math.round(num * Math.pow(10, place)) / Math.pow(10, place);
    }

    /**
     * Converts a graph to a Pane, Nodes as circles and Edges as lines
     *
     * @param graph The graph to read through
     * @return An AnchorPane with all the graph gui elements
     */
    private AnchorPane paneFromGraph(Graph graph) {
        AnchorPane root = new AnchorPane();

        // Add nodes to the scene
        for (Node node : graph.getNodes()) {
            NodeGUI nodeGUI = new NodeGUI(node);

            nodeGUI.gui.setRadius(circleRadius);
            nodeGUI.gui.fillProperty().setValue(nodeColor);
            nodeGUI.setHighlightColor(highLightColor);
            nodeGUI.setHighlightRadius(highlightRadius);

            Point2D zoomedPos = new Point2D(nodeGUI.layoutX.get() * zoomLevel, nodeGUI.layoutY.get() * zoomLevel);
            nodeGUI.setLayoutPos(zoomedPos);

            // -----------Handle selection-----------

            // Highlight when moused on
            nodeGUI.gui.setOnMouseEntered(event -> {
                onSelectable = true;
                nodeGUI.setHighlighted(true);
            });

            // unhighlight when moused off unless the node is selected
            nodeGUI.gui.setOnMouseExited(event -> {
                onSelectable = false;
                if (!selectedNodes.keySet().contains(nodeGUI))
                    nodeGUI.setHighlighted(false);
            });

            // Different selection methods based on what shortcut is being held
            nodeGUI.gui.setOnMouseClicked(event -> {
                if (event.isShiftDown()) {
                    if(!selectedNodes.keySet().contains(nodeGUI))
                        selectedNodes.put(nodeGUI, nodeGUI.getLayoutPos());
                } else if (event.isControlDown()) {
                    if (selectedNodes.keySet().contains(nodeGUI))
                        selectedNodes.remove(nodeGUI);
                    else
                        selectedNodes.put(nodeGUI, nodeGUI.getLayoutPos());
                } else {
                    clearSelection();
                    selectedNodes.put(nodeGUI, nodeGUI.getLayoutPos());
                }
            });

            // -----------Handle moving the nodes-----------

            // When a mouse drag is started, check if the node is selected and, if so, record the offset of every selected node from the mouse
            nodeGUI.gui.setOnDragDetected(event -> {
                if(selectedNodes.keySet().contains(nodeGUI) && event.isPrimaryButtonDown()) { // TODO: This condition is not met after it is met once for some reason
                    for(NodeGUI gui : selectedNodes.keySet()) {
                        selectedNodes.replace(gui, gui.getLayoutPos().subtract(new Point2D(event.getX(), event.getY())));
                        System.out.println("Offset for " + gui.node.getName() + " is " + selectedNodes.get(gui));
                    }
                    dragging = true;
                    scene.setCursor(Cursor.MOVE);
                }
            });

            // Change the position of all the selected nodes as the mouse is being dragged keeping their offset
            nodeGUI.gui.setOnMouseDragged(event -> {
                if(dragging) {
                    for (NodeGUI gui : selectedNodes.keySet()) {
                        gui.setLayoutPos(selectedNodes.get(gui).add(new Point2D(event.getX(), event.getY())));
                    }
                }
            });

            nodeGUI.gui.setOnDragDone(event -> {
                for (NodeGUI gui : selectedNodes.keySet()) {
                    gui.node.position = gui.getLayoutPos().multiply(1 / zoomLevel);
                    selectedNodes.replace(gui, gui.getLayoutPos().subtract(new Point2D(event.getX(), event.getY())));
                }
                scene.setCursor(Cursor.DEFAULT);
                dragging = false;
            });

            nodes.put(node, nodeGUI);
            node.data.put("GUI", nodeGUI);
        }

        // Add lines to the scene
        for (Edge edge : graph.getEdges()) {
            EdgeGUI edgeGUI = new EdgeGUI(edge);
            edgeGUI.gui.strokeProperty().setValue(nodeColor);
            edgeGUI.setHighlightColor(highLightColor);
            edgeGUI.setHighlightRadius(highlightRadius);

            // Set start position of the line to the source node
            edgeGUI.startX.bind(nodes.get(edge.getSource()).layoutX);
            edgeGUI.startY.bind(nodes.get(edge.getSource()).layoutY);

            // Set end position of the line to the destination node
            edgeGUI.endX.bind(nodes.get(edge.destination).layoutX);
            edgeGUI.endY.bind(nodes.get(edge.destination).layoutY);

            edgeGUI.gui.setOnMouseEntered(event -> edgeGUI.setHighlighted(true));
            edgeGUI.gui.setOnMouseExited(event -> edgeGUI.setHighlighted(false));

            root.getChildren().addAll(edgeGUI.getAllNodes());
            edges.put(edge, edgeGUI);
            edge.data.put("GUI", edgeGUI);
        }

        for (NodeGUI nodeGUI : nodes.values()) {
            root.getChildren().add(nodeGUI.gui);
        }

        return root;
    }

    private interface Highlightable {
        void setHighlighted(boolean newHighlighted);

        void setHighlightRadius(double radius);

        void setHighlightColor(Paint newColor);

        boolean getHighlighted();

        double getHighlightRadius();

        Paint getHighlightColor();

        Collection<javafx.scene.Node> getAllNodes();
    }

    private class NodeGUI implements Highlightable {
        public Circle gui = new Circle();

        public SimpleDoubleProperty layoutX = new SimpleDoubleProperty();
        public SimpleDoubleProperty layoutY = new SimpleDoubleProperty();

        private boolean highlighted;
        private double highlightRadius;

        private Node node;

        public NodeGUI(Node initNode) {
            node = initNode;

            // Set initial x and y position
            setLayoutPos(node.position);

            gui.centerXProperty().bindBidirectional(layoutX);
            gui.centerYProperty().bindBidirectional(layoutY);

            setHighlighted(false);
        }

        public void setLayoutPos(Point2D newPos) {
            layoutX.set(newPos.getX());
            layoutY.set(newPos.getY());
        }

        public void setHighlighted(boolean newHighlighted) {
            highlighted = newHighlighted;

            if (highlighted) gui.setStrokeWidth(highlightRadius);
            else gui.setStrokeWidth(0);
        }

        public void setHighlightRadius(double radius) {
            highlightRadius = radius;
        }

        public void setHighlightColor(Paint newColor) {
            gui.setStroke(newColor);
        }

        public Point2D getLayoutPos() {
            return new Point2D(layoutX.get(), layoutY.get());
        }

        public boolean getHighlighted() {
            return highlighted;
        }

        public Collection<javafx.scene.Node> getAllNodes() {
            Collection<javafx.scene.Node> retList = new ArrayList<>(1);
            retList.add(gui);
            return retList;
        }

        public double getHighlightRadius() {
            return highlightRadius;
        }

        public Paint getHighlightColor() {
            return gui.getStroke();
        }
    }

    private class EdgeGUI implements Highlightable {
        public Line gui = new Line();
        Line highlightGui = new Line();

        public SimpleDoubleProperty startX = new SimpleDoubleProperty();
        public SimpleDoubleProperty startY = new SimpleDoubleProperty();
        public SimpleDoubleProperty endX = new SimpleDoubleProperty();
        public SimpleDoubleProperty endY = new SimpleDoubleProperty();

        private Edge edge;

        public EdgeGUI(Edge initEdge) {
            edge = initEdge;

            // Set start position of the line to the source node
            setStartPos(edge.getSource().position);

            // Set end position of the line to the destination node
            setEndPos(edge.destination.position);

            gui.startXProperty().bindBidirectional(startX);
            gui.startYProperty().bindBidirectional(startY);
            gui.endXProperty().bindBidirectional(endX);
            gui.endYProperty().bindBidirectional(endY);

            highlightGui.startXProperty().bindBidirectional(startX);
            highlightGui.startYProperty().bindBidirectional(startY);
            highlightGui.endXProperty().bindBidirectional(endX);
            highlightGui.endYProperty().bindBidirectional(endY);

            setHighlighted(false);
        }

        public EdgeGUI(Edge initEdge, int lineWidth) {
            this(initEdge);

            gui.setStrokeWidth(lineWidth);
        }

        public void setStartPos(Point2D newPos) {
            startX.set(newPos.getX());
            startY.set(newPos.getY());
        }

        public void setEndPos(Point2D newPos) {
            endX.set(newPos.getX());
            endY.set(newPos.getY());
        }

        public void setHighlighted(boolean newHighlighted) {
            highlightGui.setVisible(newHighlighted);
        }

        public void setHighlightRadius(double radius) {
            highlightGui.setStrokeWidth(gui.getStrokeWidth() + (radius * 2));
        }

        public void setHighlightColor(Paint newColor) {
            highlightGui.setStroke(newColor);
        }

        public boolean getHighlighted() {
            return highlightGui.isVisible();
        }

        public Collection<javafx.scene.Node> getAllNodes() {
            Collection<javafx.scene.Node> retList = new ArrayList<>(2);
            retList.add(highlightGui);
            retList.add(gui);
            return retList;
        }

        public double getHighlightRadius() {
            return (highlightGui.getStrokeWidth() - gui.getStrokeWidth()) / 2;
        }

        public Paint getHighlightColor() {
            return highlightGui.getStroke();
        }
    }
}
