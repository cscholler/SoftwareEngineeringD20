package edu.wpi.cs3733.d20.teamL.util;

import edu.wpi.cs3733.d20.teamL.App;
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
			Platform.runLater(() -> timeLabel.setText(new SimpleDateFormat("E, MMM d h:mm aa").format(new Date())));
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

	public Timer startTimer(VoidMethod updateFunction, long delay, long period) {
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(timerWrapper(updateFunction::execute), delay, period);
		return timer;
	}
}
