package edu.wpi.cs3733.d20.teamL.services.speech;

public interface ISpeechToTextService {
	void startService();

	void stopService();

	void createClient();

	String recordAndConvertAsync(String type);

	String convertSpeechToText();

	void recordSpeech(String type);

	boolean allowStartRecording();

	void setAllowStartRecording(boolean allowStartRecording);

	boolean allowDestRecording();

	void setAllowDestRecording(boolean allowDestRecording);
}
