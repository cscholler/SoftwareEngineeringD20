package edu.wpi.leviathans.views;

//import com.google.inject.Inject;

import edu.wpi.leviathans.util.Row;
import edu.wpi.leviathans.util.io.CSVParser;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import edu.wpi.leviathans.services.db.DatabaseService;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

@Slf4j
public class DatabaseViewController {
	//@Inject
	//DatabaseService db;
    //@Inject
    DatabaseService db;

    @FXML
    private Button btnBack;
    @FXML
    private Button btnModify;
    @FXML
    private Button btnDownload;
    @FXML
    private Button btnDemonstration;
    @FXML
    private Button btnSearch, btnSave;
    @FXML
    private TableView table;

    @FXML
    private TableColumn colNodeID;
    @FXML
    private TableColumn colXCoord;
    @FXML
    private TableColumn colYCoord;
    @FXML
    private TableColumn colFloor;
    @FXML
    private TableColumn colBuilding;
    @FXML
    private TableColumn colNodeType;
    @FXML
    private TableColumn colLongName;
    @FXML
    private TableColumn colShortName;

    @FXML
    private TextField nodeIDText;
    @FXML
    private TextField xCoordText;
    @FXML
    private TextField yCoordText;
    @FXML
    private TextField floorText;
    @FXML
    private TextField buildingText;
    @FXML
    private TextField shortNameText;
    @FXML
    private TextField longNameText;
    @FXML
    private TextField nodeTypeText, searchText;


    private static ObservableList<Row> observableList;
    private Row nodeEdit;
    private int nodeNum;


    @FXML
    private void handleButtonAction(ActionEvent e) throws IOException {

        Stage stage;
        Parent root;

        if (e.getSource() == btnModify) {

            stage = new Stage();
            root = FXMLLoader.load(getClass().getResource("/edu/wpi/leviathans/Modify.fxml"));
            stage.setScene(new Scene(root));
            stage.setTitle("Node Editor");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(btnModify.getScene().getWindow());
            stage.showAndWait();

        } else if (e.getSource() == btnDownload) {
//            System.out.println((observableList.get(nodeNum)).getFloor() +"before");
            observableList = FXCollections.observableArrayList(populateRow());
            loadData();
            System.out.println((observableList.get(nodeNum)).getFloor() + "after");
            stage = (Stage) btnDownload.getScene().getWindow();
            root = FXMLLoader.load(getClass().getResource("/edu/wpi/leviathans/Download.fxml"));
        } else if (e.getSource() == btnBack) {
            stage = (Stage) btnBack.getScene().getWindow();
            root = FXMLLoader.load(getClass().getResource("/edu/wpi/leviathans/Display.fxml"));
            stage.close();
        } else if (e.getSource() == btnSearch) {
            search();
            stage = (Stage) btnBack.getScene().getWindow();
            root = FXMLLoader.load(getClass().getResource("/edu/wpi/leviathans/Display.fxml"));
        } else if (e.getSource() == btnSave) {
            save(nodeEdit, nodeNum);
            download();
            System.out.println("Here");
            stage = (Stage) btnSave.getScene().getWindow();
            root = FXMLLoader.load(getClass().getResource("/edu/wpi/leviathans/Display.fxml"));

        } else {

            stage = (Stage) btnDemonstration.getScene().getWindow();
            root = FXMLLoader.load(getClass().getResource("/edu/wpi/leviathans/Demonstration.fxml"));

        }

        if (e.getSource() != btnDownload && e.getSource() != btnSearch && e.getSource() != btnSave && e.getSource() != btnBack) {
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        }

    }


    private void showNodeDetails(Row row) {
        if (row != null) {
            // Fill the textfield with info from the row object.
            System.out.println("showDetails");
            nodeIDText.setText(row.getNodeID());
            xCoordText.setText(row.getxcoord());
            yCoordText.setText(row.getycoord());
            floorText.setText(row.getFloor());
            buildingText.setText(row.getBuilding());
            nodeTypeText.setText(row.getNodeType());
            shortNameText.setText(row.getShortName());
            longNameText.setText(row.getLongName());

        } else {

            nodeIDText.setText("");
            xCoordText.setText("");
            yCoordText.setText("");
            floorText.setText("");
            buildingText.setText("");
            nodeTypeText.setText("");
            shortNameText.setText("");
            longNameText.setText("");
        }
    }

