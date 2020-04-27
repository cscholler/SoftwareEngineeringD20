package edu.wpi.cs3733.d20.teamL.services;

import com.google.inject.AbstractModule;

import edu.wpi.cs3733.d20.teamL.services.db.DatabaseCache;
import edu.wpi.cs3733.d20.teamL.services.db.DatabaseService;
import edu.wpi.cs3733.d20.teamL.services.db.IDatabaseCache;
import edu.wpi.cs3733.d20.teamL.services.db.IDatabaseService;
import edu.wpi.cs3733.d20.teamL.services.users.ILoginManager;
import edu.wpi.cs3733.d20.teamL.services.users.LoginManager;
import edu.wpi.cs3733.d20.teamL.services.pathfinding.IPathfinderService;
import edu.wpi.cs3733.d20.teamL.services.pathfinding.PathfinderService;

public class ServiceProvider extends AbstractModule {
	protected void configure() {
		bind(IDatabaseService.class).to(DatabaseService.class).asEagerSingleton();
		bind(IDatabaseCache.class).to(DatabaseCache.class).asEagerSingleton();
		bind(IPathfinderService.class).to(PathfinderService.class).asEagerSingleton();
		bind(ILoginManager.class).to(LoginManager.class).asEagerSingleton();
	}
}
