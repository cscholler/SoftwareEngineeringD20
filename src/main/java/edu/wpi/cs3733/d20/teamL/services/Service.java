package edu.wpi.cs3733.d20.teamL.services;

public abstract class Service {
	public Service() {
		startService();
	}

	public String serviceName;

	public abstract void startService();

	public abstract void stopService();
}
