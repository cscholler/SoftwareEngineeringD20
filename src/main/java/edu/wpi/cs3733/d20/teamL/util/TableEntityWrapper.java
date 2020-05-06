package edu.wpi.cs3733.d20.teamL.util;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class TableEntityWrapper {
	public static class TableNode extends RecursiveTreeObject<TableNode> {
		private final StringProperty id;
		private final StringProperty xPos;
		private final StringProperty yPos;
		private final StringProperty floor;
		private final StringProperty building;
		private final StringProperty type;
		private final StringProperty lName;
		private final StringProperty sName;

		public TableNode(String id, String xPos, String yPos, String floor, String building, String type, String lName, String sName) {
			this.id = new SimpleStringProperty(id);
			this.xPos = new SimpleStringProperty(xPos);
			this.yPos = new SimpleStringProperty(yPos);
			this.floor = new SimpleStringProperty(floor);
			this.building = new SimpleStringProperty(building);
			this.type = new SimpleStringProperty(type);
			this.lName = new SimpleStringProperty(lName);
			this.sName = new SimpleStringProperty(sName);
		}

		public StringProperty getID() {
			return id;
		}

		public void setID(String id) {
			this.id.set(id);
		}

		public StringProperty getXPos() {
			return xPos;
		}

		public void setXPos(String xPos) {
			this.xPos.set(xPos);
		}

		public StringProperty getYPos() {
			return yPos;
		}

		public void setYPos(String yPos) {
			this.yPos.set(yPos);
		}

		public StringProperty getFloor() {
			return floor;
		}

		public void setFloor(String floor) {
			this.floor.set(floor);
		}

		public StringProperty getBuilding() {
			return building;
		}

		public void setBuilding(String building) {
			this.building.set(building);
		}

		public StringProperty getType() {
			return type;
		}

		public void setType(String type) {
			this.type.set(type);
		}

		public StringProperty getLongName() {
			return lName;
		}

		public void setLongName(String lName) {
			this.lName.set(lName);
		}

		public StringProperty getShortName() {
			return sName;
		}

		public void setShortName(String sName) {
			this.sName.set(sName);
		}
	}

	public static class TableEdge extends RecursiveTreeObject<TableEdge> {
		private final StringProperty id;
		private final StringProperty sourceNode;
		private final StringProperty destNode;

		public TableEdge(String id, String sourceNode, String destNode) {
			this.id = new SimpleStringProperty(id);
			this.sourceNode = new SimpleStringProperty(sourceNode);
			this.destNode = new SimpleStringProperty(destNode);
		}

		public StringProperty getID() {
			return id;
		}

		public void setID(String id) {
			this.id.set(id);
		}

		public StringProperty getSourceNode() {
			return sourceNode;
		}

		public void setSourceNode(String sourceNode) {
			this.sourceNode.set(sourceNode);
		}

		public StringProperty getDestNode() {
			return destNode;
		}

		public void setDestNode(String destNode) {
			this.destNode.set(destNode);
		}
	}

	public static class TableGift extends RecursiveTreeObject<TableGift> {
		private final StringProperty id;
		private final StringProperty type;
		private final StringProperty subtype;
		private final StringProperty desc;
		private final StringProperty inventory;

		public TableGift(String id, String type, String subtype, String desc, String inventory) {
			this.id = new SimpleStringProperty(id);
			this.type = new SimpleStringProperty(type);
			this.subtype = new SimpleStringProperty(subtype);
			this.desc = new SimpleStringProperty(desc);
			this.inventory = new SimpleStringProperty(inventory);
		}

		public StringProperty getID() {
			return id;
		}

		public StringProperty getType() {
			return type;
		}

		public void setType(String type) {
			this.type.set(type);
		}

		public StringProperty getSubtype() {
			return subtype;
		}

		public void setSubtype(String subtype) {
			this.subtype.set(subtype);
		}

		public StringProperty getDescription() {
			return desc;
		}

		public void setDesc(String desc) {
			this.desc.set(desc);
		}

		public StringProperty getInventory() {
			return inventory;
		}

		public void setInventory(String inventory) {
			this.inventory.set(inventory);
		}
	}

	public static class TableUser extends RecursiveTreeObject<TableUser> {
		private final StringProperty id;
		private final StringProperty fName;
		private final StringProperty lName;
		private final StringProperty username;
		private final StringProperty acctType;
		private final StringProperty services;
		private final StringProperty manager;

		public StringProperty getID() {
			return id;
		}

		public StringProperty getFName() {
			return fName;
		}

		public void setFName(String fName) {
			this.fName.set(fName);
		}

		public StringProperty getLName() {
			return lName;
		}

		public void setLName(String lName) {
			this.lName.set(lName);
		}

		public StringProperty getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username.set(username);
		}

		public StringProperty getAcctType() {
			return acctType;
		}

		public void setAcctType(String acctType) {
			this.acctType.set(acctType);
		}

		public StringProperty getServices() {
			return services;
		}

		public void setServices(String services) {
			this.services.set(services);
		}

		public StringProperty getManager() {
			return manager;
		}

		public void setManager(String manager) {
			this.manager.set(manager);
		}

		public TableUser(String id, String fName, String lName, String username, String acctType, String services, String manager) {
			this.id = new SimpleStringProperty(id);
			this.fName = new SimpleStringProperty(fName);
			this.lName = new SimpleStringProperty(lName);
			this.username = new SimpleStringProperty(username);
			this.acctType = new SimpleStringProperty(acctType);
			this.services = new SimpleStringProperty(services);
			this.manager = new SimpleStringProperty(manager);
		}
	}

	public static class TableDoctor extends RecursiveTreeObject<TableDoctor> {
		private final StringProperty id;
		private final StringProperty fName;
		private final StringProperty lName;
		private final StringProperty username;
		private final StringProperty officeID;
		private final StringProperty addInfo;

		public TableDoctor(String id, String fName, String lName, String username, String officeID, String addInfo) {
			this.id = new SimpleStringProperty(id);
			this.fName = new SimpleStringProperty(fName);
			this.lName = new SimpleStringProperty(lName);
			this.username = new SimpleStringProperty(username);
			this.officeID = new SimpleStringProperty(officeID);
			this.addInfo = new SimpleStringProperty(addInfo);
		}

		public StringProperty getID() {
			return id;
		}

		public void setID(String id) {
			this.id.set(id);
		}

		public StringProperty getFirstName() {
			return fName;
		}
		
		public void setFirstName(String fName) {
			this.fName.set(fName);
		}

		public StringProperty getLastName() {
			return lName;
		}

		public void setLastName(String lName) {
			this.lName.set(lName);
		}

		public StringProperty getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username.set(username);
		}

		public StringProperty getOfficeID() {
			return officeID;
		}

		public void setOfficeID(String officeID) {
			this.officeID.set(officeID);
		}
		
		public StringProperty getAddInfo() {
			return addInfo;
		}

		public void setAddInfo(String addInfo) {
			this.addInfo.set(addInfo);
		}
	}
}
