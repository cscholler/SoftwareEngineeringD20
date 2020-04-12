package edu.wpi.leviathans.modules;

import com.google.inject.AbstractModule;
import edu.wpi.leviathans.services.db.DatabaseService;

public class DatabaseServiceProvider extends AbstractModule {
	@Override
	protected void configure() {
		bind(DatabaseService.class).asEagerSingleton();
	}
}
