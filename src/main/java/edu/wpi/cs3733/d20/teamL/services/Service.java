package edu.wpi.cs3733.d20.teamL.services;

public abstract class Service {
	public String serviceName;

	public Service() {
		startService();
	}

	public abstract void startService();

	public abstract void stopService();
}
