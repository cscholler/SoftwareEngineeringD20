package edu.wpi.cs3733.d20.teamL.views.components;

import javafx.scene.Node;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Shape;

import java.util.Collection;

public interface Highlightable {
    void setHighlighted(boolean newHighlighted);

    double getHighlightThickness();

    void setHighlightThickness(double radius);

    void setHighlightColor(Paint newColor);

    void setSelected(boolean selected);

    boolean getHighlighted();

    boolean getSelected();

    Paint getHighlightColor();

    Collection<Node> getAllNodes();

    Node getGUI();

    void setGradient(double intensity);

    boolean isUsingGradient();
}
