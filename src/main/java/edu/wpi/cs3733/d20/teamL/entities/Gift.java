package edu.wpi.cs3733.d20.teamL.entities;

import java.awt.*;

public class Gift {
    private String id;
    private String type;
    private String subtype;
    private String description;
    private String inventory;
    private double cost;
    private Image img;

    public Gift(String id, String type, String subtype, String description, String inventory, double cost) {
        this.id = id;
        this.type = type;
        this.subtype = subtype;
        this.description = description;
        this.inventory = inventory;
        this.cost = cost;
    }

    public String costToString(int qty) {
        String costS = "$" + (cost*qty);
        if (costS.contains(".")) {
            String sub = costS.substring(costS.indexOf("."));
            sub = sub.length() < 2 ? sub + "0" : sub;
            sub = sub.length() < 3 ? sub + "0" : sub;

            costS = costS.substring(0, costS.indexOf(".")) + sub;
        } else {
            costS += ".00";
        }

        return costS;
    }

    public String getID() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getSubtype() {
        return subtype;
    }

    public String getDescription() {
        return description;
    }

    public String getInventory() {
        return inventory;
    }

    public void setInventory(String inventory) { this.inventory = inventory; }

    public double getCost() {
        return cost;
    }

    public Image getImg() {
        return img;
    }
}