    private void download() {

        try {
//                FileWriter fw = new FileWriter("src/main/java/edu/wpi/leviathans/util/pathFinding/floorMaps/MapLnodesBackup.csv", true);
//                BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter pw = new PrintWriter("src/main/java/edu/wpi/leviathans/util/pathFinding/floorMaps/MapLnodesBackup.csv");
            StringBuilder sb = new StringBuilder();

            sb.append("NodeID");
            sb.append(",");
            sb.append("xCoordinate");
            sb.append(",");
            sb.append("yCoordinate");
            sb.append(",");
            sb.append("Floor");
            sb.append(",");
            sb.append("Building");
            sb.append(",");
            sb.append("NodeType");
            sb.append(",");
            sb.append("ShortName");
            sb.append(",");
            sb.append("LongName");
            sb.append("\r\n");

            for (Row r : observableList) {

                sb.append(r.getNodeID());
                sb.append(",");
                sb.append(r.getxcoord());
                sb.append(",");
                sb.append(r.getycoord());
                sb.append(",");
                sb.append(r.getFloor());
                sb.append(",");
                sb.append(r.getBuilding());
                sb.append(",");
                sb.append(r.getNodeType());
                sb.append(",");
                sb.append(r.getShortName());
                sb.append(",");
                sb.append(r.getLongName());
                sb.append("\r\n");

//                    String id = r.getNodeID();
//                    String x = r.getxcoord();
//                    String y = r.getycoord();
//                    String floor = r.getFloor();
//                    String building = r.getBuilding();
//                    String type = r.getNodeType();
//                    String sName = r.getShortName();
//                    String lName = r.getLongName();
//                    pw.println(id + "," + x + "," + y + "," + floor + "," + building + "," + type + "," + sName + "," + lName);

            }
            pw.write(sb.toString());
            pw.close();
            System.out.print("Finished");
        } catch (Exception E) {

        }

    }

    private void search() {
        String name = searchText.getText();
        int x = 0;
        for (Row r : observableList) {
            //System.out.print("Loop");
            if (r.getShortName().equals(name)) {
                nodeEdit = r;
                showNodeDetails(r);
                nodeNum = x;
                break;
            }
            x++;

        }
    }

    private void save(Row r, int i) {
        r.setNodeID(nodeIDText.getText());
        r.setxCoord(xCoordText.getText());
        r.setyCoord(yCoordText.getText());
        r.setFloor(floorText.getText());
        r.setBuilding(buildingText.getText());
        r.setNodeType(nodeTypeText.getText());
        r.setShortName(shortNameText.getText());
        r.setLongName(longNameText.getText());

        observableList.set(i, r);
        System.out.println(r.getFloor());
        System.out.println((observableList.get(i)).getFloor());
    }


    @FXML
    public void loadData() {

        colNodeID.setCellValueFactory(new PropertyValueFactory<>("nodeID"));
        colXCoord.setCellValueFactory(new PropertyValueFactory<>("xcoord"));
        colYCoord.setCellValueFactory(new PropertyValueFactory<>("ycoord"));
        colFloor.setCellValueFactory(new PropertyValueFactory<>("floor"));
        colBuilding.setCellValueFactory(new PropertyValueFactory<>("building"));
        colNodeType.setCellValueFactory(new PropertyValueFactory<>("nodeType"));
        colLongName.setCellValueFactory(new PropertyValueFactory<>("longName"));
        colShortName.setCellValueFactory(new PropertyValueFactory<>("shortName"));

        Group groupRoot = new Group();
        Stage stage = new Stage();


        table.setItems(observableList);
        table.getColumns().clear();
        table.getColumns().addAll(colNodeID, colXCoord, colYCoord, colFloor, colBuilding, colNodeType, colLongName,
                colShortName);
    }

    private ArrayList<Row> populateRow() {
        System.out.println("loading data");
        CSVParser reader = new CSVParser();
        ArrayList<ArrayList<String>> data = reader.readCSVFile();
        ArrayList<Row> rows = new ArrayList<>();
        for (ArrayList<String> dataRow : data) {
            Row r = new Row(dataRow.get(0), dataRow.get(1), dataRow.get(2), dataRow.get(3),
                    dataRow.get(4), dataRow.get(5), dataRow.get(6), dataRow.get(7));
            rows.add(r);
        }
        return rows;
    }
}
