package edu.wpi.cs3733.d20.teamL.services;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.texttospeech.v1.AudioConfig;
import com.google.cloud.texttospeech.v1.AudioEncoding;
import com.google.cloud.texttospeech.v1.SsmlVoiceGender;
import com.google.cloud.texttospeech.v1.SynthesisInput;
import com.google.cloud.texttospeech.v1.SynthesizeSpeechResponse;
import com.google.cloud.texttospeech.v1.TextToSpeechClient;
import com.google.cloud.texttospeech.v1.TextToSpeechSettings;
import com.google.cloud.texttospeech.v1.VoiceSelectionParams;
import com.google.protobuf.ByteString;
import com.twilio.twiml.voice.Play;
import lombok.extern.slf4j.Slf4j;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

@Slf4j
public class TextToSpeechConverter {
	public void convertTextToSpeech(String text, String lang, SsmlVoiceGender gender) {
		try {
			ServiceAccountCredentials credentials = ServiceAccountCredentials.fromStream(Files.newInputStream(Paths.get("C:/Users/lukeb/Desktop/keen-ripsaw-276706-aa1edde82fc4.json")));
			TextToSpeechSettings settings = TextToSpeechSettings.newBuilder().setCredentialsProvider(FixedCredentialsProvider.create(credentials)).build();
			TextToSpeechClient textToSpeechClient = TextToSpeechClient.create(settings);
			SynthesisInput input = SynthesisInput.newBuilder().setText(text).build();
			VoiceSelectionParams voice = VoiceSelectionParams.newBuilder().setLanguageCode(lang).setSsmlGender(gender).build();
			AudioConfig audioConfig = AudioConfig.newBuilder().setAudioEncoding(AudioEncoding.MP3).build();
			SynthesizeSpeechResponse response = textToSpeechClient.synthesizeSpeech(input, voice, audioConfig);
			ByteString audioContents = response.getAudioContent();

			//OutputStream out = new FileOutputStream(("C:/Users/lukeb/Desktop/test.mp3"));
			//out.write(audioContents.toByteArray());
		} catch (IOException ex) {
			log.error("Encountered IOException.", ex);
		}
	}
}
