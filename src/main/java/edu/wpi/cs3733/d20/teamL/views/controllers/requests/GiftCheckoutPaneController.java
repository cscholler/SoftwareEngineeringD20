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
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.control.skin.TableHeaderRow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.util.Callback;
import org.apache.commons.math3.util.Precision;

import javax.inject.Inject;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class GiftCheckoutPaneController {
    private Map<String, Integer> cart = new HashMap<>();
    private double totalCost = 0;

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
    private TableColumn giftColumn, qtyColumn, costColumn, removeColumn;
    @FXML
    private JFXTextField firstNameText, lastNameText, senderText;
    @FXML
    private JFXTextArea additionalNotesText, specialMessageText;
    @FXML
    private Label confirmation, orderTxt, totalCostLbl, orderSubmitted;

    @FXML
    public void initialize() {
        cart = cache.getCartCache();
        ObservableList<GiftDetails> giftDetailsObservableList = FXCollections.observableArrayList();
        //requestReceived.setPickOnBounds(false);

        NumberFormat formatter = NumberFormat.getCurrencyInstance();

        giftColumn.setCellValueFactory(new PropertyValueFactory<GiftDetails, String>("name"));
        qtyColumn.setCellValueFactory(new PropertyValueFactory<GiftDetails, TextField>("qty"));
        costColumn.setCellValueFactory(new PropertyValueFactory<GiftDetails, TextField>("cost"));

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

                            totalCost -= deletedItem.getCostAsDouble();
                            Precision.round(totalCost,-2);
                            totalCostLbl.setText(formatter.format(totalCost));
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

        ArrayList<Gift> allGifts = cache.getGiftCache();
        for (Gift gift : allGifts) {
            if (cart.containsKey(gift.getSubtype())) {
                int qty = cart.get(gift.getSubtype());
                double cost = gift.getCost() * qty;
                GiftDetails gd = new GiftDetails(gift.getSubtype(), qty, gift.costToString(qty));
                giftDetailsObservableList.add(gd);
                totalCost += cost;
            }
        }

        Precision.round(totalCost,-2);
        totalCostLbl.setText(formatter.format(totalCost));

        orderTable.setItems(giftDetailsObservableList);
        removeColumn.setCellFactory(cellFactory);

        //removes header
        orderTable.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                Pane header = (Pane) orderTable.lookup("TableHeaderRow");
                if (header!= null && header.isVisible()){
                    header.setMaxHeight(1);
                    header.setMinHeight(0);
                    header.setPrefHeight(1);
                    header.setVisible(false);
                    header.setManaged(false);
                }

            }
        });
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
        for (String giftType : cart.keySet()) {
            int inv = cart.get(giftType);
            gifts.append("(" + inv + "x) " + giftType + (cart.keySet().size() - 1 != index ? ", " : "."));
            index++;
        }

        boolean validFields = true;

        if (db.getTableFromResultSet(db.executeQuery(new SQLEntry(DBConstants.GET_PATIENT_ID, new ArrayList<>(Arrays.asList(firstName, lastName))))).size() == 0) {
            firstNameText.setStyle("-fx-prompt-text-fill: RED");
            lastNameText.setStyle("-fx-prompt-text-fill: RED");
            validFields = false;
        } else {
            firstNameText.setStyle("-fx-prompt-text-fill: GRAY");
            lastNameText.setStyle("-fx-prompt-text-fill: GRAY");
            patientID = db.getTableFromResultSet(db.executeQuery(new SQLEntry(DBConstants.GET_PATIENT_ID, new ArrayList<>(Arrays.asList(firstName, lastName))))).get(0).get(0);
        }

        int rows = 0;
        if (validFields)
            rows = db.executeUpdate(new SQLEntry(DBConstants.ADD_GIFT_DELIVERY_REQUEST, new ArrayList<>(Arrays.asList(patientID, sender, loginManager.getCurrentUser().getUsername(), null,
                    gifts.toString(), specialMessage, deliveryInstructions, status, dateAndTime))));

        if (rows == 0) {
            confirmation.setVisible(true);
            confirmation.setStyle("-fx-text-fill: RED");
            confirmation.setText("Submission failed");
        } else {
            confirmation.setVisible(true);
            confirmation.setStyle("-fx-text-fill: WHITE");
            confirmation.setText("");
            cache.updateInventory();

            firstNameText.setText("");
            lastNameText.setText("");
            senderText.setText("");
            additionalNotesText.setText("");
            specialMessageText.setText("");

            totalCostLbl.setText("");
            orderSubmitted.setVisible(true);
            cart.clear();
        }

//        loaderHelper.showAndFade(confirmation);
    }

    public class GiftDetails {
        private String name;
        private Integer qty;
        private String cost;

        private GiftDetails(String name, Integer qty, String cost) {
            this.name = name;
            this.qty = qty;
            this.cost = cost;
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

        public String getCost() {
            return cost;
        }

        public double getCostAsDouble() {
            return Double.parseDouble(cost.substring(1));
        }
    }
}
