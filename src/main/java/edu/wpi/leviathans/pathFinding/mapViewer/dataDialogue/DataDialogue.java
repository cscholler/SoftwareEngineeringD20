package edu.wpi.leviathans.pathFinding.mapViewer.dataDialogue;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.File;

public class DataDialogue {

    public DataDialogueController controller;

    Parent root;
    Scene scene;
    Stage stage;

    File nodeFile;
    File edgeFile;

    private boolean hasInit = false;

    public DataDialogue() {}

    public DataDialogue(Window owner) {
        init(owner);
    }

    public void showDialogue(Window owner) {
        init(owner);

        stage.showAndWait();
    }

    public void showDialogue() {
        if(!hasInit) {
            try{
                throw new Exception("init method was never called");
            } catch(Exception e) {
                e.printStackTrace();
            }
        }

        stage.showAndWait();
    }

    public File getNodeFile() {
        return nodeFile;
    }

    public File getEdgeFile() {
        return edgeFile;
    }

    public void init(Window owner) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("DataDialogue.fxml"));
        try {
            root = fxmlLoader.load();
        } catch (Exception e) {
            e.printStackTrace();
        }
        scene = root.getScene();

        stage = new Stage();
        stage.setTitle("Pick csv files for data");
        stage.setScene(new Scene(root, 500, 230));
        stage.initOwner(owner);
        stage.initModality(Modality.APPLICATION_MODAL);

        controller = fxmlLoader.getController();
        controller.owner = this;
    }

}
