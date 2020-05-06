package edu.wpi.cs3733.d20.teamL.entities;

public class Floor extends Graph {
    private int floor = 0;

    public Floor() {
        super();
    }

    public Floor(int floor) {
        super();

        setFloor(floor);
    }

    /**
     * Gets the floor this floor graph is currently set to. Any node added must belong to this floor.
     *
     * @return An int representing the floor of this floor graph
     */
    public int getFloor() {
        return floor;
    }

    public String getFloorAsString() {
        return Node.floorIntToString(this.floor);
    }

    /**
     * Sets the floor to a given number to restrict all incoming nodes to that floor.
     *
     * @param floor The new floor
     */
    public void setFloor(int floor) {
        if (getNodes().isEmpty())
            this.floor = floor;
        else
            throw new IllegalArgumentException("The floor cannot be set while it still contains nodes");
    }

    @Override
    public void addNode(Node newNode) {
        String name = newNode.getID();

        if (floor != newNode.getFloor()) {
            throw new IllegalArgumentException("A node at floor " + newNode.getFloor() + " cannot be added to floor " + getFloor());
        }

        nodes.put(name, newNode);
    }
}
