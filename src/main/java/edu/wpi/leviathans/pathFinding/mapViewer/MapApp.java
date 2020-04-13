package edu.wpi.leviathans.pathFinding.mapViewer;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import java.awt.event.ActionEvent;

@Slf4j
public class MapApp extends Application {

    @Override
    public void init() {
        log.info("Starting Up");
    }

    public Stage pStage;

    Parent root;
    MapViewer controller;

    @Override
    public void start(Stage primaryStage) throws Exception {
        pStage = primaryStage;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("MapViewer.fxml"));

        root = fxmlLoader.load();
        controller = fxmlLoader.getController();

        primaryStage.setTitle("Map Viewer");
        primaryStage.setScene(new Scene(root));
        primaryStage.setMaximized(true);

        primaryStage.show();

        controller.init();
    }

    @Override
    public void stop() {
        log.info("Shutting Down");
    }
}
