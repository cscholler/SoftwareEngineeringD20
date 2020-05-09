package edu.wpi.cs3733.d20.teamL.views.controllers.logged_in;


import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import edu.wpi.cs3733.d20.teamL.util.FXMLLoaderFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.ToDoubleBiFunction;

@Slf4j
public class AnalyticsController implements Initializable {

    @FXML
    private JFXComboBox<String> timeBox;

    @FXML
    private BarChart<?, ?> ServiceReqHisto;

    @FXML
    private PieChart servicePieChart;

    private FXMLLoaderFactory loaderFactory = new FXMLLoaderFactory();

    ObservableList<String> timeOptions = FXCollections.observableArrayList("Any time", "Past hour", "Past 24 hours", "Past month", "Past year");

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        timeBox.setItems(timeOptions);
        setServiceReqHisto();
        setServiceReqPieChart();
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
}
