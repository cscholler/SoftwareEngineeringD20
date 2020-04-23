package edu.wpi.cs3733.d20.teamL.util;

import edu.wpi.cs3733.d20.teamL.App;
import javafx.fxml.FXMLLoader;

import com.google.inject.Guice;
import com.google.inject.Injector;

import edu.wpi.cs3733.d20.teamL.services.ServiceProvider;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class FXMLLoaderHelper {
	private static final String ROOT_DIR = "/edu/wpi/cs3733/d20/teamL/views/";
	private static Injector injector = Guice.createInjector(new ServiceProvider());

	public FXMLLoader getFXMLLoader(String fxmlFile) {
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(ROOT_DIR + fxmlFile + ".fxml"));
		fxmlLoader.setControllerFactory(injector::getInstance);
		return fxmlLoader;
	}

	public void setupScene(Scene scene) {
		App.stage.setScene(scene);
		App.stage.setMaximized(true);
		App.stage.show();

		App.stage.setWidth(App.SCREEN_WIDTH);
		App.stage.setHeight(App.SCREEN_HEIGHT);
	}

	public void setupPopup(Stage stage, Scene scene) {
		stage.setScene(scene);
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.initOwner(App.stage);
		stage.showAndWait();
	}

}
