package edu.wpi.cs3733.d20.teamL.views.dialogues;

import java.io.File;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

public class DataDialogue {

    public DataDialogueController controller;

    Parent root;
    Scene scene;
    Stage stage;

    File nodeFile;
    File edgeFile;

    private boolean hasInit = false;

    public DataDialogue() {
    }

    public DataDialogue(Window owner) {
        init();
        setOwner(owner);
    }

    /**
     * Shows the dialogue to get the node and edge csv files. Returns whether any data has been imported. Automatically calls the init() function and sets the owner.
     *
     * @param owner The window that is to be the owner of this dialogue
     * @return {true} If the user clicked 'OK' with some data loaded {false} if the user either confirmed with no data loaded or pressed 'Cancel'
     */
    public boolean showDialogue(Window owner) {
        if(!hasInit) {
            init();

            setOwner(owner);
        }

        stage.showAndWait();

        return nodeFile != null || edgeFile != null;
    }

    /**
     * Shows the dialogue to get the node and edge csv files. Returns whether any data has been imported. Automatically calls the init() function and sets the owner.
     * init() and setOwner(owner) must be called if you do not provide parameters.
     *
     * @return {true} If the user clicked 'OK' with some data loaded {false} if the user either confirmed with no data loaded or pressed 'Cancel'
     */
    public void showDialogue() {
        if (!hasInit) {
            try {
                throw new Exception("init method was never called");
            } catch (Exception e) {
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

    public void init() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("DataDialogue.fxml"));
        try {
            root = fxmlLoader.load();
        } catch (Exception e) {
            e.printStackTrace();
        }
        scene = root.getScene();

        stage = new Stage();
        stage.setTitle("Pick csv files for data");
        stage.setScene(new Scene(root));
        stage.initModality(Modality.APPLICATION_MODAL);
        controller = fxmlLoader.getController();
        controller.owner = this;

        hasInit = true;
    }

    public void setOwner(Window owner) {
        stage.initOwner(owner);
    }

}
