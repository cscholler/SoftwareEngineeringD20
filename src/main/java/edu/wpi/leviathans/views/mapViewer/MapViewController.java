package edu.wpi.leviathans.views.mapViewer;

import edu.wpi.leviathans.util.pathfinding.Path;
import edu.wpi.leviathans.util.pathfinding.PathFinder;
import edu.wpi.leviathans.views.mapViewer.dataDialogue.*;
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
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

import edu.wpi.leviathans.util.pathfinding.graph.Edge;
import edu.wpi.leviathans.util.pathfinding.graph.Graph;
import edu.wpi.leviathans.util.pathfinding.graph.Node;
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

    private Selector mapSelector = new Selector();
    private SelectionBox selectionBox;

    private boolean draggingNode = false;
    private boolean dragSelecting = false;
    private boolean onSelectable = false;

    private int circleRadius = 12;
    private Color nodeColor = Color.ORANGE;
    private Paint highLightColor = Color.CYAN;
    private double highlightThickness = 2;

    private double zoomLevel = 1;
    private Scene scene;

    /*private void clearSelection() {
        for (NodeGUI nodeGUI : selectedNodes.keySet())
            nodeGUI.setHighlighted(false);
        for (EdgeGUI edgeGUI : selectedEdges)
            edgeGUI.setHighlighted(false);
        selectedNodes.clear();
        selectedEdges.clear();
    }*/

    private void removeNode(NodeGUI nodeGUI) {
        nodes.remove(nodeGUI);
        body.getChildren().removeAll(nodeGUI.getAllNodes());
        graph.removeNode(nodeGUI.getNode());
        for (Node neighbor : nodeGUI.getNode().getNeighbors()) {
            Edge edgeToNode = neighbor.edgeFromDest(nodeGUI.getNode());
            Edge edgeFromNode = nodeGUI.getNode().getEdge(neighbor);
            neighbor.removeEdge(edgeToNode);
            nodeGUI.getNode().removeEdge(edgeFromNode);
            body.getChildren().removeAll(edges.get(edgeToNode).getAllNodes());
            body.getChildren().removeAll(edges.get(edgeFromNode).getAllNodes());
        }
    }

    @FXML
    public void initialize() {
        scene = body.getScene();

        coreShortcuts();

        position.setText(positionInfo());
        scroller.setPannable(true);

        // Zoom in and out
        body.setOnScroll(event -> {
            if (event.isControlDown()) {
                // Get the initial zoom level
                double prevZoomLevel = getZoomLevel();

                // Change the zoom level
                setZoomLevelToPosition(prevZoomLevel * (1 + event.getDeltaY() / 100), new Point2D(event.getX(), event.getY()));

                position.setText(positionInfo());
            }
        });

        // Deselect all nodes when clicked off
        body.setOnMouseClicked(event -> {
            if (event.getButton().equals(MouseButton.PRIMARY) && !onSelectable) {
                mapSelector.clear();
            }
        });

        // Delete selected nodes when delete key is pressed
        body.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.DELETE)) {
                for (NodeGUI nodeGUI : mapSelector.getNodes())
                    removeNode(nodeGUI);
            }
        });

        body.setOnMousePressed(event -> {
            if(!draggingNode && !onSelectable && event.isPrimaryButtonDown()) {
                dragSelecting = true;
                selectionBox = new SelectionBox(new Point2D(event.getX(), event.getY()));
                body.getChildren().add(selectionBox);
            }
        });

        // Change the position of all the selected nodes as the mouse is being dragged keeping their offset
        body.setOnMouseDragged(event -> {
            if (draggingNode) {
                for (NodeGUI gui : mapSelector.getNodes()) {
                    gui.setLayoutPos(mapSelector.getNodePosition(gui).add(new Point2D(event.getX(), event.getY())));
                }
            } else if (dragSelecting) {
                selectionBox.mouseDrag(new Point2D(event.getX(), event.getY()));
                for (NodeGUI node : nodes.values()) {
                    if (selectionBox.getBoundsInParent().contains(node.getBoundsInParent()))
                        mapSelector.add(node);
                    else if(mapSelector.contains(node) && !event.isShiftDown())
                        mapSelector.remove(node);
                }
            }
            position.setText(positionInfo());
        });

        body.setOnMouseReleased(event -> {
            dragSelecting = false;
            body.getChildren().remove(selectionBox);
        });

        body.addEventHandler(MouseEvent.ANY, event -> {
            if (event.getButton() != MouseButton.MIDDLE) event.consume();
        });

        pathFind.setOnAction(event -> {
            Path path = PathFinder.aStarPathFind(graph, graph.getNode(startNode.getText()), graph.getNode(endNode.getText()));

            Iterator<Node> nodeIterator = path.iterator();

            // Loop through each node in the path and select it as well as the edge pointing to the next node
            Node currentNode = nodeIterator.next();
            Node nextNode;
            while (nodeIterator.hasNext()) {
                nextNode = nodeIterator.next();

                NodeGUI nodeGUI = nodes.get(currentNode);
                EdgeGUI edgeGUI = edges.get(currentNode.getEdge(nextNode));

                mapSelector.add(nodeGUI);
                mapSelector.add(edgeGUI);

				currentNode = nextNode;
			}
			// The above loop does not highlight the last node, this does that
			NodeGUI nodeGUI = nodes.get(currentNode);
			mapSelector.add(nodeGUI);
			nodeGUI.setHighlighted(true);
		});

        coreShortcuts();
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

    public void setZoomLevel(double newZoomLevel) {
        newZoomLevel = Math.max(newZoomLevel, 0.01);

        Point2D prevScroll = new Point2D(scroller.getHvalue(), scroller.getVvalue());

        for (NodeGUI nodeGUI : nodes.values()) {
            Point2D prevPos = new Point2D(nodeGUI.layoutX.get(), nodeGUI.layoutY.get());
            Point2D newPos = prevPos.multiply(newZoomLevel / zoomLevel);
            nodeGUI.setLayoutPos(newPos);
        }

        scroller.layout();

        scroller.setHvalue(prevScroll.getX() * newZoomLevel / zoomLevel);
        scroller.setVvalue(prevScroll.getY() * newZoomLevel / zoomLevel);

        zoomLevel = newZoomLevel;
    }

    public void setZoomLevelToPosition(double newZoomLevel, Point2D position) {
        double percentX = position.getX() / body.getWidth();
        double percentY = position.getY() / body.getHeight();

        setZoomLevel(newZoomLevel);

        scroller.layout();

        scroller.setHvalue(percentX * scroller.getHmax());
        scroller.setVvalue(percentY * scroller.getVmax());
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

        graph = MapParser.parseMapToGraph(data.getNodeFile(), data.getEdgeFile());

        setPaneFromGraph(graph);
    }

    @FXML
    void openFromDB() {
        graph = MapParser.getGraphFromDatabase();

        setPaneFromGraph(graph);
    }

    private void setPaneFromGraph(Graph graph) {
        if (graph != null) {
            // Set names so they are simpler to test (temporary)
            /*int i = 0;
            for (Node node : graph.getNodes()) {
                node.setName("n" + i);
                i++;
            }*/

            body.getChildren().clear();
            body.getChildren().addAll(paneFromGraph(graph).getChildren());
        }
    }

    @FXML
    private void insertNode() {

    }

    private String positionInfo() {
        return "+" + round(getZoomLevel(), 3) + "\n(" +
                round(scroller.getHvalue(), 3) +
                ", " + round(scroller.getVvalue(), 3) + ")";
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

            nodeGUI.setRadius(circleRadius);
            nodeGUI.fillProperty().setValue(nodeColor);
            nodeGUI.setHighlightColor(highLightColor);
            nodeGUI.setHighlightRadius(highlightThickness);

            Point2D zoomedPos = new Point2D(nodeGUI.layoutX.get() * zoomLevel, nodeGUI.layoutY.get() * zoomLevel);
            nodeGUI.setLayoutPos(zoomedPos);

            nodeGUI.setOnMouseEntered(event -> {
                nodeGUI.setHighlighted(true);
                onSelectable = true;
            });
            nodeGUI.setOnMouseExited(event -> {
                if (!mapSelector.contains(nodeGUI))
                    nodeGUI.setHighlighted(false);
                onSelectable = false;
            });

            // -----------Handle selection-----------

            // Different selection methods based on what shortcut is being held
            nodeGUI.setOnMouseClicked(event -> {
                if (event.isShiftDown()) {
                    if (!mapSelector.contains(nodeGUI))
                        mapSelector.add(nodeGUI);
                } else if (event.isControlDown()) {
                    if (mapSelector.contains(nodeGUI))
                        mapSelector.remove(nodeGUI);
                    else
                        mapSelector.add(nodeGUI);
                } else {
                    mapSelector.clear();
                    mapSelector.add(nodeGUI);
                }
            });

            // -----------Handle moving the nodes-----------

            // Start dragging the selected nodes
            nodeGUI.setOnMousePressed(event -> {
                if (mapSelector.contains(nodeGUI) && event.isPrimaryButtonDown()) {
                    for (NodeGUI gui : mapSelector.getNodes())
                        mapSelector.setNodePosition(gui, gui.getLayoutPos().subtract(new Point2D(event.getX(), event.getY())));

                    draggingNode = true;
                }
            });

            // Done dragging
            nodeGUI.setOnMouseReleased(event -> {
                for (NodeGUI gui : mapSelector.getNodes()) {
                    gui.getNode().position = gui.getLayoutPos().multiply(1 / zoomLevel);
                    mapSelector.setNodePosition(gui, gui.getLayoutPos().subtract(new Point2D(event.getX(), event.getY())));
                }
                draggingNode = false;
            });

            nodes.put(node, nodeGUI);
            node.data.put("GUI", nodeGUI);
        }

        // Add lines to the scene
        for (Edge edge : graph.getEdges()) {
            EdgeGUI edgeGUI = new EdgeGUI(edge);
            edgeGUI.getGUI().strokeProperty().setValue(nodeColor);
            edgeGUI.setHighlightColor(highLightColor);
            edgeGUI.setHighlightRadius(highlightThickness);

            // Set start position of the line to the source node
            edgeGUI.startX.bind(nodes.get(edge.getSource()).layoutX);
            edgeGUI.startY.bind(nodes.get(edge.getSource()).layoutY);

            // Set end position of the line to the destination node
            edgeGUI.endX.bind(nodes.get(edge.destination).layoutX);
            edgeGUI.endY.bind(nodes.get(edge.destination).layoutY);

            edgeGUI.getGUI().setOnMouseEntered(event -> edgeGUI.setHighlighted(true));
            edgeGUI.getGUI().setOnMouseExited(event -> edgeGUI.setHighlighted(false));

            root.getChildren().addAll(edgeGUI.getAllNodes());
            edges.put(edge, edgeGUI);
            edge.data.put("GUI", edgeGUI);
        }

        for (NodeGUI nodeGUI : nodes.values()) {
            root.getChildren().addAll(nodeGUI.getAllNodes());
        }

        return root;
    }


}
