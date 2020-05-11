package edu.wpi.cs3733.d20.teamL.services.accessability;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

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

import lombok.extern.slf4j.Slf4j;

import edu.wpi.cs3733.d20.teamL.services.Service;

@Slf4j
public class TextToSpeechService extends Service implements ITextToSpeechService {
	private ServiceAccountCredentials credentials;
	private TextToSpeechSettings settings;
	private TextToSpeechClient client;
	private AudioConfig audioConfig;
	private final ArrayList<Clip> activeClips = new ArrayList<>();

	public TextToSpeechService() {
		super();
		this.serviceName = "google-tts-01";
	}

	@Override
	public void startService() {
		createClient();
	}

	@Override
	public void stopService() {
		getClient().close();
	}

	@Override
	public void createClient() {
		try {
			setCredentials(ServiceAccountCredentials.fromStream(Files.newInputStream(Paths.get(getClass().getResource("/edu/wpi/cs3733/d20/teamL/auth/keen-ripsaw-276706-aa1edde82fc4.json").toURI()))));
			setSettings(TextToSpeechSettings.newBuilder().setCredentialsProvider(FixedCredentialsProvider.create(credentials)).build());
			setClient(client = TextToSpeechClient.create(settings));
			setAudioConfig(audioConfig = AudioConfig.newBuilder().setAudioEncoding(AudioEncoding.LINEAR16).build());
		} catch (IOException ex) {
			log.error("Encountered IOException.", ex);
		} catch (URISyntaxException ex) {
			log.error("Encountered URISyntaxException.", ex);
		}
	}

	@Override
	public ByteString convertTextToSpeech(String text, String lang, SsmlVoiceGender gender) {
		SynthesisInput input = SynthesisInput.newBuilder().setText(text).build();
		VoiceSelectionParams voice = VoiceSelectionParams.newBuilder().setLanguageCode(lang).setSsmlGender(gender).build();
		SynthesizeSpeechResponse response = client.synthesizeSpeech(input, voice, getAudioConfig());
		writeSpeechToFile(response.getAudioContent());
		return response.getAudioContent();
	}

	@Override
	public void playSpeech(ByteString audio) {
		try {
			File audioFile = new File(getClass().getResource("/edu/wpi/cs3733/d20/teamL/audio/tts.wav").toURI());
			AudioInputStream stream = AudioSystem.getAudioInputStream(audioFile);
			Clip clip = (Clip) AudioSystem.getLine(new DataLine.Info(Clip.class, stream.getFormat()));
			for (Clip currentClip : activeClips) {
				if (currentClip != clip) {
					currentClip.stop();
					currentClip.close();
				}
			}
			activeClips.add(clip);
			clip.open(stream);
			clip.start();
		} catch (URISyntaxException ex) {
			log.error("Encountered URISyntaxException", ex);
		} catch (UnsupportedAudioFileException ex) {
			log.error("Encountered UnsupportedAudioFileException", ex);
		} catch (LineUnavailableException ex) {
			log.error("Encountered LineUnavailableException", ex);
		} catch (IOException ex) {
			log.error("Encountered IOException", ex);
		}
	}

	@Override
	public void writeSpeechToFile(ByteString audio) {
		try {
			OutputStream out = new FileOutputStream(new File(getClass().getResource("/edu/wpi/cs3733/d20/teamL/audio/tts.wav").toURI()));
			out.write(audio.toByteArray());
		} catch (IOException | URISyntaxException ex) {
			log.error("Encountered IOException.", ex);
		}
	}

	@Override
	public ServiceAccountCredentials getCredentials() {
		return credentials;
	}

	@Override
	public void setCredentials(ServiceAccountCredentials credentials) {
		this.credentials = credentials;
	}

	@Override
	public TextToSpeechSettings getSettings() {
		return settings;
	}

	@Override
	public void setSettings(TextToSpeechSettings settings) {
		this.settings = settings;
	}

	@Override
	public TextToSpeechClient getClient() {
		return client;
	}

	@Override
	public void setClient(TextToSpeechClient client) {
		this.client = client;
	}

	@Override
	public AudioConfig getAudioConfig() {
		return audioConfig;
	}

	@Override
	public void setAudioConfig(AudioConfig audioConfig) {
		this.audioConfig = audioConfig;
	}
}
