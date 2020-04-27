package edu.wpi.cs3733.d20.teamL.views.controllers.requests;

import com.google.inject.Inject;
import com.jfoenix.controls.JFXButton;
import edu.wpi.cs3733.d20.teamL.entities.Gift;
import edu.wpi.cs3733.d20.teamL.services.db.DBConstants;
import edu.wpi.cs3733.d20.teamL.services.db.IDatabaseService;
import edu.wpi.cs3733.d20.teamL.services.db.SQLEntry;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import org.apache.derby.iapi.sql.ResultSet;

import java.awt.*;
import java.util.ArrayList;

public class GiftCartController {
    @Inject
    IDatabaseService db;

    @FXML
    JFXButton flowerBtn, toysBtn, booksBtn, movieBtn;
    @FXML
    JFXButton addCart1Btn, addCart2Btn, addCart3Btn, checkoutBtn;
    @FXML
    Label name1, name2, name3, inv1, inv2, inv3, desc1, desc2, desc3;
    @FXML
    Image img1, img2, img3;

    private int index = 0;
    private ArrayList<Gift> gifts;

    @FXML
    public void initialize() {
        ArrayList<ArrayList<String>> giftsDB = db.getTableFromResultSet(db.executeQuery(new SQLEntry(DBConstants.SELECT_ALL_GIFTS)));
        ArrayList<Gift> gifts = new ArrayList<>();

        for(ArrayList<String> g : giftsDB) {
            gifts.add(new Gift(g.get(0), g.get(1), g.get(2), g.get(3), g.get(4)));
        }

        updateStore();
    }

    public void handleTypeSelection(ActionEvent event) {
        if(event.getSource() == flowerBtn) index = 0;
        else if(event.getSource() == toysBtn) index = 3;
        else if(event.getSource() == booksBtn) index = 6;
        else if(event.getSource() == movieBtn) index = 9;

        updateStore();
    }

    private void updateStore() {
        name1.setText(gifts.get(index).getSubtype());
        name2.setText(gifts.get(index+1).getSubtype());
        name3.setText(gifts.get(index+2).getSubtype());

        inv1.setText(gifts.get(index).getInventory());
        inv2.setText(gifts.get(index+1).getInventory());
        inv3.setText(gifts.get(index+2).getInventory());

        desc1.setText(gifts.get(index).getDescription());
        desc2.setText(gifts.get(index+1).getDescription());
        desc3.setText(gifts.get(index+2).getDescription());
    }
}
