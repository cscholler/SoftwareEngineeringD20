package edu.wpi.cs3733.d20.teamL.services.speech;

public interface ISpeechToTextService {
	void startService();

	void stopService();

	void createClient();

	String recordAndConvertAsync();

	String convertSpeechToText();

	void recordSpeech();
}
