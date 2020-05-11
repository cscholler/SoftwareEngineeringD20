package edu.wpi.cs3733.d20.teamL.services;

import com.google.inject.AbstractModule;

import edu.wpi.cs3733.d20.teamL.services.speech.ISpeechToTextService;
import edu.wpi.cs3733.d20.teamL.services.speech.ITextToSpeechService;
import edu.wpi.cs3733.d20.teamL.services.speech.SpeechToTextService;
import edu.wpi.cs3733.d20.teamL.services.speech.TextToSpeechService;
import edu.wpi.cs3733.d20.teamL.services.db.DatabaseCache;
import edu.wpi.cs3733.d20.teamL.services.db.DatabaseService;
import edu.wpi.cs3733.d20.teamL.services.db.IDatabaseCache;
import edu.wpi.cs3733.d20.teamL.services.db.IDatabaseService;
import edu.wpi.cs3733.d20.teamL.services.messaging.IMessengerService;
import edu.wpi.cs3733.d20.teamL.services.messaging.MessengerService;
import edu.wpi.cs3733.d20.teamL.services.pathfinding.IPathfinderService;
import edu.wpi.cs3733.d20.teamL.services.pathfinding.PathfinderService;
import edu.wpi.cs3733.d20.teamL.services.users.ILoginManager;
import edu.wpi.cs3733.d20.teamL.services.users.IRequestHandlerService;
import edu.wpi.cs3733.d20.teamL.services.users.LoginManager;
import edu.wpi.cs3733.d20.teamL.services.users.RequestHandlerService;

public class ServiceProvider extends AbstractModule {
	protected void configure() {
		bind(IDatabaseService.class).to(DatabaseService.class).asEagerSingleton();
		bind(IDatabaseCache.class).to(DatabaseCache.class).asEagerSingleton();
		bind(IPathfinderService.class).to(PathfinderService.class).asEagerSingleton();
		bind(ILoginManager.class).to(LoginManager.class).asEagerSingleton();
		bind(IMessengerService.class).to(MessengerService.class).asEagerSingleton();
		bind(IRequestHandlerService.class).to(RequestHandlerService.class).asEagerSingleton();
		bind(ITextToSpeechService.class).to(TextToSpeechService.class).asEagerSingleton();
		bind(ISpeechToTextService.class).to(SpeechToTextService.class).asEagerSingleton();
	}
}
