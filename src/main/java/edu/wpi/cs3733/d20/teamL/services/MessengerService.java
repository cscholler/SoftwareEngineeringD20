package edu.wpi.cs3733.d20.teamL.services;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.regex.Pattern;

@Slf4j
public class MessengerService extends Service implements IMessengerService {
	protected static final String ACCOUNT_SID = "AC84c0ccf3e6eb023f08553134bda07064";
	protected static final String AUTH_TOKEN = "1b1e9007a74876dade4fcb0c81adc8c8";
	protected static final String API_KEY = "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";
	protected static final PhoneNumber SENDER_PHONE_NUMBER = new PhoneNumber("+17692275167");
	protected static final Email SENDER_EMAIL = new Email("bwhmappath@gmail.com");
	protected static final String SUBJECT = "Brigham and Women's Hospital Directions";
	protected static final String SERVICE_NAME = "messenger-service-01";

    protected SendGrid sg;
    private String directions;

    public MessengerService() {
        super();
        this.serviceName = SERVICE_NAME;
    }

    @Override
    protected void startService() {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
        sg = new SendGrid(API_KEY);
    }

    @Override
    protected void stopService() {
        Twilio.destroy();
    }

    @Override
    public String sendText(String messageText, String number) {
        String processedNumber;
        if (number.startsWith("+1")) {
            processedNumber = number.substring(2);
        } else {
            processedNumber = number;
        }
        processedNumber = processedNumber.replaceAll("[^0-9]", "");
        if (processedNumber.length() != 10) {
            // TODO: make custom exception and throw it here
            log.error("Invalid phone number");
            return "Invalid Phone Number";
        }
        PhoneNumber recipientNumber = new PhoneNumber("+1" + processedNumber);
        Message message = Message.creator(recipientNumber, SENDER_PHONE_NUMBER, messageText).create();
        log.info("Text message sent with ID: {}", message.getSid());
        return "Message Sent";
    }

    @Override
    public String sendEmail(String messageText, String emailAddress) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." +
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        if (!pattern.matcher(emailAddress).matches()) {
            // TODO: make custom exception and throw it here
            log.error("Invalid email address.");
            return "Invalid Email Address";
        }
        Email recipientEmail = new Email(emailAddress);
        Content content = new Content("text/html", messageText);
        Mail mail = new Mail(SENDER_EMAIL, SUBJECT, recipientEmail, content);
        Request request = new Request();
        request.setMethod(Method.POST);
        request.setEndpoint("mail/send");
        try {
            request.setBody(mail.build());
            Response response = sg.api(request);
            String status = String.valueOf(response.getStatusCode());
            log.info("Email status code: " + status);
            if (status.startsWith("2")) {
                log.info("Email sent successfully.");
                return "Message Sent";
            } else if (status.startsWith("4")) {
                log.error("An error occurred while sending the email.");
            	return "An Error Occurred";
            } else if (status.startsWith("5")) {
                log.error("A SendGrid server error occurred.");
				return "An Error Occurred";
            } else {
				return "An Error Occurred";
			}
        } catch (IOException ex) {
            log.error("Encountered IOException", ex);
			return "An Error Occurred";
        }
    }

    @Override
    public String getDirections() {
        return directions;
    }

    @Override
    public void setDirections(String directions) {
        this.directions = directions;
    }
}
