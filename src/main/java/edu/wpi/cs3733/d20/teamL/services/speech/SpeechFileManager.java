package edu.wpi.cs3733.d20.teamL.services.speech;

import com.google.auth.Credentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Slf4j
public class SpeechFileManager {
	private Credentials credentials;
	public enum SpeechServiceType {
		TEXT_TO_SPEECH,
		SPEECH_TO_TEXT
	}

	public SpeechFileManager() {
		generateCredentials();
	}

	private void generateCredentials() {
		try {
			credentials = ServiceAccountCredentials.fromStream(Files.newInputStream(Paths.get(getClass().getResource("/edu/wpi/cs3733/d20/teamL/auth/gcloud-auth.json").toURI())));
		} catch (IOException ex) {
			log.error("Encountered IOException.", ex);
		} catch (URISyntaxException ex) {
			log.error("Encountered URISyntaxException.", ex);
		}
	}

	public void writeSpeechToFile(byte[] audioBytes, SpeechServiceType type) {
		try {
			OutputStream out = new FileOutputStream(new File(getSpeechFileURI(type)));
			out.write(audioBytes);
		} catch (IOException ex) {
			log.error("Encountered IOException.", ex);
		} catch (URISyntaxException ex) {
			log.error("Encountered URISyntaxException.", ex);
		}
	}

	public URI getSpeechFileURI(SpeechServiceType type) throws URISyntaxException {
		return getClass().getResource("/edu/wpi/cs3733/d20/teamL/audio/" + getFileNameByServiceType(type) + ".wav").toURI();
	}

	private String getFileNameByServiceType(SpeechServiceType type) {
		String fileName;
		if (type == SpeechServiceType.TEXT_TO_SPEECH) {
			fileName = "tts";
		} else if (type == SpeechServiceType.SPEECH_TO_TEXT) {
			fileName = "stt";
		} else {
			throw new EnumConstantNotPresentException(SpeechServiceType.class, type.toString());
		}
		return fileName;
	}

	public Credentials getCredentials() {
		return credentials;
	}
}
