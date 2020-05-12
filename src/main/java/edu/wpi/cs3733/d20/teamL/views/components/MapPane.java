package edu.wpi.cs3733.d20.teamL.views.components;

import com.jfoenix.controls.JFXButton;
import edu.wpi.cs3733.d20.teamL.App;
import edu.wpi.cs3733.d20.teamL.entities.*;
import edu.wpi.cs3733.d20.teamL.util.FXMLLoaderFactory;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
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
import javafx.scene.input.ZoomEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;

import java.io.IOException;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class MapPane extends ScrollPane {
    @FXML
    private Label position;
    @FXML
    private AnchorPane body;
    @FXML
    private ScrollPane scroller;
    @FXML
    private ImageView mapImage;

    FXMLLoaderFactory loaderHelper = new FXMLLoaderFactory();

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
    private int nodeRadius = 18;
    private int hallwayNodeRadius = 10;
    private Color nodeColor = Color.rgb(13, 46, 87, 0.9);
    private Color hallNodeColor = Color.rgb(13, 46, 87);
    private Color edgeColor = Color.DARKBLUE;
    private Color highLightColor = Color.GOLD;//Color.rgb(20, 194, 247);
    private double edgeThickness = 3;
    private double highlightThickness = 2;
    private Building currentBuilding;
    private Collection<Building> buildings = new ArrayList<>();

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

        scroller.setPannable(true);
        body.setFocusTraversable(true);

        // Zoom in and out when control is held
        body.setOnScroll(event -> {
            // Get the initial zoom level
            double prevZoomLevel = getZoomLevel();

            // Change the zoom level
            setZoomLevelToPosition(prevZoomLevel * (1 + event.getDeltaY() / 500), new Point2D(event.getX(), event.getY()));
            double dir = event.getDeltaY();
            //setZoomLevelToPosition((1 + event.getDeltaY() / 500), new Point2D(event.getX(), event.getY()));
            //else testZoom(1.2, new Point2D(event.getX(), event.getY()));

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

                        Node dest = new Node(currentBuilding.getUniqueNodeID(), new Point2D(event.getX(),
                                event.getY()).multiply(1 / zoomLevel), currentFloor.getFloor(), currentBuilding.getName(),
                                "HALL", "Hall", "Hall");

                        dest.setId(currentBuilding.getUniqueNodeID(dest));

                        addNode(dest);

                        Node source = tempEdge.getSource().getNode();

                        Edge edge = new Edge(source, dest,0);
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
                    selector.clear();
                    selectionBox.setRootPosition(new Point2D(event.getX(), event.getY()));
                    body.getChildren().add(selectionBox);
                    selectedNode = null;
                }
                onActionProperty().get().handle(event);
            });

            // Change the position of all the selected nodes as the mouse is being dragged keeping their offset
            body.setOnMouseDragged(event -> {
                if (!addingNode && event.isPrimaryButtonDown() && !erasing && !addingEdge) {
                    if (draggingNode) {
                        if (event.isShiftDown() && selector.getNodes().size() == 1) {
                            NodeGUI curr = selector.getNodes().get(0);
                            curr.setLayoutPos(snapNode(curr, new Point2D(event.getX(), event.getY())));
                        } else for (NodeGUI gui : selector.getNodes()) {
                            Point2D temp = selector.getNodePosition(gui);
                            if (temp != null) gui.setLayoutPos(temp.add(new Point2D(event.getX(), event.getY())));
                        }
                    } else if (dragSelecting) {
                        selectionBox.mouseDrag(new Point2D(event.getX(), event.getY()), event.isShiftDown());
                    }
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
                if (addingNode && event.getButton().equals(MouseButton.PRIMARY)) {
                    tempNode.getNode().setPosition(tempNode.getLayoutPos().multiply(1 / zoomLevel));
                    addingNode = false;
                } else if (addingNode && event.getButton().equals(MouseButton.SECONDARY)) {
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
                if (addingNode) {
                    tempNode.setLayoutPos(new Point2D(event.getX(), event.getY()));
                }
            });


            body.addEventHandler(MouseEvent.ANY, event -> {
                if (event.getButton() != MouseButton.MIDDLE) event.consume();
            });
        }
    }

    private Point2D snapNode(NodeGUI curr, Point2D mousePos) {
        ArrayList<NodeGUI> adjacents = new ArrayList<>();
        for (Edge edge : curr.getNode().getEdges()) {
            if (getNodeGUI(edge.getDestination()) != null)
                adjacents.add(getNodeGUI(edge.getDestination()));
        }

        double minAngle = Math.PI / 4;
        NodeGUI minNode = null;
        for (NodeGUI adj : adjacents) {
            double currAngle = Math.abs(Math.atan((mousePos.getY() - adj.getLayoutY()) / (mousePos.getX() - adj.getLayoutX())));
            double tempAngle = currAngle;
            double tempMin = minAngle;

            if (tempAngle > Math.PI / 4) tempAngle = (Math.PI / 2) - tempAngle;
            if (tempMin > Math.PI / 4) tempMin = (Math.PI / 2) - tempMin;

            if (tempAngle < tempMin) {
                minAngle = currAngle;
                minNode = adj;
            }
        }

        if (minNode != null) {
            if (minAngle < Math.PI / 4) {
                return new Point2D(mousePos.getX(), minNode.getLayoutY());
            }
            return new Point2D(minNode.getLayoutX(), mousePos.getY());
        }
        return mousePos;
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

        setFloor(Math.max(Math.min(getFloor(), currentBuilding.getMaxFloor()), currentBuilding.getMinFloor()));

        boolean foundBuilding = false;
        for (Building building : getBuildings())
            if (currentBuilding.getName().equals(building.getName())) foundBuilding = true;

        if (!foundBuilding)
            getBuildings().add(currentBuilding);
    }

    public void setBuilding(String buildingName) {
        for (Building building : getBuildings()) {
            if (building.getName().equals(buildingName)) {
                setBuilding(building);
                return;
            }
        }

        throw new IllegalArgumentException("Could not find the building (" + buildingName + ") in (" + getClass() + ")");
    }

    public Collection<Building> getBuildings() {
        return buildings;
    }

    /**
     * Compiles all the nodes from both buildings into one graph.
     *
     * @return A graph containing all the nodes from both buildings
     */
    public Graph getAllNodes() {
        Graph allNodes = new Graph();
        for (Building building : getBuildings())
            allNodes.addAllNodes(building);

        return allNodes;
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

    public int getHallwayNodeRadius() {
        return hallwayNodeRadius;
    }

    public void setHallwayNodeRadius(int hallwayNodeRadius) {
        this.hallwayNodeRadius = hallwayNodeRadius;
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
     * Converts the given graph into Node and Edge GUIs and displays them with the correct map image based on their floor and buildingName.
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
            if (edge.getDestination().getFloor() == getFloor() && edge.getSource().getFloor() == getFloor() && edge.getSource().getBuilding().equals(edge.getDestination().getBuilding()))
                addEdge(edge);
        }
        try {
            Image img = new Image("/edu/wpi/cs3733/d20/teamL/assets/maps/" + getBuilding().getName() + "Floor" + currentFloor.getFloorAsString() + "LM.png");
            setMapImage(img);
        } catch (IllegalArgumentException ex) {

        }
    }

    /**
     * Inserts floor buttons into a given VBox
     */
    public void generateFloorButtons(VBox floorSelector, EventMethod handleFloor) {
        while (floorSelector.getChildren().size() > 2) {
            floorSelector.getChildren().remove(1);
        }
        for (Floor floor : getBuilding().getFloors()) {
            JFXButton newButton = new JFXButton();
            newButton.setButtonType(JFXButton.ButtonType.RAISED);
            newButton.getStylesheets().add("edu/wpi/cs3733/d20/teamL/css/MapStyles.css");
            newButton.setText(floor.getFloorAsString());
            newButton.setOnAction(handleFloor::op);
            newButton.getStyleClass().add("floor-buttons");

            floorSelector.getChildren().add(1, newButton);
        }
    }

    public interface EventMethod {
        void op(ActionEvent event);
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
        if (currentBuilding.getName().equals("Faulkner")) zoomLevel = Math.max(zoomLevel, .5 * App.UI_SCALE);
        if (currentBuilding.getName().equals("Google")) zoomLevel = Math.max(zoomLevel, .8 * App.UI_SCALE);
        else zoomLevel = Math.max(zoomLevel, .25);
        zoomLevel = Math.min(zoomLevel, 4);

        for (NodeGUI nodeGUI : nodes.values()) {
            Point2D prevPos = new Point2D(nodeGUI.getXProperty().get(), nodeGUI.getYProperty().get());
            Point2D newPos = prevPos.multiply(zoomLevel / this.zoomLevel);
            nodeGUI.setLayoutPos(newPos);
        }

        // Scale the image
        mapImage.setFitWidth(mapImage.getFitWidth() * (zoomLevel / this.zoomLevel));

        //mapImage.setScaleX(mapImage.getScaleX() * (zoomLevel / this.zoomLevel));
        //mapImage.setScaleY(mapImage.getScaleY() * (zoomLevel / this.zoomLevel));


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

    private void testZoom(double factor, Point2D mousePos) {
        //factor = 1.2;
        // Zoom into the image.
        mapImage.setScaleX(mapImage.getScaleX() * factor);
        mapImage.setScaleY(mapImage.getScaleY() * factor);
        // Calculate displacement of zooming position.
        double dx = (mousePos.getX() - scroller.getLayoutX()) * (factor - 1);
        double dy = (mousePos.getY() - scroller.getLayoutY()) * (factor - 1);
        // Compensate for displacement.
        scroller.setLayoutX(scroller.getLayoutX() - dx);
        scroller.setLayoutY(scroller.getLayoutY() - dy);
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

        if (node.getType().equals("HALL")) nodeGUI.getCircle().setRadius(getHallwayNodeRadius());
        else nodeGUI.getCircle().setRadius(getNodeRadius());

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
                        for (NodeGUI gui : selector.getNodes())
                            selector.setNodePosition(gui, gui.getLayoutPos().subtract(body.sceneToLocal(new Point2D(event.getSceneX(), event.getSceneY()))));
                    }
                }

                // -----------Handle adding the edge-----------
                if (!addingNode && event.isSecondaryButtonDown() && !draggingNode && !dragSelecting && !erasing) {
                    tempEdge = new EdgeGUI((int) edgeThickness, nodeColor, highLightColor, highlightThickness);
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

                    Edge edge = new Edge(source, dest, 0);
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
            try {
                nodeGUI.getCircle().setFill(new ImagePattern(new Image("/edu/wpi/cs3733/d20/teamL/assets/nodes_filled/" + node.getType() + "_filled.png")));
                if (nodeGUI.getNode().getType().equals("HALL") && hallNodeColor != null)
                    nodeGUI.getCircle().setFill(hallNodeColor);
            } catch (IllegalArgumentException ex) {
                nodeGUI.getCircle().setFill(nodeColor);
            }
        } else {
            resetNodeVisibility(nodeGUI);
        }
        if (!currentFloor.getNodes().contains(node))
            currentFloor.addNode(node);

        nodes.put(node, nodeGUI);
        node.getData().put("GUI", nodeGUI);

        body.getChildren().add(nodeGUI);

        return nodeGUI;
    }

    public void resetNodeVisibility(NodeGUI nodeGUI) {
        if (nodeGUI != null) {
            try {
                nodeGUI.getCircle().setFill(new ImagePattern(new Image("/edu/wpi/cs3733/d20/teamL/assets/nodes_filled/" + nodeGUI.getNode().getType() + "_filled.png")));
                if (nodeGUI.getNode().getType().equals("HALL") && hallNodeColor != null)
                    nodeGUI.getCircle().setFill(hallNodeColor);
            } catch (IllegalArgumentException ex) {
                nodeGUI.getCircle().setFill(nodeColor);
            }
            List<String> visibleNodeTypes = Arrays.asList("EXIT", "REST", "ELEV", "STAI", "INFO", "RETL", "KIOS", "REFL");
            if (!visibleNodeTypes.contains(nodeGUI.getNode().getType()))
                nodeGUI.setVisible(false);
        }
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

    public AnchorPane getBody() {
        return body;
    }
}
