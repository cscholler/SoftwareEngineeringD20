package edu.wpi.cs3733.d20.teamL.services.speech;

import com.google.auth.Credentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
public class SpeechFileManager {
	private Credentials credentials;
	private Path tempDir;
	public enum SpeechServiceType {
		TEXT_TO_SPEECH,
		SPEECH_TO_TEXT
	}

	public SpeechFileManager() {
		generateCredentials();
		createTempDirectory();
	}

	private void createTempDirectory() {
		try {
			if (tempDir == null) {
				tempDir = Files.createTempDirectory(null);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void generateCredentials() {
		try {
			credentials = ServiceAccountCredentials.fromStream(getClass().getResourceAsStream("/edu/wpi/cs3733/d20/teamL/auth/gcloud-auth.json"));
		} catch (IOException ex) {
			log.error("Encountered IOException.", ex);
		}
	}

	public void writeSpeechToFile(byte[] audioBytes, SpeechServiceType type) {
		try {
			OutputStream out = new FileOutputStream(getSpeechFile(type));
			out.write(audioBytes);
		} catch (IOException ex) {
			log.error("Encountered IOException.", ex);
		}
	}

	public File getSpeechFile(SpeechServiceType type) {
		File file = new File(tempDir.toString() + getFileNameByServiceType(type) + ".wav");
		System.out.println(file.toString());
		return file;
	}

	private String getFileNameByServiceType(SpeechServiceType type) {
		String fileName;
		if (type == SpeechServiceType.TEXT_TO_SPEECH) {
			fileName = "/tts";
		} else if (type == SpeechServiceType.SPEECH_TO_TEXT) {
			fileName = "/stt";
		} else {
			throw new EnumConstantNotPresentException(SpeechServiceType.class, type.toString());
		}
		return fileName;
	}

	public Credentials getCredentials() {
		return credentials;
	}
}
