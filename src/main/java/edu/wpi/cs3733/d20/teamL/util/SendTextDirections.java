package edu.wpi.cs3733.d20.teamL.util;

import edu.wpi.cs3733.d20.teamL.util.io.Mailer;

import java.util.List;

public class SendTextDirections extends Thread {
    List<String> directions;
    String number;
    String carrier;

    public SendTextDirections(List<String> directions, String number, String carrier){
        this.directions = directions;
        this.number = number;
        this.carrier = carrier;

    }

    @Override
    public void run() {
        String allDirections = "";
        for(String d : directions){
            allDirections = allDirections + d +" \n ";
        }
        Mailer.sendTextToCarrier(allDirections,"Directions from Faulkner Hospital",number,carrier);

    }
}
