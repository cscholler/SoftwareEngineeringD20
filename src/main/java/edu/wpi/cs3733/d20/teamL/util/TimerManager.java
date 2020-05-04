package edu.wpi.cs3733.d20.teamL.util;

import com.google.inject.Inject;
import edu.wpi.cs3733.d20.teamL.App;
import edu.wpi.cs3733.d20.teamL.services.db.IDatabaseCache;
import edu.wpi.cs3733.d20.teamL.services.users.ILoginManager;
import javafx.application.Platform;
import javafx.scene.control.Label;
import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
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
	public TimerTask timerWrapper(Runnable r) {
		return new TimerTask() {
			@Override
			public void run() {
				r.run();
			}
		};
	}

	public void updateTime() {
		if (TimerManager.timeLabel != null) {
			Platform.runLater(() -> timeLabel.setText(new SimpleDateFormat("E, MMM d | h:mm aa").format(new Date())));
		}
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

	public void logOutIfNoInput() {
		Platform.runLater(() -> {
			log.info("No input for 1 minutes. Logging out...");
			loginManager.logOut(true);
		});
	}

	public Timer startTimer(VoidMethod updateFunction, long delay, long period) {
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(timerWrapper(updateFunction::method), delay, period);
		return timer;
	}
}
