package edu.wpi.leviathans.services;

public abstract class Service {
	public Service() {
		startService();
	}

	public String serviceName;

	public abstract void startService();

	public abstract void stopService();
}
