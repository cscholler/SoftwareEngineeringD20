package edu.wpi.cs3733.d20.teamL.util;

import com.google.inject.Guice;
import com.google.inject.Injector;
import edu.wpi.cs3733.d20.teamL.PathfinderModule;
import javafx.fxml.FXMLLoader;

public class FXMLLoaderHelper {
	public static final String ROOT_DIR = "/edu/wpi/cs3733/d20/teamL/views/";

	public FXMLLoader getFXMLLoader(String fxmlFile) {
		Injector injector = Guice.createInjector(new PathfinderModule());
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(ROOT_DIR + fxmlFile + ".fxml"));
		fxmlLoader.setControllerFactory(injector::getInstance);
		return fxmlLoader;
	}
}
