package edu.wpi.cs3733.d20.teamL.entities;

public class MedicineRequest {

    String patientName;
    String patientID;
    String nurseName;
    String medType;
    String dose;
    String roomNum;
    String addInfo;

    public MedicineRequest(String patientName, String patientID, String nurseName, String medType, String dose, String roomNum, String addInfo) {
        this.patientName = patientName;
        this.patientID = patientID;
        this.nurseName = nurseName;
        this.medType = medType;
        this.dose = dose;
        this.roomNum = roomNum;
        this.addInfo = addInfo;
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
}
