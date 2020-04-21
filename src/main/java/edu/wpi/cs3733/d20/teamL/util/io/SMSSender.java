package edu.wpi.cs3733.d20.teamL.util.io;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SMSSender {
	public static final String ACCOUNT_SID = "AC05e2549b9018776e263b772c2b67ff69";
	public static final String AUTH_TOKEN = "a8104706a5cab9fd9e861b8758d59373";
	public static final PhoneNumber SENDER_PHONE = new PhoneNumber("+19798032987");

	public void sendMessage(String text, String number) {
		Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
		PhoneNumber recipientNumber = new PhoneNumber("+1" + number);
		Message message = Message.creator(recipientNumber, SENDER_PHONE, text + ".").create();
		log.info("SMS sent with ID: " + message.getSid());
	}
}
