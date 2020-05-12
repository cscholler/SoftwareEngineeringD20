package edu.wpi.cs3733.d20.teamL.services.speech;

public interface ISpeechToTextService {
	void startService();

	void stopService();

	void createClient();

	String recordAndConvertAsync(String type);

	String convertSpeechToText();

	void recordSpeech(String type);

	boolean isStartRecording();

	void setStartRecording(boolean startRecording);

	boolean isDestRecording();

	void setDestRecording(boolean destRecording);

	boolean allowRecording();

	void setAllowRecording(boolean allowRecording);
}
