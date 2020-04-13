package edu.wpi.leviathans;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class App extends Application {

	@Override
	public void init() {
		log.info("Starting Up");
	}

        @FXML private Button btnBack;
        @FXML private Button btnDisplay;
        @FXML private Button btnModify;
        @FXML private Button btnDownload;
        @FXML private Button btnDemonstration;

  @FXML
  private void handleButtonAction(ActionEvent e) throws IOException {

    Stage stage;
    Parent root;

    if (e.getSource() == btnDisplay) {

      stage = (Stage) btnDisplay.getScene().getWindow();
      root = FXMLLoader.load(getClass().getResource("Display.fxml"));

    } else if (e.getSource() == btnModify) {

      stage = (Stage) btnModify.getScene().getWindow();
      root = FXMLLoader.load(getClass().getResource("Modify.fxml"));


    } else if (e.getSource() == btnDownload) {

      stage = (Stage) btnDownload.getScene().getWindow();
      root = FXMLLoader.load(getClass().getResource("Download.fxml"));

    } else if (e.getSource() == btnDemonstration) {

      stage = (Stage) btnDemonstration.getScene().getWindow();
      root = FXMLLoader.load(getClass().getResource("Demonstration.fxml"));

    } else {

      stage = (Stage) btnBack.getScene().getWindow();
      root = FXMLLoader.load(getClass().getResource("Display.fxml"));

    }

    Scene scene = new Scene(root);
    stage.setScene(scene);
    stage.show();

  }

  @Override
  public void start(Stage primaryStage) throws IOException {

    Parent root = FXMLLoader.load(getClass().getResource("Window.fxml"));
    primaryStage.setTitle("Startup Window");
    primaryStage.setScene(new Scene(root, 600, 400));
    primaryStage.show();

  }
	@Override
	public void stop() {
		log.info("Shutting Down");
	}
}
