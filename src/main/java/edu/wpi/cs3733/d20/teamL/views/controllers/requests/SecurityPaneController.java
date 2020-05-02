package edu.wpi.cs3733.d20.teamL.views.controllers.requests;

import com.jfoenix.controls.JFXTextField;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyEvent;

import java.io.IOException;

public class SecurityPaneController {
    @FXML
    ToggleGroup urgency = new ToggleGroup();

    @FXML
    JFXTextField personnelText;

    public void initialize() throws IOException {
        // restrict key input to numerals on personnel needed textfield
        personnelText.addEventFilter(KeyEvent.KEY_TYPED, keyEvent -> {
            if (!"0123456789".contains(keyEvent.getCharacter())) {
                keyEvent.consume();
            }

//            if(Integer.parseInt(personnelText.getText()) > 13 || Integer.parseInt(personnelText.getText()) < 1) {
//                keyEvent.consume();
//            }

        });










    }
}
