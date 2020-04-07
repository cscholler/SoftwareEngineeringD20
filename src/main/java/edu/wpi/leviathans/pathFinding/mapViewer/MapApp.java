package edu.wpi.leviathans.pathFinding.mapViewer;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import javafx.event.EventHandler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MapApp extends Application {

    @Override
    public void init() {
        log.info("Starting Up");
    }

    Parent root;
    Scene scene;
    MapViewer controller;

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("MapViewer.fxml"));
        root = fxmlLoader.load();
        primaryStage.setTitle("Map Viewer");
        primaryStage.setScene(new Scene(root, 600, 400));
        primaryStage.show();

        scene = root.getScene();
        controller = fxmlLoader.getController();

        coreShortcuts();
    }

    private void coreShortcuts() {
        KeyCombination cq = new KeyCodeCombination(KeyCode.Q, KeyCombination.CONTROL_DOWN);
        KeyCombination cs = new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN);
        KeyCombination css = new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN);
        KeyCombination cz = new KeyCodeCombination(KeyCode.Z, KeyCombination.CONTROL_DOWN);
        KeyCombination cy = new KeyCodeCombination(KeyCode.Y, KeyCombination.CONTROL_DOWN);

        controller.quit.setAccelerator(cq);
        controller.save.setAccelerator(cs);
        controller.saveAs.setAccelerator(css);
        controller.undo.setAccelerator(cz);
        controller.redo.setAccelerator(cy);

        scene.getAccelerators().put(cq, () -> controller.quit());
        scene.getAccelerators().put(cs, () -> controller.save());
        scene.getAccelerators().put(css, () -> controller.saveAs());
    }

    @Override
    public void stop() {
        log.info("Shutting Down");
    }
}
