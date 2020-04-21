package edu.wpi.cs3733.d20.teamL;

import com.google.inject.AbstractModule;

import edu.wpi.cs3733.d20.teamL.services.db.DBCache;
import edu.wpi.cs3733.d20.teamL.services.db.DatabaseService;
import edu.wpi.cs3733.d20.teamL.services.db.IDBCache;
import edu.wpi.cs3733.d20.teamL.services.db.IDatabaseService;

public class PathfinderModule extends AbstractModule {
	protected void configure() {
		bind(IDatabaseService.class).to(DatabaseService.class).asEagerSingleton();
		bind(IDBCache.class).to(DBCache.class).asEagerSingleton();
	}
}
