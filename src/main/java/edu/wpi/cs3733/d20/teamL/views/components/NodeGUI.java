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

public class NodeGUI extends Circle implements Highlightable {
    private boolean highlighted;

    private double highlightRadius;

    private DoubleProperty xProperty = new DoublePropertyBase() {
        @Override
        public Object getBean() {
            return this;
        }

        @Override
        public String getName() {
            return "xPosition";
        }
    };

    private DoubleProperty yProperty = new DoublePropertyBase() {
        @Override
        public Object getBean() {
            return this;
        }

        @Override
        public String getName() {
            return "yPosition";
        }
    };

    private Node node;

    //private Label nameLabel = new Label();

    private boolean selected = false;

    public NodeGUI(Node initNode) {
        node = initNode;

        // Set initial x and y position
        setLayoutPos(node.getPosition());

        //nameLabel.setText(node.getID());
        //nameLabel.setMouseTransparent(true);

        setHighlighted(false);
    }

    public Circle getCircle() {
        return this;
    }

    public DoubleProperty getXProperty() {
        return xProperty;
    }

    public DoubleProperty getYProperty() {
        return yProperty;
    }

    public void setLayoutPos(Point2D newPos) {
        getXProperty().set(newPos.getX());
        getYProperty().set(newPos.getY());

        layoutXProperty().set(newPos.getX());
        layoutYProperty().set(newPos.getY());
    }

    public void setHighlighted(boolean highlighted) {
        this.highlighted = highlighted;

        if (this.highlighted) getCircle().setStrokeWidth(highlightRadius);
        else getCircle().setStrokeWidth(0);
    }

    public void setHighlightRadius(double highlightRadius) {
        this.highlightRadius = highlightRadius;
    }

    public void setHighlightColor(Paint color) {
        getCircle().setStroke(color);
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public Point2D getLayoutPos() {
        return new Point2D(getXProperty().get(), getYProperty().get());
    }

    public boolean getHighlighted() {
        return highlighted;
    }

    public Collection<javafx.scene.Node> getAllNodes() {
        Collection<javafx.scene.Node> retList = new ArrayList<>(1);
        retList.add(this);
        //retList.add(nameLabel);
        return retList;
    }

    public double getHighlightRadius() {
        return highlightRadius;
    }

    public Paint getHighlightColor() {
        return getCircle().getStroke();
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
