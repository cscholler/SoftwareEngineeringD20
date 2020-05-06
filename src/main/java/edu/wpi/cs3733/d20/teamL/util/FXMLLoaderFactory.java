package edu.wpi.cs3733.d20.teamL.util;

import edu.wpi.cs3733.d20.teamL.App;
import edu.wpi.cs3733.d20.teamL.services.users.ILoginManager;
import javafx.animation.FadeTransition;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;

import com.google.inject.Guice;
import com.google.inject.Injector;

import edu.wpi.cs3733.d20.teamL.services.ServiceProvider;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import lombok.extern.slf4j.Slf4j;

import java.util.Stack;

@Slf4j
public class FXMLLoaderFactory {
	private static final String ROOT_DIR = "/edu/wpi/cs3733/d20/teamL/views/";
	public static Injector injector = Guice.createInjector(new ServiceProvider());
	private static Stack<Scene> history = new Stack<>();

	public static Stack<Scene> getHistory() {
		return history;
	}

	public static void resetHistory() {
		history.clear();
	}

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
		scene.getRoot().addEventHandler(Event.ANY, event -> {
			App.startIdleTimer();
			ILoginManager loginManager = injector.getInstance(ILoginManager.class);
			if (loginManager.isAuthenticated()) {
				App.startLogoutTimer();
			}
		});

		Point2D prevDimensions = new Point2D(App.stage.getWidth(), App.stage.getHeight());
		App.stage.show();

		App.stage.setWidth(prevDimensions.getX());
		App.stage.setHeight(prevDimensions.getY());
		history.push(scene);
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
	 * Loads the previous page that the user was on.
	 */
	public void goBack() {
		history.pop();
		setupScene(history.pop());
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

	/**
	 * Plays an animation for a given label in which the label waits for 2 seconds and then fades away
	 *
	 * @param image
	 */
	public void showAndFade(ImageView image){
		image.setVisible(true);

		FadeTransition fadeTransition = new FadeTransition(Duration.millis(2000), image);
		fadeTransition.setDelay(Duration.millis(2000));
		fadeTransition.setFromValue(1.0);
		fadeTransition.setToValue(0.0);
		fadeTransition.setCycleCount(1);

		fadeTransition.play();
	}
}
