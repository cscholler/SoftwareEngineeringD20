package edu.wpi.cs3733.d20.teamL.util;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class TableEntityWrapper {
	public static class TableNode extends RecursiveTreeObject<TableNode> {
		private final StringProperty id;
		private final StringProperty fName;
		private final StringProperty lName;
		private final StringProperty username;
		private final StringProperty acctType;
		private final StringProperty services;
		private final StringProperty manager;

		public TableNode(String id, String fName, String lName, String username, String acctType, String services, String manager) {
			this.id = new SimpleStringProperty(id);
			this.fName = new SimpleStringProperty(fName);
			this.lName = new SimpleStringProperty(lName);
			this.username = new SimpleStringProperty(username);
			this.acctType = new SimpleStringProperty(acctType);
			this.services = new SimpleStringProperty(services);
			this.manager = new SimpleStringProperty(manager);
		}
	}

	public static class TableEdge extends RecursiveTreeObject<TableEdge> {
		private final StringProperty id;
		private final StringProperty fName;
		private final StringProperty lName;
		private final StringProperty username;
		private final StringProperty acctType;
		private final StringProperty services;
		private final StringProperty manager;

		public TableEdge(String id, String fName, String lName, String username, String acctType, String services, String manager) {
			this.id = new SimpleStringProperty(id);
			this.fName = new SimpleStringProperty(fName);
			this.lName = new SimpleStringProperty(lName);
			this.username = new SimpleStringProperty(username);
			this.acctType = new SimpleStringProperty(acctType);
			this.services = new SimpleStringProperty(services);
			this.manager = new SimpleStringProperty(manager);
		}
	}

	public static class TableGift extends RecursiveTreeObject<TableGift> {
		private final StringProperty id;
		private final StringProperty fName;
		private final StringProperty lName;
		private final StringProperty username;
		private final StringProperty acctType;
		private final StringProperty services;
		private final StringProperty manager;

		public TableGift(String id, String fName, String lName, String username, String acctType, String services, String manager) {
			this.id = new SimpleStringProperty(id);
			this.fName = new SimpleStringProperty(fName);
			this.lName = new SimpleStringProperty(lName);
			this.username = new SimpleStringProperty(username);
			this.acctType = new SimpleStringProperty(acctType);
			this.services = new SimpleStringProperty(services);
			this.manager = new SimpleStringProperty(manager);
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
		private final StringProperty acctType;
		private final StringProperty services;
		private final StringProperty manager;

		public TableDoctor(String id, String fName, String lName, String username, String acctType, String services, String manager) {
			this.id = new SimpleStringProperty(id);
			this.fName = new SimpleStringProperty(fName);
			this.lName = new SimpleStringProperty(lName);
			this.username = new SimpleStringProperty(username);
			this.acctType = new SimpleStringProperty(acctType);
			this.services = new SimpleStringProperty(services);
			this.manager = new SimpleStringProperty(manager);
		}
	}


}
