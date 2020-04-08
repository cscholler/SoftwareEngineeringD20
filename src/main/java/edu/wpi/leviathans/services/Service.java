package edu.wpi.leviathans.services;

public abstract class Service {
	public Service() {
		startService();
	}

	public abstract void startService();

	public abstract void stopService();
}
