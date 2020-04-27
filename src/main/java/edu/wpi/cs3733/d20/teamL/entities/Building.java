package edu.wpi.cs3733.d20.teamL.entities;

import com.sun.jdi.request.DuplicateRequestException;

import java.util.*;

public class Building extends Graph {
    private String name;

    private Comparator<Floor> floorComparator = Comparator.comparing(Floor::getFloor);
    private SortedSet<Floor> floors = new TreeSet<>(floorComparator);

    public Building(String name) {
        super();
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    @Override
    public void addNode(Node newNode) {
        if (!getNodes().contains(newNode)) {
            if (newNode.getFloor() > getMaxFloor()) {
                for (int i = getMaxFloor() + 1; i <= newNode.getFloor(); i++) {
                    Floor newFloor = new Floor();
                    newFloor.setFloor(i);
                    floors.add(newFloor);
                }
            }

            getFloor(newNode.getFloor()).addNode(newNode);
        } else {
            throw new IllegalArgumentException("Tried to add (" + newNode.getID() + ") more than once");
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
            return allFloors.get(floor - 1);
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
            return 0;
    }
}
