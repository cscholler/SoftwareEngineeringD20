package edu.wpi.cs3733.d20.teamL.services.messaging;

import com.sendgrid.helpers.mail.objects.Email;
import com.twilio.type.PhoneNumber;

public interface IMessengerService {


	String sendText(String messageText, String number);

	String sendEmail(String messageText, String emailAddress);

	String getDirections();

	void setDirections(String directions);
}
