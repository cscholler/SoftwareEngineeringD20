package edu.wpi.cs3733.d20.teamL.services.speech;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.cloud.texttospeech.v1.AudioConfig;
import com.google.cloud.texttospeech.v1.AudioEncoding;
import com.google.cloud.texttospeech.v1.SsmlVoiceGender;
import com.google.cloud.texttospeech.v1.SynthesisInput;
import com.google.cloud.texttospeech.v1.SynthesizeSpeechResponse;
import com.google.cloud.texttospeech.v1.TextToSpeechClient;
import com.google.cloud.texttospeech.v1.TextToSpeechSettings;
import com.google.cloud.texttospeech.v1.VoiceSelectionParams;
import com.google.protobuf.ByteString;

import edu.wpi.cs3733.d20.teamL.util.AsyncTaskManager;
import lombok.extern.slf4j.Slf4j;

import edu.wpi.cs3733.d20.teamL.services.Service;

@Slf4j
public class TextToSpeechService extends Service implements ITextToSpeechService {
	private TextToSpeechClient client;
	private AudioConfig audioConfig;
	private SpeechFileManager speechFileManager;
	private ArrayList<Clip> activeClips;
	private boolean isMuted = false;

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
		client.close();
	}

	@Override
	public void createClient() {
		speechFileManager = new SpeechFileManager();
		try {
			TextToSpeechSettings settings = TextToSpeechSettings.newBuilder().setCredentialsProvider(FixedCredentialsProvider.create(speechFileManager.getCredentials())).build();
			client = TextToSpeechClient.create(settings);
			audioConfig = AudioConfig.newBuilder().setAudioEncoding(AudioEncoding.LINEAR16).build();
			activeClips = new ArrayList<>();
		} catch (IOException ex) {
			log.error("Encountered IOException.", ex);
		}
		convertTextToSpeech("load");
	}

	@Override
	public void convertAndPlayAsync(String text) {
		convertAndPlayAsync(text, "en-US", SsmlVoiceGender.MALE);
	}

	@Override
	public void convertAndPlayAsync(String text, String lang, SsmlVoiceGender gender) {
		AsyncTaskManager.newTask(() -> playSpeech(convertTextToSpeech(text, lang, gender)));
	}

	@Override
	public ByteString convertTextToSpeech(String text) {
		SynthesisInput input = SynthesisInput.newBuilder().setText(text).build();
		VoiceSelectionParams voice = VoiceSelectionParams.newBuilder().setLanguageCode("en-US").setSsmlGender(SsmlVoiceGender.MALE).build();
		SynthesizeSpeechResponse response = client.synthesizeSpeech(input, voice, audioConfig);
		ByteString audioContent = response.getAudioContent();
		speechFileManager.writeSpeechToFile(audioContent.toByteArray(), SpeechFileManager.SpeechServiceType.TEXT_TO_SPEECH);
		return audioContent;
	}

	@Override
	public ByteString convertTextToSpeech(String text, String lang, SsmlVoiceGender gender) {
		SynthesisInput input = SynthesisInput.newBuilder().setText(text).build();
		VoiceSelectionParams voice = VoiceSelectionParams.newBuilder().setLanguageCode(lang).setSsmlGender(gender).build();
		SynthesizeSpeechResponse response = client.synthesizeSpeech(input, voice, audioConfig);
		ByteString audioContent = response.getAudioContent();
		speechFileManager.writeSpeechToFile(audioContent.toByteArray(), SpeechFileManager.SpeechServiceType.TEXT_TO_SPEECH);
		return audioContent;
	}

	@Override
	public void playSpeech(ByteString audio) {
		for (Clip currentClip : activeClips) {
			currentClip.stop();
			currentClip.close();
		}
		try {
			File audioFile = new File(speechFileManager.getSpeechFileURI(SpeechFileManager.SpeechServiceType.TEXT_TO_SPEECH));
			AudioInputStream stream = AudioSystem.getAudioInputStream(audioFile);
			Clip clip = (Clip) AudioSystem.getLine(new DataLine.Info(Clip.class, stream.getFormat()));
			clip.open(stream);
			clip.start();
			activeClips.add(clip);
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
	public boolean isMuted() {
		return isMuted;
	}

	@Override
	public void setMuted(boolean muted) {
		isMuted = muted;
	}
}
