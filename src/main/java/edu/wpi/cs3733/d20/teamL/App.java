package edu.wpi.cs3733.d20.teamL;

import java.io.IOException;

import com.google.inject.Guice;
import com.google.inject.Injector;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import lombok.extern.slf4j.Slf4j;


@Slf4j
public class App extends Application {
	Scene scene;
	Parent root;

	@Override
	public void init() {
		log.info("Starting Up");
	}

	@Override
	public void start(Stage primaryStage) throws IOException {
		//root = FXMLLoader.load(getClass().getClassLoader().getResource("edu/wpi/cs3733/d20/teamL/views/Home.fxml"));
		Injector injector = Guice.createInjector(new PathfinderModule());
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/edu/wpi/cs3733/d20/teamL/views/Home.fxml"));
		fxmlLoader.setControllerFactory(injector::getInstance);
		root = fxmlLoader.load();
		primaryStage.setTitle("Home");
		primaryStage.setScene(new Scene(root));
		primaryStage.setMaximized(true);
		primaryStage.show();

	}

	@Override
	public void stop() {
		log.info("Shutting Down");
	}
}
