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


		public StringProperty getXPos() {
			return xPos;
		}

		public StringProperty getYPos() {
			return yPos;
		}


		public StringProperty getFloor() {
			return floor;
		}


		public StringProperty getBuilding() {
			return building;
		}

		public StringProperty getType() {
			return type;
		}

		public StringProperty getLongName() {
			return lName;
		}

		public StringProperty getShortName() {
			return sName;
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

		public StringProperty getSourceNode() {
			return sourceNode;
		}

		public StringProperty getDestNode() {
			return destNode;
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

		public StringProperty getSubtype() {
			return subtype;
		}


		public StringProperty getDesc() {
			return desc;
		}

		public StringProperty getInventory() {
			return inventory;
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

		public StringProperty getLName() {
			return lName;
		}

		public StringProperty getUsername() {
			return username;
		}

		public StringProperty getAcctType() {
			return acctType;
		}

		public StringProperty getServices() {
			return services;
		}

		public StringProperty getManager() {
			return manager;
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

		public StringProperty getFName() {
			return fName;
		}

		public StringProperty getLName() {
			return lName;
		}

		public StringProperty getUsername() {
			return username;
		}

		public StringProperty getOfficeID() {
			return officeID;
		}

		public StringProperty getAddInfo() {
			return addInfo;
		}
	}
}
