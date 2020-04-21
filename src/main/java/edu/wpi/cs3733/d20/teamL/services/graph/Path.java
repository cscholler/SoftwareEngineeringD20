package edu.wpi.cs3733.d20.teamL.services.graph;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import javafx.geometry.Point2D;

import edu.wpi.cs3733.d20.teamL.entities.Edge;
import edu.wpi.cs3733.d20.teamL.entities.Node;

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
            retLength += current.getEdge(next).getLength();
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
            length += lastEdge.getLength();
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
            length += firstEdge.getLength();
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

    public String generateTextMessage() {
        StringBuilder message = new StringBuilder();
        Point2D start, end;
        Node prev, curr, next;
        double angle;
        boolean wasStraight = true;
        String sign = "right";
        boolean empty = true;

        for (int i = 1; i < pathNodes.size() - 1; i++) {
            prev = pathNodes.get(i-1);
            curr = pathNodes.get(i);
            next = pathNodes.get(i+1);

            start = new Point2D(curr.getPosition().getX() - prev.getPosition().getX(), curr.getPosition().getY() - prev.getPosition().getY());
            end = new Point2D(next.getPosition().getX() - curr.getPosition().getX(), next.getPosition().getY() - curr.getPosition().getY());
            angle = start.angle(end);

            sign = determineDirection(start, end);

            if(angle > 10) {
                if (empty) {
                    message.append("Head towards " + curr.getLongName() + ".\n\n");
                    empty = false;
                }
                message.append("Once you reach " + curr.getLongName() + ", ");

                if(angle < 45) message.append("take a slight ");
                else if (angle > 95) message.append("take a sharp ");
                else message.append("turn ");

                if(next.getType().equals("HALL")) message.append(sign + " and continue down the hall.\n\n");
                else message.append(sign + " towards " + next.getLongName() + ".\n\n");
            } else if(angle <= 10 && !curr.getType().equals("HALL")) {
                message.append("Pass straight though the " + curr.getLongName() + ".\n\n");
            }
        }

        message.append("Continue straight until your destination at " + pathNodes.get(pathNodes.size()-1).getLongName());

        return message.toString();
    }

    private String determineDirection(Point2D start, Point2D end) {
        double dir = -start.getX() * end.getY() + start.getY() * end.getX();
        if (dir > 0) return "left";
        return "right";
    }

}