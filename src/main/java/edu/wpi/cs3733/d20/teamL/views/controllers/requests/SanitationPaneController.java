package edu.wpi.cs3733.d20.teamL.views.controllers.requests;

import javafx.scene.input.MouseEvent;

import java.io.IOException;

public class SanitationPaneController {

    public void closePane(MouseEvent mouseEvent) throws IOException {
        UserLandingController ulc = new UserLandingController();
        ulc.launchDefaultPane();
    }
}
