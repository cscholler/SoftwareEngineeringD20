package edu.wpi.cs3733.d20.teamL.entities;

import java.util.ArrayList;

public class GiftRequest {
    private String id;
    private String senderName;
    private String patientName;
    private String patientID;
    private String assigneeName;
    private String room;
    private String status;
    private String dateAndTime;
    private String message;
    private String notes;
    private ArrayList<String> gift;

    public GiftRequest(String id, String senderName, String patientName, String patientID,
                       String assigneeName, String room, String status, String dateAndTime,
                       String message, String notes, ArrayList<String> gifts) {
        this.id = id;
        this.senderName = senderName;
        this.patientName = patientName;
        this.patientID = patientID;
        this.assigneeName = assigneeName;
        this.room = room;
        this.status = status;
        this.dateAndTime = dateAndTime;
        this.message = message;
        this.notes = notes;
        this.gift = gifts;
    }

    public String getId() {
        return id;
    }

    public String getSenderName() {
        return senderName;
    }

    public String getPatientName() {
        return patientName;
    }

    public String getPatientID() {
        return patientID;
    }

    public String getAssigneeName() {
        return assigneeName;
    }

    public String getRoom() {
        return room;
    }

    public String getStatus() {
        return status;
    }

    public String getDateAndTime() {
        return dateAndTime;
    }

    public String getMessage() {
        return message;
    }

    public String getNotes() {
        return notes;
    }

    public ArrayList<String> getGift() {
        return gift;
    }
}
