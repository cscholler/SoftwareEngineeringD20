package edu.wpi.cs3733.d20.teamL.entities;

public class User {
	private String id;
	private String fName;
	private String lName;
	private String username;
	private String acctType;
	private String services;
	private boolean isManager;
	private String dept;

	public User(String id, String fName, String lName, String username, String acctType, String services, String manager) {
		this.id = id;
		this.fName = fName;
		this.lName = lName;
		this.username = username;
		this.acctType = acctType;
		this.services = services;
		this.isManager = manager != null;
		this.dept = isManager ? manager : null;
	}

	public String getID() {
		return id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getFName() {
		return fName;
	}

	public void setFName(String fName) {
		this.fName = fName;
	}

	public String getLName() {
		return lName;
	}

	public void setLName(String lName) {
		this.lName = lName;
	}

	public String getAcctType() {
		return acctType;
	}

	public void setAcctType(String acctType) {
		this.acctType = acctType;
	}

	public String getServices() {
		return services;
	}

	public boolean isManager() {
		return isManager;
	}

	public String getDept() {
		return dept;
	}
}
