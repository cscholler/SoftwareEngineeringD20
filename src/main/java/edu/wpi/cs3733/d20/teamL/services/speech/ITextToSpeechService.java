package edu.wpi.cs3733.d20.teamL.services.speech;

import com.google.cloud.texttospeech.v1.SsmlVoiceGender;
import com.google.protobuf.ByteString;

public interface ITextToSpeechService {
	void startService();

	void stopService();

	void createClient();

	ByteString convertTextToSpeech(String text, String lang, SsmlVoiceGender gender);

	void playSpeech(ByteString audio);
}
