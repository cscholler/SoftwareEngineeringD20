package edu.wpi.cs3733.d20.teamL.entities;

public class Gift {
    private String id;
    private String type;
    private String subtype;
    private String description;
    private int inventory;

    public Gift(String id, String type, String subtype, String description, String inventory) {
        this.id = id;
        this.type = type;
        this.subtype = subtype;
        this.description = description;
        this.inventory = Integer.parseInt(inventory);
    }

    public String getId() {
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

    public int getInventory() {
        return inventory;
    }

    public void setInventory(int inventory) { this.inventory = inventory; }
}
