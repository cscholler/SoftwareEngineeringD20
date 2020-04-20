package edu.wpi.cs3733.d20.teamL.entities;

public class MedicineRequest {
    public MedicineRequest(String patientName, String nurseName, String medType, String dose, String roomNum) {
        this.patientName = patientName;
        this.nurseName = nurseName;
        this.medType = medType;
        this.dose = dose;
        this.roomNum = roomNum;
    }

    String patientName;
    String nurseName;
    String medType;
    String dose;
    String roomNum;

}
