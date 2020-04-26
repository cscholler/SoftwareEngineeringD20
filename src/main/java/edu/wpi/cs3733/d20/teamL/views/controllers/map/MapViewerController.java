package edu.wpi.cs3733.d20.teamL.views.controllers.map;

import java.io.IOException;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Iterator;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import com.google.inject.Inject;

import com.jfoenix.controls.JFXAutoCompletePopup;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;

import lombok.extern.slf4j.Slf4j;

import edu.wpi.cs3733.d20.teamL.entities.Node;
import edu.wpi.cs3733.d20.teamL.services.db.IDatabaseCache;
import edu.wpi.cs3733.d20.teamL.entities.Graph;
import edu.wpi.cs3733.d20.teamL.entities.Path;
import edu.wpi.cs3733.d20.teamL.util.pathfinding.PathFinder;
import edu.wpi.cs3733.d20.teamL.services.mail.IMailerService;
import edu.wpi.cs3733.d20.teamL.util.FXMLLoaderHelper;
import edu.wpi.cs3733.d20.teamL.services.search.SearchFields;
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
    JFXButton btnNavigate;

    @FXML
    VBox instructions, floorSelector;

    @FXML
    JFXButton btnTextMe;

    @Inject
    private IDatabaseCache cache;
    @Inject
	private IMailerService mailer;

    private SearchFields sf;
    private JFXAutoCompletePopup<String> autoCompletePopup;
    private FXMLLoaderHelper loaderHelper = new FXMLLoaderHelper();

    private int floor = 2;

    @FXML
    private void initialize() {
        cache.cacheAllFromDB();

        map.setEditable(false);
        btnNavigate.setDisableVisualFocus(true);

        Graph newGraph = new Graph();
        for (Node node : cache.getNodeCache()) {
            if (node.getFloor() == floor)
                newGraph.addNode(node);
        }
        map.setGraph(newGraph);

        map.setZoomLevel(1);
        map.init();
        map.getScroller().setVvalue(0.5);
        map.getScroller().setHvalue(0.5);

        sf = new SearchFields(cache.getNodeCache());
        sf.getFields().addAll(Arrays.asList(SearchFields.Field.shortName, SearchFields.Field.longName));
        sf.populateSearchFields();
        autoCompletePopup = new JFXAutoCompletePopup<>();
        autoCompletePopup.getSuggestions().addAll(sf.getSuggestions());
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

        if (startNode != null && destNode != null) {
            String directions = highlightSourceToDestination(startNode, destNode);
            mailer.setDirections(directions);
            Label directionsLabel = new Label();
            directionsLabel.setText(directions);
            directionsLabel.setTextFill(Color.WHITE);
            directionsLabel.setWrapText(true);

            instructions.getChildren().clear();
            instructions.getChildren().add(directionsLabel);
            instructions.setVisible(true);
            btnTextMe.setDisable(false);
            btnTextMe.setVisible(true);
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

    private String highlightSourceToDestination(Node source, Node destination) {
        map.getSelector().clear();

        Path path = PathFinder.aStarPathFind(map.getGraph(), source, destination);
        Iterator<Node> nodeIterator = path.iterator();

        // Loop through each node in the path and select it as well as the edge pointing to the next node
        Node currentNode = nodeIterator.next();
        Node nextNode;

        while (nodeIterator.hasNext()) {
            nextNode = nodeIterator.next();
            NodeGUI nodeGUI = map.getNodeGUI(currentNode);
            EdgeGUI edgeGUI = map.getEdgeGUI(currentNode.getEdge(nextNode));

            map.getSelector().add(edgeGUI);

            currentNode = nextNode;
        }

        // The above loop does not highlight the last node, this does that
        NodeGUI nodeGUI = map.getNodeGUI(currentNode);
        map.getSelector().add(nodeGUI);
        nodeGUI.setHighlighted(true);

        ArrayList<String> message = path.generateTextMessage();
        StringBuilder builder = new StringBuilder();

        for(String direction : message) {
            builder.append(direction + "\n\n");
        }

        return builder.toString();
    }

    public MapPane getMap() {
        return map;
    }

    @FXML
    public void handleText(){
        try {
            Parent root = loaderHelper.getFXMLLoader("SendDirectionsPage").load();
            loaderHelper.setupPopup(new Stage(), new Scene(root));
        } catch (IOException e) {
            log.error("Encountered IOException", e);
        }
    }

    @FXML
    public void handleFloor (Event event) {
        JFXButton button = (JFXButton) event.getSource();
        floor = Integer.parseInt(button.getText());

        Graph newGraph = new Graph();
        for (Node node : cache.getNodeCache()) {
            if (node.getFloor() == floor)
                newGraph.addNode(node);
        }
        map.setGraph(newGraph);
    }
}
