package edu.wpi.cs3733.d20.teamL.views.components;

import edu.wpi.cs3733.d20.teamL.entities.Node;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.DoublePropertyBase;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;

import java.util.ArrayList;
import java.util.Collection;

public class NodeGUI extends StackPane implements Highlightable {
    private boolean highlighted;

    private double highlightRadius;

    private Node node;

    private Circle circle = new Circle();
    private Label nameLabel = new Label();

    private DoubleProperty xProperty = new DoublePropertyBase() {
        @Override
        public Object getBean() {
            return this;
        }

        @Override
        public String getName() {
            return "xProperty";
        }
    };
    private DoubleProperty yProperty = new DoublePropertyBase() {
        @Override
        public Object getBean() {
            return this;
        }

        @Override
        public String getName() {
            return "xProperty";
        }
    };;

    private boolean selected = false;

    public NodeGUI(Node initNode) {
        node = initNode;

        getChildren().addAll(circle, nameLabel);

        nameLabel.setText(node.getID());
        nameLabel.setMouseTransparent(true);

        setAlignment(Pos.CENTER);

        setHighlighted(false);

        // Set initial x and y position
        setLayoutPos(node.getPosition());
    }

    public DoubleProperty getXProperty() {
        return xProperty;
    }

    public DoubleProperty getYProperty() {
        return yProperty;
    }

    public DoubleProperty yPropertyProperty() {
        return yProperty;
    }

    public Circle getCircle() {
        return circle;
    }

    public void setCircle(Circle circle) {
        this.circle = circle;
    }

    public Label getNameLabel() {
        return nameLabel;
    }

    public void setNameLabel(Label nameLabel) {
        this.nameLabel = nameLabel;
    }

    public Point2D getLayoutPos() {
        return new Point2D(getXProperty().get(), getYProperty().get());
    }

    public void setLayoutPos(Point2D newPos) {
        getXProperty().set(newPos.getX());
        getYProperty().set(newPos.getY());

        layout();
        newPos = new Point2D(newPos.getX() - getWidth()/2, newPos.getY() - getHeight()/2);

        setLayoutX(newPos.getX());
        setLayoutY(newPos.getY());
    }

    public void setHighlighted(boolean highlighted) {
        this.highlighted = highlighted;

        if (this.highlighted) circle.setStrokeWidth(highlightRadius);
        else circle.setStrokeWidth(0);
    }

    public void setHighlightRadius(double highlightRadius) {
        this.highlightRadius = highlightRadius;
    }

    public void setHighlightColor(Paint color) {
        circle.setStroke(color);
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
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
        return circle.getStroke();
    }

    public boolean getSelected() {
        return selected;
    }

    public StackPane getGUI() {
        return this;
    }

    public Node getNode() {
        return node;
    }
}
