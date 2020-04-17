package edu.wpi.leviathans.views.mapViewer;

import edu.wpi.leviathans.util.pathfinding.graph.Node;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Point2D;
import javafx.scene.control.Label;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;

import java.util.ArrayList;
import java.util.Collection;

public class NodeGUI extends Circle implements Highlightable {
    public SimpleDoubleProperty layoutX = new SimpleDoubleProperty();
    public SimpleDoubleProperty layoutY = new SimpleDoubleProperty();

    private boolean highlighted;

    private double highlightRadius;

    private Node node;

    private Label nameLabel = new Label();

    private boolean selected = false;

    public NodeGUI(Node initNode) {
        node = initNode;

        // Set initial x and y position
        setLayoutPos(node.position);

        centerXProperty().bindBidirectional(layoutX);
        centerYProperty().bindBidirectional(layoutY);

        nameLabel.setText(node.getName());
        nameLabel.layoutXProperty().bindBidirectional(layoutX);
        nameLabel.layoutYProperty().bindBidirectional(layoutY);

        setHighlighted(false);
    }

    public void setLayoutPos(Point2D newPos) {
        layoutX.set(newPos.getX());
        layoutY.set(newPos.getY());
    }

    public void setHighlighted(boolean highlighted) {
        this.highlighted = highlighted;

        if (this.highlighted) setStrokeWidth(highlightRadius);
        else setStrokeWidth(0);
    }

    public void setHighlightRadius(double highlightRadius) {
        this.highlightRadius = highlightRadius;
    }

    public void setHighlightColor(Paint color) {
        setStroke(color);
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public Point2D getLayoutPos() {
        return new Point2D(layoutX.get(), layoutY.get());
    }

    public boolean getHighlighted() {
        return highlighted;
    }

    public Collection<javafx.scene.Node> getAllNodes() {
        Collection<javafx.scene.Node> retList = new ArrayList<>(1);
        retList.add(this);
        retList.add(nameLabel);
        return retList;
    }

    public double getHighlightRadius() {
        return highlightRadius;
    }

    public Paint getHighlightColor() {
        return getStroke();
    }

    public boolean getSelected() {
        return selected;
    }

    public Circle getGUI() {
        return this;
    }

    public Node getNode() {
        return node;
    }
}
