package edu.wpi.cs3733.d20.teamL;

import java.io.IOException;
import java.util.Timer;

import edu.wpi.cs3733.d20.teamL.util.TimerManager;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

import lombok.extern.slf4j.Slf4j;

import edu.wpi.cs3733.d20.teamL.util.FXMLLoaderHelper;

@Slf4j
public class App extends Application {
	private final FXMLLoaderHelper loaderHelper = new FXMLLoaderHelper();
	private static final TimerManager timerManager = new TimerManager();
	public static Timer clockTimer;
	public static Timer cacheTimer;
	public static Timer idleTimer;
	public static Stage stage;
	public static final double SCREEN_WIDTH = Screen.getPrimary().getVisualBounds().getWidth();
	public static final double SCREEN_HEIGHT = Screen.getPrimary().getVisualBounds().getHeight();
	public static boolean doUpdateCache = true;
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
		FXMLLoaderHelper.getHistory().push(homeScene);
		clockTimer = timerManager.startTimer(timerManager::updateTime, 0, 1000);
		cacheTimer = timerManager.startTimer(timerManager::forceUpdateCache, 0, 300000);
		idleTimer = timerManager.startTimer(timerManager::updateCacheIfNoInput, 0, 30000);
	}

	@Override
	public void stop() {
		Platform.exit();
		System.exit(0);
	}
}
