package edu.wpi.cs3733.d20.teamL.util;

import edu.wpi.cs3733.d20.teamL.App;
import javafx.animation.FadeTransition;
import javafx.fxml.FXMLLoader;

import com.google.inject.Guice;
import com.google.inject.Injector;

import edu.wpi.cs3733.d20.teamL.services.ServiceProvider;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

public class FXMLLoaderHelper {
	private static final String ROOT_DIR = "/edu/wpi/cs3733/d20/teamL/views/";
	private static Injector injector = Guice.createInjector(new ServiceProvider());

	/**
	 * Loads the fxml file based on its name from the views package in resources.
	 *
	 * @param fxmlFile the path of the fxml file starting from resources/
	 * @return The fxmlLoader containing the fxml file and its controller
	 */
	public FXMLLoader getFXMLLoader(String fxmlFile) {
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(ROOT_DIR + fxmlFile + ".fxml"));
		fxmlLoader.setControllerFactory(injector::getInstance);
		return fxmlLoader;
	}

	/**
	 * Switches the stage view to a new scene.
	 *
	 * @param scene The new scene created from the root of this fxmlLoaderHelper (loaderHelper.load())
	 */
	public void setupScene(Scene scene) {
		App.stage.setScene(scene);
		App.stage.setMaximized(true);
		App.stage.show();

		App.stage.setWidth(App.SCREEN_WIDTH);
		App.stage.setHeight(App.SCREEN_HEIGHT);
	}

	/**
	 * Sets up and showAndWaits a new popup. The code will not progress past this function before the popup has been closed.
	 *
	 * @param stage The new window this popup will display in
	 * @param scene The scene of the popup
	 */
	public void setupPopup(Stage stage, Scene scene) {
		stage.setScene(scene);
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.initOwner(App.stage);
		stage.showAndWait();
	}

	/**
	 * Plays an animation for a given label in which the label waits for 2 seconds and then fades away
	 *
	 * @param label
	 */
	public void showAndFade(Label label){
		label.setVisible(true);

		FadeTransition fadeTransition = new FadeTransition(Duration.millis(2000), label);
		fadeTransition.setDelay(Duration.millis(2000));
		fadeTransition.setFromValue(1.0);
		fadeTransition.setToValue(0.0);
		fadeTransition.setCycleCount(1);

		fadeTransition.play();
	}
}
