package edu.wpi.cs3733.d20.teamL.views.controllers.map;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import com.jfoenix.controls.*;
import com.twilio.twiml.voice.Echo;
import edu.wpi.cs3733.d20.teamL.entities.Building;
import edu.wpi.cs3733.d20.teamL.entities.Graph;
import edu.wpi.cs3733.d20.teamL.services.messaging.IMessengerService;
import edu.wpi.cs3733.d20.teamL.services.pathfinding.IPathfinderService;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import edu.wpi.cs3733.d20.teamL.util.search.SearchFields;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import com.google.inject.Inject;

import lombok.extern.slf4j.Slf4j;

import edu.wpi.cs3733.d20.teamL.entities.Node;
import edu.wpi.cs3733.d20.teamL.services.db.IDatabaseCache;
import edu.wpi.cs3733.d20.teamL.entities.Path;
import edu.wpi.cs3733.d20.teamL.util.FXMLLoaderHelper;
import edu.wpi.cs3733.d20.teamL.util.search.SearchFields;
import edu.wpi.cs3733.d20.teamL.views.components.EdgeGUI;
import edu.wpi.cs3733.d20.teamL.views.components.MapPane;
import edu.wpi.cs3733.d20.teamL.views.components.NodeGUI;

@Slf4j
public class MapViewerController {
    @FXML
    MapPane map;

    @FXML
    JFXTextField startingPoint, destination;

    @FXML
    JFXButton btnNavigate, floorUp, floorDown;

    @FXML
    ScrollPane scroll;

    @FXML
    VBox instructions;
    @FXML
	VBox floorSelector;

    @FXML
    JFXButton btnTextMe, btnQR;

    @FXML
    StackPane stackPane;

    @FXML
    JFXListView listHall, listDept, listStair, listElev, listRest, listServ, listLabs, listExit, listRetail, listConf;

    @FXML
    private Label timeLabel;

    @Inject
    private IDatabaseCache cache;
    @Inject
    private IPathfinderService pathfinderService;
    @Inject
    private IMessengerService messenger;
    @Inject
    private IMessengerService messengerService;

    private SearchFields sf;
    private JFXAutoCompletePopup<String> autoCompletePopup;
    private FXMLLoaderHelper loaderHelper = new FXMLLoaderHelper();
    private final Timer timer = new Timer();
    private Path path = new Path();

    @FXML
    private void initialize() {
        timer.scheduleAtFixedRate(timerWrapper(this::updateTime), 0, 1000);
        cache.cacheAllFromDB();

        map.setEditable(false);
        map.setHighLightColor(Color.GOLD);
        btnNavigate.setDisableVisualFocus(true);

        Building startBuilding = new Building("Faulkner");
        Graph nodes = Graph.graphFromCache(cache.getNodeCache(), cache.getEdgeCache());
        startBuilding.addAllNodes(nodes.getNodes());
        map.setBuilding(startBuilding);

        // Add floor buttons
        for (int i = 1; i <= startBuilding.getMaxFloor(); i++) {
            JFXButton newButton = new JFXButton();
            newButton.setButtonType(JFXButton.ButtonType.RAISED);
            newButton.getStylesheets().add("edu/wpi/cs3733/d20/teamL/css/MapStyles.css");
            newButton.setText("" + i);
            newButton.setOnAction(this::handleFloor);
            newButton.getStyleClass().add("floor-buttons");

            floorSelector.getChildren().add(1, newButton);
        }

        setFloor(2);

        map.setZoomLevel(0.65);
        map.init();


        sf = new SearchFields(cache.getNodeCache());
        sf.getFields().addAll(Arrays.asList(SearchFields.Field.shortName, SearchFields.Field.longName));
        sf.populateSearchFields();
        autoCompletePopup = new JFXAutoCompletePopup<>();
        autoCompletePopup.getSuggestions().addAll(sf.getSuggestions());

        Collection <Node> allNodes = nodes.getNodes();
        Collection<String> hallNodes = new ArrayList<>();
        Collection<String> deptNodes = new ArrayList<>();
        Collection<String> stairNodes = new ArrayList<>();
        Collection<String> elevNodes = new ArrayList<>();
        Collection<String> restNodes = new ArrayList<>();
        Collection<String> servNodes = new ArrayList<>();
        Collection<String> labsNodes = new ArrayList<>();
        Collection<String> exitNodes = new ArrayList<>();
        Collection<String> retlNodes = new ArrayList<>();
        Collection<String> confNodes = new ArrayList<>();

        for (Node node : allNodes) {
            if (node.getType().equals("HALL")) { hallNodes.add(node.getLongName());
            } else if (node.getType().equals("DEPT")) { deptNodes.add(node.getLongName());
            } else if (node.getType().equals("STAI")) { stairNodes.add(node.getLongName());
            } else if (node.getType().equals("ELEV")) { elevNodes.add(node.getLongName());
            } else if (node.getType().equals("REST")) { restNodes.add(node.getLongName());
            } else if (node.getType().equals("SERV")) { servNodes.add(node.getLongName());
            } else if (node.getType().equals("LABS")) { labsNodes.add(node.getLongName());
            } else if (node.getType().equals("EXIT")) { exitNodes.add(node.getLongName());
            } else if (node.getType().equals("RETL")) { retlNodes.add(node.getLongName());
            } else { confNodes.add(node.getLongName());
            }
        }

        listHall.getItems().addAll(hallNodes);
        listDept.getItems().addAll(deptNodes);
        listStair.getItems().addAll(stairNodes);
        listElev.getItems().addAll(elevNodes);
        listRest.getItems().addAll(restNodes);
        listServ.getItems().addAll(servNodes);
        listLabs.getItems().addAll(labsNodes);
        listExit.getItems().addAll(exitNodes);
        listRetail.getItems().addAll(retlNodes);
        listConf.getItems().addAll(confNodes);

    }

