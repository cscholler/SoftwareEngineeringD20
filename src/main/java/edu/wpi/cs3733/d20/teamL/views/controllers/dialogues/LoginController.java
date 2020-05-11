package edu.wpi.cs3733.d20.teamL.views.controllers.dialogues;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Base64;

import com.github.sarxos.webcam.Webcam;
import com.squareup.okhttp.*;
import edu.wpi.cs3733.d20.teamL.services.IHTTPClientService;
import javafx.animation.FadeTransition;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import com.google.inject.Inject;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;

import edu.wpi.cs3733.d20.teamL.services.users.ILoginManager;
import edu.wpi.cs3733.d20.teamL.util.FXMLLoaderFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.imageio.ImageIO;

@Slf4j
public class LoginController {
    FXMLLoaderFactory loaderHelper = new FXMLLoaderFactory();
    @FXML
    private JFXTextField usernameField;
    @FXML
    private JFXPasswordField passwordField;
    @FXML
    private JFXButton login, btnCancel;
    @FXML
    private Text incorrectText;
    @FXML
    private AnchorPane anchorPane;
    @Inject
    private ILoginManager loginManager;
    @Inject
    private IHTTPClientService clientService;

    /**
     * logs the user in when the enter key is pressed
     *
     * @param event Tracks which key is pressed
     */
    @FXML
    private void enterHandle(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            //"presses" the login button
            login.fire();
        }
    }

    /**
     * Dummy function to allow enter to be pressed from the password box
     *
     * @throws
     */
    @FXML
    private void bugfix() {
    }

    /**
     * Controls the login feature setting usernames and passwords and only accepting correct usernames and passwords
     *
     * @param event Tracks which button is pressed
     * @throws IOException
     */
    @FXML
    private void handleLogin(ActionEvent event) throws IOException {


        Stage stage;

        String username = usernameField.getText();
        String password = passwordField.getText();
        incorrectText.setVisible(false);

        //set up flashing text
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(200), anchorPane);
        fadeTransition.setFromValue(0.5);
        fadeTransition.setToValue(1.0);
        fadeTransition.setCycleCount(1);

        //closes login popup
        if (event.getSource() == btnCancel) {
            stage = (Stage) btnCancel.getScene().getWindow();
            stage.close();


//            log.info(response.code()+"");
//            log.info(response.body().string());
        } else if (event.getSource() == login) {
            loginManager.logIn(username, password);
            if (loginManager.isAuthenticated()) {
                ((Stage) login.getScene().getWindow()).close();
                String view;
                if (loginManager.getCurrentUser().getAcctType().equals("3")) {
                    view = "admin/AdminView";
                } else {
                    view = "requests/UserLandingPage";
                }
                loaderHelper.setupScene(new Scene(loaderHelper.getFXMLLoader(view).load()));
            } else {
                incorrectText.setText("Invalid username or password");
                incorrectText.setVisible(true);
                fadeTransition.play();
            }
            usernameField.clear();
            passwordField.clear();

        }
    }

    @FXML
    private void facialRecognition() throws IOException {

        FadeTransition fadeTransition = new FadeTransition(Duration.millis(200), anchorPane);
        String loginUser = null;
        double confidence = 0;
        Webcam webcam = Webcam.getDefault();
        webcam.open();
        BufferedImage image = webcam.getImage();
        webcam.close();

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ImageIO.write(image, "PNG", bos);
        byte[] fileContent = bos.toByteArray();
        String encodedString = Base64.getEncoder().encodeToString(fileContent);

        JSONObject json = new JSONObject();
        json.put("image", encodedString);
        json.put("gallery_name", "users");


        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, json.toString());
        log.info(json.toString());

        Request request = new Request.Builder()
                .url("https://kairosapi-karios-v1.p.rapidapi.com/recognize")
                .post(body)
                .addHeader("x-rapidapi-host", "kairosapi-karios-v1.p.rapidapi.com")
                .addHeader("x-rapidapi-key", "e9d19d8ab2mshee9ab7d6044378bp106222jsnc2e9a579d919")
                .addHeader("content-type", "application/json")
                .addHeader("accept", "application/json")
                .build();

        Response response = clientService.getClient().newCall(request).execute();

        String result = response.body().string();

        try{
        JSONObject obj = new JSONObject(result);
        JSONArray arr = obj.getJSONArray("images");
        for (int i = 0; i < arr.length(); i++) {
            JSONObject find = arr.getJSONObject(i);
            JSONObject transaction = find.getJSONObject("transaction");
            loginUser = transaction.getString("subject_id");
            confidence = transaction.getDouble("confidence");
            System.out.println(loginUser);
            System.out.println(confidence);
        }
        } catch(org.json.JSONException e){
            incorrectText.setText("Invalid photo");
            incorrectText.setVisible(true);
            fadeTransition.play();
            }

        if (!(loginUser == null)) {
            loginManager.logInFR(loginUser, confidence);
            if (loginManager.isAuthenticated()) {
            ((Stage) login.getScene().getWindow()).close();
                String view;
                if (loginManager.getCurrentUser().getAcctType().equals("3")) {
                    view = "admin/AdminView";
                } else {
                    view = "requests/UserLandingPage";
                }
                loaderHelper.setupScene(new Scene(loaderHelper.getFXMLLoader(view).load()));
            } else {
                incorrectText.setText("No user found");
                incorrectText.setVisible(true);
                fadeTransition.play();
            }
        }
    }
}
