package edu.wpi.cs3733.d20.teamL.services.accessability;

import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.texttospeech.v1.AudioConfig;
import com.google.cloud.texttospeech.v1.SsmlVoiceGender;
import com.google.cloud.texttospeech.v1.TextToSpeechClient;
import com.google.cloud.texttospeech.v1.TextToSpeechSettings;
import com.google.protobuf.ByteString;

import javax.sound.sampled.Clip;

public interface ITextToSpeechService {
	void startService();

	void stopService();

	void createClient();

	ByteString convertTextToSpeech(String text, String lang, SsmlVoiceGender gender);

	void playSpeech(ByteString audio);

	void writeSpeechToFile(ByteString audio);

	ServiceAccountCredentials getCredentials();

	void setCredentials(ServiceAccountCredentials credentials);

	TextToSpeechSettings getSettings();

	void setSettings(TextToSpeechSettings settings);

	TextToSpeechClient getClient();

	void setClient(TextToSpeechClient client);

	AudioConfig getAudioConfig();

	void setAudioConfig(AudioConfig audioConfig);
}
