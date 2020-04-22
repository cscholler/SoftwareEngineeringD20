package edu.wpi.cs3733.d20.teamL.services.mail;

import java.util.HashMap;

public interface IMailerService {
	void sendMail();

	void sendTextToCarrier();

	HashMap<String,String> buildCarrierMap();

	String getDirections();

	void setDirections(String directions);

	String getPhoneNumber();

	void setPhoneNumber(String phoneNumber);

	String getCarrier();

	void setCarrier(String carrier);

	String getEmailAddress();

	void setEmailAddress(String emailAddress);

	String getSubject();

	void setSubject(String subject);

	boolean isText();

	void setIsText(boolean isText);
}
