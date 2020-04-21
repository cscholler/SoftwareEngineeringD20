package edu.wpi.cs3733.d20.teamL.views.components;

import edu.wpi.cs3733.d20.teamL.entities.Edge;
import edu.wpi.cs3733.d20.teamL.entities.Node;
import edu.wpi.cs3733.d20.teamL.services.graph.Graph;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MapPane extends StackPane {
    @FXML
    private Label position;
    @FXML
    private AnchorPane body;
    @FXML
    private ScrollPane scroller;
    @FXML
    private ImageView mapImage;

    private Map<Node, NodeGUI> nodes = new ConcurrentHashMap<>();
    private Map<Edge, EdgeGUI> edges = new ConcurrentHashMap<>();

    private Selector selector = new Selector();

    private SelectionBox selectionBox = new SelectionBox(new Point2D(0, 0), selector, (Collection<Highlightable>) (Collection<?>) nodes.values());

    private Graph graph = new Graph();
    private Node selectedNode = null;
    private NodeGUI selectedNodeGUI = null;

    private double zoomLevel = 1;

    private boolean editable = true;

    private boolean onSelectable = false;
    private boolean draggingNode = false;
    private boolean dragSelecting = false;
    private boolean addingEdge = false;
    private boolean erasing = false;

    private EdgeGUI tempEdge;
    private int circleRadius = 12;
    private Color nodeColor = Color.DARKBLUE;
    private Paint highLightColor = Color.rgb(20, 194, 247);
    private double highlightThickness = 2;

    private ArrayList<Node> editedNodes = new ArrayList<>();

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
        /*body.setOnScroll(event -> {
            if (event.isControlDown()) {
                // Get the initial zoom level
                double prevZoomLevel = getZoomLevel();

                // Change the zoom level
                setZoomLevelToPosition(prevZoomLevel * (1 + event.getDeltaY() / 100), new Point2D(event.getX(), event.getY()));

                position.setText(positionInfo());
            }
        });*/
    }

    public void init() {
        if (isEditable()) {
            // Delete selected nodes when delete key is pressed
            scroller.setOnKeyPressed(event -> {
                if (event.getCode().equals(KeyCode.DELETE)) {
                    for (NodeGUI nodeGUI : selector.getNodes())
                        removeNode(nodeGUI);
                }
            });

            // Deselect all nodes when clicked off, add new node if a new edge is clicked in empty space
            body.setOnMouseClicked(event -> {
                if (event.getButton().equals(MouseButton.PRIMARY) && !onSelectable && !erasing) {
                    selector.clear();
                    selectedNode = null;
                    onActionProperty().get().handle(event);
                }
                if (addingEdge && !onSelectable && !erasing) {
                    if (event.getButton().equals(MouseButton.PRIMARY)) {

                        Node dest = new Node(graph.getUniqueNodeID(), new Point2D(event.getX(), event.getY()).multiply(1 / zoomLevel));

                        addNode(dest);

                        Node source = tempEdge.getSource().getNode();

                        Edge edge = new Edge(source, dest);
                        source.addEdgeTwoWay(edge);

                        addEdge(edge);

                        body.getChildren().remove(tempEdge);
                    } else if (event.getButton().equals(MouseButton.SECONDARY)) {
                        body.getChildren().remove(tempEdge);
                    }
                    addingEdge = false;
                }
            });

            body.setOnMousePressed(event -> {
                if (!addingEdge && !draggingNode && !onSelectable && event.isPrimaryButtonDown() && !erasing) {
                    dragSelecting = true;
                    selectionBox.setRootPosition(new Point2D(event.getX(), event.getY()));
                    body.getChildren().add(selectionBox);
                    selectedNode = null;
                    onActionProperty().get().handle(event);
                }
            });

            // Change the position of all the selected nodes as the mouse is being dragged keeping their offset
            body.setOnMouseDragged(event -> {
                if (event.isPrimaryButtonDown() && !erasing && !addingEdge) {
                    if (draggingNode) {
                        for (NodeGUI gui : selector.getNodes()) {
                            Point2D temp = selector.getNodePosition(gui);
                            if (temp != null) gui.setLayoutPos(temp.add(new Point2D(event.getX(), event.getY())));
                        }
                    } else if (dragSelecting) {
                        selectionBox.mouseDrag(new Point2D(event.getX(), event.getY()), event.isShiftDown());
                    }
                    position.setText(positionInfo());
                }
                if (event.isPrimaryButtonDown() && erasing) {
                    Point2D mousePos = new Point2D(event.getX(), event.getY());
                    for (NodeGUI node : getNodes()) {
                        if (node.getBoundsInParent().contains(mousePos)) {
                            removeNode(node);
                        }
                    }
                    for (EdgeGUI edge : getEdges()) {
                        if (edge.contains(mousePos)) {
                            body.getChildren().removeAll(edge.getAllNodes());
                        }
                    }
                }
            });

            body.setOnMouseReleased(event -> {
                dragSelecting = false;
                body.getChildren().remove(selectionBox);
            });

            body.setOnMouseMoved(event -> {
                if (addingEdge) {
                    tempEdge.setEndX(event.getX());
                    tempEdge.setEndY(event.getY());
                }
            });


            body.addEventHandler(MouseEvent.ANY, event -> {
                if (event.getButton() != MouseButton.MIDDLE) event.consume();
            });
        } else {
            body.setOnMouseClicked(event -> {
                if (event.getButton().equals(MouseButton.PRIMARY) && !onSelectable) {
                    selector.clear();
                    selectedNode = null;
                    //onActionProperty().get().handle(event);
                }
            });
        }
    }

    //---------- Getters/Setters ----------//


    public ScrollPane getScroller() {
        return scroller;
    }

    public ArrayList<Node> getEditedNodes() {
        return editedNodes;
    }

    public boolean isErasing() {
        return erasing;
    }

    public void setErasing(boolean erasing) {
        this.erasing = erasing;
    }

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

        recalculatePositions();
    }

    public void recalculatePositions() {
        setZoomLevel(getZoomLevel());
    }

    public double getZoomLevel() {
        return zoomLevel;
    }

    // Sets the zoom level by changing the spacing between all the nodes
    public void setZoomLevel(double zoomLevel) {
        zoomLevel = Math.max(zoomLevel, 0.01);

        for (NodeGUI nodeGUI : nodes.values()) {
            Point2D prevPos = new Point2D(nodeGUI.getXProperty().get(), nodeGUI.getYProperty().get());
            Point2D newPos = prevPos.multiply(zoomLevel / this.zoomLevel);
            nodeGUI.setLayoutPos(newPos);
        }

        mapImage.setFitWidth(mapImage.getFitWidth() * (zoomLevel / this.zoomLevel));
        mapImage.setFitHeight(mapImage.getFitHeight() * (zoomLevel / this.zoomLevel));

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
    public NodeGUI addNode(Node node) {
        NodeGUI nodeGUI = new NodeGUI(node);

        nodeGUI.getCircle().setRadius(circleRadius);
        nodeGUI.getCircle().fillProperty().setValue(nodeColor);
        nodeGUI.setHighlightColor(highLightColor);
        nodeGUI.setHighlightRadius(highlightThickness);

        Point2D zoomedPos = new Point2D(nodeGUI.getXProperty().get() * zoomLevel, nodeGUI.getYProperty().get() * zoomLevel);
        nodeGUI.setLayoutPos(zoomedPos);

        // Highlight and unhighlight as the node is moused over, set the cursor to arrows if it is movable
        nodeGUI.getCircle().setOnMouseEntered(event -> {
            if(!erasing) {
                nodeGUI.setHighlighted(true);
                onSelectable = true;
            }
        });
        nodeGUI.getCircle().setOnMouseExited(event -> {
            if (!selector.contains(nodeGUI))
                nodeGUI.setHighlighted(false);
            onSelectable = false;
        });

        // Features involving selection and drag-and-drop only happen if this map is editable
        if (isEditable()) {
            nodeGUI.getCircle().setCursor(Cursor.MOVE);

            nodeGUI.getCircle().setOnMousePressed(event -> {
                if (event.isPrimaryButtonDown() && !addingEdge && !erasing) {
                    // -----------Handle selection-----------
                    if (event.isShiftDown()) {
                        if (!selector.contains(nodeGUI))
                            selector.add(nodeGUI);
                    } else if (event.isControlDown()) {
                        if (selector.contains(nodeGUI))
                            selector.remove(nodeGUI);
                        else
                            selector.add(nodeGUI);
                    } else if (!selector.contains(nodeGUI)) {
                        selector.clear();
                        selector.add(nodeGUI);
                    }

                    // -----------Handle moving the nodes-----------
                    if (selector.contains(nodeGUI) && event.isPrimaryButtonDown()) {
                        for (NodeGUI gui : selector.getNodes()) {
                            selector.setNodePosition(gui, gui.getLayoutPos().subtract(body.sceneToLocal(new Point2D(event.getSceneX(), event.getSceneY()))));
                        }
                    }
                }

                // -----------Handle adding the edge-----------
                if (event.isSecondaryButtonDown() && !draggingNode && !dragSelecting && !erasing) {
                    tempEdge = new EdgeGUI(circleRadius / 4, nodeColor, highLightColor, highlightThickness);
                    tempEdge.startXProperty().bind(nodeGUI.getXProperty());
                    tempEdge.startYProperty().bind(nodeGUI.getYProperty());
                    tempEdge.setEndX(tempEdge.getStartX());
                    tempEdge.setEndY(tempEdge.getStartY());
                    tempEdge.setMouseTransparent(true);
                    tempEdge.setSource(nodeGUI);

                    body.getChildren().addAll(tempEdge);

                    addingEdge = true;
                }

                if (event.isPrimaryButtonDown() && addingEdge && !erasing) {
                    Node source = tempEdge.getSource().getNode();
                    Node dest = nodeGUI.getNode();
                    int length = (int) source.getPosition().distance(dest.getPosition());

                    Edge edge = new Edge(source, dest);
                    source.addEdgeTwoWay(edge);

                    addEdge(edge);

                    body.getChildren().remove(tempEdge);

                    addingEdge = false;
                }
            });

            //Dragging
            nodeGUI.setOnMouseDragged(event -> {
                if (event.isPrimaryButtonDown() && !erasing) draggingNode = true;
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
                if (!addingEdge && !erasing && draggingNode) {
                    for (NodeGUI gui : selector.getNodes()) {
                        gui.getNode().setPosition(gui.getLayoutPos().multiply(1 / zoomLevel));
                        editedNodes.add(gui.getNode());
                        selector.setNodePosition(gui, gui.getLayoutPos().subtract(new Point2D(event.getX(), event.getY())));
                    }
                    draggingNode = false;
                }
                if(selector.getNodes().size() == 1) {
                    selectedNode = nodeGUI.getNode();
                    selectedNodeGUI = nodeGUI;
                    onActionProperty().get().handle(event);
                }
            });
        } else {
            nodeGUI.getCircle().setOnMousePressed(event -> {
                if (event.isPrimaryButtonDown() && !addingEdge && !erasing) {
                    if (!selector.contains(nodeGUI)) {
                        selector.clear();
                        selector.add(nodeGUI);
                    }
                }
            });
            nodeGUI.setOnMouseClicked(event -> {
                if (selector.getNodes().size() == 1) {
                    selectedNode = nodeGUI.getNode();
                    selectedNodeGUI = nodeGUI;
                    onActionProperty().get().handle(event);
                }
            });

            nodeGUI.setVisible(false);
        }
        if(!graph.getNodes().contains(node))
            graph.addNode(node);

        nodes.put(node, nodeGUI);
        node.data.put("GUI", nodeGUI);

        body.getChildren().add(nodeGUI);

        recalculatePositions();
        return nodeGUI;
    }

    public void removeNode(NodeGUI nodeGUI) {
        // Remove all the Edges and EdgeGUIs going to and from the node
        for (Node neighbor : nodeGUI.getNode().getNeighbors()) {
            Edge edgeToNode = neighbor.edgeFromDest(nodeGUI.getNode());
            Edge edgeFromNode = nodeGUI.getNode().getEdge(neighbor);
            neighbor.removeEdge(edgeToNode);
            nodeGUI.getNode().removeEdge(edgeFromNode);

            if(edges.get(edgeToNode) != null) body.getChildren().removeAll(edges.get(edgeToNode).getAllNodes());
            if(edges.get(edgeFromNode) != null) body.getChildren().removeAll(edges.get(edgeFromNode).getAllNodes());
        }

        // Remove the node from the graph and the nodeGUI from the Pane
        nodes.remove(nodeGUI.getNode());
        body.getChildren().removeAll(nodeGUI.getAllNodes());
        graph.removeNode(nodeGUI.getNode());
    }

    /**
     * Takes a new edge and adds it to the MapPane as an EdgeGUI.
     *
     * @param edge The edge to be added
     */
    public void addEdge(Edge edge) {
        EdgeGUI edgeGUI = new EdgeGUI(edge);
        edgeGUI.strokeProperty().setValue(nodeColor);
        edgeGUI.setHighlightColor(highLightColor);
        edgeGUI.setHighlightRadius(highlightThickness);

        // Set start position of the line to the source node
        edgeGUI.startXProperty().bind(getNodeGUI(edge.getSource()).getXProperty());
        edgeGUI.startYProperty().bind(getNodeGUI(edge.getSource()).getYProperty());

        // Set end position of the line to the destination node
        edgeGUI.endXProperty().bind(getNodeGUI(edge.getDestination()).getXProperty());
        edgeGUI.endYProperty().bind(getNodeGUI(edge.getDestination()).getYProperty());

        edges.put(edge, edgeGUI);
        edge.data.put("GUI", edgeGUI);

        body.getChildren().addAll(0, edgeGUI.getAllNodes());

        if(!isEditable())
            edgeGUI.setVisible(false);
    }

    public void removeEdge(EdgeGUI edgeGUI) {

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

    private ObjectProperty<EventHandler<MouseEvent>> propertyOnAction = new SimpleObjectProperty<>();

    public final ObjectProperty<EventHandler<MouseEvent>> onActionProperty() {
        return propertyOnAction;
    }

    public final void setOnAction(EventHandler<MouseEvent> handler) {
        propertyOnAction.set(handler);
    }

    public final EventHandler<MouseEvent> getOnAction() {
        return propertyOnAction.get();

    }

    public Node getSelectedNode() {
        return selectedNode;
    }
    public NodeGUI getSelectedNodeGUI() {
        return selectedNodeGUI;
    }

}
