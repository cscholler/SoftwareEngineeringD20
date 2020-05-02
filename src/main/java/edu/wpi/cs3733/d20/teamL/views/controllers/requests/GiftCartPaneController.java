package edu.wpi.cs3733.d20.teamL.views.controllers.requests;

import com.jfoenix.controls.JFXButton;
import edu.wpi.cs3733.d20.teamL.entities.Gift;
import edu.wpi.cs3733.d20.teamL.services.db.IDatabaseCache;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import javax.inject.Inject;
import javax.swing.text.Element;
import javax.swing.text.html.ImageView;
import java.util.ArrayList;

public class GiftCartPaneController {
    private ArrayList<Gift> gifts = new ArrayList<>();
    private ArrayList<Image> images = new ArrayList<>();

    @Inject
    IDatabaseCache cache;
    @FXML
    private TabPane giftTabPane;

    @FXML
    public void initialize() {
        cache.cacheGiftsFromDB();
        gifts = cache.getGiftsCache();

        for (Gift gift : gifts) { loadImage(gift.getSubtype()); }

        String previousGiftType = gifts.get(0).getType();

        for (int i = 0; i < gifts.size(); i++) {
            Tab tab = new Tab();
            tab.setText(gifts.get(i).getType());
            while (gifts.get(i).getType().equals(previousGiftType)) {

            }


                giftTabPane.getTabs().add(tab);
            }

        }

    private void addGift(Gift gift, int i) {
        VBox vbox = new VBox();
        vbox.setAlignment(Pos.TOP_CENTER);
        vbox.prefWidth(300);
        vbox.prefHeight(375);

        ImageView imageView = new ImageView(images.get(i)); //this doesn't see right
        VBox imageVBox = new VBox(imageView);
        imageVBox.setAlignment(Pos.TOP_CENTER);
        imageVBox.setMargin(imageView, new Insets(0,0,30,0));

        Label item = new Label("Item: ");
        Label giftName = new Label(gift.getSubtype());
        HBox nameHBox = new HBox(item,giftName);
        nameHBox.setAlignment(Pos.CENTER_LEFT);

        Label quantity = new Label("Quantity: ");
        Label giftQuantity = new Label(gift.getInventory());
        HBox quantityHBox = new HBox(quantity,giftQuantity);
        quantityHBox.setAlignment(Pos.CENTER_LEFT);

        Label description = new Label("Description: ");
        Label giftDescription = new Label(gift.getDescription());
        giftDescription.setWrapText(true);
        VBox descriptionVBox = new VBox(description,giftDescription);

        JFXButton addToCart = new JFXButton("Add to Cart");
        addToCart.getStyleClass().add("save-button-jfx");

        vbox.getChildren().addAll(imageView,nameHBox,quantityHBox,descriptionVBox,addToCart);

    }


    private void loadImage(String subType) {
        Image image;
        try {
            image = new Image("/edu/wpi/cs3733/d20/teamL/assets/giftDelivery/" + subType + ".png", 0, 200, true, false, true);
        } catch (Exception e) {
            image = new Image("/edu/wpi/cs3733/d20/teamL/assets/giftDelivery/noImage.png", 0, 200, true, false, true);
        }
        images.add(image);
    }
}
