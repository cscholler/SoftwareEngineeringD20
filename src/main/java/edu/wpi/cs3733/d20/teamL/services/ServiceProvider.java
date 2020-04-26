package edu.wpi.cs3733.d20.teamL.services;

import com.google.inject.AbstractModule;

import edu.wpi.cs3733.d20.teamL.services.db.DatabaseCache;
import edu.wpi.cs3733.d20.teamL.services.db.DatabaseService;
import edu.wpi.cs3733.d20.teamL.services.db.IDatabaseCache;
import edu.wpi.cs3733.d20.teamL.services.db.IDatabaseService;
import edu.wpi.cs3733.d20.teamL.services.mail.IMailerService;
import edu.wpi.cs3733.d20.teamL.services.mail.MailerService;

public class ServiceProvider extends AbstractModule {
	protected void configure() {
		bind(IDatabaseService.class).to(DatabaseService.class).asEagerSingleton();
		bind(IDatabaseCache.class).to(DatabaseCache.class).asEagerSingleton();
		bind(IMailerService.class).to(MailerService.class).asEagerSingleton();
	}
}
