package edu.wpi.cs3733.d20.teamL.views.components;

import edu.wpi.cs3733.d20.teamL.entities.Edge;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;

import java.util.ArrayList;
import java.util.Collection;

public class EdgeGUI extends Line implements Highlightable {

    private Line highlightGui = new Line();
    private Edge edge;
    private NodeGUI source;
    private boolean selected = false;

    public EdgeGUI(int strokeWidth, Color nodeColor, Paint highLightColor, double highlightThickness) {
        this.setStrokeWidth(strokeWidth);
        this.strokeProperty().setValue(nodeColor);
        this.setHighlightColor(highLightColor);
        this.setHighlightRadius(highlightThickness);
    }

    public EdgeGUI(Edge initEdge) {
        edge = initEdge;

        // Set start position of the line to the source node
        setStartPos(edge.getSource().position);

        // Set end position of the line to the destination node
        setEndPos(edge.destination.position);

        highlightGui.startXProperty().bindBidirectional(startXProperty());
        highlightGui.startYProperty().bindBidirectional(startYProperty());
        highlightGui.endXProperty().bindBidirectional(endXProperty());
        highlightGui.endYProperty().bindBidirectional(endYProperty());
        highlightGui.setMouseTransparent(true);

        setHighlighted(false);
    }

    public EdgeGUI(Edge initEdge, int lineWidth) {
        this(initEdge);

        setStrokeWidth(lineWidth);
    }

    public void setStartPos(Point2D newPos) {
        startXProperty().setValue(newPos.getX());
        startYProperty().setValue(newPos.getY());
    }

    public void setEndPos(Point2D newPos) {
        endXProperty().setValue(newPos.getX());
        endYProperty().setValue(newPos.getY());
    }

    public NodeGUI getSource() {
        return source;
    }

    public void setSource(NodeGUI source) {
        this.source = source;
    }

    public void setHighlighted(boolean newHighlighted) {
        highlightGui.setVisible(newHighlighted);
    }

    public void setHighlightRadius(double radius) {
        highlightGui.setStrokeWidth(getStrokeWidth() + (radius * 2));
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
        retList.add(this);
        return retList;
    }

    public Line getGUI() {
        return this;
    }

    public double getHighlightRadius() {
        return (highlightGui.getStrokeWidth() - getStrokeWidth()) / 2;
    }

    public boolean getSelected() {
        return false;
    }

    public Paint getHighlightColor() {
        return highlightGui.getStroke();
    }
}
