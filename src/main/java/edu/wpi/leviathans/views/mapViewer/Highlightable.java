package edu.wpi.leviathans.views.mapViewer;

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
