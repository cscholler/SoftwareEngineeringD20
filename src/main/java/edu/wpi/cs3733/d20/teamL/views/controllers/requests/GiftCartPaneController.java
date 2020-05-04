package edu.wpi.cs3733.d20.teamL.views.controllers.requests;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXScrollPane;
import edu.wpi.cs3733.d20.teamL.entities.Gift;
import edu.wpi.cs3733.d20.teamL.services.db.IDatabaseCache;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import javax.inject.Inject;
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
        gifts = cache.getGiftCache();

        for (Gift gift : gifts) { loadImage(gift.getSubtype()); }

        int i = 0;
        while (i < gifts.size() - 1) {
            String currentGiftTabType = gifts.get(i).getType();
            Tab tab = new Tab();
            tab.setText(gifts.get(i).getType());
            tab.setClosable(false);

            JFXScrollPane scrollPane = new JFXScrollPane();
            VBox vBox = new VBox();

            while (gifts.get(i).getType().equals(currentGiftTabType)) {
                int numColumns = 0;
                HBox giftRow = new HBox();
                while (numColumns < 3 && gifts.get(i).getType().equals(currentGiftTabType)){
                    giftRow.getChildren().add(makeGift(gifts.get(i), i));

                    numColumns++;
                    if (i < gifts.size() - 1) i++;
                    else break;
                }
                vBox.getChildren().add(giftRow);
                if (i >= gifts.size() - 1) break;
            }
            scrollPane.setContent(vBox);
            tab.setContent(scrollPane);
            giftTabPane.getTabs().add(tab);
            }

        }

    private VBox makeGift(Gift gift, int i) {
        VBox vbox = new VBox();
        vbox.setAlignment(Pos.TOP_CENTER);
        vbox.prefWidth(300);
        vbox.prefHeight(375);

        ImageView imageView = new ImageView(new Image("/edu/wpi/cs3733/d20/teamL/assets/gift_Delivery/noImage.png", 0, 200, true, false, true));
        imageView.setImage(images.get(i));
        VBox imageVBox = new VBox(imageView);
        imageVBox.setAlignment(Pos.TOP_CENTER);
        imageVBox.setMargin(imageView, new Insets(0,0,30,0));

        Label item = new Label("Item: ");
        Label giftName = new Label(gift.getSubtype());
        HBox nameHBox = new HBox(item,giftName);
        nameHBox.setAlignment(Pos.CENTER_LEFT);

        Label quantity = new Label("Amount in stock: ");
        Label giftQuantity = new Label(gift.getInventory());
        HBox quantityHBox = new HBox(quantity,giftQuantity);
        quantityHBox.setAlignment(Pos.CENTER_LEFT);

        Label description = new Label("Description: ");
        Label giftDescription = new Label(gift.getDescription());
        giftDescription.setWrapText(true);
        VBox descriptionVBox = new VBox(description,giftDescription);

        EventHandler<ActionEvent> addToCart = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e)
            {
                System.out.println(giftName);
            }
        };

        JFXButton addToCartButton = new JFXButton("Add to Cart");
        addToCartButton.getStyleClass().add("save-button-jfx");
        addToCartButton.setOnAction(addToCart);

        vbox.getChildren().addAll(imageView,nameHBox,quantityHBox,descriptionVBox,addToCartButton);

        return vbox;
    }


    private void loadImage(String subType) {
        Image image;
        try {
            image = new Image("/edu/wpi/cs3733/d20/teamL/assets/gift_Delivery/" + subType + ".png", 0, 200, true, false, true);
        } catch (Exception e) {
            image = new Image("/edu/wpi/cs3733/d20/teamL/assets/gift_Delivery/noImage.png", 0, 200, true, false, true);
        }
        images.add(image);
    }
}
