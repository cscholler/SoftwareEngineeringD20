package edu.wpi.leviathans.views;

import com.google.inject.Inject;

import edu.wpi.leviathans.util.Row;
import edu.wpi.leviathans.util.io.CSVParser;
import javafx.beans.value.ObservableValue;
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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Callback;
import lombok.extern.slf4j.Slf4j;

import edu.wpi.leviathans.services.db.DatabaseService;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;

@Slf4j
public class DatabaseViewController {
	@Inject
	DatabaseService db;

	@FXML private Button btnBack;
	@FXML private Button btnModify;
	@FXML private Button btnDownload;
	@FXML private Button btnDemonstration;
	@FXML private TableView table;

	@FXML private TableColumn colNodeID;
	@FXML private TableColumn colXCoord;
	@FXML private TableColumn colYCoord;
	@FXML private TableColumn colFloor;
	@FXML private TableColumn colBuilding;
	@FXML private TableColumn colNodeType;
	@FXML private TableColumn colLongName;
	@FXML private TableColumn colShortName;


	private ObservableList<Row> observableList = FXCollections.observableArrayList(populateRow());

	@FXML
	private void handleButtonAction(ActionEvent e) throws IOException {

		Stage stage;
		Parent root;

		if (e.getSource() == btnModify) {

			stage = (Stage) btnModify.getScene().getWindow();
			root = FXMLLoader.load(getClass().getResource("Modify.fxml"));

		}  else if (e.getSource() == btnDownload) {

			stage = (Stage) btnDownload.getScene().getWindow();
			root = FXMLLoader.load(getClass().getResource("Download.fxml"));

		} else if (e.getSource() == btnDemonstration){

			stage = (Stage) btnDemonstration.getScene().getWindow();
			root = FXMLLoader.load(getClass().getResource("Demonstration.fxml"));

		} else {
			stage = (Stage) btnBack.getScene().getWindow();
			root = FXMLLoader.load(getClass().getResource("Window.fxml"));
		}

		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.show();

	}

	@FXML
	private void loadData() {

		Group groupRoot = new Group();
		Stage stage = new Stage();

		colNodeID.setCellValueFactory(new PropertyValueFactory<>("nodeID"));
		colXCoord.setCellValueFactory(new PropertyValueFactory<>("xcoord"));
		colYCoord.setCellValueFactory(new PropertyValueFactory<>("ycoord"));
		colFloor.setCellValueFactory(new PropertyValueFactory<>("floor"));
		colBuilding.setCellValueFactory(new PropertyValueFactory<>("building"));
		colNodeType.setCellValueFactory(new PropertyValueFactory<>("nodeType"));
		colLongName.setCellValueFactory(new PropertyValueFactory<>("longName"));
		colShortName.setCellValueFactory(new PropertyValueFactory<>("shortName"));

		table.setItems(observableList);
		table.getColumns().addAll(colNodeID, colXCoord, colYCoord, colFloor, colBuilding, colNodeType, colLongName,
				colShortName);

		Scene scene = new Scene(groupRoot);
		stage.setScene(scene);
		stage.show();
	}

	private ArrayList<Row> populateRow(){
		CSVParser reader = new CSVParser();
		ArrayList<ArrayList<String>> data = reader.readCSVFile();
		ArrayList<Row> rows = new ArrayList<>();
		for (ArrayList<String> dataRow : data) {
			Row r = new Row(dataRow.get(0), dataRow.get(1), dataRow.get(2), dataRow.get(3),
					dataRow.get(4), dataRow.get(5), dataRow.get(6), dataRow.get(7));
			rows.add(r);
			//observableList.add(r);
		}
		return rows;
	}
}
