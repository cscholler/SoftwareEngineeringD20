package edu.wpi.cs3733.d20.teamL.views.components;

import javafx.geometry.Point2D;
import javafx.scene.Cursor;

import java.util.*;

public class Selector {
    private Map<NodeGUI, Point2D> selectedNodes = new HashMap<>();
    private Collection<EdgeGUI> selectedEdges = new ArrayList<>();

    private Collection<Highlightable> selected = new ArrayList<>();

    public Collection<NodeGUI> getNodes() {
        return selectedNodes.keySet();
    }

    public Collection<EdgeGUI> getEdges() {
        return List.copyOf(selectedEdges);
    }

    public Point2D getNodePosition(NodeGUI nodeGUI) {
        return selectedNodes.get(nodeGUI);
    }

    public void setNodePosition(NodeGUI nodeGUI, Point2D position) {
        selectedNodes.replace(nodeGUI, position);
    }

    public void add(Highlightable newItem) {
        if (newItem.getClass().equals(NodeGUI.class))
            selectedNodes.put((NodeGUI) newItem, null);
        else if (newItem.getClass().equals(EdgeGUI.class))
            selectedEdges.add((EdgeGUI) newItem);

        selected.add(newItem);
        newItem.setHighlighted(true);
        newItem.setSelected(true);
    }

    public void addAll(Highlightable... newItems) {
        for (Highlightable item : newItems)
            add(item);
    }

    public void remove(Highlightable item) {
        try {
            if (selected.contains(item)) {
                if (item.getClass().equals(NodeGUI.class))
                    selectedNodes.remove(item);
                else if (item.getClass().equals(EdgeGUI.class))
                    selectedEdges.remove(item);

                selected.remove(item);
                item.setHighlighted(false);
                item.setSelected(false);
            } else {
                throw new IllegalArgumentException("Item to remove must be selected");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void removeAll(Highlightable... items) {
        for (Highlightable item : items)
            remove(item);
    }

    public void clear() {
        removeAll(selected.toArray(new Highlightable[selected.size()]));
    }

    public boolean contains(Highlightable item) {
        return selected.contains(item);
    }
}
