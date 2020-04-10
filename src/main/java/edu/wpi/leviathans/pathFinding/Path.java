package edu.wpi.leviathans.pathFinding;

import edu.wpi.leviathans.pathFinding.graph.*;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class Path implements Iterable<Node> {

    private List<Node> pathNodes = new LinkedList<>();
    private int length = 0;

    public List<Node> getPathNodes() {
        return pathNodes;
    }

    public int getLength() {
        return length;
    }

    /**
     * Gets the sum of the path lengths
     *
     * @return
     */
    int recalculateLength() {
        int retLength = 0;

        Iterator<Node> iterator = pathNodes.iterator();

        Node current = iterator.next();

        while (iterator.hasNext()) {
            Node next = iterator.next();
            retLength += current.getEdge(next).length;
            current = next;
        }

        length = retLength;

        return length;
    }

    /**
     * Adds a node to the end of the path.
     *
     * @param node The new node to add
     */
    public void add(Node node) {
        Edge lastEdge = null;
        if (pathNodes.size() > 0) {
            lastEdge = pathNodes.get(pathNodes.size() - 1).getEdge(node);
            length += lastEdge.length;
        }

        if (pathNodes.size() == 0 || lastEdge != null) {
            pathNodes.add(node);
        } else {
            throw new IllegalArgumentException(
                    "Last node does not have an Edge pointing to the given node");
        }
    }

    /**
     * Adds a node to the beginning of the path.
     *
     * @param node The new node to add
     */
    public void addFirst(Node node) {
        Edge firstEdge = null;
        if (pathNodes.size() > 0) firstEdge = node.getEdge(pathNodes.get(0));

        if (pathNodes.size() == 0 || firstEdge != null) {
            pathNodes.add(node);
            length += firstEdge.length;
        } else {
            throw new IllegalArgumentException(
                    "Given node does not have an edge pointing to the previous first node");
        }
    }

    /**
     * Adds each node in order to the path.
     *
     * @param nodes An ordered list of nodes to add
     */
    public void addAll(List<Node> nodes) {
        for (Node node : nodes) {
            add(node);
        }
    }

    public Iterator<Node> iterator() {
        return pathNodes.iterator();
    }

    /**
     * Converts an ordered list of nodes to a path. Ensure that there is an actual path of edges
     * between the nodes.
     *
     * @param list A list of nodes
     * @return A path containing all the nodes
     */
    public static Path listToPath(List<Node> list) {
        Path newPath = new Path();
        newPath.addAll(list);

        return newPath;
    }
}
