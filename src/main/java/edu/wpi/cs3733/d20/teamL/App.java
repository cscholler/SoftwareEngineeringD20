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
	public static Timer clockTimer;
	public static Timer forceUpdateTimer;
	public static Timer idleTimer;
	public static Timer logoutTimer;
	public static Stage stage;
	public static final double SCREEN_WIDTH = Screen.getPrimary().getVisualBounds().getWidth();
	public static final double SCREEN_HEIGHT = Screen.getPrimary().getVisualBounds().getHeight();
	public static boolean doUpdateCache = true;
	public static boolean allowCacheUpdates = true;

	public static void startIdleTimer() {
		if (idleTimer != null) {
			idleTimer.cancel();
		}
		//idleTimer = timerManager.startTimer(timerManager::updateCacheIfNoInput, 30000, 30000);
		idleTimer = timerManager.startTimer(timerManager::updateCacheIfNoInput, 5000, 5000);
	}

	public static void startForceUpdateTimer() {
		if (forceUpdateTimer != null) {
			forceUpdateTimer.cancel();
		}
		//forceUpdateTimer = timerManager.startTimer(timerManager::forceUpdateCache, 300000, 300000);
		forceUpdateTimer = timerManager.startTimer(timerManager::forceUpdateCache, 15000, 15000);
	}

	public static void startLogoutTimer() {
		if (logoutTimer != null) {
			logoutTimer.cancel();
		}
		//logoutTimer = timerManager.startTimer(timerManager::logOutIfNoInput, 60000, 60000);
		logoutTimer = timerManager.startTimer(timerManager::logOutIfNoInput, 30000, 30000);
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
		clockTimer = timerManager.startTimer(timerManager::updateTime, 0, 1000);
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
