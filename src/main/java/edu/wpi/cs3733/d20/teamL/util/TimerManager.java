package edu.wpi.cs3733.d20.teamL.util;

import edu.wpi.cs3733.d20.teamL.App;
import edu.wpi.cs3733.d20.teamL.entities.Kiosk;
import edu.wpi.cs3733.d20.teamL.services.db.IDatabaseCache;
import edu.wpi.cs3733.d20.teamL.services.users.ILoginManager;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.stage.Window;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

@Slf4j
public class TimerManager {
	public static Label timeLabel;
	private boolean isCacheBeingUpdated = false;
	// For some reason, dependency injection has to be done this way in this class only.
	// Might have something to do with the use of runnables for timer tasks via Platform.runLater().
	// Don't touch.
	private final IDatabaseCache cache = FXMLLoaderFactory.injector.getInstance(IDatabaseCache.class);
	private final ILoginManager loginManager = FXMLLoaderFactory.injector.getInstance(ILoginManager.class);
	private final FXMLLoaderFactory loaderFactory = new FXMLLoaderFactory();
	private long logoutTimeoutPeriod;
	private long idleCacheTimeoutPeriod;
	private long forceCacheTimeoutPeriod;
	private long screeSaverTimeout;

	public TimerTask timerWrapper(Runnable r) {
		return new TimerTask() {
			@Override
			public void run() {
				r.run();
			}
		};
	}

	public void updateTime(Label timeLabel) {
		if (timeLabel != null) {
			Platform.runLater(() -> timeLabel.setText(new SimpleDateFormat("h:mm aa").format(new Date())));
		}
	}

	public void updateDate(Label timeLabel) {
		if (timeLabel != null) {
			Platform.runLater(() -> timeLabel.setText(new SimpleDateFormat("E, MMM d").format(new Date())));
		}
	}

	public void logOutIfNoInput() {
		Platform.runLater(() -> {
			log.info("No input for 1 minute. Logging out...");
			loginManager.logOut(true);
			FXMLLoaderFactory.resetHistory();
			try {
				try {
					Stage openPopup = (Stage) Stage.getWindows().stream().filter(Window::isFocused).findFirst().orElse(null);
					assert openPopup != null;
					openPopup.close();
				} catch (NullPointerException ex) {
					log.warn("Attempted to close an unfocused window on timeout.");
				}
				Parent root = loaderFactory.getFXMLLoader("map_viewer/MapViewer").load();
				loaderFactory.setupScene(new Scene(root));
			} catch (IOException ex) {
				log.error("Encountered IOException", ex);
			}
		});
	}

	public void updateCacheIfNoInput() {
		if (!isCacheBeingUpdated && App.allowCacheUpdates) {
			isCacheBeingUpdated = true;
			Platform.runLater(() -> {
				log.info("No input for 30 seconds. Caching from database...");
				assert cache != null;
				cache.cacheAllFromDB();
				App.startForceUpdateTimer();
			});
		}
		isCacheBeingUpdated = false;
	}

	public void forceUpdateCache() {
		if (!isCacheBeingUpdated && App.allowCacheUpdates) {
			isCacheBeingUpdated = true;
			Platform.runLater(() -> {
				log.info("5 minutes have passed since last update. Caching from database...");
				assert cache != null;
				cache.cacheAllFromDB();
			});
		}
		isCacheBeingUpdated = false;
	}

	public void showScreensaverIfNoInput() {
		Platform.runLater(() -> {
			log.info("5 minutes have passed since last update. Caching from database...");
			assert cache != null;
			cache.cacheAllFromDB();
		});
	}

	public long determineTimeoutPeriod(String methodName) {
		long period;
		cache.cacheKiosksFromDB();
		// Hard coded to only kiosk in database. Could be changed to find kiosk by ip.
		Kiosk currentKiosk = cache.getKioskCache().get(0);
		switch (methodName) {
			case "logOutIfNoInput": {
				period = currentKiosk.getLogoutTimeoutPeriod();
			}
			break;
			case "updateCacheIfNoInput": {
				period = currentKiosk.getIdleCacheTimeout();
			}
			break;
			case "forceUpdateCache": {
				period = currentKiosk.getForceCacheTimout();
			}
			break;
			case "showScreensaverIfNoInput": {
				period = currentKiosk.getScreenSaverTimeout();
			}
			break;
			default: {
				log.warn("Attempted to obtain timeout period for invalid timer task. Using default value of 30 seconds.");
				period = 30000;
			}
		}
		return period;
	}

	public Timer startTimer(String methodName) {
		Timer timer = new Timer();
		long period = determineTimeoutPeriod(methodName);
		try {
			Method updateMethod = this.getClass().getDeclaredMethod(methodName);
			timer.scheduleAtFixedRate(timerWrapper(() -> {
				try {
					updateMethod.invoke(this);
				} catch (IllegalAccessException ex) {
					log.error("Encountered IllegalAccessException.", ex);
				} catch (InvocationTargetException ex) {
					log.error("Encountered InvocationTargetException.", ex);
				}
			}), period, period);
			return timer;
		} catch (NoSuchMethodException ex) {
			log.error("Encountered NoSuchMethodException.", ex);
		}
		return null;
	}

	public Timer startTimer(VoidMethod updateMethod, long delay, long period) {
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(timerWrapper(updateMethod::execute), delay, period);
		return timer;
	}

	public long getLogoutTimeoutPeriod() {
		return logoutTimeoutPeriod;
	}

	public void setLogoutTimeoutPeriod(long logoutTimeoutPeriod) {
		this.logoutTimeoutPeriod = logoutTimeoutPeriod;
	}

	public long getIdleCacheTimeoutPeriod() {
		return idleCacheTimeoutPeriod;
	}

	public void setIdleCacheTimeoutPeriod(long idleCacheTimeoutPeriod) {
		this.idleCacheTimeoutPeriod = idleCacheTimeoutPeriod;
	}

	public long getForceCacheTimeoutPeriod() {
		return forceCacheTimeoutPeriod;
	}

	public void setForceCacheTimeoutPeriod(long forceCacheTimeoutPeriod) {
		this.forceCacheTimeoutPeriod = forceCacheTimeoutPeriod;
	}

	public long getScreeSaverTimeout() {
		return screeSaverTimeout;
	}

	public void setScreeSaverTimeout(long screeSaverTimeout) {
		this.screeSaverTimeout = screeSaverTimeout;
	}
}
