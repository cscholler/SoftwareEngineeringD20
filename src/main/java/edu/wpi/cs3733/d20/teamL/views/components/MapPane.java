package edu.wpi.cs3733.d20.teamL.views.components;

import edu.wpi.cs3733.d20.teamL.entities.Edge;
import edu.wpi.cs3733.d20.teamL.entities.Node;
import edu.wpi.cs3733.d20.teamL.services.graph.Graph;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MapPane extends StackPane {
    @FXML
    private Label position;
    @FXML
    private AnchorPane body;
    @FXML
    private ScrollPane scroller;

    private Map<Node, NodeGUI> nodes = new HashMap<>();
    private Map<Edge, EdgeGUI> edges = new HashMap<>();

    private Selector selector = new Selector();

    private SelectionBox selectionBox = new SelectionBox(new Point2D(0, 0), selector, (Collection<Highlightable>) (Collection<?>) nodes.values());

    private Graph graph = new Graph();

    private double zoomLevel = 1;

    private boolean editable = true;

    private boolean onSelectable = false;
    private boolean draggingNode = false;
    private boolean dragSelecting = false;
    private boolean addingEdge = false;

    private int circleRadius = 12;
    private Color nodeColor = Color.ORANGE;
    private Paint highLightColor = Color.CYAN;
    private double highlightThickness = 2;

    public MapPane() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("MapPane.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        position.setText(positionInfo());
        scroller.setPannable(true);
        body.setFocusTraversable(true);

        // Zoom in and out when control is held
        body.setOnScroll(event -> {
            if (event.isControlDown()) {
                // Get the initial zoom level
                double prevZoomLevel = getZoomLevel();

                // Change the zoom level
                setZoomLevelToPosition(prevZoomLevel * (1 + event.getDeltaY() / 100), new Point2D(event.getX(), event.getY()));

                position.setText(positionInfo());
            }
        });

        // Delete selected nodes when delete key is pressed
        scroller.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.DELETE)) {
                for (NodeGUI nodeGUI : selector.getNodes())
                    removeNode(nodeGUI);
            }
        });

        // Deselect all nodes when clicked off
        body.setOnMouseClicked(event -> {
            if (event.getButton().equals(MouseButton.PRIMARY) && !onSelectable) {
                selector.clear();
            }
        });

        body.setOnMousePressed(event -> {
            if (!draggingNode && !onSelectable && event.isPrimaryButtonDown()) {
                dragSelecting = true;
                selectionBox.setRootPosition(new Point2D(event.getX(), event.getY()));
                body.getChildren().add(selectionBox);
            }
        });

        // Change the position of all the selected nodes as the mouse is being dragged keeping their offset
        body.setOnMouseDragged(event -> {
            if(event.isPrimaryButtonDown()) {
                if (draggingNode) {
                    for (NodeGUI gui : selector.getNodes()) {
                        gui.setLayoutPos(selector.getNodePosition(gui).add(new Point2D(event.getX(), event.getY())));
                    }
                } else if (dragSelecting) {
                    selectionBox.mouseDrag(new Point2D(event.getX(), event.getY()), event.isShiftDown());
                }
                position.setText(positionInfo());
            }
        });

        body.setOnMouseReleased(event -> {
            dragSelecting = false;
            body.getChildren().remove(selectionBox);
        });

        body.addEventHandler(MouseEvent.ANY, event -> {
            if (event.getButton() != MouseButton.MIDDLE) event.consume();
        });
    }

    //---------- Getters/Setters ----------//

    public Collection<NodeGUI> getNodes() {
        return nodes.values();
    }

    public Collection<EdgeGUI> getEdges() {
        return edges.values();
    }

    public Selector getSelector() {
        return selector;
    }

    public void setSelector(Selector selector) {
        this.selector = selector;
    }

    public SelectionBox getSelectionBox() {
        return selectionBox;
    }

    public void setSelectionBox(SelectionBox selectionBox) {
        this.selectionBox = selectionBox;
    }

    public Graph getGraph() {
        return graph;
    }

    public void setGraph(Graph graph) {
        this.graph = graph;
        nodes.clear();
        edges.clear();
        body.getChildren().clear();

        // Add nodes to the scene
        for (Node node : graph.getNodes()) {
            addNode(node);
        }

        // Add lines to the scene
        for (Edge edge : graph.getEdges()) {
            addEdge(edge);
        }
    }

    public double getZoomLevel() {
        return zoomLevel;
    }

    // Sets the zoom level by changing the spacing between all the nodes
    public void setZoomLevel(double zoomLevel) {
        zoomLevel = Math.max(zoomLevel, 0.01);

        for (NodeGUI nodeGUI : nodes.values()) {
            Point2D prevPos = new Point2D(nodeGUI.layoutXProperty().get(), nodeGUI.layoutYProperty().get());
            Point2D newPos = prevPos.multiply(zoomLevel / this.zoomLevel);
            nodeGUI.setLayoutPos(newPos);
        }

        this.zoomLevel = zoomLevel;
    }

    /**
     * Sets the zoom level while preserving the camera position with respect to the given Point2D.
     *
     * @param newZoomLevel The new zoom level.
     * @param position The coordinates of the position to zoom to.
     */
    public void setZoomLevelToPosition(double newZoomLevel, Point2D position) {
        double percentX = position.getX() / body.getWidth();
        double percentY = position.getY() / body.getHeight();

        setZoomLevel(newZoomLevel);

        scroller.layout();

        scroller.setHvalue(percentX * scroller.getHmax());
        scroller.setVvalue(percentY * scroller.getVmax());
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    /**
     * Takes a node and converts it into a NodeGUI with appropriate mouseEvents for highlighting. Also implements selection if this Map is editable.
     *
     * @param node The Node to be added.
     */
    public void addNode(Node node) {
        NodeGUI nodeGUI = new NodeGUI(node);

        nodeGUI.setRadius(circleRadius);
        nodeGUI.fillProperty().setValue(nodeColor);
        nodeGUI.setHighlightColor(highLightColor);
        nodeGUI.setHighlightRadius(highlightThickness);

        Point2D zoomedPos = new Point2D(nodeGUI.layoutXProperty().get() * zoomLevel, nodeGUI.layoutYProperty().get() * zoomLevel);
        nodeGUI.setLayoutPos(zoomedPos);

        // Highlight and unhighlight as the node is moused over, set the cursor to arrows if it is movable
        nodeGUI.setOnMouseEntered(event -> {
            nodeGUI.setHighlighted(true);
            onSelectable = true;
        });
        nodeGUI.setOnMouseExited(event -> {
            if (!selector.contains(nodeGUI) && isEditable())
                nodeGUI.setHighlighted(false);
            onSelectable = false;
        });

        // Features involving selection and drag-and-drop only happen if this map is editable
        if (isEditable()) {
            nodeGUI.setOnMousePressed(event -> {
                if(event.isPrimaryButtonDown() && !addingEdge) {
                    // -----------Handle selection-----------
                    if (event.isShiftDown()) {
                        if (!selector.contains(nodeGUI))
                            selector.add(nodeGUI);
                    } else if (event.isControlDown()) {
                        if (selector.contains(nodeGUI))
                            selector.remove(nodeGUI);
                        else
                            selector.add(nodeGUI);
                    } else {
                        selector.add(nodeGUI);
                    }

                    // -----------Handle moving the nodes-----------
                    if (selector.contains(nodeGUI) && event.isPrimaryButtonDown()) {
                        for (NodeGUI gui : selector.getNodes()) {
                            selector.setNodePosition(gui, gui.getLayoutPos().subtract(body.sceneToLocal(new Point2D(event.getSceneX(), event.getSceneY()))));
                        }
                    }
                }
            });

            //Dragging
            nodeGUI.setOnMouseDragged(event -> {
                if(event.isPrimaryButtonDown()) draggingNode = true;
            });

            //Handles the case where you just want to select 1 node and deselect the rest
            nodeGUI.setOnMouseReleased(event -> {
                if (!draggingNode) {
                    selector.clear();
                    selector.add(nodeGUI);
                }
            });

            // Done dragging
            nodeGUI.setOnMouseClicked(event -> {
                if(!addingEdge) {
                    for (NodeGUI gui : selector.getNodes()) {
                        gui.getNode().position = gui.getLayoutPos().multiply(1 / zoomLevel);
                        selector.setNodePosition(gui, gui.getLayoutPos().subtract(new Point2D(event.getX(), event.getY())));
                    }
                    draggingNode = false;
                }
            });

            // -----------Handle adding the edge-----------
           /* nodeGUI.setOnMousePressed(event -> {
                if(event.isSecondaryButtonDown() && !draggingNode && !dragSelecting) {
                    Line tempEdge = new Line();
                    tempEdge.setStartX(nodeGUI.getCenterX());
                    tempEdge.setEndY(nodeGUI.getCenterY());
                    tempEdge.setEndX(event.getX());
                    tempEdge.setEndY(event.getY());
                    tempEdge.setMouseTransparent(true);

                    addingEdge = true;
                }
            });*/

        }

        if(!graph.getNodes().contains(node))
            graph.addNode(node);

        nodes.put(node, nodeGUI);
        node.data.put("GUI", nodeGUI);

        body.getChildren().addAll(nodeGUI.getAllNodes());
    }

    private void removeNode(NodeGUI nodeGUI) {
        // Remove the node from the graph and the nodeGUI from the Pane
        nodes.remove(nodeGUI);
        body.getChildren().removeAll(nodeGUI.getAllNodes());
        graph.removeNode(nodeGUI.getNode());

        // Remove all the Edges and EdgeGUIs going to and from the node
        for (Node neighbor : nodeGUI.getNode().getNeighbors()) {
            Edge edgeToNode = neighbor.edgeFromDest(nodeGUI.getNode());
            Edge edgeFromNode = nodeGUI.getNode().getEdge(neighbor);
            neighbor.removeEdge(edgeToNode);
            nodeGUI.getNode().removeEdge(edgeFromNode);
            body.getChildren().removeAll(edges.get(edgeToNode).getAllNodes());
            body.getChildren().removeAll(edges.get(edgeFromNode).getAllNodes());
        }
    }

    /**
     * Takes a new edge and adds it to the MapPane as an EdgeGUI.
     *
     * @param edge The edge to be added
     */
    private void addEdge(Edge edge) {
        EdgeGUI edgeGUI = new EdgeGUI(edge);
        edgeGUI.strokeProperty().setValue(nodeColor);
        edgeGUI.setHighlightColor(highLightColor);
        edgeGUI.setHighlightRadius(highlightThickness);

        // Set start position of the line to the source node
        edgeGUI.startXProperty().bind(getNodeGUI(edge.getSource()).layoutXProperty());
        edgeGUI.startYProperty().bind(getNodeGUI(edge.getSource()).layoutYProperty());

        // Set end position of the line to the destination node
        edgeGUI.endXProperty().bind(getNodeGUI(edge.destination).layoutXProperty());
        edgeGUI.endYProperty().bind(getNodeGUI(edge.destination).layoutYProperty());

        // Highlight on moused over
        edgeGUI.setOnMouseEntered(event -> edgeGUI.setHighlighted(true));
        edgeGUI.setOnMouseExited(event -> edgeGUI.setHighlighted(false));

        edges.put(edge, edgeGUI);
        edge.data.put("GUI", edgeGUI);

        body.getChildren().addAll(0, edgeGUI.getAllNodes());
    }

    public NodeGUI getNodeGUI(Node node) {
        return nodes.get(node);
    }

    public EdgeGUI getEdgeGUI(Edge edge) {
        return edges.get(edge);
    }

    /**
     * Generates a string to display information about the cameras current location and zoom.
     *
     * @return A string containing the zoom level and the H and V values of the scrollbar.
     */
    private String positionInfo() {
        return "+" + round(getZoomLevel(), 3) + "\n(" +
                round(scroller.getHvalue(), 3) +
                ", " + round(scroller.getVvalue(), 3) + ")";
    }

    double round(double num, int place) {
        return Math.round(num * Math.pow(10, place)) / Math.pow(10, place);
    }

}
