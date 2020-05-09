package edu.wpi.cs3733.d20.teamL.views.controllers.logged_in;


import com.jfoenix.controls.JFXAutoCompletePopup;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import edu.wpi.cs3733.d20.teamL.App;
import edu.wpi.cs3733.d20.teamL.entities.Building;
import edu.wpi.cs3733.d20.teamL.entities.Node;
import edu.wpi.cs3733.d20.teamL.services.db.IDatabaseCache;
import edu.wpi.cs3733.d20.teamL.util.FXMLLoaderFactory;
import edu.wpi.cs3733.d20.teamL.util.search.SearchFields;
import edu.wpi.cs3733.d20.teamL.views.components.MapPane;
import edu.wpi.cs3733.d20.teamL.views.controllers.map.MapViewerController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.ResourceBundle;
import java.util.function.ToDoubleBiFunction;

@Slf4j
public class AnalyticsController implements Initializable {

    @FXML
    MapPane map;
    @FXML
    private JFXComboBox<String> timeBox, buildingChooser;
    @FXML
    private BarChart<?, ?> ServiceReqHisto;
    @FXML
    private PieChart servicePieChart;
    @FXML
    VBox floorSelector;
    @FXML
    JFXButton floorUp, floorDown;

    private FXMLLoaderFactory loaderFactory = new FXMLLoaderFactory();
    ObservableList<String> timeOptions = FXCollections.observableArrayList("Any time", "Past hour", "Past 24 hours", "Past month", "Past year");
    private String defaultBuilding = "Faulkner";
    private int defaultFloor = 2;
    public static final String MAIN = "Main";

    @Inject
    private IDatabaseCache cache;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        timeBox.setItems(timeOptions);
        setServiceReqHisto();
        setServiceReqPieChart();

        if (App.doUpdateCacheOnLoad) {
            cache.cacheAllFromDB();
            App.doUpdateCacheOnLoad = false;
        }
        map.setEditable(false);
        map.setHighLightColor(Color.GOLD);

        // Import all the nodes from the cache and set the current building to Faulkner
        String startB = "Faulkner";
        Building faulkner = cache.getBuilding("Faulkner");
        Building main = cache.getBuilding(MAIN);

        if (!faulkner.getNodes().isEmpty()) map.setBuilding(faulkner);
        if (!main.getNodes().isEmpty()) map.getBuildings().add(main);
        buildingChooser.getItems().addAll("Faulkner", MAIN);
        buildingChooser.getSelectionModel().select(startB);

        // Add floor buttons
        generateFloorButtons();

        setFloor(2);

