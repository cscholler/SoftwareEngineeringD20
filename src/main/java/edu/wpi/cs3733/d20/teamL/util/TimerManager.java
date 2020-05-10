package edu.wpi.cs3733.d20.teamL.util;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;

import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import edu.wpi.cs3733.d20.teamL.services.IHTTPClientService;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import lombok.extern.slf4j.Slf4j;

import edu.wpi.cs3733.d20.teamL.App;
import edu.wpi.cs3733.d20.teamL.entities.Kiosk;
import edu.wpi.cs3733.d20.teamL.services.db.IDatabaseCache;
import edu.wpi.cs3733.d20.teamL.services.users.ILoginManager;
import org.json.JSONArray;
import org.json.JSONObject;

@Slf4j
public class TimerManager {
	private boolean isCacheBeingUpdated = false;
	private final IDatabaseCache cache = FXMLLoaderFactory.injector.getInstance(IDatabaseCache.class);
	private final ILoginManager loginManager = FXMLLoaderFactory.injector.getInstance(ILoginManager.class);
	private final IHTTPClientService clientService = FXMLLoaderFactory.injector.getInstance(IHTTPClientService.class);
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

	public void updateWeather(Label tempLabel, ImageView weatherPic) {
		if (tempLabel != null && weatherPic != null){
			Platform.runLater(() -> {
				try {
					String[] currentWeather = getCurrentWeather();
						tempLabel.setText(currentWeather[0].substring(0,currentWeather[0].indexOf('.')) + "\u00B0 F");
						weatherPic.setImage(new Image("/edu/wpi/cs3733/d20/teamL/assets/weather/" + currentWeather[1] + ".png", 0, 100, true, true, true));
						weatherPic.setFitHeight(45);
				} catch (IOException ex) {
					log.error("Encountered IOException", ex);
				}
			});
		}
	}

	private String[] getCurrentWeather() throws IOException {
		String[] currentWeather = new String[2];

		Request request = new Request.Builder()
				.url("https://dark-sky.p.rapidapi.com/42.358429,-71.059769?lang=en&extend=hourly&units=auto")
				.get()
				.addHeader("x-rapidapi-host", "dark-sky.p.rapidapi.com")
				.addHeader("x-rapidapi-key", "70ccc13a26mshe5c361ac8a3b00bp1ac6fajsn2a92abbf35b4")
				.build();
		Response response = clientService.getClient().newCall(request).execute();
		JSONObject obj = new JSONObject(response.body().string());
		currentWeather[0] = String.valueOf(obj.getJSONObject("currently").getDouble("temperature"));
		currentWeather[1] = obj.getJSONObject("currently").getString("icon");

		return currentWeather;
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
