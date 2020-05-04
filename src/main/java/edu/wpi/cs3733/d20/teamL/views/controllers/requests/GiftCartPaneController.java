package edu.wpi.cs3733.d20.teamL.views.controllers.requests;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXScrollPane;
import edu.wpi.cs3733.d20.teamL.entities.Gift;
import edu.wpi.cs3733.d20.teamL.services.db.IDatabaseCache;
import edu.wpi.cs3733.d20.teamL.util.FXMLLoaderFactory;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GiftCartPaneController {
    private ArrayList<Gift> gifts = new ArrayList<>();
    private ArrayList<Image> images = new ArrayList<>();
    Map<String, Integer> cart = new HashMap<String, Integer>();

    @Inject
    private IDatabaseCache cache;
    private FXMLLoaderFactory loaderHelper = new FXMLLoaderFactory();
    @FXML
    private TabPane giftTabPane;
    @FXML
    private StackPane stackPane;
    @FXML
    private AnchorPane checkoutPane;
    @FXML
    private ImageView addedToCart, outOfStock;

    @FXML
    public void initialize() {
        cache.cacheGiftsFromDB();
        gifts = cache.getGiftsCache();

        checkoutPane.setPickOnBounds(false);

        addedToCart = new ImageView(new Image("/edu/wpi/cs3733/d20/teamL/assets/gift_Delivery/addToCartIcon.png", 0,150,true,false,true));
        stackPane.getChildren().add(addedToCart);
        addedToCart.setVisible(false);
        addedToCart.setPickOnBounds(false);

        outOfStock = new ImageView(new Image("/edu/wpi/cs3733/d20/teamL/assets/gift_Delivery/outOfStock.png",0,150,true,false,true));
        stackPane.getChildren().add(outOfStock);
        outOfStock.setVisible(false);
        outOfStock.setPickOnBounds(false);

        for (Gift gift : gifts) { loadImage(gift.getSubtype()); }

        // Logic of filling scroll pane with tabs and filling those tabs
        int i = 0;
        while (i < gifts.size() - 1) {
            String currentGiftTabType = gifts.get(i).getType();
            Tab tab = new Tab();
            tab.setText(gifts.get(i).getType());
            tab.setClosable(false);

            ScrollPane scrollPane = new ScrollPane();
            scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            VBox vBox = new VBox();
            vBox.setSpacing(5);
            vBox.setPadding(new Insets(5,5,0,5));
            vBox.setAlignment(Pos.TOP_CENTER);

            while (gifts.get(i).getType().equals(currentGiftTabType)) {
                int numColumns = 0;
                HBox giftRow = new HBox();
                giftRow.setSpacing(10);
                giftRow.setAlignment(Pos.CENTER);
                while (numColumns < 4 && gifts.get(i).getType().equals(currentGiftTabType)){
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

        giftTabPane.prefWidthProperty().bind(stackPane.widthProperty());
        giftTabPane.prefHeightProperty().bind(stackPane.heightProperty());

        }

    /**
     * Creates a VBox with a gift image, name, quantity, description
     * and button to add it to the cart with all that jazz
     * @param gift Gift type of the gift you want to be formated
     * @param i This is only used to get the corresponding pic (hopefully will be changes l8r)
     * @return Returns a formated Vbox to be placed in Tab Pane
     */
    private VBox makeGift(Gift gift, int i) {
        VBox vbox = new VBox();
        vbox.setAlignment(Pos.TOP_CENTER);
        vbox.prefWidth(300);
        vbox.prefHeight(375);

        ImageView imageView = new ImageView(new Image("/edu/wpi/cs3733/d20/teamL/assets/gift_Delivery/noImage.png", 0, 200, true, false, true));
        imageView.setImage(images.get(i));
        imageView.setFitWidth(200);
        VBox imageVBox = new VBox(imageView);
        imageVBox.setAlignment(Pos.TOP_CENTER);
        imageVBox.setMargin(imageView, new Insets(0,0,30,0));
        imageVBox.setMinHeight(200);

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

        EventHandler<ActionEvent> addToCartAction = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                int quantityInCart = cart.getOrDefault(gift.getSubtype(), 0);
                System.out.println(quantityInCart);
                if (quantityInCart == 0) cart.put(gift.getSubtype(), 0);
                if (Integer.parseInt(gift.getInventory()) - quantityInCart > 0) {
                    cart.replace(gift.getSubtype(), quantityInCart + 1);
                    loaderHelper.showAndFade(addedToCart);
                } else loaderHelper.showAndFade(outOfStock);
            }
        };

        JFXButton addToCartButton = new JFXButton("Add to Cart");
        addToCartButton.getStyleClass().add("save-button-jfx");
        addToCartButton.setOnAction(addToCartAction);
        vbox.setMargin(addToCartButton, new Insets(5,0,5,0));

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

    public void goToCheckout(MouseEvent mouseEvent) {
        System.out.println("Go to Checkout");
    }
}
