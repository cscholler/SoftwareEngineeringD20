package edu.wpi.cs3733.d20.teamL.entities;

public class Doctor {
	private String id;
	private String fName;
	private String lName;
	private String username;
	private String officeID;
	private String addInfo;

	public Doctor(String id, String fName, String lName, String username, String officeID, String addInfo) {
		this.id = id;
		this.fName = fName;
		this.lName = lName;
		this.username = username;
		this.officeID = officeID;
		this.addInfo = addInfo;
	}

	public String getId() {
		return id;
	}

	public String getFName() {
		return fName;
	}

	public String getLName() {
		return lName;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getAddInfo() {
		return addInfo;
	}

	public void setAddInfo(String addInfo) {
		this.addInfo = addInfo;
	}

	public String getOfficeID() {
		return officeID;
	}

	public void setOfficeID(String officeID) {
		this.officeID = officeID;
	}
}
