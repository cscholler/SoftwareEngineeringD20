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

	//Parent root;

	@Override
	public void init() {
		log.info("Starting Up");
	}

	@Override
	public void start(Stage stage) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("views/LoginPageAgain.fxml"));
		Scene scene = new Scene(root);

		stage.setScene(scene);
		stage.setTitle("Employee/Administrator Login");
		stage.show();
	}


	@Override
	public void stop() {
		log.info("Shutting Down");
	}
}