    @FXML
    private void startingPointAutocomplete() {
        sf.applyAutocomplete(startingPoint, autoCompletePopup);
    }

    @FXML
    private void destinationAutocomplete() {
        sf.applyAutocomplete(destination, autoCompletePopup);
    }

    public void setStartingPoint(String startingPoint) {
        this.startingPoint.setText(startingPoint);
    }

    public void setDestination(String destination) {
        this.destination.setText(destination);
    }

    /**
     * Shows everything required for a navigations, includes:
     * highlighting the path
     * showing text directions
     * showing 'text me directions' button
     */
    @FXML
    public void navigate() {
        Node startNode = sf.getNode(startingPoint.getText());
        Node destNode = sf.getNode(destination.getText());

        setFloor(startNode.getFloor());
        if (startNode != null && destNode != null) {
            String directions = highlightSourceToDestination(startNode, destNode);
            messengerService.setDirections(directions);

            messenger.setDirections(directions);
            Label directionsLabel = new Label();
            directionsLabel.setFont(new Font(14));
            directionsLabel.setText(directions);
            directionsLabel.setTextFill(Color.WHITE);
            directionsLabel.setWrapText(true);

            instructions.getChildren().clear();
            instructions.getChildren().add(directionsLabel);
            scroll.setVisible(true);
            btnTextMe.setDisable(false);
            btnTextMe.setVisible(true);
            btnQR.setDisable(false);
            btnQR.setVisible(true);
        }
    }

    /**
     * Shows the key popup
     *
     */
    @FXML
    private void showLegend() {
        try {
            Parent root = loaderHelper.getFXMLLoader("map_viwer/keyPopUp").load();
            loaderHelper.setupPopup(new Stage(), new Scene(root));
        } catch (IOException ex) {
            log.error("Couldn't load LegendPopup.fxml", ex);
        }
    }

    private String highlightSourceToDestination(Node source, Node destination) {
        map.getSelector().clear();

        if(!path.getPathNodes().isEmpty()) {
            NodeGUI start = map.getNodeGUI(path.getPathNodes().get(0));
            NodeGUI end = map.getNodeGUI(path.getPathNodes().get(path.getPathNodes().size()-1));

            map.resetNodeVisibility(start);
            map.resetNodeVisibility(end);
        }


        path = pathfinderService.pathfind(map.getBuilding(), source, destination);
        highLightPath();

        ArrayList<String> message = path.generateTextMessage();
        StringBuilder builder = new StringBuilder();

        for(String direction : message) {
            builder.append(direction + "\n\n");
        }

        return builder.toString();
    }

    private void highLightPath() {
        Iterator<Node> nodeIterator = path.iterator();

        // Loop through each node in the path and select it as well as the edge pointing to the next node
        Node currentNode = nodeIterator.next();
        Node nextNode;

        while (nodeIterator.hasNext()) {
            nextNode = nodeIterator.next();
            EdgeGUI edgeGUI = map.getEdgeGUI(currentNode.getEdge(nextNode));

            if (edgeGUI != null) map.getSelector().add(edgeGUI);

            currentNode = nextNode;
        }

        NodeGUI start = map.getNodeGUI(path.getPathNodes().get(0));
        NodeGUI end = map.getNodeGUI(path.getPathNodes().get(path.getPathNodes().size() - 1));

        if (start != null) {
            start.setVisible(true);
            start.getCircle().setFill(new ImagePattern(new Image("/edu/wpi/cs3733/d20/teamL/assets/nodes_filled/START_filled.png")));
        }
        if (end != null) {
            end.setVisible(true);
            end.getCircle().setFill(new ImagePattern(new Image("/edu/wpi/cs3733/d20/teamL/assets/nodes_filled/END_filled.png")));
        }
    }

