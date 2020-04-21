package edu.wpi.cs3733.d20.teamL.util;

import javafx.fxml.FXMLLoader;

import com.google.inject.Guice;
import com.google.inject.Injector;

import edu.wpi.cs3733.d20.teamL.PathfinderModule;

public class FXMLLoaderHelper {
	private static final String ROOT_DIR = "/edu/wpi/cs3733/d20/teamL/views/";
	private static Injector injector = Guice.createInjector(new PathfinderModule());

	public FXMLLoader getFXMLLoader(String fxmlFile) {
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(ROOT_DIR + fxmlFile + ".fxml"));
		fxmlLoader.setControllerFactory(injector::getInstance);
		return fxmlLoader;
	}
}
