package edu.wpi.cs3733.d20.teamL.entities;

import java.util.HashMap;

public class Edge {

    //TODO make private & getters/setters
    private Node source;
    private Node destination;
    private String id;

    public HashMap<String, Object> data = new HashMap<>();


    public Edge(String id, Node source, Node destination) {
        this.id = id;
        this.source = source;
        this.destination = destination;
    }

    public double getLength() {
        return source.getPosition().distance(destination.getPosition());
    }

    public Node getDestination() {
        return destination;
    }

    public void setDestination(Node destination) {
        this.destination = destination;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
     * Getter for source, the node this edge comes from.
     *
     * @return The Node object that this edge comes from
     */
    public Node getSource() {
        return source;
    }

    /**
     * Setter for source
     *
     * @param newSource The Node that is to be the new source Node for this Edge
     */
    public void setSource(Node newSource) {
        if (source != null) source.removeEdge(this);
        if (newSource != null) newSource.addEdge(this);
        source = newSource;
    }
}
