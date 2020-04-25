package edu.wpi.cs3733.d20.teamL.services.mail;

import lombok.extern.slf4j.Slf4j;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.HashMap;
import java.util.Properties;

@Slf4j
public class MailerService implements IMailerService {
	private String directions;
	private String phoneNumber;
	private String carrier;
	private String emailAddress;
	private String subject;
	private boolean isText;

    @Override
	public synchronized void sendMail() {
		System.out.println("Got to sendMail");
        // Recipient's email ID needs to be mentioned.
        String to = emailAddress;
        // Sender's email ID needs to be mentioned
        String from = "bwhmappath@gmail.com";
        // Assuming you are sending email from through gmails smtp
        String host = "smtp.gmail.com";
        // Get system properties
        Properties properties = System.getProperties();
        // Setup mail server
        properties.put("mail.smtp.host", host);
        // Use port 465 for running as a JAR:
		properties.put("mail.smtp.port", "465");
		// Use port 587 running in IntelliJ:
        //properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.auth", "true");
        // Get the Session object.// and pass username and password
        Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("bwhmappath@gmail.com", "LinLevi1");
            }
        });
        // Used to debug SMTP issues
        session.setDebug(true);
        try {
			// Create a default MimeMessage object.
			MimeMessage message = new MimeMessage(session);
			// Set From: header field of the header.
			message.setFrom(new InternetAddress(from));
			// Set To: header field of the header.
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
			// Set Subject: header field
			message.setSubject(getSubject());
			if (isText) {
				String currentMessage = "";
				int i = 1;
				for (char c : getDirections().toCharArray()) {
					currentMessage += c;
					if (c == '.') {
						message.setText(i + ". " + currentMessage);
						Transport.send(message);
						currentMessage = "";
						i++;
					}
				}
			} else {
				// Now set the actual message
				message.setText(getDirections());
				log.info("Sending message.");
				// Send message
				Transport.send(message);
			}
			log.info("Message sent successfully.");
        } catch (MessagingException ex) {
            log.error("Encountered MessagingException.", ex);
        }
    }

	@Override
	public void sendTextToCarrier() {
		HashMap<String, String> carrierMap = buildCarrierMap();
		setEmailAddress(getPhoneNumber() + "@" + carrierMap.get(getCarrier()));
		sendMail();
	}

	@Override
	public HashMap<String,String> buildCarrierMap() {
		HashMap<String,String> carrierEmailList = new HashMap<>();
		carrierEmailList.put("AT&T", "txt.att.net");
		carrierEmailList.put("Verizon", "vtext.com");
		carrierEmailList.put("T-Mobile", "tmomail.net");
		carrierEmailList.put("Sprint", "messaging.sprintpcs.com");
		return carrierEmailList;
	}

	@Override
	public String getDirections() {
		return directions;
	}

	@Override
	public void setDirections(String directions) {
		this.directions = directions;
	}

	@Override
	public String getPhoneNumber() {
		return phoneNumber;
	}

	@Override
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	@Override
	public String getCarrier() {
		return carrier;
	}

	@Override
	public void setCarrier(String carrier) {
		this.carrier = carrier;
	}

	@Override
	public String getEmailAddress() {
		return emailAddress;
	}

	@Override
	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	@Override
	public String getSubject() {
		return subject;
	}

	@Override
	public void setSubject(String subject) {
		this.subject = subject;
	}

	public boolean isText() {
		return isText;
	}

	public void setIsText(boolean text) {
		isText = text;
	}
}