    private void labelNode(NodeGUI nodeGUI, Label label) {
        AnchorPane parent = (AnchorPane) nodeGUI.getParent();

        parent.getChildren().add(label);

        label.setLayoutX(nodeGUI.getLayoutX());
        label.setLayoutY(nodeGUI.getLayoutY());
    }

    public MapPane getMap() {
        return map;
    }

    @FXML
    public void handleText(){
        try {
            Parent root = loaderHelper.getFXMLLoader("map_viewer/SendDirectionsPage").load();
            loaderHelper.setupPopup(new Stage(), new Scene(root));
        } catch (IOException e) {
            log.error("Encountered IOException", e);
        }
    }

    @FXML
    public void genQR(){
        try {
            Parent root = loaderHelper.getFXMLLoader("map_viewer/QRCode").load();
            loaderHelper.setupPopup(new Stage(), new Scene(root));
        } catch (IOException e) {
            log.error("Encountered IOException", e);
        }
    }

    @FXML
    public void handleFloor(ActionEvent event) {
        JFXButton sourceButton = (JFXButton) event.getSource();

        if (event.getSource() == floorUp && map.getFloor() < 5) {
            setFloor(map.getFloor() + 1);
        } else if (event.getSource() == floorDown) {
            setFloor(map.getFloor() - 1);
        } else if (MapEditorController.isNumeric(sourceButton.getText())) {
            setFloor(Integer.parseInt(sourceButton.getText()));
        }

        if (!path.getPathNodes().isEmpty()) highLightPath();
    }

    @FXML
    private void zoomIn() {
        map.setZoomLevel(map.getZoomLevel() * 1.2);
    }

    @FXML
    private void zoomOut() {
        map.setZoomLevel(map.getZoomLevel() * 0.8);
    }

    public void setFloor(int newFloor) {
        map.setFloor(Math.max(1, Math.min(newFloor, map.getBuilding().getMaxFloor())));

        for (javafx.scene.Node node : floorSelector.getChildren()) {
            JFXButton floorButton = (JFXButton) node;
            if (!floorButton.getText().equals(String.valueOf(map.getFloor()))) {
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

    /**
     * Clears the text in source textfield
     *
     */
    @FXML
    private void clearSource() {
        startingPoint.clear();
    }

    /**
     * Clears the text in destination textfield
     *
     */
    @FXML
    private void clearDest() { destination.clear(); }

    /**
     * login pops up when login button is clicked
     */
    @FXML
    private void loginBtnClicked() {
        try {
            Parent root = loaderHelper.getFXMLLoader("Staff/LoginPage").load();
            loaderHelper.setupPopup(new Stage(), new Scene(root));
        } catch (IOException ex) {
            log.error("Encountered IOException", ex);
        }
    }

    /**
     * Displays the About page of the application
     *
     */
    @FXML
    public void handleAbout() {

        JFXDialogLayout content = new JFXDialogLayout();
        content.setHeading(new Text("About"));
        content.setBody(new Text("WPI Computer Science Department\n" +
                "CS3733-D20 Software Engineering\n" +
                "Prof. Wilson Wong\n" +
                "Team Coach: Chris Myers\n" +
                "Lead Software Engineer: Conrad Tulig\n" +
                "Assistant Lead Software Engineer: Luke Bodwell\n" +
                "Assistant Lead Software Engineer: Caleb Farwell\n" +
                "Project Manager: Joshua Hoy\n" +
                "Scrum Master: Colin Scholler\n" +
                "Product Owner: Tori Buyck\n" +
                "Algorithms Specialist: Cameron Jacobson\n" +
                "UI Engineer: Winnie Ly\n" +
                "Documentation Analyst: Zaiyang Zhong\n\n" +
                "Thank you Brigham and Women's Hospital and Andrew Shinn for your time and input."));
        JFXDialog dialog = new JFXDialog(stackPane, content, JFXDialog.DialogTransition.CENTER);
        JFXButton btnDone = new JFXButton("Done");
        btnDone.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                dialog.close();
            }
        });
        content.setActions(btnDone);
        dialog.show();
    }

    private TimerTask timerWrapper(Runnable r) {
        return new TimerTask() {
            @Override
            public void run() {
                r.run();
            }
        };
    }

    private void updateTime() {
        ;
        Platform.runLater(() -> timeLabel.setText(new SimpleDateFormat("E, MMM d | h:mm aa").format(new Date())));
    }

    /**
     * Changes starting location with destination and vice-versa.
     *
     */
    @FXML
    public void handleLocationChange() {

        String startLoc = startingPoint.getText();
        String destLoc = destination.getText();

        startingPoint.setText(destLoc);
        destination.setText(startLoc);
    }

    @FXML
    public void handleDestination() {

        String dest = (String) listHall.getSelectionModel().getSelectedItem();
        destination.setText(dest);

    }
}
