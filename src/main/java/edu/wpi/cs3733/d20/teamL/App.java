package edu.wpi.cs3733.d20.teamL;

import java.io.IOException;
import java.util.Stack;

import com.google.inject.Guice;
import com.google.inject.Injector;
import edu.wpi.cs3733.d20.teamL.util.FXMLLoaderHelper;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

import lombok.extern.slf4j.Slf4j;


@Slf4j
public class App extends Application {

	FXMLLoaderHelper loaderHelper = new FXMLLoaderHelper();
	Scene scene;
	Parent root;
	Stage primaryStage;

	public static final double SCREEN_WIDTH = Screen.getPrimary().getVisualBounds().getWidth();
	public static final double SCREEN_HEIGHT = Screen.getPrimary().getVisualBounds().getHeight();

	@Override
	public void init() {
		log.info("Starting Up");
	}

	@Override
	public void start(Stage primaryStage) throws IOException {

		root = loaderHelper.getFXMLLoader("Home").load();
		primaryStage.setTitle("Team L");
		primaryStage.setScene(new Scene(root));
		primaryStage.setMaximized(true);
		primaryStage.show();
	}


	@Override
	public void stop() {
		log.info("Shutting Down");
	}
}
