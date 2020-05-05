package edu.wpi.cs3733.d20.teamL.views.controllers.requests;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import edu.wpi.cs3733.d20.teamL.entities.Gift;
import edu.wpi.cs3733.d20.teamL.services.db.DBConstants;
import edu.wpi.cs3733.d20.teamL.services.db.IDatabaseCache;
import edu.wpi.cs3733.d20.teamL.services.db.IDatabaseService;
import edu.wpi.cs3733.d20.teamL.services.db.SQLEntry;
import edu.wpi.cs3733.d20.teamL.services.users.ILoginManager;
import edu.wpi.cs3733.d20.teamL.util.FXMLLoaderFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.ImageView;
import javafx.util.Callback;

import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.*;

public class GiftCheckoutPaneController {
    private Map<String,Integer> cart = new HashMap<>();

    @Inject
    private IDatabaseCache cache;
    private FXMLLoaderFactory loaderHelper = new FXMLLoaderFactory();
    @Inject
    private IDatabaseService db;
    @Inject
    private ILoginManager loginManager;
    @FXML
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
    public void initialize() {
        cart = cache.getCartCache();
        ObservableList<GiftDetails> giftDetailsObservableList = FXCollections.observableArrayList();
        requestReceived.setPickOnBounds(false);

        giftColumn.setCellValueFactory(
                new PropertyValueFactory<GiftDetails, String>("name"));
        qtyColumn.setCellValueFactory(new PropertyValueFactory<GiftDetails, TextField>("qty"));

        removeColumn.setCellValueFactory(new PropertyValueFactory<>("DUMMY"));
        Callback<TableColumn<GiftDetails, String>, TableCell<GiftDetails, String>> cellFactory = new Callback<>() {
                    @Override
                    public TableCell call(final TableColumn<GiftDetails, String> param) {
                        final TableCell<GiftDetails, String> cell = new TableCell<>() {
                            final Button btn = new Button("X");
                            {
                                btn.setOnAction(event -> {
                                    GiftDetails deletedItem = (GiftDetails) orderTable.getItems().get(getIndex());
                                    orderTable.getItems().remove(getIndex());
                                    cart.remove(deletedItem.getName());
                                });
                            }

                            @Override
                            public void updateItem(String item, boolean empty) {
                                super.updateItem(item, empty);
                                if (empty) {
                                    setGraphic(null);
                                } else {
                                    setGraphic(btn);
                                }
                                setText(null);
                            }
                        };
                        return cell;
                    }
                };

        removeColumn.setEditable(true);
        orderTable.setEditable(true);

        for (String giftType : cart.keySet()) {
            GiftDetails gd = new GiftDetails(giftType,cart.get(giftType));
            giftDetailsObservableList.add(gd);
        }

        orderTable.setItems(giftDetailsObservableList);
        removeColumn.setCellFactory(cellFactory);
    }

    public void placeOrder(ActionEvent actionEvent) {
        String firstName = firstNameText.getText();
        String lastName = lastNameText.getText();
        String patientID = "";
        String sender = senderText.getText();
        String deliveryInstructions = additionalNotesText.getText();
        String specialMessage = specialMessageText.getText();
        StringBuilder gifts = new StringBuilder();
        String status = "0";
        String dateAndTime = new SimpleDateFormat("MM-dd-yyyy hh:mm:ss").format(new Date());

        cart = cache.getCartCache();
        int index = 0;
        for(String giftType : cart.keySet()) {
            int inv = cart.get(giftType);
            gifts.append("(" + inv + "x) " + giftType + (cart.keySet().size()-1 != index ? ", " : "."));
            index ++;
        }

        boolean validFields = true;

        if(db.getTableFromResultSet(db.executeQuery(new SQLEntry(DBConstants.GET_PATIENT_ID, new ArrayList<>(Arrays.asList(firstName, lastName))))).size() == 0) {
            firstNameText.setStyle("-fx-prompt-text-fill: RED");
            lastNameText.setStyle("-fx-prompt-text-fill: RED");
            validFields = false;
        } else {
            firstNameText.setStyle("-fx-prompt-text-fill: GRAY");
            lastNameText.setStyle("-fx-prompt-text-fill: GRAY");
            patientID = db.getTableFromResultSet(db.executeQuery(new SQLEntry(DBConstants.GET_PATIENT_ID, new ArrayList<>(Arrays.asList(patientFName, patientLName))))).get(0).get(0);
        }
        if (gifts.toString().length() < 2) {
            validFields = false;
        }


        int rows = 0;
        if(validFields) rows = db.executeUpdate(new SQLEntry(DBConstants.ADD_GIFT_DELIVERY_REQUEST, new ArrayList<>(Arrays.asList(patientID, sender, loginManager.getCurrentUser().getUsername(), null,
               gifts.toString(), specialMessage, deliveryInstructions, status, dateAndTime))));

        if(rows == 0) {
            confirmation.setVisible(true);
            confirmation.setTextFill(Color.RED);
            confirmation.setText("Submission failed");
        } else {
            confirmation.setVisible(true);
            confirmation.setTextFill(Color.WHITE);
            confirmation.setText("");
            cache.updateInventory();

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

        public void setQty(Integer qty) {
            this.qty = qty;
        }
    }
}
