package edu.wpi.cs3733.d20.teamL.services.messaging;

import com.google.zxing.WriterException;
import com.sendgrid.helpers.mail.objects.Email;
import com.twilio.type.PhoneNumber;

import java.awt.image.BufferedImage;
import java.io.IOException;

public interface IMessengerService {


	String sendText(String messageText, String number);

	String sendEmail(String messageText, String emailAddress);

	String getDirections();

	void setDirections(String directions);

	BufferedImage getQRCodeImage(String text) throws WriterException, IOException;
}
