package edu.wpi.leviathans.pathFinding.mapViewer;

import edu.wpi.leviathans.pathFinding.MapParser;
import edu.wpi.leviathans.pathFinding.graph.*;

import edu.wpi.leviathans.pathFinding.mapViewer.dataDialogue.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import javafx.geometry.Point2D;

import java.util.ArrayList;
import java.util.List;

public class MapViewer {
    @FXML MenuItem save;
    @FXML MenuItem saveAs;
    @FXML MenuItem open;
    @FXML MenuItem quit;

    @FXML MenuItem undo;
    @FXML MenuItem redo;

    @FXML MenuItem node;
    @FXML MenuItem edge;

    @FXML ToggleGroup tools;

    @FXML AnchorPane body;
    @FXML Label position;

    AnchorPane nodePane = new AnchorPane();
    Graph graph = new Graph();

    List<javafx.scene.Node> nodes = new ArrayList<>();

    @FXML MapApp appInstance;

    private int circleRadius = 12;
    private Color nodeColor = Color.ORANGE;
    private double zoomLevel = 0.5;

    public void setZoomLevel(double newZoomLevel) {
        newZoomLevel = Math.max(newZoomLevel, 0.01);

        for(javafx.scene.Node node : nodes) {
            Point2D prevPos = new Point2D(node.getLayoutX(), node.getLayoutY());
            Point2D newPos = prevPos.multiply(newZoomLevel / zoomLevel);
            node.setLayoutX(newPos.getX());
            node.setLayoutY(newPos.getY());
        }

        zoomLevel = newZoomLevel;
    }

    public void changeZoomLevelBy(double deltaZoomLevel) {
        setZoomLevel(zoomLevel + deltaZoomLevel);
    }

    public double getZoomLevel() {
        return zoomLevel;
    }

    @FXML void quit() {
        Platform.exit();
    }

    @FXML void save() {
        Alert saveAlert = new Alert(Alert.AlertType.WARNING);
        saveAlert.setTitle("Cannot complete operation");
        saveAlert.setContentText("Nothing to save");

        saveAlert.showAndWait();
    }

    @FXML public void saveAs() {
        save();
    }

    @FXML public void open() {
        DataDialogue data = new DataDialogue();

        data.showDialogue(appInstance.pStage);

        graph = MapParser.parseMapToGraph(data.getNodeFile(), data.getEdgeFile());

        nodePane = paneFromGraph(graph);

        body.getChildren().add(nodePane);
    }

    private AnchorPane paneFromGraph(Graph graph) {
        AnchorPane root = new AnchorPane();

        for (Node node : graph.getNodes()) {
            Circle nodeGUI = new Circle(circleRadius);
            nodeGUI.fillProperty().setValue(nodeColor);

            nodeGUI.setLayoutX(((int) node.data.get(MapParser.DATA_LABELS.X)) * zoomLevel);
            nodeGUI.setLayoutY(((int) node.data.get(MapParser.DATA_LABELS.Y)) * zoomLevel);

            root.getChildren().add(nodeGUI);
            nodes.add(nodeGUI);
            node.data.put("GUI", nodeGUI);
        }

        return root;
    }
}
