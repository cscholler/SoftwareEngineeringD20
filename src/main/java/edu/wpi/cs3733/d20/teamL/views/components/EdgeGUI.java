package edu.wpi.cs3733.d20.teamL.views.components;

import edu.wpi.cs3733.d20.teamL.entities.Edge;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;

import java.util.ArrayList;
import java.util.Collection;

public class EdgeGUI implements Highlightable {
    public SimpleDoubleProperty startX = new SimpleDoubleProperty();
    public SimpleDoubleProperty startY = new SimpleDoubleProperty();
    public SimpleDoubleProperty endX = new SimpleDoubleProperty();
    public SimpleDoubleProperty endY = new SimpleDoubleProperty();

    private Line gui = new Line();
    private Line highlightGui = new Line();
    private Edge edge;
    private boolean selected = false;

    public EdgeGUI(Edge initEdge) {
        edge = initEdge;

        // Set start position of the line to the source node
        setStartPos(edge.getSource().position);

        // Set end position of the line to the destination node
        setEndPos(edge.destination.position);

        gui.startXProperty().bindBidirectional(startX);
        gui.startYProperty().bindBidirectional(startY);
        gui.endXProperty().bindBidirectional(endX);
        gui.endYProperty().bindBidirectional(endY);

        highlightGui.startXProperty().bindBidirectional(startX);
        highlightGui.startYProperty().bindBidirectional(startY);
        highlightGui.endXProperty().bindBidirectional(endX);
        highlightGui.endYProperty().bindBidirectional(endY);

        setHighlighted(false);
    }

    public EdgeGUI(Edge initEdge, int lineWidth) {
        this(initEdge);

        gui.setStrokeWidth(lineWidth);
    }

    public void setStartPos(Point2D newPos) {
        startX.set(newPos.getX());
        startY.set(newPos.getY());
    }

    public void setEndPos(Point2D newPos) {
        endX.set(newPos.getX());
        endY.set(newPos.getY());
    }

    public void setHighlighted(boolean newHighlighted) {
        highlightGui.setVisible(newHighlighted);
    }

    public void setHighlightRadius(double radius) {
        highlightGui.setStrokeWidth(gui.getStrokeWidth() + (radius * 2));
    }

    public void setHighlightColor(Paint newColor) {
        highlightGui.setStroke(newColor);
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean getHighlighted() {
        return highlightGui.isVisible();
    }

    public Collection<Node> getAllNodes() {
        Collection<javafx.scene.Node> retList = new ArrayList<>(2);
        retList.add(highlightGui);
        retList.add(gui);
        return retList;
    }

    public Line getGUI() {
        return gui;
    }

    public double getHighlightRadius() {
        return (highlightGui.getStrokeWidth() - gui.getStrokeWidth()) / 2;
    }

    public boolean getSelected() {
        return false;
    }

    public Paint getHighlightColor() {
        return highlightGui.getStroke();
    }
}
