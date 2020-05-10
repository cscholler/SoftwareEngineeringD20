package edu.wpi.cs3733.d20.teamL.services.accessability;

import com.google.cloud.speech.v1.*;
import com.google.protobuf.ByteString;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class SpeechToTextService {
	public void test() {
		try {
			SpeechClient speechClient = SpeechClient.create();
			String fileName = "C:/Users/lukeb/Desktop/test.wav";
			Path path = Paths.get(fileName);
			byte[] data = Files.readAllBytes(path);
			ByteString audioBytes = ByteString.copyFrom(data);
			RecognitionConfig config = RecognitionConfig.newBuilder().setEncoding(RecognitionConfig.AudioEncoding.LINEAR16).setSampleRateHertz(16000).setLanguageCode("en-US").build();
			RecognitionAudio audio = RecognitionAudio.newBuilder().setContent(audioBytes).build();
			RecognizeResponse response = speechClient.recognize(config, audio);
			List<SpeechRecognitionResult> results = response.getResultsList();
			for (SpeechRecognitionResult result : results) {
				SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);
				System.out.printf("Transcription: %s%n", alternative.getTranscript());
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
}
