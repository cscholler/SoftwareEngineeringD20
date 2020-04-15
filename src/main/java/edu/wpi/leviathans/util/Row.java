package edu.wpi.leviathans.util;

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
