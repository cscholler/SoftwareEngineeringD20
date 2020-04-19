package edu.wpi.cs3733.d20.teamL.views.components;

import javafx.scene.Node;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Shape;

import java.util.Collection;

public interface Highlightable {
    void setHighlighted(boolean newHighlighted);

    void setHighlightRadius(double radius);

    void setHighlightColor(Paint newColor);

    void setSelected(boolean selected);

    boolean getHighlighted();

    double getHighlightRadius();

    boolean getSelected();

    Paint getHighlightColor();

    Collection<Node> getAllNodes();

    Shape getGUI();
}