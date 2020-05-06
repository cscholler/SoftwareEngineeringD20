package edu.wpi.cs3733.d20.teamL.entities;

import java.util.ArrayList;

public class GiftDeliveryRequest {
	private String id;
	private String patientID;
	private String patientName;
	private String roomNum;
	private String senderName;
	private String requestUsername;
	private String assigneeUsername;
	private String status;
	private String dateAndTime;
	private String message;
	private String notes;
	private String gifts;

	public GiftDeliveryRequest(String id, String patientID, String patientName, String roomNum, String senderName,
							   String requestUsername, String assigneeUsername, String gifts,
							   String message, String notes, String status, String dateAndTime) {
		this.id = id;
		this.patientID = patientID;
		this.patientName = patientName;
		this.roomNum = roomNum;
		this.senderName = senderName;
		this.requestUsername = requestUsername;
		this.assigneeUsername = assigneeUsername;
		this.gifts = gifts;
		this.message = message;
		this.notes = notes;
		this.status = status;
		this.dateAndTime = dateAndTime;
	}

	public String getID() {
		return id;
	}

	public String getPatientID() {
		return patientID;
	}

	public String getPatientName() {
		return patientName;
	}

	public String getRoomNum() {
		return roomNum;
	}

	public String getSenderName() {
		return senderName;
	}

	public String getRequestUsername() {
		return requestUsername;
	}

	public String getAssigneeUsername() {
		return assigneeUsername;
	}

	public String getGifts() {
		return gifts;
	}

	public String getMessage() {
		return message;
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
