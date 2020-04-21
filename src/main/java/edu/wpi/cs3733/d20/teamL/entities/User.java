package edu.wpi.cs3733.d20.teamL.entities;

public class User {
	private String id;
	private String username;
	private String fName;
	private String lName;
	private String acctType;

	public User(String id, String username, String fName, String lName, String acctType) {
		this.id = id;
		this.username = username;
		this.fName = fName;
		this.lName = lName;
		this.acctType = acctType;
	}

	public String getID() {
		return id;
	}

	public void setID(String id) {
		this.id = id;
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
}
