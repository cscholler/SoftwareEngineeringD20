package edu.wpi.cs3733.d20.teamL.views.components;

import edu.wpi.cs3733.d20.teamL.entities.Building;
import edu.wpi.cs3733.d20.teamL.entities.Node;
import edu.wpi.cs3733.d20.teamL.views.controllers.map.MapViewerController;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

import java.util.ArrayList;


public class MapLink extends StackPane {

    private ImageView map;
    private Label label;
    private MapViewerController controller;

    private String building;
    private int floor;

    public MapLink(String building, int fitWidth, MapViewerController controller, Image img) {
        super();
        setAlignment(Pos.CENTER);

        this.building = building;
        this.floor = 1;
        this.controller = controller;

        map = new ImageView(new Image("/edu/wpi/cs3733/d20/teamL/assets/maps/" + building + ".PNG"));
        map.setPreserveRatio(true);
        map.setFitWidth(fitWidth);

        label = new Label();
        label.setText("Drive");
        label.setTextFill(Color.BLACK);
        label.setStyle("-fx-font-size: 20; -fx-font-weight: bolder;");

        getChildren().add(map);
        getChildren().add(label);

        setOnMouseClicked(mouseEvent -> {
            controller.getMap().setBuilding(new Building("Google"));
            controller.getMap().setMapImage(img);
            controller.getMap().setZoomLevel(.5);
        });

        setCursor(Cursor.HAND);
        setStyle("-fx-border-color: black;");
    }

    public MapLink(String building, int floor, double fitWidth, MapViewerController controller) {
        super();
        setAlignment(Pos.CENTER);

        this.building = building;
        this.floor = floor;
        this.controller = controller;

        label = new Label();
        label.setText(building + " " + Node.floorIntToString(floor));
        label.setTextFill(Color.BLACK);
        label.setStyle("-fx-font-size: 20; -fx-font-weight: bolder;");


        map = new ImageView(new Image("/edu/wpi/cs3733/d20/teamL/assets/maps/" + building + "Floor" + Node.floorIntToString(floor) + "LM.png"));
        map.setPreserveRatio(true);
        map.setFitWidth(fitWidth);

        getChildren().add(map);
        getChildren().add(label);

        setOnMouseClicked(mouseEvent -> {
            controller.setBuilding(this.building);
            controller.setFloor(this.floor);
            controller.zoomToNodes(controller.getPath().getNodesOnFloor(floor, building));
        });

        setCursor(Cursor.HAND);
        setStyle("-fx-border-color: black;");
    }

    public ImageView getMap() {
        return map;
    }

    public Label getLabel() {
        return label;
    }

    public MapViewerController getController() {
        return controller;
    }

    public void setController(MapViewerController controller) {
        this.controller = controller;
    }

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;

        setOnMouseClicked(mouseEvent -> {
            controller.setBuilding(this.building);
            controller.setFloor(this.floor);
        });

        label.setText(building + " " + Node.floorIntToString(floor));
    }

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;

        setOnMouseClicked(mouseEvent -> {
            controller.setBuilding(this.building);
            controller.setFloor(this.floor);
        });

        label.setText(building + " " + Node.floorIntToString(floor));
    }
}
