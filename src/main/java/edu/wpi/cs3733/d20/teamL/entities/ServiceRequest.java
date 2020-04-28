package edu.wpi.cs3733.d20.teamL.entities;

import java.util.regex.Pattern;

public class ServiceRequest {
	private String id;
	private String patientID;
	private String patientName;
	private String requestUsername;
	private String assigneeUsername;
	private String location;
	private String service;
	private String type;
	private String notes;
	private String status;
	private String dateAndTime;

	public ServiceRequest(String id, String patientID, String patientName, String requestUsername,
						  String assigneeUsername, String location, String service, String type,
						  String notes, String status, String dateAndTime) {
		this.id = id;
		this.patientID = patientID;
		this.patientName = patientName;
		this.requestUsername = requestUsername;
		this.assigneeUsername = assigneeUsername;
		this.location = location;
		this.service = service;
		this.type = type;
	}

	public String getStatus() {
		return status;
	}
}
