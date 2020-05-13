package edu.wpi.cs3733.d20.teamL.entities;

public class Reservation {
    String username;
    String place;
    String date;
    String startTime;
    String endTime;

    public Reservation(String username, String place, String date, String startTime, String endTime) {
        this.username = username;
        this.place = place;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getUsername() {
        return username;
    }

    public String getPlace() {
        return place;
    }

    public String getDate() {
        return date;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }
}
