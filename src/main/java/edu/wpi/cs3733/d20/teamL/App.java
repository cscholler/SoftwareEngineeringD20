package edu.wpi.cs3733.d20.teamL;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import lombok.extern.slf4j.Slf4j;


@Slf4j
public class App extends Application {

	public Stage pStage;

	Parent root;

	@Override
	public void init() {
		log.info("Starting Up");
	}

	@Override
	public void start(Stage primaryStage) throws IOException {
		FXMLLoader fxmlLoader = new FXMLLoader();
		pStage = primaryStage;

		primaryStage.setTitle("Startup Window");
		primaryStage.setScene(new Scene(root));

		primaryStage.show();
	}

	@Override
	public void stop() {
		log.info("Shutting Down");
	}
}
