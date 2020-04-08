package edu.wpi.leviathans.pathFinding.mapViewer;

import edu.wpi.leviathans.pathFinding.MapParser;
import edu.wpi.leviathans.pathFinding.graph.*;

import java.io.File;

import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;

public class MapViewer {
    public MenuItem save;
    public MenuItem saveAs;
    public MenuItem open;
    public MenuItem quit;

    public MenuItem undo;
    public MenuItem redo;

    public MenuItem node;
    public MenuItem edge;

    public ToggleGroup tools;

    public AnchorPane body;

    public MapApp appInstance;

    private int circleRadius;
    private Color nodeColor;

    public void quit() {
        Platform.exit();
    }

    public void save() {
        Alert saveAlert = new Alert(Alert.AlertType.WARNING);
        saveAlert.setTitle("Cannot complete operation");
        saveAlert.setContentText("Nothing to save");

        saveAlert.showAndWait();
    }

    public void saveAs() {
        save();
    }

    public void open() {
        FileChooser nodeChooser = new FileChooser();
        FileChooser edgeChooser = new FileChooser();

        File nodesFile = nodeChooser.showOpenDialog(appInstance.pStage);
        File edgesFile = edgeChooser.showOpenDialog(appInstance.pStage);

        Graph graph = MapParser.parseMapToGraph(nodesFile, edgesFile);

        AnchorPane graphUI = paneFromGraph(graph);

        body.getChildren().add(graphUI);
    }

    private AnchorPane paneFromGraph(Graph graph) {
        AnchorPane root = new AnchorPane();

        for (Node node : graph.getNodes()) {
            Circle nodeGUI = new Circle(circleRadius);
            nodeGUI.fillProperty().setValue(nodeColor);

            nodeGUI.setLayoutX((int) node.data.get(MapParser.DATA_LABELS.X));
            nodeGUI.setLayoutY((int) node.data.get(MapParser.DATA_LABELS.Y));

            root.getChildren().add(nodeGUI);
            node.data.put("GUI", nodeGUI);
        }

        return root;
    }
}
