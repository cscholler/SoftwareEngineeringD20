package edu.wpi.cs3733.d20.teamL.util;
import edu.wpi.cs3733.d20.teamL.util.io.Mailer;

import java.util.List;

public class SendEmailDirections extends Thread {
//    List<String> directions;
//    String email;
//    //String carrier;
//
//    public SendEmailDirections(List<String> directions, String email){
//        this.directions = directions;
//        this.email = email;
//
//    }
//
//    @Override
//    public void run() {
//        String allDirections = "";
//        for(String d : directions){
//            allDirections = allDirections + d +"  \n";
//        }
//        Mailer.sendMail(allDirections,"Directions from Faulkner Hospital",email);
//
//    }
//
        String directions;
        String email;
        //String carrier;

    public SendEmailDirections(String directions, String email){
            this.directions = directions;
            this.email = email;

        }

        @Override
        public void run() {

            Mailer.sendMail(directions,"Directions from Faulkner Hospital",email);

        }
}
