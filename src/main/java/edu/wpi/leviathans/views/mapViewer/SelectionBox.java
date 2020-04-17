package edu.wpi.leviathans.views.mapViewer;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class SelectionBox extends Rectangle {

    public Point2D rootPosition;
    public Selector selector;

    public SelectionBox(Point2D root) {
        super();

        rootPosition = root;

        setFill(new Color(0, 0, 1, 0.1));
        setStroke(Color.BLUE);
        setStrokeWidth(2);
    }

    public void mouseDrag(Point2D mousePosition) {
        if(mousePosition.getX() < rootPosition.getX()) {
            setLayoutX(mousePosition.getX());
            setWidth(rootPosition.getX() - mousePosition.getX());
        } else {
            setLayoutX(rootPosition.getX());
            setWidth(mousePosition.getX() - rootPosition.getX());
        }

        if(mousePosition.getY() < rootPosition.getY()) {
            setLayoutY(mousePosition.getY());
            setHeight(rootPosition.getY() - mousePosition.getY());
        } else {
            setLayoutY(rootPosition.getY());
            setHeight(mousePosition.getY() - rootPosition.getY());
        }
    }
}
