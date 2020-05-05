package edu.wpi.cs3733.d20.teamL.views.controllers.map;

import com.jfoenix.controls.*;
import edu.wpi.cs3733.d20.teamL.App;
import edu.wpi.cs3733.d20.teamL.entities.*;
import edu.wpi.cs3733.d20.teamL.entities.Edge;
import edu.wpi.cs3733.d20.teamL.services.db.IDatabaseCache;
import edu.wpi.cs3733.d20.teamL.services.pathfinding.IPathfinderService;
import edu.wpi.cs3733.d20.teamL.services.pathfinding.MapParser;
import edu.wpi.cs3733.d20.teamL.services.pathfinding.PathfinderService;
import edu.wpi.cs3733.d20.teamL.util.FXMLLoaderFactory;
import edu.wpi.cs3733.d20.teamL.util.io.CSVHelper;
import edu.wpi.cs3733.d20.teamL.util.search.SearchFields;
import edu.wpi.cs3733.d20.teamL.views.components.*;
import edu.wpi.cs3733.d20.teamL.views.controllers.dialogues.DataDialogue;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.ImageCursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.BorderPane;

import javafx.geometry.Point2D;

import java.util.*;
import java.util.List;
import javax.inject.Inject;

import javafx.scene.layout.VBox;


import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MapEditorController {
    @FXML
    JFXTextField startNode, endNode;
    @FXML
    JFXButton pathFind, btnCancel, btnSave, saveToDB, saveToCSV, open, node, saveOptions, loadOptions, pathfindingOptions, floorUp, floorDown;
    @FXML
    JFXButton hall, elev, rest, stai, dept, labs, info, conf, exit, retl, serv;
    @FXML
    JFXToggleNode eraser;
    @FXML
    BorderPane root;
    @FXML
    MapPane map;
    @FXML
    Label nodeIDText, numberlbl;
    @FXML
    JFXTextField xCoordText, yCoordText, buildingText, nodeTypeText, shortNameText, longNameText;
    @FXML
    ComboBox nodeTypeValue, numberText;
    @FXML
    VBox editor, multiFloorConnection, nodeConnectionsTab, floorSelector, edgeList;
    @FXML
    JFXNodesList saveNodesList, loadNodesList, pathNodesList;
    @FXML
    JFXComboBox<String> buildingChooser;
    @FXML
    Tooltip saveTooltip, loadTooltip, pathfindTooltip;
    @FXML
    ImageView saveOptImg, loadOptionsImage, pathfindImage;

    @Inject
    private IDatabaseCache cache;
    @Inject
    private IPathfinderService pathfinder;

    private Scene scene;
    private FXMLLoaderFactory loaderHelper = new FXMLLoaderFactory();
    private SearchFields sf;
    private JFXAutoCompletePopup<String> autoCompletePopup;
    private boolean eraserBool = false;
    private char pathFindingAlg;
    private Path path = new Path();

    private final List<String> types = Arrays.asList("HALL", "ELEV", "REST", "STAI", "DEPT", "LABS", "INFO", "CONF", "EXIT", "RETL", "SERV");
    private int floor = 2;

    private Image breadthFirstIcon = new Image("/edu/wpi/cs3733/d20/teamL/assets/map_editor/Breath First.png", 100, 0, true, false, true);
    private Image depthFirstIcon = new Image("/edu/wpi/cs3733/d20/teamL/assets/map_editor/DepthFirst.png", 100, 0, true, false, true);
    private Image aStarIcon = new Image("/edu/wpi/cs3733/d20/teamL/assets/map_editor/AStar.png", 60, 0, true, false, true);
    private Image xButtonIcon = new Image("/edu/wpi/cs3733/d20/teamL/assets/map_editor/xButton.png", 40, 0, true, false, true);
    private Image saveToFileIcon = new Image("/edu/wpi/cs3733/d20/teamL/assets/map_editor/SaveToFile.png", 40, 0, true, false, true);
    private Image uploadFromFolderIcon = new Image("/edu/wpi/cs3733/d20/teamL/assets/map_editor/UploadFromFolder.png", 40, 0, true, false, true);


    @FXML
    public void initialize() {
        scene = root.getScene();

        coreShortcuts();

        pathFind.setOnAction(event -> {
            path = pathfinder.pathfind(map.getBuilding(), sf.getNode(startNode.getText()), sf.getNode(endNode.getText()));

            highlightPath();
        });

        sf = new SearchFields(cache.getNodeCache());
        sf.getFields().add(SearchFields.Field.nodeID);
        sf.getFields().add(SearchFields.Field.longName);
        sf.populateSearchFields();
        autoCompletePopup = new JFXAutoCompletePopup<>();
        autoCompletePopup.getSuggestions().addAll(sf.getSuggestions());

        map.setEditable(true);
        map.init();
        openFromDB();

        map.setZoomLevel(0.65);

        buildingChooser.getItems().addAll("Faulkner", "BTM");
        buildingChooser.getSelectionModel().select("Faulkner");

        generateFloorButtons();
        setFloor(2);

        //Hides the node editor VBox
        editor.setPrefWidth(0);
        editor.setVisible(false);

        //Hides the edges editor VBox
        nodeConnectionsTab.setPrefWidth(0);
        nodeConnectionsTab.setVisible(false);

        map.recalculatePositions();

        eraser.setDisableAnimation(true);

        if (pathfinder.getPathfindingMethod() == PathfinderService.PathfindingMethod.Astar) {
            pathFindingAlg = 'A';
            pathfindImage.setImage(aStarIcon);
        }
        if (pathfinder.getPathfindingMethod() == PathfinderService.PathfindingMethod.BFS) {
            pathFindingAlg = 'B';
            pathfindImage.setImage(breadthFirstIcon);
        }
        if (pathfinder.getPathfindingMethod() == PathfinderService.PathfindingMethod.DFS) {
            pathFindingAlg = 'D';
            pathfindImage.setImage((depthFirstIcon));
        }
    }

    private void generateFloorButtons() {
        while (floorSelector.getChildren().size() > 2) {
            floorSelector.getChildren().remove(1);
        }
        for (Floor floor : map.getBuilding().getFloors()) {
            JFXButton newButton = new JFXButton();
            newButton.setButtonType(JFXButton.ButtonType.RAISED);
            newButton.getStylesheets().add("edu/wpi/cs3733/d20/teamL/css/MapStyles.css");
            newButton.setText(floor.getFloorAsString());
            newButton.setOnAction(this::changeFloor);
            newButton.getStyleClass().add("floor-buttons");

            floorSelector.getChildren().add(1, newButton);
        }
    }

    @FXML
    private void switchBuilding() {
        String selected = buildingChooser.getSelectionModel().getSelectedItem();

        map.setBuilding(selected);

        int prevFloor = map.getFloor();
        generateFloorButtons();
        setFloor(Math.max(map.getBuilding().getMinFloor(), Math.min(prevFloor, map.getBuilding().getMaxFloor())));
    }

    private void highlightPath() {
        Iterator<Node> nodeIterator = path.iterator();

        // Loop through each node in the path and select it as well as the edge pointing to the next node
        Node currentNode = nodeIterator.next();
        Node nextNode;
        while (nodeIterator.hasNext()) {
            nextNode = nodeIterator.next();

            NodeGUI nodeGUI = map.getNodeGUI(currentNode);
            EdgeGUI edgeGUI = map.getEdgeGUI(currentNode.getEdge(nextNode));

            if (nodeGUI != null) map.getSelector().add(nodeGUI);
            if (edgeGUI != null) map.getSelector().add(edgeGUI);

            currentNode = nextNode;
        }
        // The above loop does not highlight the last node, this does that
        NodeGUI nodeGUI = map.getNodeGUI(currentNode);
        if (nodeGUI != null) {
            map.getSelector().add(nodeGUI);
            nodeGUI.setHighlighted(true);
        }
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

    }

    /**
     * @return a map pane that is used for both map_editor and map viewer
     */
    public MapPane getMap() {
        return map;
    }

    @FXML
    private void autocompletestart() {
        sf.applyAutocomplete(startNode, autoCompletePopup);
    }

    @FXML
    private void autocompleteend() {
        sf.applyAutocomplete(endNode, autoCompletePopup);
    }

    @FXML
    private void quit() {
        cache.updateDB();
        Platform.exit();
    }

    @FXML
    private void saveToDB() {
        Graph nodes = new Graph();
        for (Building building : map.getBuildings()) {
            nodes.addAllNodes(building);
        }
        //ArrayList<Edge> blackList = new ArrayList<>();
        ArrayList<Edge> newEdges = new ArrayList<>(nodes.getEdgesOneWay());

        /*for (Node node : nodes) {
            for (Edge edge : node.getEdges()) {
                if (!newEdges.contains(edge) && blackList.contains(edge)) newEdges.add(edge);
                if (edge.getDestination().getNeighbors().contains(node))
                    blackList.add(edge.getDestination().getEdge(node));
            }
        }*/

        cache.cacheNodes(new ArrayList<>(nodes.getNodes()), map.getEditedNodes());
        cache.cacheEdges(newEdges);
        cache.updateDB();

        map.getEditedNodes().clear();
    }

    @FXML
    public void saveToCSV() {
        // Show a data dialogue to get the path to save the nodes and edges
        DataDialogue data = new DataDialogue();
        data.setSaving(true);
        data.showDialogue(pathFind.getScene().getWindow());
        String nodeFilePath = data.getNodeFile().getAbsolutePath();
        String edgeFilePath = data.getEdgeFile().getAbsolutePath();

        // Set up the CSV helper and make empty tables to populate then load into the csv
        CSVHelper csvHelper = new CSVHelper();
        ArrayList<ArrayList<String>> nodeTable = new ArrayList<>();
        ArrayList<ArrayList<String>> edgeTable = new ArrayList<>();

        // Compile the nodes from each building into one graph to export them all
        Graph allNodes = new Graph();
        for (Building building : map.getBuildings())
            allNodes.addAllNodes(building);

        // Get nodes and one way edges from the compiled graph
        ArrayList<Node> nodes = new ArrayList<>(allNodes.getNodes());
        ArrayList<Edge> edges = new ArrayList<>(allNodes.getEdgesOneWay());

        // Add headings and populate the Node and edge 2D arraylists
        nodeTable.add(new ArrayList<>(Arrays.asList("nodeID", "xCoord", "yCoord", "floor", "building", "nodeType", "longName", "shortName")));
        for (Node node : nodes) {
            nodeTable.add(node.toArrayList());
        }
        edgeTable.add(new ArrayList<>(Arrays.asList("edgeID", "startNode", "endNode")));
        for (Edge edge : edges) {
            edgeTable.add(edge.toArrayList());
        }

        // Write the 2D arraylists to csv files using the CSV Helper
        csvHelper.writeToCSV(nodeFilePath, nodeTable);
        csvHelper.writeToCSV(edgeFilePath, edgeTable);
    }

    @FXML
    public void open() {
        DataDialogue data = new DataDialogue();
        boolean confirmed = data.showDialogue(pathFind.getScene().getWindow());

        if (confirmed) {
            Graph newGraph = MapParser.parseMapToGraph(data.getNodeFile(), data.getEdgeFile());
            Building faulkner = new Building("Faulkner", newGraph);
            Building BTM = new Building("BTM", newGraph);
            map.setBuilding(faulkner);
            map.getBuildings().add(BTM);
        }
    }

    @FXML
    private void openFromDB() {
        Building faulkner = cache.getBuilding("Faulkner");
        Building btm = cache.getBuilding("BTM");

        if(!faulkner.getNodes().isEmpty()) map.setBuilding(faulkner);
        if(!btm.getNodes().isEmpty()) map.setBuilding(btm);
    }

    @FXML
    private void insertNode() {
        //Node node = new Node(map.getBuilding().getUniqueNodeID(), new Point2D(100, 100), map.getFloor(), map.getBuilding().getName());
        //map.addNode(node);
    }

    @FXML
    private void handleTools() {
        if (eraser.isSelected()) {
            map.setErasing(true);
        } else {
            map.setErasing(false);
        }
    }

    @FXML
    private void backToMain() {
        try {
            loaderHelper.goBack();
        } catch (Exception ex) {
            log.error("Encountered Exception.", ex);
        }
    }

    @FXML
    private void myCustomAction(MouseEvent event) {
        Node selectedNode = map.getSelectedNode();

        if (selectedNode == null) {
            editor.setPrefWidth(0);
            editor.setVisible(false);
        } else {
            editor.setPrefWidth(200);
            editor.setVisible(true);
            nodeIDText.setText(selectedNode.getID());
            double x = selectedNode.getPosition().getX();
            double y = selectedNode.getPosition().getY();
            xCoordText.setText(String.valueOf(x));
            yCoordText.setText(String.valueOf(y));
            nodeTypeValue.getSelectionModel().select(types.indexOf(selectedNode.getType()));
            shortNameText.setText(selectedNode.getShortName());
            longNameText.setText(selectedNode.getLongName());
            nodeTypeChanged();
        }

        closeConnections();
    }

    @FXML
    private void nodeTypeChanged() {
        int index = nodeTypeValue.getSelectionModel().getSelectedIndex();
        Node selected = map.getSelectedNode();

        if (index == 1) {
            numberlbl.setText("Elevator Number:");
            updateShaftList(selected);
        } else if (index == 3) {
            numberlbl.setText("Stairwell Number:");
            updateShaftList(selected);
        } else if (index == 8) {
            numberlbl.setText("Outdoor Connection Number:");
            updateShaftList(selected);
        }
        else {
            multiFloorConnection.setVisible(false);
        }
        numberText.getSelectionModel().select(selected.getShaft());
    }

    private void updateShaftList(Node selected) {
        multiFloorConnection.setVisible(true);
        numberText.getItems().clear();
        numberText.getItems().add("No Connection");

        int i;
        for (i = 1; i <= map.getBuilding().getMaxShaft(selected.getType()); i++) {
            numberText.getItems().add(String.valueOf(i));
        }

        numberText.getItems().add(i + " (new shaft)");

        numberText.getSelectionModel().select(selected.getShaft());
    }

    @FXML
    private void updateNode() {
        Node selectedNode = map.getSelectedNode();
        NodeGUI selectedNodeGUI = map.getNodeGUI(selectedNode);
        Collection<Node> neighbors = selectedNode.getNeighbors();

        //selectedNode.setId(nodeIDText.getText());
        double x = Double.parseDouble(xCoordText.getText());
        double y = Double.parseDouble(yCoordText.getText());
        selectedNode.setPosition(new Point2D(x, y));
        selectedNode.setBuilding(map.getBuilding().getName());
        selectedNode.setType(types.get(nodeTypeValue.getSelectionModel().getSelectedIndex()));
        selectedNode.setShortName(shortNameText.getText());
        selectedNode.setLongName(longNameText.getText());
        selectedNode.setShaft(numberText.getSelectionModel().getSelectedIndex());

        //selectedNode.setId(map.getBuilding().getUniqueNodeID(selectedNode));

        if (selectedNode.getShaft() > 0) {
            addMultiFloorEdge(selectedNode);
        }

        nodeIDText.setText(selectedNode.getID());

        for (Node neighbor : neighbors) {
            Edge edge = selectedNode.addEdgeTwoWay(neighbor);
            if (neighbor.getFloor() == map.getFloor())
                map.addEdge(edge);
        }
    }

    /**
     * Adds a node with edges linking to nodes on other floors.
     *
     * @param node The new node to add
     */
    private void addMultiFloorEdge(Node node) {
        Iterator<Edge> iterator = node.getEdges().iterator();
        while (iterator.hasNext()) {
            Edge edge = iterator.next();
            if (edge.getDestination().getFloor() != node.getFloor()) {
                edge.getDestination().removeEdge(node);
                iterator.remove();
            }
        }
        if (node.getType().equals("ELEV") || node.getType().equals("STAI") || node.getType().equals("EXIT"))
            for (Node adj : map.getBuilding().getNodes())
                if (node.getType().equals(adj.getType()) && node.getShaft() == adj.getShaft() && !node.equals(adj))
                    node.addEdgeTwoWay(adj);
    }

    @FXML
    private void changeFloor(ActionEvent event) {
        JFXButton sourceButton = (JFXButton) event.getSource();

        if (event.getSource() == floorUp && map.getFloor() < 5) {
            setFloor(map.getFloor() + 1);
        } else if (event.getSource() == floorDown) {
            setFloor(map.getFloor() - 1);
        } else {
            setFloor(Node.floorStringToInt(sourceButton.getText()));
        }

        if (!path.getPathNodes().isEmpty()) highlightPath();
    }

    @FXML
    private void handleAdd(ActionEvent event) {
        // hall, elev, rest, stai, dept, labs, info, conf, exit, retl, serv

        Node newNode = new Node("not_unique", new Point2D(hall.getLayoutX(), hall.getLayoutY()), map.getCurrentFloor().getFloor(), (map.getBuilding().getName()));

        if (event.getSource() == hall) {
            newNode.setType("HALL");
            newNode.setLongName("Hall");
            newNode.setShortName("Hall");
        } else if (event.getSource() == elev) {
            newNode.setType("ELEV");
            newNode.setLongName("Elevator");
            newNode.setShortName("Elevator");
        } else if (event.getSource() == rest) {
            newNode.setType("REST");
            newNode.setLongName("restroom");
            newNode.setShortName("restroom");
        } else if (event.getSource() == stai) {
            newNode.setType("STAI");
            newNode.setLongName("stairs");
            newNode.setShortName("stairs");
        } else if (event.getSource() == dept) {
            newNode.setType("DEPT");
            newNode.setLongName("New Department Room");
            newNode.setShortName("Department Room");
        } else if (event.getSource() == labs) {
            newNode.setType("LABS");
            newNode.setLongName("New Lab");
            newNode.setShortName("Lab");
        } else if (event.getSource() == info) {
            newNode.setType("INFO");
            newNode.setLongName("New Informational Room");
            newNode.setShortName("Informational Room");
        } else if (event.getSource() == conf) {
            newNode.setType("CONF");
            newNode.setLongName("New Conference Room");
            newNode.setShortName("Conference Room");
        } else if (event.getSource() == exit) {
            newNode.setType("EXIT");
            newNode.setLongName("Exit");
            newNode.setShortName("Exit");
        } else if (event.getSource() == retl) {
            newNode.setType("RETL");
            newNode.setLongName("New Retail Room");
            newNode.setShortName("Retail Room");
        } else if (event.getSource() == serv) {
            newNode.setType("SERV");
            newNode.setLongName("New Service Room");
            newNode.setShortName("Service Room");
        }

        newNode.setId(map.getCurrentFloor().getUniqueNodeID(newNode));
        NodeGUI newNodeGUI = map.addNode(newNode);
        map.setAddingNode(true);
        map.setTempNode(newNodeGUI);
    }

    public void setFloor(int newFloor) {
        map.setFloor(Math.max(map.getBuilding().getMinFloor(), Math.min(newFloor, map.getBuilding().getMaxFloor())));

        for (javafx.scene.Node node : floorSelector.getChildren()) {
            JFXButton floorButton = (JFXButton) node;
            if (!floorButton.getText().equals(Node.floorIntToString(map.getFloor()))) {
                if (floorButton.getStyleClass().contains("selected-floor")) {
                    floorButton.getStyleClass().clear();
                    floorButton.getStyleClass().add("button");
                    floorButton.getStyleClass().add("floor-buttons");
                }
            } else {
                if (!floorButton.getStyleClass().contains("selected-floor"))
                    floorButton.getStyleClass().add("selected-floor");
            }
        }
    }

    public static boolean isNumeric(String string) {
        try {
            Double.parseDouble(string);
            return true;
        } catch (NumberFormatException error) {
            return false;
        }
    }

    /**
     * Show editConnects tab
     */
    @FXML
    private void editConnections() {
        closeConnections();
        nodeConnectionsTab.setPrefWidth(200);
        nodeConnectionsTab.setVisible(true);

        Collection<Edge> edges = map.getSelectedNode().getEdges();
        for (Edge edge : edges) {
            EdgeField newEdgeField = new EdgeField(map.getBuilding());
            newEdgeField.setText(edge.getDestination().getID());

            edgeList.getChildren().add(newEdgeField);
        }
    }

    /**
     * Hides editConnections tab
     */
    @FXML
    private void saveConnections() {
        Node selectedNode = map.getSelectedNode();

        // Remove all the edges so the new set of edges can be added
        Iterator<Edge> iterator = selectedNode.getEdges().iterator();
        while (iterator.hasNext()) {
            Edge edge = iterator.next();
            EdgeGUI edgeGUITo = map.getEdgeGUI(edge);
            EdgeGUI edgeGUIFrom = map.getEdgeGUI(edge.getDestination().getEdge(selectedNode));

            if (edgeGUITo != null) map.removeEdgeGUI(edgeGUITo);
            if (edgeGUIFrom != null) map.removeEdgeGUI(edgeGUIFrom);

            edge.getDestination().removeEdge(selectedNode);
            iterator.remove();
        }

        // Add the new set of edges from the connection editing pane
        for (javafx.scene.Node node : edgeList.getChildren()) {
            EdgeField edgeField = (EdgeField) node;
            Node destination = map.getBuilding().getNode(edgeField.getText());

            selectedNode.addEdgeTwoWay(destination);
            map.addEdge(selectedNode.getEdge(destination));
        }

        closeConnections();
    }

    @FXML
    private void addConnection() {
        edgeList.getChildren().add(new EdgeField(map.getBuilding()));
    }

    private void closeConnections() {
        nodeConnectionsTab.setPrefWidth(0);
        nodeConnectionsTab.setVisible(false);

        edgeList.getChildren().clear();
    }

    /**
     * toggles mouse between normal mouse and eraser
     */
    @FXML
    private void eraserMouse() {
        if (!eraserBool) {
            Image eraserImage = new Image("/edu/wpi/cs3733/d20/teamL/assets/map_editor/eraserMouse.png");  //pass in the image path
            map.getParent().getScene().setCursor(new ImageCursor(eraserImage));
            eraserBool = true;
        } else if (eraserBool) {
            map.getParent().getScene().setCursor(Cursor.DEFAULT);
            eraserBool = false;
        }
    }

    @FXML
    private void saveOptionsClicked() {
        //show/hide options image
        if (saveNodesList.isExpanded()) {
            saveTooltip.setText("Click to Close");
            saveOptImg.setImage(xButtonIcon);
        } else {
            saveTooltip.setText("Click to Show Save Options");
            saveOptImg.setImage(saveToFileIcon);
        }
    }

    @FXML
    private void loadOptionsClicked() {
        //show/hide options image
        if (loadNodesList.isExpanded()) {
            loadTooltip.setText("Click to Close");
            loadOptionsImage.setImage(xButtonIcon);
        } else {
            loadTooltip.setText("Click to Show Load Options");
            loadOptionsImage.setImage(uploadFromFolderIcon);
        }
    }

    @FXML
    private void pathfindOptionsClicked() {
        //show/hide options image
        if (pathNodesList.isExpanded()) {
            pathfindTooltip.setText("Click to Close");
            pathfindImage.setImage(xButtonIcon);
        } else {
            pathfindTooltip.setText("Switch Pathfinding Algorithm");
            if (pathFindingAlg == 'A')
                pathfindImage.setImage(aStarIcon);
            if (pathFindingAlg == 'B')
                pathfindImage.setImage(breadthFirstIcon);
            if (pathFindingAlg == 'D')
                pathfindImage.setImage(depthFirstIcon);
        }
    }

    @FXML
    private void aStarSelected() {
        pathFindingAlg = 'A';
        pathfindImage.setImage(aStarIcon);
        pathNodesList.animateList(false);
        pathfinder.setPathfindingMethod(PathfinderService.PathfindingMethod.Astar);
    }

    @FXML
    private void depthSelected() {
        pathFindingAlg = 'D';
        pathfindImage.setImage(depthFirstIcon);
        pathNodesList.animateList(false);
        pathfinder.setPathfindingMethod(PathfinderService.PathfindingMethod.DFS);
    }

    @FXML
    private void breadthSelected() {
        pathFindingAlg = 'B';
        pathfindImage.setImage(breadthFirstIcon);
        pathNodesList.animateList(false);
        pathfinder.setPathfindingMethod(PathfinderService.PathfindingMethod.BFS);
    }
}
