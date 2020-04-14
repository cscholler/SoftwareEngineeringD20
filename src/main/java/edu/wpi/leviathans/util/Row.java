package edu.wpi.leviathans.util;

import javafx.beans.property.SimpleStringProperty;

public class Row{
    SimpleStringProperty nodeID;
    SimpleStringProperty xCoord;
    SimpleStringProperty yCoord;
    SimpleStringProperty floor;
    SimpleStringProperty building;
    SimpleStringProperty nodeType;
    SimpleStringProperty longName;
    SimpleStringProperty shortName;

    public Row(String nodeID, String xCoord,String yCoord,String floor,String building, String nodeType, String longName, String shortName){
        this.nodeID = new SimpleStringProperty(nodeID);
        this.xCoord = new SimpleStringProperty(xCoord);
        this.yCoord =  new SimpleStringProperty(yCoord);
        this.floor =  new SimpleStringProperty(floor);
        this. building =  new SimpleStringProperty(building);
        this.nodeType =  new SimpleStringProperty(nodeType);
        this.longName =  new SimpleStringProperty(longName);
        this.shortName =  new SimpleStringProperty(shortName);
    }

}
