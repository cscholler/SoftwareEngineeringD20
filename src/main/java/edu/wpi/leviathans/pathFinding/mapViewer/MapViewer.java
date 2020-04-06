package edu.wpi.leviathans.pathFinding.mapViewer;

import javafx.application.Platform;
import javafx.scene.control.*;

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
}
