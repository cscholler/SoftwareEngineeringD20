package edu.wpi.cs3733.d20.teamL.entities;

public class MedicationRequest {
	private String id;
	private String doctorID;
	private String patientID;
    private String patientName;
	private String roomNum;
    private String nurseUsername;
    private String delivererUsername;
    private String medType;
    private String dose;
    private String notes;
    private String status;
    private String dateAndTime;
    //TODO: add username for doctor, nurse, and deliverer

    public MedicationRequest(String id, String patientID, String patientName, String doctorID, String roomNum,
							 String nurseUsername, String delivererUsername, String dose, String medType,
							 String notes, String status, String dateAndTime) {
    	this.id = id;
		this.patientID = patientID;
		this.patientName = patientName;
		this.doctorID = doctorID;
		this.roomNum = roomNum;
        this.nurseUsername = nurseUsername;
        this.delivererUsername = delivererUsername;
		this.dose = dose;
        this.medType = medType;
        this.notes = notes;
        this.status = status;
        this.dateAndTime = dateAndTime;
    }

	public String getID() {
		return id;
	}

	public String getDoctorID() {
		return doctorID;
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

    public String getNurseUsername() {
        return nurseUsername;
    }

	public String getDelivererUsername() {
		return delivererUsername;
	}

	public void setDelivererUsername(String delivererUsername) {
		this.delivererUsername = delivererUsername;
	}

	public String getDose() {
		return dose;
	}

    public String getMedType() {
        return medType;
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
