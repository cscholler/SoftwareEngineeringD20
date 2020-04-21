package edu.wpi.cs3733.d20.teamL.views.controllers;

import com.jfoenix.controls.JFXAutoCompletePopup;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import edu.wpi.cs3733.d20.teamL.entities.Node;
import edu.wpi.cs3733.d20.teamL.services.db.DBCache;
import edu.wpi.cs3733.d20.teamL.services.graph.MapParser;
import edu.wpi.cs3733.d20.teamL.services.graph.Path;
import edu.wpi.cs3733.d20.teamL.services.graph.PathFinder;
import edu.wpi.cs3733.d20.teamL.services.navSearch.SearchFields;
import edu.wpi.cs3733.d20.teamL.util.FXMLLoaderHelper;
import edu.wpi.cs3733.d20.teamL.views.components.EdgeGUI;
import edu.wpi.cs3733.d20.teamL.views.components.MapPane;
import edu.wpi.cs3733.d20.teamL.views.components.NodeGUI;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.io.IOException;
import java.util.Iterator;

@Slf4j
public class MapViewerController {
    @FXML
    MapPane map;

    @FXML
    JFXTextField startingPoint, destination;

    @FXML
    VBox instructions;

    @FXML
    JFXButton textMe;

    @Inject
    private DBCache dbCache;

    private SearchFields sf;
    private JFXAutoCompletePopup<String> autoCompletePopup;
    private FXMLLoaderHelper loaderHelper = new FXMLLoaderHelper();

    @FXML
    public void initialize() {
        dbCache.cacheAllFromDB();

        map.setEditable(false);

        dbCache.cacheAllFromDB();
        map.setGraph(MapParser.getGraphFromCache(dbCache.getNodeCache()));

        map.setZoomLevel(1);
        map.init();

        sf = new SearchFields(dbCache.getNodeCache());
        sf.populateSearchFields();
        autoCompletePopup = new JFXAutoCompletePopup<>();
        autoCompletePopup.getSuggestions().addAll(sf.getSuggestions());
    }

    @FXML
    private void startingPointAutocomplete() {
        autocomplete(startingPoint);
    }

    @FXML
    private void destinationAutocomplete() {
        autocomplete(destination);
    }

    private void autocomplete(JFXTextField field) {
        autoCompletePopup.setSelectionHandler(event -> field.setText(event.getObject()));
        field.textProperty().addListener(observable -> {
            autoCompletePopup.filter(string ->
                    string.toLowerCase().contains(field.getText().toLowerCase()));
            if (autoCompletePopup.getFilteredSuggestions().isEmpty() ||
                    field.getText().isEmpty()) {
                autoCompletePopup.hide();
            } else {
                autoCompletePopup.show(field);
            }
        });
    }

    @FXML
    private void navigate() {
        Node startNode = sf.getNode(startingPoint.getText());
        Node destNode = sf.getNode(destination.getText());

        if(startNode != null && destNode != null) {
            String directions = highlightSourceToDestination(startNode, destNode);
            Label directionsLabel = new Label();
            directionsLabel.setText(directions);
            directionsLabel.setTextFill(Color.WHITE);
            directionsLabel.setWrapText(true);

            instructions.getChildren().clear();
            instructions.getChildren().add(directionsLabel);
            instructions.setVisible(true);
            textMe.setVisible(true);
        }
    }

    @FXML
    private void backToMain() {
        try {
            Stage stage = (Stage) startingPoint.getScene().getWindow();
            Parent newRoot = loaderHelper.getFXMLLoader("Home").load();
            Scene newScene = new Scene(newRoot);
            stage.setScene(newScene);
            stage.show();
        } catch (Exception ex) {
            log.error("Encountered Exception.", ex);
        }
    }

    private String highlightSourceToDestination(Node source, Node destination) {
        Path path = PathFinder.aStarPathFind(map.getGraph(), source, destination);
        Iterator<Node> nodeIterator = path.iterator();

        // Loop through each node in the path and select it as well as the edge pointing to the next node
        Node currentNode = nodeIterator.next();
        Node nextNode;

        while (nodeIterator.hasNext()) {
            nextNode = nodeIterator.next();
            NodeGUI nodeGUI = map.getNodeGUI(currentNode);
            EdgeGUI edgeGUI = map.getEdgeGUI(currentNode.getEdge(nextNode));

            //map.getSelector().add(nodeGUI);
            map.getSelector().add(edgeGUI);

            currentNode = nextNode;
        }

        // The above loop does not highlight the last node, this does that
        NodeGUI nodeGUI = map.getNodeGUI(currentNode);
        map.getSelector().add(nodeGUI);
        nodeGUI.setHighlighted(true);

        return path.generateTextMessage();
    }

    public MapPane getMap() {
        return map;
    }

    public void handleText() throws IOException {
        Stage stage = new Stage();
        Parent root = loaderHelper.getFXMLLoader("SendDirectionsPage").load();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(textMe.getScene().getWindow());
        stage.showAndWait();
    }
}
