package edu.wpi.leviathans;

import java.io.IOException;

import edu.wpi.leviathans.views.mapViewer.MapViewController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import lombok.extern.slf4j.Slf4j;

//import edu.wpi.leviathans.modules.DatabaseServiceProvider;

@Slf4j
public class App extends Application {

    public Stage pStage;

    Parent root;
    MapViewController controller;

    @Override
    public void init() {
        log.info("Starting Up");
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
		//Injector injector = Guice.createInjector(new DatabaseServiceProvider());
		FXMLLoader fxmlLoader = new FXMLLoader();
		//fxmlLoader.setControllerFactory(injector::getInstance);
        pStage = primaryStage;

        root = FXMLLoader.load(getClass().getResource("mapViewer/MapViewController.fxml"));
        controller = fxmlLoader.getController();

        primaryStage.setTitle("Map Viewer");
        primaryStage.setScene(new Scene(root));

        primaryStage.show();

        controller.init();
    }

    @Override
    public void stop() {
        log.info("Shutting Down");
    }
}