        map.setZoomLevel(0.25 * App.UI_SCALE);
        map.init();
    }

    @FXML
    void handleCancel() {
        try {
            loaderFactory.goBack();
        } catch (Exception ex) {
            log.error("Encountered Exception.", ex);
        }
    }

    @FXML
    public void handleLive() {
    }

    @FXML
    public void handleRefresh() {
    }

    public void setServiceReqHisto() {
        XYChart.Series set = new XYChart.Series<>();
        set.setName("Type of Request");

        //Hard-coded Data
        //TODO: Add actual data
        set.getData().add(new XYChart.Data("Gift Delivery", 10));
        set.getData().add(new XYChart.Data("Security", 20));
        set.getData().add(new XYChart.Data("Maintenance", 14));
        set.getData().add(new XYChart.Data("Internal Transportation", 23));
        set.getData().add(new XYChart.Data("External Transportation", 36));
        set.getData().add(new XYChart.Data("Medicine", 47));
        set.getData().add(new XYChart.Data("Sanitation", 50));
        set.getData().add(new XYChart.Data("IT", 13));
        set.getData().add(new XYChart.Data("Interpreter", 25));
        set.getData().add(new XYChart.Data("Reflection Room", 33));
        set.getData().add(new XYChart.Data("On-Call Bed", 55));

        ServiceReqHisto.getData().addAll(set);
    }

    public void setServiceReqPieChart() {
        //Hard-coded Test Data
        ObservableList<PieChart.Data> serviceReqData = FXCollections.observableArrayList(
                new PieChart.Data("Gift Delivery", 20),
                new PieChart.Data("Security", 10),
                new PieChart.Data("Maintenance", 5),
                new PieChart.Data("Internal Transportation", 25),
                new PieChart.Data("External Transportation", 30),
                new PieChart.Data("Medicine", 10),
                new PieChart.Data("IT", 25),
                new PieChart.Data("Interpreter", 40),
                new PieChart.Data("Reflection Room", 35),
                new PieChart.Data("On-Call Bed", 10));

        servicePieChart.setData(serviceReqData);
        servicePieChart.setStartAngle(90);
    }

    @FXML
    void handleGiftPieChart() {

        ObservableList<PieChart.Data> giftData = FXCollections.observableArrayList(
                new PieChart.Data("Roses", 10),
                new PieChart.Data("Tulips", 10),
                new PieChart.Data("Dandelions", 10),
                new PieChart.Data("Building blocks", 10),
                new PieChart.Data("Play-Do", 10),
                new PieChart.Data("Hot Wheels", 10),
                new PieChart.Data("LOTR", 10),
                new PieChart.Data("Harry Potter", 10),
                new PieChart.Data("Inheritance", 10),
                new PieChart.Data("LORT films", 10),
                new PieChart.Data("Star Wars", 10),
                new PieChart.Data("Pulp Fiction", 10));

        servicePieChart.setData(giftData);
        servicePieChart.setStartAngle(90);
    }

    @FXML
    private void switchBuilding() {
        String selected = buildingChooser.getSelectionModel().getSelectedItem();

        Building newBuilding = cache.getBuilding(selected);
        map.setBuilding(newBuilding);

        int prevFloor = map.getFloor();
        generateFloorButtons();
        setFloor(Math.max(map.getBuilding().getMinFloor(), Math.min(prevFloor, map.getBuilding().getMaxFloor())));
        map.setZoomLevel(.25 * App.UI_SCALE);
        //if (!path.getPathNodes().isEmpty()) highLightPath();
    }

    private void generateFloorButtons() {
        map.generateFloorButtons(floorSelector, this::handleFloor);
    }

    @FXML
    public void handleFloor(ActionEvent event) {
        JFXButton sourceButton = (JFXButton) event.getSource();

        if (event.getSource() == floorUp && map.getFloor() < 5) {
            setFloor(map.getFloor() + 1);
        } else if (event.getSource() == floorDown) {
            setFloor(map.getFloor() - 1);
        } else {
            setFloor(Node.floorStringToInt(sourceButton.getText()));
        }

       // if (!path.getPathNodes().isEmpty()) highLightPath();
    }

    public void setFloor(int newFloor) {
        map.setFloor(Math.max(map.getBuilding().getMinFloor(), Math.min(newFloor, map.getBuilding().getMaxFloor())));

        for (javafx.scene.Node node : floorSelector.getChildren()) {
            JFXButton floorButton = (JFXButton) node;
            if (!floorButton.getText().equals(Node.floorIntToString(map.getFloor()))) {
                if (floorButton.getStyleClass().contains("selected-floor")) {
                    floorButton.getStyleClass().clear();
                    floorButton.getStyleClass().add("button");
                    floorButton.getStyleClass().add("floor-buttons");
                }
            } else {
                if (!floorButton.getStyleClass().contains("selected-floor"))
                    floorButton.getStyleClass().add("selected-floor");
            }
        }
    }

    @FXML
    private void zoomIn() {
        map.setZoomLevelToPosition(map.getZoomLevel() * 1.2, new Point2D(map.getBody().getWidth() / 2, map.getBody().getHeight() / 2));
    }

    @FXML
    private void zoomOut() {
        map.setZoomLevelToPosition(map.getZoomLevel() * 0.8, new Point2D(map.getBody().getWidth() / 2, map.getBody().getHeight() / 2));
    }
}
