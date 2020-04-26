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

    public MedicationRequest(String id, String doctorID, String patientID, String patientName, String roomNum, String nurseUsername, String delivererUsername, String dose, String medType, String notes, String status, String dateAndTime) {
    	this.id = id;
		this.doctorID = doctorID;
		this.patientID = patientID;
        this.patientName = patientName;
		this.roomNum = roomNum;
        this.nurseUsername = nurseUsername;
        this.delivererUsername = delivererUsername;
		this.dose = dose;
        this.medType = medType;
        this.notes = notes;
        this.status = status;
        this.dateAndTime = dateAndTime;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getPatientID() {
        return patientID;
    }

    public void setPatientID(String patientID) {
        this.patientID = patientID;
    }

    public String getNurseUsername() {
        return nurseUsername;
    }

    public void setNurseUsername(String nurseUsername) {
        this.nurseUsername = nurseUsername;
    }

	public String getDelivererUsername() {
		return delivererUsername;
	}

	public void setDelivererUsername(String delivererUsername) {
		this.delivererUsername = delivererUsername;
	}

    public String getMedType() {
        return medType;
    }

    public void setMedType(String medType) {
        this.medType = medType;
    }

    public String getDose() {
        return dose;
    }

    public void setDose(String dose) {
        this.dose = dose;
    }

    public String getRoomNum() {
        return roomNum;
    }

    public void setRoomNum(String roomNum) {
        this.roomNum = roomNum;
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

	public void setDateAndTime(String dateAndTime) {
		this.dateAndTime = dateAndTime;
	}

	public String getDoctorID() {
		return doctorID;
	}

	public void setDoctorID(String doctorID) {
		this.doctorID = doctorID;
	}

	public String getID() {
		return id;
	}

	public void setID(String id) {
		this.id = id;
	}
}
