package edu.wpi.cs3733.d20.teamL.views.controllers.requests;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import edu.wpi.cs3733.d20.teamL.services.db.DBConstants;
import edu.wpi.cs3733.d20.teamL.services.db.IDatabaseCache;
import edu.wpi.cs3733.d20.teamL.services.db.IDatabaseService;
import edu.wpi.cs3733.d20.teamL.services.db.SQLEntry;
import edu.wpi.cs3733.d20.teamL.services.users.ILoginManager;
import edu.wpi.cs3733.d20.teamL.util.FXMLLoaderHelper;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.control.Label;

import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.*;

public class GiftCheckoutPaneController {
    private Map<String,Integer> cart = new HashMap<>();

    @Inject
    private IDatabaseCache cache;
    private FXMLLoaderHelper loaderHelper = new FXMLLoaderHelper();
    @Inject
    private IDatabaseService db;
    @Inject
    private ILoginManager loginManager;
    public ImageView requestReceived;
    @FXML
    private TableView orderTable;
    @FXML
    private TableColumn giftColumn, qtyColumn, removeColumn;
    @FXML
    private JFXTextField firstNameText, lastNameText, senderText;
    @FXML
    private JFXTextArea additionalNotesText, specialMessageText;
    @FXML
    private Label confirmation;

    @FXML
    public void initialize() {
        cart = cache.getCartCache();
        ObservableList<GiftDetails> giftDetailsObservableList = FXCollections.observableArrayList();

        giftColumn.setCellValueFactory(
                new PropertyValueFactory<GiftDetails, String>("name"));
        qtyColumn.setCellValueFactory(
                new PropertyValueFactory<GiftDetails, Integer>("qty"));

        for (String giftType : cart.keySet()) {
            GiftDetails gd = new GiftDetails(giftType,cart.get(giftType));
            giftDetailsObservableList.add(gd);
        }

        orderTable.setItems(giftDetailsObservableList);
    }

    public void placeOrder(ActionEvent actionEvent) {
        String firstName = firstNameText.getText();
        String lastName = lastNameText.getText();
        String patientID = db.getTableFromResultSet(db.executeQuery(new SQLEntry(DBConstants.GET_PATIENT_ID, new ArrayList<>(Arrays.asList(firstName, lastName))))).get(0).get(0);
        String sender = senderText.getText();
        String deliveryInstructions = additionalNotesText.getText();
        String specialMessage = specialMessageText.getText();
        StringBuilder gifts = new StringBuilder();
        String status = "0";
        String dateAndTime = new SimpleDateFormat("MM-dd-yyyy hh:mm:ss").format(new Date());

        cart = cache.getCartCache();
        int index = 0;
        for(String giftType : cart.keySet()) {
            index ++;
            int inv = cart.get(giftType);
            gifts.append("(" + inv + "x) " + giftType + (cart.keySet().size()-1 != index ? ", " : "."));
        }
        cache.updateInventory();

        int rows = db.executeUpdate(new SQLEntry(DBConstants.ADD_GIFT_DELIVERY_REQUEST, new ArrayList<>(Arrays.asList(patientID, sender, loginManager.getCurrentUser().getUsername(), null,
               gifts.toString(), specialMessage, deliveryInstructions, status, dateAndTime))));

        if(rows == 0) {
//            confirmation.setVisible(true);
//            confirmation.setTextFill(Color.RED);
//            confirmation.setText("Submission failed");
        } else {
//            confirmation.setVisible(true);
//            confirmation.setTextFill(Color.WHITE);
//            confirmation.setText("");

            firstNameText.setText("");
            lastNameText.setText("");
            senderText.setText("");
            additionalNotesText.setText("");
            specialMessageText.setText("");

            loaderHelper.showAndFade(requestReceived);
        }

//        loaderHelper.showAndFade(confirmation);
    }

    public class GiftDetails {
        private String name;
        private Integer qty;

        private GiftDetails(String name, Integer qty) {
            this.name = name;
            this.qty = qty;
        }

        public String getName() {
            return name;
        }

        public Integer getQty() {
            return qty;
        }
    }
}
