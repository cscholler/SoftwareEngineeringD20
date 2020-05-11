package edu.wpi.cs3733.d20.teamL.views.controllers.logged_in;

import com.jfoenix.controls.JFXComboBox;
import edu.wpi.cs3733.d20.teamL.util.FXMLLoaderFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.*;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.util.ResourceBundle;

@Slf4j
public class AnalyticsController implements Initializable {

    @FXML
    private JFXComboBox<String> timeBox;

    @FXML
    private BarChart<String, Number> ServiceReqHisto;

    @FXML
    private PieChart servicePieChart;

    private FXMLLoaderFactory loaderFactory = new FXMLLoaderFactory();

    ObservableList<String> timeOptions = FXCollections.observableArrayList("Any time", "Past hour", "Past 24 hours", "Past month", "Past year");

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        timeBox.setItems(timeOptions);
        setServiceReqHisto();
        handleAllServiceReq();
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
        set.getData().add(new XYChart.Data("Internal", 23));
        set.getData().add(new XYChart.Data("External", 36));
        set.getData().add(new XYChart.Data("Medicine", 47));
        set.getData().add(new XYChart.Data("Sanitation", 50));
        set.getData().add(new XYChart.Data("IT", 13));
        set.getData().add(new XYChart.Data("Interpreter", 25));
        set.getData().add(new XYChart.Data("Reflection Room", 33));
        set.getData().add(new XYChart.Data("On-Call Bed", 55));

        ServiceReqHisto.getData().addAll(set);

    }

    public void handleAllServiceReq() {
        //Hard-coded Test Data
        //TODO: Add actual data
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

        servicePieChart.setTitle("Service Request Pie Chart");
        servicePieChart.setData(serviceReqData);
        servicePieChart.setStartAngle(90);
    }

    @FXML
    void handleGiftPieChart() {

        //TODO: Add actual data
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

        servicePieChart.setTitle("Gift Delivery Pie Chart");
        servicePieChart.setData(giftData);
        servicePieChart.setStartAngle(90);
    }
    @FXML
    void handleSanitationPieChart() {

        //TODO: Add actual data
        ObservableList<PieChart.Data> sanitationData = FXCollections.observableArrayList(
                new PieChart.Data("Biohazard", 10),
                new PieChart.Data("Spill", 10));

        servicePieChart.setTitle("Sanitation Pie Chart");
        servicePieChart.setData(sanitationData);
        servicePieChart.setStartAngle(90);
    }

    @FXML
    void handleSecurityPieChart() {

        //TODO: Add actual data
        ObservableList<PieChart.Data> securityData = FXCollections.observableArrayList(
                new PieChart.Data("High Priority", 10),
                new PieChart.Data("Medium Priority", 10),
                new PieChart.Data("Low Priority", 10));

        servicePieChart.setTitle("Security Pie Chart");
        servicePieChart.setData(securityData);
        servicePieChart.setStartAngle(90);
    }

    @FXML
    void handleMaintenancePieChart() {

        //TODO: Add actual data
        ObservableList<PieChart.Data> maintenanceData = FXCollections.observableArrayList(
                new PieChart.Data("Plumbing", 10),
                new PieChart.Data("Medical Equipment", 10),
                new PieChart.Data("Electrical", 10),
                new PieChart.Data("IT", 10),
                new PieChart.Data("Other", 10));

        servicePieChart.setTitle("Maintenance Pie Chart");
        servicePieChart.setData(maintenanceData);
        servicePieChart.setStartAngle(90);
    }

    @FXML
    void handleMedicationPieChart() {

        //TODO: Add actual data
        ObservableList<PieChart.Data> medicationData = FXCollections.observableArrayList(
                new PieChart.Data("Ibuprofen", 10),
                new PieChart.Data("Advil", 10),
                new PieChart.Data("Pain Killers", 10),
                new PieChart.Data("Other", 10));

        servicePieChart.setTitle("Medication Pie Chart");
        servicePieChart.setData(medicationData);
        servicePieChart.setStartAngle(90);
    }

    @FXML
    void handleOnCallPieChart() {

        //TODO: Add actual data
        ObservableList<PieChart.Data> onCallData = FXCollections.observableArrayList(
                new PieChart.Data("Bed 1", 10),
                new PieChart.Data("Bed 2", 10),
                new PieChart.Data("Bed 3", 10),
                new PieChart.Data("Bed 4", 10),
                new PieChart.Data("Bed 5", 10),
                new PieChart.Data("Bed 6", 10),
                new PieChart.Data("Bed 7", 10));

        servicePieChart.setTitle("On-Call Bed Pie Chart");
        servicePieChart.setData(onCallData);
        servicePieChart.setStartAngle(90);
    }

    @FXML
    void handleReflectionPieChart() {

        //TODO: Add actual data
        ObservableList<PieChart.Data> reflectionData = FXCollections.observableArrayList(
                new PieChart.Data("Floor 1", 10),
                new PieChart.Data("Floor 3", 10),
                new PieChart.Data("Floor 4", 10));

        servicePieChart.setTitle("Reflection Room Pie Chart");
        servicePieChart.setData(reflectionData);
        servicePieChart.setStartAngle(90);
    }

    @FXML
    void handleITPieChart() {

        //TODO: Add actual data
        ObservableList<PieChart.Data> ITData = FXCollections.observableArrayList(
                new PieChart.Data("General Help", 10),
                new PieChart.Data("Data Backup", 10),
                new PieChart.Data("Hardware/Software Issues", 10),
                new PieChart.Data("Cyber Attacks", 10));

        servicePieChart.setTitle("IT Pie Chart");
        servicePieChart.setData(ITData);
        servicePieChart.setStartAngle(90);

    }

    @FXML
    void handleInternalPieChart() {

        //TODO: Add actual data
        ObservableList<PieChart.Data> internalData = FXCollections.observableArrayList(
                new PieChart.Data("Wheelchair w/ Operator", 10),
                new PieChart.Data("Wheelchair w/o Operator", 10),
                new PieChart.Data("Crutches", 10),
                new PieChart.Data("Walker", 10),
                new PieChart.Data("Gurnie", 10));

        servicePieChart.setTitle("Internal Transportation Pie Chart");
        servicePieChart.setData(internalData);
        servicePieChart.setStartAngle(90);
    }

    @FXML
    void handleExternalPieChart() {

        //TODO: Add actual data
        ObservableList<PieChart.Data> externalData = FXCollections.observableArrayList(
                new PieChart.Data("Taxi", 10),
                new PieChart.Data("Bus", 10),
                new PieChart.Data("Uber", 10),
                new PieChart.Data("Lyft", 10));

        servicePieChart.setTitle("External Transportation Pie Chart");
        servicePieChart.setData(externalData);
        servicePieChart.setStartAngle(90);
    }

    @FXML
    void handleInterpreterPieChart() {

        //TODO: Add actual data
        ObservableList<PieChart.Data> interpreterData = FXCollections.observableArrayList(
                new PieChart.Data("French", 10),
                new PieChart.Data("Chinese", 10),
                new PieChart.Data("American Sign Language", 10),
                new PieChart.Data("Spanish", 10),
                new PieChart.Data("Italian", 10));

        servicePieChart.setTitle("Interpreter Pie Chart");
        servicePieChart.setData(interpreterData);
        servicePieChart.setStartAngle(90);
    }
}
