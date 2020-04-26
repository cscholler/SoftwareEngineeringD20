package edu.wpi.cs3733.d20.teamL.entities;

import java.util.Date;

public class MedicationRequest {
	private String id;
    private String patientName;
    private String patientID;
    private String doctorName;
    private String nurseName;
    private String medType;
    private String dose;
    private String roomNum;
    private String addInfo;
    private String status;
    private String dateAndTime;

    public MedicationRequest(String id, String patientName, String patientID, String doctorName, String nurseName, String dose, String medType, String roomNum, String addInfo, String status, String dateAndTime) {
    	this.id = id;
        this.patientName = patientName;
        this.patientID = patientID;
        this.doctorName = doctorName;
        this.nurseName = nurseName;
		this.dose = dose;
        this.medType = medType;
        this.roomNum = roomNum;
        this.addInfo = addInfo;
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

    public String getNurseName() {
        return nurseName;
    }

    public void setNurseName(String nurseName) {
        this.nurseName = nurseName;
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

    public String getAddInfo() {
        return addInfo;
    }

    public void setAddInfo(String addInfo) {
        this.addInfo = addInfo;
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

	public String getDoctorName() {
		return doctorName;
	}

	public void setDoctorName(String doctorName) {
		this.doctorName = doctorName;
	}

	public String getID() {
		return id;
	}

	public void setID(String id) {
		this.id = id;
	}
}
