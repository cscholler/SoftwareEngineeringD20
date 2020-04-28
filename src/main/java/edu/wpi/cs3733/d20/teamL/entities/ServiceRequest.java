package edu.wpi.cs3733.d20.teamL.entities;

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
		this.notes = notes;
		this.status = status;
		this.dateAndTime = dateAndTime;
	}

	public String getId() {
		return id;
	}

	public String getPatientID() {
		return patientID;
	}

	public String getPatientName() {
		return patientName;
	}

	public String getRequestUsername() {
		return requestUsername;
	}

	public String getAssigneeUsername() {
		return assigneeUsername;
	}

	public void setAssigneeUsername(String assigneeUsername) {
		this.assigneeUsername = assigneeUsername;
	}

	public String getLocation() {
		return location;
	}

	public String getService() {
		return service;
	}

	public String getType() {
		return type;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getDateAndTime() {
		return dateAndTime;
	}
}
