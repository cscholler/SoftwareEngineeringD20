package edu.wpi.cs3733.d20.teamL;

import java.io.IOException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

import lombok.extern.slf4j.Slf4j;

import edu.wpi.cs3733.d20.teamL.util.FXMLLoaderHelper;

@Slf4j
public class App extends Application {
	FXMLLoaderHelper loaderHelper = new FXMLLoaderHelper();
	public static Stage stage;
	public static final double SCREEN_WIDTH = Screen.getPrimary().getVisualBounds().getWidth();
	public static final double SCREEN_HEIGHT = Screen.getPrimary().getVisualBounds().getHeight();

	@Override
	public void init() {
		log.info("Starting Up");
	}

	@Override
	public void start(Stage primaryStage) throws IOException {
		stage = primaryStage;
		Scene homeScene = new Scene(loaderHelper.getFXMLLoader("Staff/test").load());
		stage.setScene(homeScene);
		stage.setMaximized(true);
		stage.setWidth(SCREEN_WIDTH);
		stage.setHeight(SCREEN_HEIGHT);
		stage.setTitle("Team L");
		stage.show();
		FXMLLoaderHelper.getHistory().push(homeScene);
	}

	@Override
	public void stop() {
		Platform.exit();
		System.exit(0);
	}
}
