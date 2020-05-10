package edu.wpi.cs3733.d20.teamL;

import edu.wpi.cs3733.d20.teamL.services.speech.SpeechToTextService;

public class Main {
	public static void main(String[] args) {
		//App.launch(App.class, args);
		SpeechToTextService speechToTextService = new SpeechToTextService();
		speechToTextService.recordSpeech();
	}
}
