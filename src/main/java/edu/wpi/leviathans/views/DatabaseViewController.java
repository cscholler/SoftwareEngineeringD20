package edu.wpi.leviathans.views;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import edu.wpi.leviathans.views.mapViewer.MapViewController;
import edu.wpi.leviathans.views.mapViewer.dataDialogue.DataDialogue;
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

//import com.google.inject.Inject;

import lombok.extern.slf4j.Slf4j;

import entities.Row;
import edu.wpi.leviathans.util.io.CSVParser;

@Slf4j
public class DatabaseViewController {
	//@Inject
	//DatabaseService db;

    @FXML
    private Button btnBack;
    @FXML
    private Button btnEdit;
    @FXML
    private Button btnLoad;
    @FXML
    private Button btnMap;
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

        if (e.getSource() == btnEdit) {
            stage = new Stage();
            root = FXMLLoader.load(getClass().getResource("/edu/wpi/leviathans/views/EditData.fxml"));
            stage.setScene(new Scene(root));
            stage.setTitle("Node Editor");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(btnEdit.getScene().getWindow());
            stage.showAndWait();

        } else if (e.getSource() == btnLoad) {
            ArrayList<Row> data = populateRow();
            if(data != null)
                observableList = FXCollections.observableArrayList();
			loadData();
            stage = (Stage) btnLoad.getScene().getWindow();
            root = FXMLLoader.load(getClass().getResource("/edu/wpi/leviathans/views/LoadData.fxml"));
        } else if (e.getSource() == btnBack) {
            stage = (Stage) btnBack.getScene().getWindow();
            root = FXMLLoader.load(getClass().getResource("/edu/wpi/leviathans/views/DisplayData.fxml"));
            stage.close();
        } else if (e.getSource() == btnSearch) {
            search();
            stage = (Stage) btnBack.getScene().getWindow();
            root = FXMLLoader.load(getClass().getResource("/edu/wpi/leviathans/views/DisplayData.fxml"));
        } else if (e.getSource() == btnSave) {
            save(nodeEdit, nodeNum);
            download();
            stage = (Stage) btnSave.getScene().getWindow();
            root = FXMLLoader.load(getClass().getResource("/edu/wpi/leviathans/views/DisplayData.fxml"));
        } else if (e.getSource() == btnMap) {
            stage = new Stage();
            root = FXMLLoader.load(getClass().getResource("/edu/wpi/leviathans/views/mapViewer/MapViewer.fxml"));
            stage.setScene(new Scene(root));
            stage.setTitle("Map Viewer");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(btnMap.getScene().getWindow());
            stage.showAndWait();
        } else {
        	log.error("Invalid button ID");
		}
//CAUSED ERROR DOESNT SEEM TO BREAK ANYTHING WHEN GONE
//        if (e.getSource() != btnLoad && e.getSource() != btnSearch && e.getSource() != btnSave && e.getSource() != btnBack) {
//            Scene scene = new Scene(root);
//            stage.setScene(scene);
//            stage.show();
//        }

    }


    private void showNodeDetails(Row row) {
        if (row != null) {
            // Fill the textfield with info from the row object.
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
            PrintWriter pw = new PrintWriter("src/main/resources/edu/wpi/leviathans/csvFiles/PrototypeNodesEdit.csv");
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
            }
            pw.write(sb.toString());
            pw.close();
        } catch (Exception ex) {
        	ex.printStackTrace();
        }
    }

    private void search() {
        String name = searchText.getText();
        int x = 0;
        for (Row r : observableList) {
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
    }

    @FXML
    public void loadData() {
    	//TODO: deal with warnings

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
        DataDialogue dialogue = new DataDialogue();

        boolean hasData = dialogue.showDialogue(btnLoad.getScene().getWindow());

        if(hasData) {
            File nodesFile = dialogue.getNodeFile();

            CSVParser reader = new CSVParser(nodesFile);

            ArrayList<ArrayList<String>> data = reader.readCSVFile();
            ArrayList<Row> rows = new ArrayList<>();
            for (ArrayList<String> dataRow : data) {
                Row r = new Row(dataRow.get(0), dataRow.get(1), dataRow.get(2), dataRow.get(3),
                        dataRow.get(4), dataRow.get(5), dataRow.get(6), dataRow.get(7));
                rows.add(r);
            }
            return rows;
        }

        return null;
    }
}
