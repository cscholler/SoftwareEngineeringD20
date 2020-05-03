package edu.wpi.cs3733.d20.teamL.util;

import edu.wpi.cs3733.d20.teamL.services.db.IDatabaseCache;
import javafx.application.Platform;
import javafx.scene.control.Label;
import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

// Don't even bother trying to understand this one. I still don't.

@Slf4j
public class TimerManager {
	public static Label timeLabel;
	private boolean isCacheBeingUpdated = false;
	private IDatabaseCache cache = FXMLLoaderHelper.injector.getInstance(IDatabaseCache.class);
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
		if (!isCacheBeingUpdated) {
			isCacheBeingUpdated = true;
			Platform.runLater(() -> {
				log.info("5 minutes have passed. Forcing cache update");
				assert cache != null;
				cache.cacheAllFromDB();
				log.info("finished force");
			});
		}
		isCacheBeingUpdated = false;
	}

	public void updateCacheIfNoInput() {
		if (!isCacheBeingUpdated) {
			isCacheBeingUpdated = true;
			Platform.runLater(() -> {
				log.info("No input for 30 seconds. Caching from database.");
				assert cache != null;
				cache.cacheAllFromDB();
				log.info("finished timeout");
			});
		}
		isCacheBeingUpdated = false;
	}

	public Timer startTimer(VoidFunction updateFunction, long delay, long period) {
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(timerWrapper(updateFunction::update), delay, period);
		return timer;
	}

	public interface VoidFunction {
		void update();
	}
}
