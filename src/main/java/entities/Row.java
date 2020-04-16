package entities;

import javafx.beans.property.SimpleStringProperty;

public class Row {
    public String getNodeID() {
        return nodeID.get();
    }

    public SimpleStringProperty nodeIDProperty() {
        return nodeID;
    }

    public String getxcoord() {
        return xCoord.get();
    }

    public SimpleStringProperty xcoordProperty() {
        return xCoord;
    }

    public String getycoord() {
        return yCoord.get();
    }

    public SimpleStringProperty ycoordProperty() {
        return yCoord;
    }

    public String getFloor() {
        return floor.get();
    }

    public SimpleStringProperty floorProperty() {
        return floor;
    }

    public String getBuilding() {
        return building.get();
    }

    public SimpleStringProperty buildingProperty() {
        return building;
    }

    public String getNodeType() {
        return nodeType.get();
    }

    public SimpleStringProperty nodeTypeProperty() {
        return nodeType;
    }

    public String getLongName() {
        return longName.get();
    }

    public SimpleStringProperty longNameProperty() {
        return longName;
    }

    public String getShortName() {
        return shortName.get();
    }

    public SimpleStringProperty shortNameProperty() {
        return shortName;
    }

    public void setNodeID(String nodeID) {
        this.nodeID.set(nodeID);
    }

    public void setxCoord(String xCoord) {
        this.xCoord.set(xCoord);
    }

    public void setyCoord(String yCoord) {
        this.yCoord.set(yCoord);
    }

    public void setFloor(String floor) {
        this.floor.set(floor);
    }

    public void setBuilding(String building) {
        this.building.set(building);
    }

    public void setNodeType(String nodeType) {
        this.nodeType.set(nodeType);
    }

    public void setLongName(String longName) {
        this.longName.set(longName);
    }

    public void setShortName(String shortName) {
        this.shortName.set(shortName);
    }

    SimpleStringProperty nodeID;
    SimpleStringProperty xCoord;
    SimpleStringProperty yCoord;
    SimpleStringProperty floor;
    SimpleStringProperty building;
    SimpleStringProperty nodeType;
    SimpleStringProperty longName;
    SimpleStringProperty shortName;

    public Row(String nodeID, String xCoord, String yCoord, String floor, String building, String nodeType, String longName, String shortName) {
        this.nodeID = new SimpleStringProperty(nodeID);
        this.xCoord = new SimpleStringProperty(xCoord);
        this.yCoord = new SimpleStringProperty(yCoord);
        this.floor = new SimpleStringProperty(floor);
        this.building = new SimpleStringProperty(building);
        this.nodeType = new SimpleStringProperty(nodeType);
        this.longName = new SimpleStringProperty(longName);
        this.shortName = new SimpleStringProperty(shortName);
    }

}
