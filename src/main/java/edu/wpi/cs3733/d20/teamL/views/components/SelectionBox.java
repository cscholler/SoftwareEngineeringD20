package edu.wpi.cs3733.d20.teamL.views.components;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.Collection;

public class SelectionBox extends Rectangle {

    private Point2D rootPosition;
    private Selector selector;
    private Collection<Highlightable> items;

    public SelectionBox(Point2D rootPosition, Selector selector, Collection<Highlightable> items) {
        super();

        this.selector = selector;
        this.rootPosition = rootPosition;
        this.items = items;

        setFill(new Color(0, 0, 1, 0.1));
        setStroke(Color.BLUE);
        setStrokeWidth(1);
    }

    public Point2D getRootPosition() {
        return rootPosition;
    }

    public void setRootPosition(Point2D rootPosition) {
        this.rootPosition = rootPosition;
        mouseDrag(rootPosition, false);
    }

    public Selector getSelector() {
        return selector;
    }

    public void setSelector(Selector selector) {
        this.selector = selector;
    }

    public void mouseDrag(Point2D mousePosition, boolean isShiftDown) {
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

        for (Highlightable item : items) {
            if (getBoundsInParent().contains(item.getGUI().getBoundsInParent()))
                selector.add(item);
            else if(selector.contains(item) && !isShiftDown)
                selector.remove(item);
        }
    }
}
