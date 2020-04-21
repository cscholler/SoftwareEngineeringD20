package edu.wpi.cs3733.d20.teamL.views.controllers;

import edu.wpi.cs3733.d20.teamL.entities.Node;
import edu.wpi.cs3733.d20.teamL.services.db.DBCache;
import edu.wpi.cs3733.d20.teamL.services.graph.MapParser;
import edu.wpi.cs3733.d20.teamL.services.graph.Path;
import edu.wpi.cs3733.d20.teamL.services.graph.PathFinder;
import edu.wpi.cs3733.d20.teamL.views.components.EdgeGUI;
import edu.wpi.cs3733.d20.teamL.views.components.MapPane;
import edu.wpi.cs3733.d20.teamL.views.components.NodeGUI;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;

import javax.inject.Inject;
import java.util.Iterator;

public class MapViewerController {
    @FXML
    MapPane map;

    @Inject
    private DBCache dbCache;

    Node source;

    @FXML
    public void initialize() {
        dbCache.cacheAllFromDB();

        map.setEditable(false);

        dbCache.cacheAllFromDB();
        map.setGraph(MapParser.getGraphFromCache(dbCache.getNodeCache()));

        map.setZoomLevel(1);
        source = map.getGraph().getNode("LHALL01402");
    }

    @FXML
    private void nodePressed(MouseEvent event) {
        Node destination = map.getSelectedNode();

        Path path = PathFinder.aStarPathFind(map.getGraph(), source, destination);
        System.out.println(path.generateTextMessage());

        Iterator<Node> nodeIterator = path.iterator();

        // Loop through each node in the path and select it as well as the edge pointing to the next node
        Node currentNode = nodeIterator.next();
        Node nextNode;
        while (nodeIterator.hasNext()) {
            nextNode = nodeIterator.next();

            NodeGUI nodeGUI = map.getNodeGUI(currentNode);
            EdgeGUI edgeGUI = map.getEdgeGUI(currentNode.getEdge(nextNode));
            map.getSelector().add(nodeGUI);
            map.getSelector().add(edgeGUI);

            currentNode = nextNode;
        }
        // The above loop does not highlight the last node, this does that
        NodeGUI nodeGUI = map.getNodeGUI(currentNode);
        map.getSelector().add(nodeGUI);
        nodeGUI.setHighlighted(true);
    }

    public MapPane getMap() {
        return map;
    }
}
