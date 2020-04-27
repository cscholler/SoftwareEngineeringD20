package edu.wpi.cs3733.d20.teamL.views.controllers;

import com.google.inject.Inject;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.encoder.QRCode;
import com.jfoenix.controls.JFXButton;
import edu.wpi.cs3733.d20.teamL.services.IMessengerService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ResourceBundle;




@Slf4j
public class QRCodeController implements Initializable {
    @FXML
    ImageView qrImage;

    @FXML
    JFXButton btnCancel;
    @Inject
    private IMessengerService messenger;


    @SneakyThrows
    @Override
    public void initialize(URL location, ResourceBundle resources) {
       try {
           generateQRCodeImage("Directions");
           //TODO make it actual directions

       } catch (IOException | WriterException e){
           log.info("Generation Failed");
        }
        QRCodeWriter qrCodeWriter = new QRCodeWriter();

        FileInputStream input = new FileInputStream("src/main/resources/edu/wpi/cs3733/d20/teamL/assets/QRCode.png");
        Image image = new Image(input);
       qrImage.setImage(image);
       }

    private static void generateQRCodeImage(String text)
            throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, 1000, 1000);

        Path path = FileSystems.getDefault().getPath("src/main/resources/edu/wpi/cs3733/d20/teamL/assets/QRCode.png");
        MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);
    }

    @FXML
    void cancelClicked() {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }
}
