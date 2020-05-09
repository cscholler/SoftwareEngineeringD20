package edu.wpi.cs3733.d20.teamL.util;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executor;

import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import lombok.extern.slf4j.Slf4j;

import edu.wpi.cs3733.d20.teamL.App;
import edu.wpi.cs3733.d20.teamL.entities.Kiosk;
import edu.wpi.cs3733.d20.teamL.services.db.IDatabaseCache;
import edu.wpi.cs3733.d20.teamL.services.users.ILoginManager;

@Slf4j
public class TimerManager {
	private boolean isCacheBeingUpdated = false;
	private final IDatabaseCache cache = FXMLLoaderFactory.injector.getInstance(IDatabaseCache.class);
	private final ILoginManager loginManager = FXMLLoaderFactory.injector.getInstance(ILoginManager.class);
	private final FXMLLoaderFactory loaderFactory = new FXMLLoaderFactory();
	private boolean isLogoutDialogueOpen = false;
	private long logoutTimeoutPeriod;
	private long idleCacheTimeoutPeriod;
	private long forceCacheTimeoutPeriod;
	private long screenSaverTimeoutPeriod;
	private int logoutTicks = 15;
	private Alert logoutWarning;
	private Timer logOutTickTimer;

	public TimerTask timerWrapper(Runnable r) {
		return new TimerTask() {
			@Override
			public void run() {
				r.run();
			}
		};
	}

	public void determineTimeoutPeriods() {
		cache.cacheKiosksFromDB();
		// Hard coded to only kiosk in database. Could be changed to find kiosk by ip.
		Kiosk currentKiosk = cache.getKioskCache().get(0);
		logoutTimeoutPeriod = currentKiosk.getLogoutTimeoutPeriod();
		idleCacheTimeoutPeriod = currentKiosk.getIdleCacheTimeout();
		forceCacheTimeoutPeriod = currentKiosk.getForceCacheTimout();
		screenSaverTimeoutPeriod = currentKiosk.getScreenSaverTimeout();
	}

	public String millisToMinsAndSecs(long millis) {
		String mins = String.valueOf(millis / 1000);
		long rawSecs = (((millis % 1000) / 1000) * 60);
		String secs = rawSecs > 9 ? String.valueOf(rawSecs) : "0" + rawSecs;
		return mins + ":" + secs;
	}

	public void updateTime(Label timeLabel) {
		if (timeLabel != null) {
			Platform.runLater(() -> timeLabel.setText(new SimpleDateFormat("h:mm aa").format(new Date())));
		}
	}

	public void updateDate(Label dataLabel) {
		if (dataLabel != null) {
			Platform.runLater(() -> dataLabel.setText(new SimpleDateFormat("E, MMM d").format(new Date())));
		}
	}

	public void logOutTick() {
		Platform.runLater(() -> {
			if (!isLogoutDialogueOpen) {
				isLogoutDialogueOpen = true;
				logoutWarning = new Alert(Alert.AlertType.WARNING);
				logoutWarning.setContentText("Press 'OK' to remain logged in.");
				logoutWarning.setHeaderText("Session will expire in " + logoutTicks + " seconds.");
				Optional<ButtonType> result = logoutWarning.showAndWait();
				if (result.isPresent()) {
					if (result.get() == ButtonType.OK) {
						logOutTickTimer.cancel();
						isLogoutDialogueOpen = false;
						logoutTicks = 15;
					}
				}
			} else {
				if (logoutTicks > 0) {
					logoutTicks--;
				}
				logoutWarning.setHeaderText("Session will expire in " + logoutTicks + " seconds.");
				if (logoutTicks == 0) {
					log.info("No input for " + millisToMinsAndSecs(logoutTimeoutPeriod) + ". Logging out...");
					loginManager.logOut(true);
					FXMLLoaderFactory.resetHistory();
					Object[] windowObjects =  Stage.getWindows().toArray();
					ArrayList<Stage> openStages = new ArrayList<>();
					for (Object obj : windowObjects) {
						openStages.add((Stage) obj);
					}
					for (Stage stage : openStages) {
						if (!stage.equals(App.stage)) {
							log.info("closing stage: " + stage.getTitle());
							stage.close();
						}
					}
					try {
						Parent root = loaderFactory.getFXMLLoader("map_viewer/MapViewer").load();
						loaderFactory.setupScene(new Scene(root));
					} catch (IOException ex) {
						log.error("Encountered IOException", ex);
					}
					logOutTickTimer.cancel();
					isLogoutDialogueOpen = false;
					logoutTicks = 15;
				}
			}
		});
	}

	public void showLogoutDialogueIfNoInput() {
		Platform.runLater(() -> {
			log.info("here");
			logOutTickTimer = startTimer(this::logOutTick, 0, 1000);
		});
	}

	public void updateCacheIfNoInput() {
		Platform.runLater(() -> {
			if (!isCacheBeingUpdated && App.allowCacheUpdates) {
				isCacheBeingUpdated = true;
				log.info("No input for " + millisToMinsAndSecs(idleCacheTimeoutPeriod) + ". Caching from database...");
				assert cache != null;
				cache.cacheAllFromDB();
				determineTimeoutPeriods();
				App.startForceUpdateTimer();
			}
			isCacheBeingUpdated = false;
		});
	}

	public void forceUpdateCache() {
		Platform.runLater(() -> {
			if (!isCacheBeingUpdated && App.allowCacheUpdates) {
				isCacheBeingUpdated = true;
				log.info(millisToMinsAndSecs(forceCacheTimeoutPeriod) + " since last update. Caching from database...");
				assert cache != null;
				cache.cacheAllFromDB();
			}
			isCacheBeingUpdated = false;
		});
	}

	public void showScreensaverIfNoInput() {
		Platform.runLater(() -> {
			log.info(millisToMinsAndSecs(screenSaverTimeoutPeriod) + " since last update. Showing screensaver...");
			assert cache != null;
			cache.cacheAllFromDB();
		});
	}

	public Timer startTimer(VoidMethod updateMethod, String methodName) {
		Timer timer = new Timer();
		long period = 60000;
		switch (methodName) {
			case "showLogoutDialogueIfNoInput": {
				period = Math.max(logoutTimeoutPeriod - 15000, 15000);
			}
			break;
			case "updateCacheIfNoInput": {
				period = idleCacheTimeoutPeriod;
			}
			break;
			case "forceUpdateCache": {
				period = forceCacheTimeoutPeriod;
			}
			break;
			case "showScreensaverIfNoInput": {
				period = screenSaverTimeoutPeriod;
			}
		}
		timer.scheduleAtFixedRate(timerWrapper(updateMethod::execute), period, period);
		return timer;
	}

	public Timer startTimer(VoidMethod updateMethod, long delay, long period) {
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(timerWrapper(updateMethod::execute), delay, period);
		return timer;
	}
}
