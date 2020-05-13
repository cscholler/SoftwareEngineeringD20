package edu.wpi.cs3733.d20.teamL.views.components;

import edu.wpi.cs3733.d20.teamL.entities.Edge;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.paint.*;
import javafx.scene.shape.Line;

import java.util.ArrayList;
import java.util.Collection;

public class EdgeGUI extends Line implements Highlightable {

    private Line highlightGui = new Line();
    private Edge edge;
    private NodeGUI source;
    private boolean selected = false;
    private boolean usingGradient = false;

    public EdgeGUI(int strokeWidth, Color nodeColor, Paint highLightColor, double highlightThickness) {
        this.setStrokeWidth(strokeWidth);
        this.strokeProperty().setValue(nodeColor);
        this.setHighlightColor(highLightColor);
        this.setHighlightThickness(highlightThickness);
        highlightGui.setMouseTransparent(true);

        //setMouseTransparent(true);
    }

    public EdgeGUI(Edge initEdge) {
        edge = initEdge;

        // Set start position of the line to the source node
        setStartPos(edge.getSource().getPosition());

        // Set end position of the line to the destination node
        setEndPos(edge.getDestination().getPosition());

        highlightGui.startXProperty().bindBidirectional(startXProperty());
        highlightGui.startYProperty().bindBidirectional(startYProperty());
        highlightGui.endXProperty().bindBidirectional(endXProperty());
        highlightGui.endYProperty().bindBidirectional(endYProperty());
        highlightGui.setMouseTransparent(true);
        //setMouseTransparent(true);

        setHighlighted(false);
    }

    public EdgeGUI(Edge initEdge, int lineWidth) {
        this(initEdge);

        setStrokeWidth(lineWidth);
    }

    public Point2D getStartPos() {
        return new Point2D(getStartX(), getStartY());
    }

    public void setStartPos(Point2D newPos) {
        startXProperty().setValue(newPos.getX());
        startYProperty().setValue(newPos.getY());
    }

    public Point2D getEndPos() {
        return new Point2D(getEndX(), getEndY());
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
        //highlightGui.getParent().getStylesheets().add("edu/wpi/cs3733/d20/teamL/css/MarchingAnts.css");
        //highlightGui.getStyleClass().add("box");
    }

    public double getHighlightThickness() {
        return (highlightGui.getStrokeWidth() - getStrokeWidth()) / 2;
    }

    public void setHighlightThickness(double thickness) {
        highlightGui.setStrokeWidth(getStrokeWidth() + (thickness * 2));
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

    public Line getHighlightGUI() {return highlightGui;}

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


    /**
     * Determines the length of the EdgeUI on the screen
     *
     * @return a double representing the length
     */
    public double getLengthOnScreen() {
        return getEndPos().subtract(getStartPos()).magnitude();
    }

    /**
     * Checks to see if a given Point2D is located within the 'area' of the edge.
     *
     * @param point The Point2D to check
     * @return true if point is within area
     */
    public boolean contains(Point2D point) {
        if(getStartX()-getEndX()==0 || getStartY()-getEndY()==0) return checkBounds(point.getX(), point.getY(),3);

        Point2D lineDirection = getEndPos().subtract(getStartPos());
        Point2D pointDirection = point.subtract(getStartPos());

        Point2D projection = lineDirection.multiply((lineDirection.dotProduct(pointDirection)) / (lineDirection.dotProduct(lineDirection)));

        double distanceFromLine = pointDirection.subtract(projection).magnitude();
        double distanceAlongLine = projection.magnitude();

        return (distanceFromLine < 5 && distanceAlongLine < getLengthOnScreen()) && checkBounds(point.getX(), point.getY(),0);
    }

    private boolean checkBounds(double x, double y, double off) {
        double sX = getStartX();
        double eX = getEndX();
        double sY = getStartY();
        double eY = getEndY();
        boolean withinX, withinY;

        if(sX < eX) withinX = x > sX-off && x < eX+off;
        else withinX = x < sX+off && x > eX-off;

        if(sY < eY) withinY = y > sY-off && y < eY+off;
        else withinY = y < sY+off && y > eY-off;

        return withinX && withinY;
    }

    public Edge getEdge() {
        return edge;
    }

    public boolean isUsingGradient() {
        return usingGradient;
    }

    @Override
    public void setGradient(double intensity) {
        usingGradient = true;

        Stop[] stops = new Stop [] {
                new Stop(0.0, Color.TRANSPARENT),
                new Stop(0.4, new Color(1, 0, 0, 0.5)),
                new Stop(0.5, Color.RED),
                new Stop(0.6, new Color(1, 0, 0, 0.5)),
                new Stop(1.0, Color.TRANSPARENT)
        };

        Point2D centerPoint = getStartPos().add(getEndPos().subtract(getStartPos()).multiply(0.5));
        Point2D lineVector = getEndPos().subtract(getStartPos());
        Point2D perpVector = new Point2D(lineVector.getY(), -lineVector.getX());

        Point2D gradientStart = centerPoint.subtract(perpVector.normalize().multiply(getHighlightThickness()));
        Point2D gradientEnd = centerPoint.subtract(perpVector.normalize().multiply(-getHighlightThickness()));

        setStrokeWidth(intensity);
        LinearGradient linearGradient = new LinearGradient(gradientStart.getX(), gradientStart.getY(), gradientEnd.getX(), gradientEnd.getY(),
                false, CycleMethod.NO_CYCLE, stops);

        setStroke(linearGradient);
    }

}
