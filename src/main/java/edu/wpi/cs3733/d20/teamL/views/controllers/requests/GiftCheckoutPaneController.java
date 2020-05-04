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

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class GiftCheckoutPaneController {
    private Map<String,Integer> cart = new HashMap<>();

    @Inject
    private IDatabaseCache cache;
    private FXMLLoaderHelper loaderHelper = new FXMLLoaderHelper();
    private IDatabaseService db;
    private ILoginManager loginManager;
    @FXML
    private TableView orderTable;
    @FXML
    private TableColumn giftColumn, qtyColumn, removeColumn;
    @FXML
    private JFXTextField firstNameText, lastNameText, senderText;
    @FXML
    private JFXTextArea additionalNotesText, specialMessageText;

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

        //TODO: Set up the database so this will actually work
//        int rows = db.executeUpdate((new SQLEntry(DBConstants.ADD_GIFT_DELIVERY_REQUEST,
//                new ArrayList<>(Arrays.asList(patientID,sender,loginManager.getCurrentUser().getUsername(),null,)))))
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
