package edu.wpi.cs3733.d20.teamL.entities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class User {
	private String id;
	private String fName;
	private String lName;
	private String username;
	private String acctType;
	private List<String> services;

	public User(String id, String fName, String lName, String username, String acctType, String services) {
		this.id = id;
		this.fName = fName;
		this.lName = lName;
		this.username = username;
		this.acctType = acctType;
		if(services != null)
			this.services = Arrays.asList(services.split(","));
		else
			this.services = new ArrayList<>();
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

	public List<String> getServices() {
		return services;
	}
}
