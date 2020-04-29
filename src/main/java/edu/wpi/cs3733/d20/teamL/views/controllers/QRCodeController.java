package edu.wpi.cs3733.d20.teamL.views.controllers;

import com.google.inject.Inject;
import com.google.zxing.WriterException;
import com.jfoenix.controls.JFXButton;
import edu.wpi.cs3733.d20.teamL.services.messaging.IMessengerService;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

@Slf4j
public class QRCodeController implements Initializable {
    @FXML
    ImageView qrImage;

    @FXML
    JFXButton btnCancel;
    @Inject
    private IMessengerService messenger;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
       try {

           messenger.getQRCodeImage(messenger.getDirections());
           Image image = SwingFXUtils.toFXImage(messenger.getQRCodeImage(messenger.getDirections()), null);
           qrImage.setImage(image);
           //TODO make it actual directions
       } catch (IOException | WriterException e){
           log.info("Generation Failed");
        }

       }

    @FXML
    void cancelClicked() {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }
}
