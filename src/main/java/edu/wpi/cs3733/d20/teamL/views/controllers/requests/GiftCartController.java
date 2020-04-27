package edu.wpi.cs3733.d20.teamL.views.controllers.requests;

import com.google.inject.Inject;
import edu.wpi.cs3733.d20.teamL.entities.Gift;
import edu.wpi.cs3733.d20.teamL.services.db.DBConstants;
import edu.wpi.cs3733.d20.teamL.services.db.IDatabaseService;
import edu.wpi.cs3733.d20.teamL.services.db.SQLEntry;
import javafx.fxml.FXML;
import org.apache.derby.iapi.sql.ResultSet;

import java.util.ArrayList;

public class GiftCartController {
    @Inject
    IDatabaseService db;

    @FXML
    public void initialize() {
        ArrayList<ArrayList<String>> giftsDB = db.getTableFromResultSet(db.executeQuery(new SQLEntry(DBConstants.SELECT_ALL_GIFTS)));
        ArrayList<Gift> gifts;

        for(ArrayList<String> g : giftsDB) {
            //gifts.add(new Gift(g.get(0), g.get(1), g.get(2), g.get(3), g.get(4))
        }
    }
}
