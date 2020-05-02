package edu.wpi.cs3733.d20.teamL.views.controllers.requests;

import com.google.inject.Inject;
import com.jfoenix.controls.JFXButton;
import edu.wpi.cs3733.d20.teamL.entities.Gift;
import edu.wpi.cs3733.d20.teamL.services.db.IDatabaseCache;
import edu.wpi.cs3733.d20.teamL.util.FXMLLoaderHelper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Paint;
import lombok.extern.slf4j.Slf4j;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class GiftCartController {
    @Inject
    IDatabaseCache cache;
    FXMLLoaderHelper loaderHelper = new FXMLLoaderHelper();
    @FXML
    JFXButton flowerBtn, toysBtn, booksBtn, movieBtn;
    @FXML
    JFXButton addCart1Btn, addCart2Btn, addCart3Btn, remove1, remove2, remove3;
    @FXML
    Label name1, name2, name3, inv1, inv2, inv3, desc1, desc2, desc3, checkout1, checkout2, checkout3, error;
    @FXML
    ImageView image1, image2, image3;

    List<Label> checkoutLabels;
    List<JFXButton> removeBtns;
    List<JFXButton> typeBtns;

    private int index = 0;
    private ArrayList<Gift> gifts = new ArrayList<>();
    private ArrayList<Gift> cart = new ArrayList<>();
    private ArrayList<Image> images = new ArrayList<>();

    Image loadedImage1 = new Image("/edu/wpi/cs3733/d20/teamL/assets/giftDelivery/Roses.png", 0, 200, true, false, true);
    Image loadedImage2 = new Image("/edu/wpi/cs3733/d20/teamL/assets/giftDelivery/Tulips.png", 0, 200, true, false, true);
    Image loadedImage3 = new Image("/edu/wpi/cs3733/d20/teamL/assets/giftDelivery/Dandelion.png", 0, 200, true, false, true);
    Image loadedImage4 = new Image("/edu/wpi/cs3733/d20/teamL/assets/giftDelivery/Building blocks.png", 0, 200, true, false, true);
    Image loadedImage5 = new Image("/edu/wpi/cs3733/d20/teamL/assets/giftDelivery/Play-Do.png", 0, 200, true, false, true);
    Image loadedImage6 = new Image("/edu/wpi/cs3733/d20/teamL/assets/giftDelivery/hotWheels.png", 0, 200, true, false, true);
    Image loadedImage7 = new Image("/edu/wpi/cs3733/d20/teamL/assets/giftDelivery/LOTR.png", 0, 200, true, false, true);
    Image loadedImage8 = new Image("/edu/wpi/cs3733/d20/teamL/assets/giftDelivery/Harry Potter.png", 0, 200, true, false, true);
    Image loadedImage9 = new Image("/edu/wpi/cs3733/d20/teamL/assets/giftDelivery/Inheritance.png", 0, 200, true, false, true);
    Image loadedImage10 = new Image("/edu/wpi/cs3733/d20/teamL/assets/giftDelivery/LOTR Films.png", 0, 200, true, false, true);
    Image loadedImage11 = new Image("/edu/wpi/cs3733/d20/teamL/assets/giftDelivery/Star Wars.png", 0, 200, true, false, true);
    Image loadedImage12 = new Image("/edu/wpi/cs3733/d20/teamL/assets/giftDelivery/Pulp Fiction.png", 0, 200, true, false, true);


    @FXML
    public void initialize() {
        checkoutLabels = Arrays.asList(checkout1, checkout2, checkout3);
        removeBtns = Arrays.asList(remove1, remove2, remove3);
        typeBtns = Arrays.asList(flowerBtn, toysBtn, booksBtn, movieBtn);

        images.add(loadedImage1);
        images.add(loadedImage2);
        images.add(loadedImage3);
        images.add(loadedImage4);
        images.add(loadedImage5);
        images.add(loadedImage6);
        images.add(loadedImage7);
        images.add(loadedImage8);
        images.add(loadedImage9);
        images.add(loadedImage10);
        images.add(loadedImage11);
        images.add(loadedImage12);


        cache.cacheGiftsFromDB();
        gifts = cache.getGiftsCache();

        makeInvisible();
        updateStore();
    }

    public void handleTypeSelection(ActionEvent event) {
        if (event.getSource() == flowerBtn) index = 0;
        else if (event.getSource() == toysBtn) index = 3;
        else if (event.getSource() == booksBtn) index = 6;
        else if (event.getSource() == movieBtn) index = 9;

        //updateBtnStyle();
        updateStore();
        updateCart();
    }

    private void updateStore() {
        name1.setText(gifts.get(index).getSubtype());
        name2.setText(gifts.get(index + 1).getSubtype());
        name3.setText(gifts.get(index + 2).getSubtype());

        inv1.setText(gifts.get(index).getInventory());
        inv2.setText(gifts.get(index + 1).getInventory());
        inv3.setText(gifts.get(index + 2).getInventory());

        desc1.setText(gifts.get(index).getDescription());
        desc2.setText(gifts.get(index + 1).getDescription());
        desc3.setText(gifts.get(index + 2).getDescription());

        image1.setImage(images.get(index));
        image2.setImage(images.get(index + 1));
        image3.setImage(images.get(index + 2));

    }

    @FXML
    private void checkoutBtnPressed() {
        try {
            cache.cacheCart(cart);
            Parent root = loaderHelper.getFXMLLoader("GiftCheckout").load();
            loaderHelper.setupScene(new Scene(root));
        } catch (IOException ex) {
            log.error("Encountered IOException", ex);
        }
    }

    @FXML
    private void addCartPressed(ActionEvent event) {
        if (cart.size() < 3) {
            if (event.getSource() == addCart1Btn) {
                cart.add(gifts.get(index));
            } else if (event.getSource() == addCart2Btn) {
                cart.add(gifts.get(index + 1));
            } else if (event.getSource() == addCart3Btn) {
                cart.add(gifts.get(index + 2));
            }

            updateCart();
        } else {
            error.setVisible(true);
        }
    }

    @FXML
    private void backBtn() throws IOException {
        cache.clearCartCache();
        loaderHelper.goBack();
    }

    private void updateBtnStyle() {
        for (JFXButton btn : typeBtns) {
            btn.textFillProperty().setValue(Paint.valueOf("white"));
            btn.setStyle("-fx-background-color: #0d2e57");
        }

        typeBtns.get(index / 3).textFillProperty().setValue(Paint.valueOf("black"));
        typeBtns.get(index / 3).setStyle("-fx-background-color: white");
    }

    private void updateCart() {
        makeInvisible();
        for (int i = 0; i < cart.size(); i++) {
            checkoutLabels.get(i).setVisible(true);
            removeBtns.get(i).setVisible(true);
            removeBtns.get(i).setDisable(false);
            checkoutLabels.get(i).setText(cart.get(i).getSubtype());
        }
    }

    private void makeInvisible() {
        error.setVisible(false);
        for (int i = 0; i < 3; i++) {
            checkoutLabels.get(i).setVisible(false);
            removeBtns.get(i).setVisible(false);
            removeBtns.get(i).setDisable(true);
        }
    }

    @FXML
    private void removeItem(ActionEvent event) {
        cart.remove(removeBtns.indexOf(event.getSource()));
        updateCart();
    }
}
