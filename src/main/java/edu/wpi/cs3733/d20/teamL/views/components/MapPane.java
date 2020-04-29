package edu.wpi.cs3733.d20.teamL.views.components;

import edu.wpi.cs3733.d20.teamL.entities.*;
import edu.wpi.cs3733.d20.teamL.util.FXMLLoaderHelper;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.Paint;
import org.apache.derby.iapi.services.io.LimitInputStream;

import java.io.IOException;

import java.util.*;
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

    FXMLLoaderHelper loaderHelper = new FXMLLoaderHelper();

    private Map<Node, NodeGUI> nodes = new ConcurrentHashMap<>();
    private Map<Edge, EdgeGUI> edges = new ConcurrentHashMap<>();

    private Selector selector = new Selector();

    private SelectionBox selectionBox = new SelectionBox(new Point2D(0, 0), selector, (Collection<Highlightable>) (Collection<?>) nodes.values());

    private Floor currentFloor = new Floor(1);
    private Node selectedNode = null;
    private NodeGUI selectedNodeGUI = null;

    private double zoomLevel = 1;

    private boolean editable = true;

    private boolean onSelectable = false;
    private boolean draggingNode = false;
    private boolean dragSelecting = false;
    private boolean addingEdge = false;
    private boolean erasing = false;
    private boolean addingNode = false;

    private EdgeGUI tempEdge;
    private NodeGUI tempNode;
    private int nodeRadius = 12;
    private Color nodeColor = Color.rgb(13, 46, 87, 0.9);
    private Color edgeColor = Color.DARKBLUE;
    private Color highLightColor = Color.rgb(20, 194, 247);
    private double edgeThickness = 3;
    private double highlightThickness = 2;
    private Building currentBuilding;

    private ArrayList<Node> editedNodes = new ArrayList<>();

    public MapPane() {
        FXMLLoader fxmlLoader = loaderHelper.getFXMLLoader("components/MapPane");
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
            // Get the initial zoom level
            double prevZoomLevel = getZoomLevel();

            // Change the zoom level
            setZoomLevelToPosition(prevZoomLevel * (1 + event.getDeltaY() / 500), new Point2D(event.getX(), event.getY()));

            position.setText(positionInfo());
            event.consume();
        });
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
                if (!addingNode && event.getButton().equals(MouseButton.PRIMARY) && !onSelectable && !erasing) {
                    selector.clear();
                    selectedNode = null;
                    onActionProperty().get().handle(event);
                }
                if (!addingNode && addingEdge && !onSelectable && !erasing) {
                    if (event.getButton().equals(MouseButton.PRIMARY)) {

                        Node dest = new Node(currentBuilding.getUniqueNodeID(), new Point2D(event.getX(), event.getY()).multiply(1 / zoomLevel), currentFloor.getFloor(), currentBuilding.getName());

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
                if (!addingNode && !addingEdge && !draggingNode && !onSelectable && event.isPrimaryButtonDown() && !erasing) {
                    dragSelecting = true;
                    selectionBox.setRootPosition(new Point2D(event.getX(), event.getY()));
                    body.getChildren().add(selectionBox);
                    selectedNode = null;
                    onActionProperty().get().handle(event);
                }
            });

            // Change the position of all the selected nodes as the mouse is being dragged keeping their offset
            body.setOnMouseDragged(event -> {
                if (!addingNode && event.isPrimaryButtonDown() && !erasing && !addingEdge) {
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
                if (!addingNode && event.isPrimaryButtonDown() && erasing) {
                    Point2D mousePos = new Point2D(event.getX(), event.getY());
                    for (NodeGUI node : getNodes()) {
                        if (node.getBoundsInParent().contains(mousePos)) {
                            removeNode(node);
                        }
                    }
                    for (EdgeGUI edgeGUI : getEdges()) {
                        if (edgeGUI.contains(mousePos)) {
                            removeEdge(edgeGUI);
                        }
                    }
                }
            });

            body.setOnMouseReleased(event -> {
                if(addingNode && event.getButton().equals(MouseButton.PRIMARY)) {
                    tempNode.getNode().setPosition(tempNode.getLayoutPos().multiply(1 / zoomLevel));
                    addingNode = false;
                } else if(addingNode && event.getButton().equals(MouseButton.SECONDARY)) {
                    removeNode(tempNode);
                    addingNode = false;
                }
                dragSelecting = false;
                body.getChildren().remove(selectionBox);
            });

            body.setOnMouseMoved(event -> {
                if (addingEdge) {
                    tempEdge.setEndX(event.getX());
                    tempEdge.setEndY(event.getY());
                }
                if(addingNode) {
                    tempNode.setLayoutPos(new Point2D(event.getX(), event.getY()));
                }
            });


            body.addEventHandler(MouseEvent.ANY, event -> {
                if (event.getButton() != MouseButton.MIDDLE) event.consume();
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

    public SelectionBox getSelectionBox() {
        return selectionBox;
    }

    public Building getBuilding() {
        return currentBuilding;
    }

    public void setBuilding(Building currentBuilding) {
        this.currentBuilding = currentBuilding;

        currentFloor = currentBuilding.getFloor(Math.min(getFloor(), currentBuilding.getMaxFloor()));
    }

    public void setSelectedNode(Node selectedNode) {
        this.selectedNode = selectedNode;
    }

    public int getNodeRadius() {
        return nodeRadius;
    }

    public void setNodeRadius(int nodeRadius) {
        this.nodeRadius = nodeRadius;
    }

    public Color getNodeColor() {
        return nodeColor;
    }

    public void setNodeColor(Color nodeColor) {
        this.nodeColor = nodeColor;
        for (NodeGUI nodeGUI : nodes.values())
            nodeGUI.getCircle().setFill(nodeColor);
    }

    public Color getEdgeColor() {
        return edgeColor;
    }

    public void setEdgeColor(Color edgeColor) {
        this.edgeColor = edgeColor;
        for (EdgeGUI edgeGUI : edges.values())
            edgeGUI.setFill(edgeColor);
    }

    public Paint getHighLightColor() {
        return highLightColor;
    }

    public void setHighLightColor(Color highLightColor) {
        this.highLightColor = highLightColor;
        for (NodeGUI nodeGUI : nodes.values())
            nodeGUI.setHighlightColor(highLightColor);
        for (EdgeGUI edgeGUI : edges.values())
            edgeGUI.setHighlightColor(highLightColor);
    }

    public double getHighlightThickness() {
        return highlightThickness;
    }

    public void setHighlightThickness(double highlightThickness) {
        this.highlightThickness = highlightThickness;
        for (NodeGUI nodeGUI : nodes.values())
            nodeGUI.setHighlightThickness(this.highlightThickness);
        for (EdgeGUI edgeGUI : edges.values())
            setHighlightThickness(highlightThickness);
    }

    public double getEdgeThickness() {
        return edgeThickness;
    }

    public void setEdgeThickness(double edgeThickness) {
        this.edgeThickness = edgeThickness;
        for (EdgeGUI edgeGUI : edges.values())
            edgeGUI.setStrokeWidth(this.edgeThickness);
    }

    /**
     * Gets what floor the map is currently displaying. This is simply a shortcut for mapPane.getCurrentFloor.getFloor().
     *
     * @return An integer representing the current floor being displayed
     */
    public int getFloor() {
        return currentFloor.getFloor();
    }

    /**
     * Gets the Floor object containing all the nodes currently being displayed.
     *
     * @return A Floor object of the current floor
     */
    public Floor getCurrentFloor() {
        return currentFloor;
    }

    /**
     * Converts the given graph into Node and Edge GUIs and displays them with the correct map image based on their floor and building.
     *
     * @param floor The floor to display
     */
    public void setFloor(int floor) {
        this.currentFloor = currentBuilding.getFloor(floor);
        nodes.clear();
        edges.clear();
        body.getChildren().clear();

        // Add nodes to the scene
        for (Node node : currentFloor.getNodes()) {
            addNode(node);
        }

        // Add lines to the scene
        for (Edge edge : currentFloor.getEdges()) {
            if (edge.getDestination().getFloor() == getFloor() && edge.getSource().getFloor() == getFloor())
                addEdge(edge);
        }

        setMapImage(new Image("/edu/wpi/cs3733/d20/teamL/assets/maps/Floor" + getFloor() + "LM.png"));
    }

    public void recalculatePositions() {
        setZoomLevel(getZoomLevel());
    }

    public void setMapImage(Image mapImage) {
        this.mapImage.setImage(mapImage);
        this.mapImage.setFitWidth(this.mapImage.getImage().getWidth() * zoomLevel);
    }

    public Image getMapImage() {
        return mapImage.getImage();
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

        // Scale the image
        mapImage.setFitWidth(mapImage.getFitWidth() * (zoomLevel / this.zoomLevel));

        this.zoomLevel = zoomLevel;
    }

    /**
     * Sets the zoom level while preserving the camera position with respect to the given Point2D.
     *
     * @param newZoomLevel The new zoom level.
     * @param position     The coordinates of the position to zoom to.
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

        nodeGUI.getCircle().setRadius(nodeRadius);
        nodeGUI.getCircle().fillProperty().setValue(nodeColor);
        nodeGUI.setHighlightColor(highLightColor);
        nodeGUI.setHighlightThickness(highlightThickness);

        Point2D zoomedPos = new Point2D(nodeGUI.getXProperty().get() * zoomLevel, nodeGUI.getYProperty().get() * zoomLevel);
        nodeGUI.setLayoutPos(zoomedPos);

        // Set node icon based on the node type


        // Features involving selection and drag-and-drop only happen if this map is editable
        if (isEditable()) {
            nodeGUI.getCircle().setCursor(Cursor.MOVE);

            // Highlight and unhighlight as the node is moused over, set the cursor to arrows if it is movable
            nodeGUI.getCircle().setOnMouseEntered(event -> {
                if (!erasing) {
                    nodeGUI.setHighlighted(true);
                    onSelectable = true;
                }
            });

            nodeGUI.getCircle().setOnMouseExited(event -> {
                if (!selector.contains(nodeGUI))
                    nodeGUI.setHighlighted(false);
                onSelectable = false;
            });

            nodeGUI.getCircle().setOnMousePressed(event -> {
                if (!addingNode && event.isPrimaryButtonDown() && !addingEdge && !erasing) {
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
                if (!addingNode && event.isSecondaryButtonDown() && !draggingNode && !dragSelecting && !erasing) {
                    tempEdge = new EdgeGUI(nodeRadius / 4, nodeColor, highLightColor, highlightThickness);
                    tempEdge.startXProperty().bind(nodeGUI.getXProperty());
                    tempEdge.startYProperty().bind(nodeGUI.getYProperty());
                    tempEdge.setEndX(tempEdge.getStartX());
                    tempEdge.setEndY(tempEdge.getStartY());
                    tempEdge.setMouseTransparent(true);
                    tempEdge.setSource(nodeGUI);

                    body.getChildren().addAll(tempEdge);

                    addingEdge = true;
                }

                if (!addingNode && event.isPrimaryButtonDown() && addingEdge && !erasing) {
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
                if (event.isPrimaryButtonDown() && !erasing) {
                    draggingNode = true;
                }
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
                if (!addingNode && !addingEdge && !erasing && draggingNode) {
                    for (NodeGUI gui : selector.getNodes()) {
                        gui.getNode().setPosition(gui.getLayoutPos().multiply(1 / zoomLevel));
                        editedNodes.add(gui.getNode());
                        selector.setNodePosition(gui, gui.getLayoutPos().subtract(new Point2D(event.getX(), event.getY())));
                    }
                    draggingNode = false;
                }
                if (!addingNode && selector.getNodes().size() == 1) {
                    selectedNode = nodeGUI.getNode();
                    selectedNodeGUI = nodeGUI;
                    onActionProperty().get().handle(event);
                }
            });
            nodeGUI.getCircle().setFill(new ImagePattern(new Image("/edu/wpi/cs3733/d20/teamL/assets/nodes_filled/" + node.getType() + "_filled.png")));
        } else {
            /*nodeGUI.getCircle().setOnMousePressed(event -> {
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
            });*/
            resetNodeVisibility(nodeGUI);
        }
        if (!currentFloor.getNodes().contains(node))
            currentFloor.addNode(node);

        nodes.put(node, nodeGUI);
        node.getData().put("GUI", nodeGUI);

        body.getChildren().add(nodeGUI);

        recalculatePositions();
        return nodeGUI;
    }

    public void resetNodeVisibility(NodeGUI nodeGUI) {
        nodeGUI.getCircle().setFill(new ImagePattern(new Image("/edu/wpi/cs3733/d20/teamL/assets/nodes_filled/" + nodeGUI.getNode().getType() + "_filled.png")));
        List<String> visibleNodeTypes = Arrays.asList("EXIT", "REST", "ELEV", "STAI", "INFO", "RETL");
        if(!visibleNodeTypes.contains(nodeGUI.getNode().getType()))
            nodeGUI.setVisible(false);
    }

    public void removeNode(NodeGUI nodeGUI) {
        // Remove all the Edges and EdgeGUIs going to and from the node
        for (Node neighbor : nodeGUI.getNode().getNeighbors()) {
            Edge edgeToNode = neighbor.getEdge(nodeGUI.getNode());
            Edge edgeFromNode = nodeGUI.getNode().getEdge(neighbor);
            neighbor.removeEdge(edgeToNode);
            nodeGUI.getNode().removeEdge(edgeFromNode);

            if (edges.get(edgeToNode) != null) body.getChildren().removeAll(edges.get(edgeToNode).getAllNodes());
            if (edges.get(edgeFromNode) != null) body.getChildren().removeAll(edges.get(edgeFromNode).getAllNodes());
        }

        // Remove the node from the graph and the nodeGUI from the Pane
        nodes.remove(nodeGUI.getNode());
        body.getChildren().removeAll(nodeGUI.getAllNodes());
        currentFloor.removeNode(nodeGUI.getNode());
    }

    /**
     * Takes a new edge and adds it to the MapPane as an EdgeGUI.
     *
     * @param edge The edge to be added
     */
    public void addEdge(Edge edge) {
        EdgeGUI edgeGUI = new EdgeGUI(edge);
        edgeGUI.strokeProperty().setValue(edgeColor);
        edgeGUI.setHighlightColor(highLightColor);
        edgeGUI.setHighlightThickness(highlightThickness);
        edgeGUI.setStrokeWidth(edgeThickness);

        // Set start position of the line to the source node
        edgeGUI.startXProperty().bind(getNodeGUI(edge.getSource()).getXProperty());
        edgeGUI.startYProperty().bind(getNodeGUI(edge.getSource()).getYProperty());

        // Set end position of the line to the destination node
        edgeGUI.endXProperty().bind(getNodeGUI(edge.getDestination()).getXProperty());
        edgeGUI.endYProperty().bind(getNodeGUI(edge.getDestination()).getYProperty());

        edges.put(edge, edgeGUI);
        edge.getData().put("GUI", edgeGUI);

        body.getChildren().addAll(0, edgeGUI.getAllNodes());

        if (!isEditable())
            edgeGUI.setVisible(false);
    }

    public void removeEdge(EdgeGUI edgeGUI) {
        body.getChildren().removeAll(edgeGUI.getAllNodes());

        Edge edge = edgeGUI.getEdge();

        if (edge.getDestination() != null) edge.getDestination().removeEdge(edge.getSource());
        if (edge.getSource() != null) edge.getSource().removeEdge(edge);
    }


    /**
     * Removes an Edges GUI element without removing the edge entity
     *
     * @param edgeGUI
     */
    public void removeEdgeGUI(EdgeGUI edgeGUI) {
        body.getChildren().removeAll(edgeGUI.getAllNodes());

        Edge edge = edgeGUI.getEdge();
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

    public boolean isAddingNode() {
        return addingNode;
    }

    public void setAddingNode(boolean addingNode) {
        this.addingNode = addingNode;
    }

    public NodeGUI getTempNode() {
        return tempNode;
    }

    public void setTempNode(NodeGUI tempNode) {
        this.tempNode = tempNode;
    }
}
