package edu.wpi.leviathans;


import edu.wpi.leviathans.util.Row;
import edu.wpi.leviathans.util.io.CSVParser;
import edu.wpi.leviathans.views.DatabaseViewController;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;

@Slf4j
public class App extends Application {


  @Override
  public void init() {
    log.info("Starting Up");
  }

  @Override
  public void start(Stage primaryStage) throws IOException {

    Parent root = FXMLLoader.load(getClass().getResource("Display.fxml"));
    primaryStage.setTitle("Startup Window");
    primaryStage.setScene(new Scene(root));
    primaryStage.show();

  }

  @Override
  public void stop() {
    log.info("Shutting Down");
  }
}
