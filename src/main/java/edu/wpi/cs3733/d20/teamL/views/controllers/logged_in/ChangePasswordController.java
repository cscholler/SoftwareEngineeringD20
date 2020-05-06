package edu.wpi.cs3733.d20.teamL.views.controllers.logged_in;


import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import edu.wpi.cs3733.d20.teamL.entities.User;
import edu.wpi.cs3733.d20.teamL.services.db.DBConstants;
import edu.wpi.cs3733.d20.teamL.services.db.IDatabaseService;
import edu.wpi.cs3733.d20.teamL.services.db.SQLEntry;
import edu.wpi.cs3733.d20.teamL.services.users.ILoginManager;
import edu.wpi.cs3733.d20.teamL.services.users.PasswordManager;
import edu.wpi.cs3733.d20.teamL.util.FXMLLoaderFactory;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

@Slf4j
public class ChangePasswordController {
    @FXML
    private JFXPasswordField newPasswordText, confirmPasswordText, oldPassText;
    @FXML
    private JFXButton btnCancel, btnConfirm;
    @FXML
    private JFXTextField usernameText;
    @FXML
    private Label incorrectPassword;
    private User user;
    private boolean isAuthenticated = false;
    @Inject
    private IDatabaseService db;
    FXMLLoaderFactory loader = new FXMLLoaderFactory();

    @FXML
    public void handleChangePassword(ActionEvent event) {
        Stage stage;
            stage = (Stage) btnCancel.getScene().getWindow();
            stage.close();
    }

    @FXML
    public void confirmClicked() {
        String un = usernameText.getText();
        String oldPW = oldPassText.getText();
        String newPW = newPasswordText.getText();
        if (newPW.equals(confirmPasswordText.getText())){
            ArrayList<ArrayList<String>> results = db.getTableFromResultSet(db.executeQuery(new SQLEntry(DBConstants.GET_USER, new ArrayList<>(Collections.singletonList(un)))));
            if (results.size() == 1) {
                ArrayList<String> userInfo = results.get(0);
                user = new User(userInfo.get(0), userInfo.get(1), userInfo.get(2), userInfo.get(3), userInfo.get(5), userInfo.get(6), userInfo.get(7));
                try {
                    isAuthenticated = PasswordManager.isPasswordCorrect(oldPW, userInfo.get(4));
                } catch (IllegalArgumentException ex) {
                    isAuthenticated = false;
                }
                if (isAuthenticated) {
                    db.executeUpdate(new SQLEntry(DBConstants.UPDATE_USER_PASSWORD, new ArrayList<>(Arrays.asList(PasswordManager.hashPassword(newPW), user.getID()))));
                    incorrectPassword.setTextFill(Color.BLACK);
                    incorrectPassword.setText("Password Updated");
                    usernameText.setText("");
                    oldPassText.setText("");
                    newPasswordText.setText("");
                    confirmPasswordText.setText("");

                } else {
                    incorrectPassword.setTextFill(Color.RED);
                    incorrectPassword.setText("Incorrect username or password");
                }
            } else {
                // No user found
                log.warn("No user found with the given username and password");
                incorrectPassword.setTextFill(Color.RED);
                incorrectPassword.setText("Incorrect username or password");
            }
        }else{
            incorrectPassword.setTextFill(Color.RED);
            incorrectPassword.setText("New passwords did not match");
        }
        loader.showAndFade(incorrectPassword);
    }
}
