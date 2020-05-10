package edu.wpi.cs3733.d20.teamL.entities;

public class Kiosk {
	private final String id;
	private final String nodeID;
	private final long logoutTimeoutPeriod;
	private final long idleCacheTimeout;
	private final long forceCacheTimout;
	private final long screenSaverTimeout;

	public Kiosk(String id, String nodeID, long logoutTimeoutPeriod, long idleCacheTimeout, long forceCacheTimout, long screenSaverTimeout) {
		this.id = id;
		this.nodeID = nodeID;
		this.logoutTimeoutPeriod = logoutTimeoutPeriod;
		this.idleCacheTimeout = idleCacheTimeout;
		this.forceCacheTimout = forceCacheTimout;
		this.screenSaverTimeout =screenSaverTimeout;
	}

	public String getID() {
		return id;
	}

	public String getNodeID() {
		return nodeID;
	}

	public long getLogoutTimeoutPeriod() {
		return logoutTimeoutPeriod;
	}

	public long getIdleCacheTimeout() {
		return idleCacheTimeout;
	}

	public long getForceCacheTimout() {
		return forceCacheTimout;
	}

	public long getScreenSaverTimeout() {
		return screenSaverTimeout;
	}
}
