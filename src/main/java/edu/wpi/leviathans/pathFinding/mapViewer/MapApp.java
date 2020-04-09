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
    Scene scene;
    MapViewer controller;

    private Point2D mouseStart;
    private Point2D paneStart;

    @Override
    public void start(Stage primaryStage) throws Exception {
        pStage = primaryStage;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("MapViewer.fxml"));
        root = fxmlLoader.load();
        primaryStage.setTitle("Map Viewer");
        primaryStage.setScene(new Scene(root));
        primaryStage.setMaximized(true);
        primaryStage.show();

        scene = root.getScene();
        controller = fxmlLoader.getController();

        controller.appInstance = this;

        coreShortcuts();

        // Start mouse drag
        controller.body.setOnMousePressed(event -> {
            if(event.isMiddleButtonDown()) {
                AnchorPane nodePane = controller.nodePane;

                // Set the initial Pane and mouse positions
                mouseStart = new Point2D(event.getSceneX(), event.getSceneY());
                paneStart = new Point2D(nodePane.getLayoutX(), nodePane.getLayoutY());

                scene.setCursor(Cursor.MOVE);
            }
        });

        controller.body.setOnMouseDragged(event -> {
            if(event.isMiddleButtonDown() &&  mouseStart != null) {
                AnchorPane nodePane = controller.nodePane;

                double xOffset = event.getSceneX() - mouseStart.getX();
                double yOffset = event.getSceneY() - mouseStart.getY();

                nodePane.setLayoutX(paneStart.getX() + xOffset);
                nodePane.setLayoutY(paneStart.getY() + yOffset);
            }
        });

        controller.body.setOnMouseReleased(event -> {
            scene.setCursor(Cursor.DEFAULT);
        });

        controller.body.setOnScroll(event -> {
            // Get the initial zoom level
            double prevZoomLevel = controller.getZoomLevel();

            // Change the zoom level
            controller.setZoomLevel(prevZoomLevel * (1 + event.getDeltaY() / 100));

            // Get how much the zoom level scaled
            double deltaZoom = controller.getZoomLevel() / prevZoomLevel;

            // Calculate how much the AnchorPane should now be offset from the mouse at the new zoom level
            Point2D prevPos = new Point2D(controller.nodePane.getLayoutX(), controller.nodePane.getLayoutY());
            Point2D mousePos = new Point2D(event.getX(), event.getY());
            Point2D mouseOffset = mousePos.subtract(prevPos);
            Point2D newMouseOffset = mouseOffset.multiply(deltaZoom);

            // Change the offset of the nodePane so it zooms from the mouse
            controller.nodePane.setLayoutX(mousePos.getX() + newMouseOffset.getX());
            controller.nodePane.setLayoutY(mousePos.getY() + newMouseOffset.getY());

            System.out.println(new Point2D(controller.nodePane.getLayoutX(), controller.nodePane.getLayoutY()));
        });

        new AnimationTimer() {
            @Override
            public void handle(long currentNanoTime) {
                controller.position.setText("(" + controller.nodePane.getLayoutX() + ", " + controller.nodePane.getLayoutY() + ")");
            }
        }.start();
    }

    private void coreShortcuts() {
        KeyCombination cq = new KeyCodeCombination(KeyCode.Q, KeyCombination.CONTROL_DOWN);
        KeyCombination cs = new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN);
        KeyCombination css = new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN);
        KeyCombination cz = new KeyCodeCombination(KeyCode.Z, KeyCombination.CONTROL_DOWN);
        KeyCombination cy = new KeyCodeCombination(KeyCode.Y, KeyCombination.CONTROL_DOWN);
        KeyCombination co = new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN);

        controller.quit.setAccelerator(cq);
        controller.save.setAccelerator(cs);
        controller.saveAs.setAccelerator(css);
        controller.undo.setAccelerator(cz);
        controller.redo.setAccelerator(cy);
        controller.open.setAccelerator(co);

        scene.getAccelerators().put(cq, () -> controller.quit());
        scene.getAccelerators().put(cs, () -> controller.save());
        scene.getAccelerators().put(css, () -> controller.saveAs());
        scene.getAccelerators().put(co, () -> controller.open());
    }

    @Override
    public void stop() {
        log.info("Shutting Down");
    }
}
