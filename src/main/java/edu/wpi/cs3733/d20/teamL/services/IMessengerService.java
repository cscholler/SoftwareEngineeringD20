package edu.wpi.cs3733.d20.teamL.services;

import com.sendgrid.helpers.mail.objects.Email;
import com.twilio.type.PhoneNumber;

public interface IMessengerService {
	String ACCOUNT_SID = "AC84c0ccf3e6eb023f08553134bda07064";
	String AUTH_TOKEN = "1b1e9007a74876dade4fcb0c81adc8c8";
	String API_KEY = "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";
	PhoneNumber SENDER_PHONE_NUMBER = new PhoneNumber("+17692275167");
	Email SENDER_EMAIL = new Email("bwhmappath@gmail.com");
	String SUBJECT = "Brigham and Women's Hospital Directions";
	String SERVICE_NAME = "messenger-service-01";

	void sendText(String messageText, String number);

	void sendEmail(String messageText, String emailAddress);

	String getDirections();

	void setDirections(String directions);
}
