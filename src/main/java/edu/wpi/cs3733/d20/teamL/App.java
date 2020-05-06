package edu.wpi.cs3733.d20.teamL;

import java.io.IOException;
import java.util.Timer;

import edu.wpi.cs3733.d20.teamL.util.TimerManager;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

import lombok.extern.slf4j.Slf4j;

import edu.wpi.cs3733.d20.teamL.util.FXMLLoaderFactory;

@Slf4j
public class App extends Application {
	private final FXMLLoaderFactory loaderHelper = new FXMLLoaderFactory();
	private static final TimerManager timerManager = new TimerManager();
	public static Timer forceCacheUpdateTimer;
	public static Timer idleCacheUpdateTimer;
	public static Timer idleLogoutTimer;
	public static Stage stage;
	public static final double SCREEN_WIDTH = Screen.getPrimary().getVisualBounds().getWidth();
	public static final double SCREEN_HEIGHT = Screen.getPrimary().getVisualBounds().getHeight();
	public static boolean doUpdateCacheOnLoad = true;
	public static boolean allowCacheUpdates = true;
	public static double UI_SCALE = 1.34 * (SCREEN_WIDTH / 1920);

	public static void startForceUpdateTimer() {
		if (forceCacheUpdateTimer != null) {
			forceCacheUpdateTimer.cancel();
		}
		forceCacheUpdateTimer = timerManager.startTimer(timerManager::forceUpdateCache, 300000, 300000);
	}

	public static void startIdleTimer() {
		if (idleCacheUpdateTimer != null) {
			idleCacheUpdateTimer.cancel();
		}
		idleCacheUpdateTimer = timerManager.startTimer(timerManager::updateCacheIfNoInput, 30000, 30000);
	}

	public static void startLogoutTimer() {
		if (idleLogoutTimer != null) {
			idleLogoutTimer.cancel();
		}
		idleLogoutTimer = timerManager.startTimer(timerManager::logOutIfNoInput, 60000, 60000);
	}

	@Override
	public void init() {
		log.info("Starting Up");
	}

	@Override
	public void start(Stage primaryStage) throws IOException {
		stage = primaryStage;
		Scene homeScene = new Scene(loaderHelper.getFXMLLoader("map_viewer/MapViewer").load());
		stage.setScene(homeScene);
		stage.setMaximized(true);
		stage.setWidth(SCREEN_WIDTH);
		stage.setHeight(SCREEN_HEIGHT);
		stage.setTitle("Team L");
		stage.show();
		FXMLLoaderFactory.getHistory().push(homeScene);
		homeScene.addEventHandler(Event.ANY, event -> startIdleTimer());
		startIdleTimer();
		startForceUpdateTimer();
		startLogoutTimer();
	}

	@Override
	public void stop() {
		Platform.exit();
		System.exit(0);
	}
}
