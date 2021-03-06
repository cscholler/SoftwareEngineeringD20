package edu.wpi.cs3733.d20.teamL.entities;

import com.sun.jdi.request.DuplicateRequestException;

import java.util.*;

public class Building extends Graph {
    private String name = "";

    private Comparator<Floor> floorComparator = Comparator.comparing(Floor::getFloor);
    private SortedSet<Floor> floors = new TreeSet<>(floorComparator);

    public Building() {
        super();
        floors.add(new Floor(1));
    }

    public Building(String name) {
        this();
        this.name = name;
    }

    public Building(String name, Iterable<Node> nodes) {
        this(name);
        addAllNodes(nodes);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (getNodes().isEmpty())
            this.name = name;
        else
            throw new RuntimeException("Tried to set the name of (" + getClass().toString() + ") to (" + name +
                    "), but it contained Nodes belonging to (" + getName() + ")");
    }

    @Override
    public Collection<Node> getNodes() {
        Collection<Node> retList = new ArrayList<>();

        for (Graph floor : floors)
            retList.addAll(floor.getNodes());

        return retList;
    }

    @Override
    public Node getNode(String nodeID) {
        for (Graph floor : floors) {
            Node foundNode = floor.getNode(nodeID);

            if (foundNode != null)
                return foundNode;
        }

        return null;
    }

    /**
     * Adds a new node to this graph. Will not add the node if it does not belong to this building.
     *
     * @param newNode The new node to add
     */
    @Override
    public void addNode(Node newNode) {
        // If the building hasn't been set yet, set the building name to the newNode's building,
        // otherwise check if the nodes building matches this building's name
        if (getName().isEmpty())
            setName(newNode.getBuilding());
        else if (!newNode.getBuilding().equals(getName()))
            return;
//            throw new IllegalArgumentException("Tried to add node (" + newNode.getID() + ") to (" + getName() +
//                    "), but its building was (" + newNode.getBuilding() + ")");

        // Add the node to its respective floor, if its floor exceeds the maximum floor or is less than the minimum floor,
        // add new floors up to and including the added floor
        if (!getNodes().contains(newNode)) {
            if (newNode.getFloor() > getMaxFloor()) {
                setMaxFloor(newNode.getFloor());
            } else if (newNode.getFloor() < getMinFloor()) {
                setMinFloor(newNode.getFloor());
            }

            getFloor(newNode.getFloor()).addNode(newNode);
        } else { // Throw an exception if you try to add the same node twice
            throw new IllegalArgumentException("Tried to add (" + newNode.getID() + ") to (" + getName() + ") more than once");
        }
    }

    /**
     * Returns the graph of nodes at the given floor.
     *
     * @param floor The floor to get, this must be less than the max floor
     * @return A floor object containing all the nodes of a given floor
     */
    public Floor getFloor(int floor) {
        List<Floor> allFloors = new ArrayList<>();
        allFloors.addAll(floors);

        if (floor > getMaxFloor())
            throw new IndexOutOfBoundsException("floor must not exceed the max floor of this building");
        else
            return allFloors.get(floor - getMinFloor());
    }

    /**
     * Gets the number of floors in this building.
     *
     * @return The maximum floor node that is contained in this building
     */
    public int getMaxFloor() {
        if (!floors.isEmpty())
            return floors.last().getFloor();
        else
            return 1;
    }

    public int getMinFloor() {
        if (!floors.isEmpty())
            return floors.first().getFloor();
        else
            return 1;
    }

    /**
     * Sets the max floor to a new max. Adds or removes the floors from this building respectively.
     *
     * @param maxFloor the new maximum floor
     */
    public void setMaxFloor(int maxFloor) {
        if (getMinFloor() > maxFloor) {
            throw new IndexOutOfBoundsException("Tried to set max floor of (" + getName() + ") to (" + maxFloor +
                    "), but it was out of range");
        } else if (getMaxFloor() > maxFloor) {
            floors.removeAll(floors.subSet(getFloor(maxFloor), floors.last()));
        } else if (getMaxFloor() < maxFloor) {
            for (int i = getMaxFloor() + 1; i <= maxFloor; i++) {
                Floor newFloor = new Floor();
                newFloor.setFloor(i);
                floors.add(newFloor);
            }
        }
    }

    public void setMinFloor(int minFloor) {
        if (getMaxFloor() < minFloor) {
            throw new IndexOutOfBoundsException("Tried to set min floor of (" + getName() + ") to (" + minFloor +
                    "), but it was out of range");
        } else if (getMinFloor() < minFloor) {
            floors.removeAll(floors.subSet(floors.first(), getFloor(minFloor)));
        } else if (getMinFloor() > minFloor) {
            for (int i = getMinFloor() - 1; i >= minFloor; i--) {
                Floor newFloor = new Floor();
                newFloor.setFloor(i);
                floors.add(newFloor);
            }
        }
    }

    /**
     * Returns a list of floor objects collectively containing all the nodes in this graph.
     *
     * @return an ArrayList of Floors using the List interface
     */
    public List<Floor> getFloors() {
        return new ArrayList<>(floors);
    }
}
