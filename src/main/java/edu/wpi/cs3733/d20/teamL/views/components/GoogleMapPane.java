package edu.wpi.cs3733.d20.teamL.views.components;

import com.lynden.gmapsfx.GoogleMapView;
import com.lynden.gmapsfx.MapComponentInitializedListener;
import com.lynden.gmapsfx.javascript.object.LatLong;
import com.lynden.gmapsfx.javascript.object.MapOptions;
import com.lynden.gmapsfx.javascript.object.MapTypeIdEnum;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

public class GoogleMapPane extends GoogleMapView implements Initializable, MapComponentInitializedListener {

    public GoogleMapPane() {
        super("english", "AIzaSyDuWk9RV1yVAfw6-ZLuZhZN9AiBdKSdyxE");
        addMapInializedListener(this);
    }

    @FXML
    public void initialize(URL url, ResourceBundle resourceBundle) {
        addMapInializedListener(this);
    }

    @Override
    public void mapInitialized() {
        MapOptions mapOptions = new MapOptions();
        mapOptions.center(new LatLong(42.301724, -71.130992))
                .mapType(MapTypeIdEnum.ROADMAP)
                .rotateControl(false)
                .streetViewControl(false)
                .zoom(18);

        createMap(mapOptions);
    }

    public void navigate(String source, String destination) {

    }

}
