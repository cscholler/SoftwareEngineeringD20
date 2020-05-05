package edu.wpi.cs3733.d20.teamL.views.LoggedInView;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import edu.wpi.cs3733.d20.teamL.entities.*;
import edu.wpi.cs3733.d20.teamL.services.db.IDatabaseCache;
import edu.wpi.cs3733.d20.teamL.services.db.IDatabaseService;
import edu.wpi.cs3733.d20.teamL.util.FXMLLoaderFactory;
import edu.wpi.cs3733.d20.teamL.util.TableEntityWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.control.cell.TextFieldTreeTableCell;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;

import edu.wpi.cs3733.d20.teamL.services.users.ILoginManager;

import javax.inject.Inject;

@Slf4j
public class AdminLandingPageController implements Initializable {
    private FXMLLoaderFactory loaderFactory = new FXMLLoaderFactory();
    private ArrayList<String> tableNames = new ArrayList<>(Arrays.asList("Nodes", "Edges", "Gifts", "Users", "Doctors"));
    private final ObservableList<String> tableOptions = FXCollections.observableArrayList("Map Nodes", "Map Edges", "Gift Inventory", "User Information", "Doctor Information");
	@FXML
	private JFXComboBox<String> tableSelector;
    @FXML
	private JFXTreeTableView<TableEntityWrapper.TableNode> nodesTable;
	@FXML
	private JFXTreeTableView<TableEntityWrapper.TableEdge> edgesTable;
	@FXML
	private JFXTreeTableView<TableEntityWrapper.TableGift> giftsTable;
	@FXML
	private JFXTreeTableView<TableEntityWrapper.TableUser> usersTable;
	@FXML
	private JFXTreeTableView<TableEntityWrapper.TableDoctor> doctorsTable;
    @Inject
    private ILoginManager loginManager;
    @Inject
    private IDatabaseService db;
    @Inject
	private IDatabaseCache cache;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
		tableSelector.setItems(tableOptions);
		tableSelector.getSelectionModel().select(0);
		hideAllTablesExceptCurrent("Nodes");
		loadNodesTable();
    }

    @FXML
    public void logoutBtn() {
        loginManager.logOut(true);
        try {
            Parent root = loaderFactory.getFXMLLoader("map_viewer/MapViewer").load();
            loaderFactory.setupScene(new Scene(root));
        } catch (IOException ex) {
            log.error("Encountered IOException", ex);
        }
    }

    @FXML
    private void btnMapClicked() {
        try {
            Parent root = loaderFactory.getFXMLLoader("Admin/MapEditor").load();
            loaderFactory.setupPopup(new Stage(), new Scene(root));
        } catch (IOException ex) {
            log.error("Encountered IOException", ex);
        }
    }

    @FXML
    private void rebuildDatabaseClicked() {
        try {
            Parent root = loaderFactory.getFXMLLoader("Admin/databaseDialogue").load();
            loaderFactory.setupPopup(new Stage(), new Scene(root));
        } catch (IOException ex) {
            log.error("Encountered IOException", ex);
        }
    }

    @FXML
    private void addUserClicked() {
        try {
            System.out.println("Got here");
            Parent root = loaderFactory.getFXMLLoader("admin/AddPerson").load();
            loaderFactory.setupScene(new Scene(root));
        } catch (IOException ex) {
            log.error("Encountered IOException", ex);
        }
    }


    @FXML
    public void importDBClicked() {
        try {
            Parent root = loaderFactory.getFXMLLoader("dialogues/ImportDialogue").load();
            loaderFactory.setupPopup(new Stage(), new Scene(root));
        } catch (IOException ex) {
            log.error("Encountered IOException", ex);
        }
    }

    @FXML
    public void saveDBClicked() {
        try {
            Parent root = loaderFactory.getFXMLLoader("dialogues/ExportDialogue").load();
            loaderFactory.setupPopup(new Stage(), new Scene(root));
        } catch (IOException ex) {
            log.error("Encountered IOException", ex);
        }
    }

	@FXML
	public void importCSVClicked() {
		try {
			Parent root = loaderFactory.getFXMLLoader("dialogues/ImportDialogue").load();
			loaderFactory.setupPopup(new Stage(), new Scene(root));
		} catch (IOException ex) {
			log.error("Encountered IOException", ex);
		}
	}

	@FXML
	public void saveCSVClicked() {
		try {
			Parent root = loaderFactory.getFXMLLoader("dialogues/ExportDialogue").load();
			loaderFactory.setupPopup(new Stage(), new Scene(root));
		} catch (IOException ex) {
			log.error("Encountered IOException", ex);
		}
	}

    @FXML
	public void tableSelected() {
		if (tableSelector != null) {
			if (tableSelector.getValue() != null) {
				switch (tableSelector.getValue()) {
					default:
					case "Map Nodes": {
						hideAllTablesExceptCurrent("Nodes");
						loadNodesTable();
					}
					break;
					case "Map Edges": {
						hideAllTablesExceptCurrent("Edges");
						loadEdgesTable();
					}
					break;
					case "Gift Inventory": {
						hideAllTablesExceptCurrent("Gifts");
						loadGiftsTable();
					}
					break;
					case "User Information": {
						hideAllTablesExceptCurrent("Users");
						loadUsersTable();
					}
					break;
					case "Doctor Information": {
						hideAllTablesExceptCurrent("Doctors");
						loadDoctorsTable();
					}
				}
			}
		}
	}

	private void loadNodesTable() {
		JFXTreeTableColumn<TableEntityWrapper.TableNode, String> idCol = new JFXTreeTableColumn<>("id");
		idCol.setCellValueFactory((TreeTableColumn.CellDataFeatures<TableEntityWrapper.TableNode, String> param) -> {
			if (idCol.validateValue(param)) {
				return param.getValue().getValue().getID();
			} else {
				return idCol.getComputedValue(param);
			}
		});
		JFXTreeTableColumn<TableEntityWrapper.TableNode, String> xPosCol = new JFXTreeTableColumn<>("x_pos");
		xPosCol.setCellValueFactory((TreeTableColumn.CellDataFeatures<TableEntityWrapper.TableNode, String> param) -> {
			if (xPosCol.validateValue(param)) {
				return param.getValue().getValue().getXPos();
			} else {
				return xPosCol.getComputedValue(param);
			}
		});
		JFXTreeTableColumn<TableEntityWrapper.TableNode, String> yPosCol = new JFXTreeTableColumn<>("y_pos");
		yPosCol.setCellValueFactory((TreeTableColumn.CellDataFeatures<TableEntityWrapper.TableNode, String> param) -> {
			if (yPosCol.validateValue(param)) {
				return param.getValue().getValue().getYPos();
			} else {
				return yPosCol.getComputedValue(param);
			}
		});
		JFXTreeTableColumn<TableEntityWrapper.TableNode, String> floorCol = new JFXTreeTableColumn<>("floor");
		floorCol.setCellValueFactory((TreeTableColumn.CellDataFeatures<TableEntityWrapper.TableNode, String> param) -> {
			if (floorCol.validateValue(param)) {
				return param.getValue().getValue().getFloor();
			} else {
				return floorCol.getComputedValue(param);
			}
		});
		JFXTreeTableColumn<TableEntityWrapper.TableNode, String> buildingCol = new JFXTreeTableColumn<>("building");
		buildingCol.setCellValueFactory((TreeTableColumn.CellDataFeatures<TableEntityWrapper.TableNode, String> param) -> {
			if (buildingCol.validateValue(param)) {
				return param.getValue().getValue().getBuilding();
			} else {
				return buildingCol.getComputedValue(param);
			}
		});
		JFXTreeTableColumn<TableEntityWrapper.TableNode, String> typeCol = new JFXTreeTableColumn<>("type");
		typeCol.setCellValueFactory((TreeTableColumn.CellDataFeatures<TableEntityWrapper.TableNode, String> param) -> {
			if (typeCol.validateValue(param)) {
				return param.getValue().getValue().getType();
			} else {
				return typeCol.getComputedValue(param);
			}
		});
		JFXTreeTableColumn<TableEntityWrapper.TableNode, String> lNameCol = new JFXTreeTableColumn<>("l_name");
		lNameCol.setCellValueFactory((TreeTableColumn.CellDataFeatures<TableEntityWrapper.TableNode, String> param) -> {
			if (lNameCol.validateValue(param)) {
				return param.getValue().getValue().getLongName();
			} else {
				return lNameCol.getComputedValue(param);
			}
		});
		JFXTreeTableColumn<TableEntityWrapper.TableNode, String> sNameCol = new JFXTreeTableColumn<>("s_name");
		sNameCol.setCellValueFactory((TreeTableColumn.CellDataFeatures<TableEntityWrapper.TableNode, String> param) -> {
			if (sNameCol.validateValue(param)) {
				return param.getValue().getValue().getShortName();
			} else {
				return sNameCol.getComputedValue(param);
			}
		});

		ObservableList<TableEntityWrapper.TableNode> nodes = FXCollections.observableArrayList();
		for (Node node : cache.getNodeCache()) {
			nodes.add(new TableEntityWrapper.TableNode(node.getID(), String.valueOf(node.getPosition().getX()), String.valueOf(node.getPosition().getY()),
					String.valueOf(node.getFloor()), node.getBuilding(), node.getType(), node.getLongName(), node.getShortName()));
		}

		final TreeItem<TableEntityWrapper.TableNode> root = new RecursiveTreeItem<>(nodes, RecursiveTreeObject::getChildren);
		idCol.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());
		xPosCol.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());
		yPosCol.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());
		floorCol.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());
		buildingCol.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());
		typeCol.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());
		lNameCol.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());
		sNameCol.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());
		ArrayList<JFXTreeTableColumn<TableEntityWrapper.TableNode, String>> nodeCols = new ArrayList<>(Arrays.asList(idCol, xPosCol, yPosCol, floorCol, buildingCol, typeCol, lNameCol, sNameCol));
		nodesTable.getColumns().setAll(nodeCols);

		nodesTable.getColumns().addListener(new ListChangeListener<>() {
			private boolean isColSuspended;
			@Override
			public void onChanged(ListChangeListener.Change change) {
				change.next();
				if (change.wasReplaced() && !isColSuspended) {
					this.isColSuspended = true;
					nodesTable.getColumns().setAll(nodeCols);
					this.isColSuspended = false;
				}
			}
		});

		nodesTable.setRoot(root);
		nodesTable.setShowRoot(false);
	}

	private void loadEdgesTable() {
		JFXTreeTableColumn<TableEntityWrapper.TableEdge, String> idCol = new JFXTreeTableColumn<>("id");
		idCol.setCellValueFactory((TreeTableColumn.CellDataFeatures<TableEntityWrapper.TableEdge, String> param) -> {
			if (idCol.validateValue(param)) {
				return param.getValue().getValue().getID();
			} else {
				return idCol.getComputedValue(param);
			}
		});
		JFXTreeTableColumn<TableEntityWrapper.TableEdge, String> startNodeCol = new JFXTreeTableColumn<>("start_node");
		startNodeCol.setCellValueFactory((TreeTableColumn.CellDataFeatures<TableEntityWrapper.TableEdge, String> param) -> {
			if (startNodeCol.validateValue(param)) {
				return param.getValue().getValue().getSourceNode();
			} else {
				return startNodeCol.getComputedValue(param);
			}
		});
		JFXTreeTableColumn<TableEntityWrapper.TableEdge, String> endNodeCol = new JFXTreeTableColumn<>("end_node");
		endNodeCol.setCellValueFactory((TreeTableColumn.CellDataFeatures<TableEntityWrapper.TableEdge, String> param) -> {
			if (endNodeCol.validateValue(param)) {
				return param.getValue().getValue().getDestNode();
			} else {
				return endNodeCol.getComputedValue(param);
			}
		});
		ObservableList<TableEntityWrapper.TableEdge> edges = FXCollections.observableArrayList();
		for (Edge edge : cache.getEdgeCache()) {
			edges.add(new TableEntityWrapper.TableEdge(edge.getID(), edge.getSource().getID(), edge.getDestination().getID()));
		}

		final TreeItem<TableEntityWrapper.TableEdge> root = new RecursiveTreeItem<>(edges, RecursiveTreeObject::getChildren);
		idCol.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());
		startNodeCol.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());
		endNodeCol.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());
		ArrayList<JFXTreeTableColumn<TableEntityWrapper.TableEdge, String>> edgeCols = new ArrayList<>(Arrays.asList(idCol, startNodeCol, endNodeCol));
		edgesTable.getColumns().setAll(edgeCols);

		edgesTable.getColumns().addListener(new ListChangeListener<>() {
			private boolean isColSuspended;
			@Override
			public void onChanged(ListChangeListener.Change change) {
				change.next();
				if (change.wasReplaced() && !isColSuspended) {
					this.isColSuspended = true;
					edgesTable.getColumns().setAll(edgeCols);
					this.isColSuspended = false;
				}
			}
		});

		edgesTable.setRoot(root);
		edgesTable.setShowRoot(false);
	}

	private void loadGiftsTable() {
		JFXTreeTableColumn<TableEntityWrapper.TableGift, String> idCol = new JFXTreeTableColumn<>("id");
		idCol.setCellValueFactory((TreeTableColumn.CellDataFeatures<TableEntityWrapper.TableGift, String> param) -> {
			if (idCol.validateValue(param)) {
				return param.getValue().getValue().getID();
			} else {
				return idCol.getComputedValue(param);
			}
		});
		JFXTreeTableColumn<TableEntityWrapper.TableGift, String> typeCol = new JFXTreeTableColumn<>("type");
		typeCol.setCellValueFactory((TreeTableColumn.CellDataFeatures<TableEntityWrapper.TableGift, String> param) -> {
			if (typeCol.validateValue(param)) {
				return param.getValue().getValue().getType();
			} else {
				return typeCol.getComputedValue(param);
			}
		});
		JFXTreeTableColumn<TableEntityWrapper.TableGift, String> subtypeCol = new JFXTreeTableColumn<>("subtype");
		subtypeCol.setCellValueFactory((TreeTableColumn.CellDataFeatures<TableEntityWrapper.TableGift, String> param) -> {
			if (subtypeCol.validateValue(param)) {
				return param.getValue().getValue().getSubtype();
			} else {
				return subtypeCol.getComputedValue(param);
			}
		});
		JFXTreeTableColumn<TableEntityWrapper.TableGift, String> descCol = new JFXTreeTableColumn<>("description");
		descCol.setCellValueFactory((TreeTableColumn.CellDataFeatures<TableEntityWrapper.TableGift, String> param) -> {
			if (descCol.validateValue(param)) {
				return param.getValue().getValue().getDesc();
			} else {
				return descCol.getComputedValue(param);
			}
		});
		JFXTreeTableColumn<TableEntityWrapper.TableGift, String> invCol = new JFXTreeTableColumn<>("inventory");
		invCol.setCellValueFactory((TreeTableColumn.CellDataFeatures<TableEntityWrapper.TableGift, String> param) -> {
			if (invCol.validateValue(param)) {
				return param.getValue().getValue().getInventory();
			} else {
				return invCol.getComputedValue(param);
			}
		});
		ObservableList<TableEntityWrapper.TableGift> gifts = FXCollections.observableArrayList();
		for (Gift gift : cache.getGiftCache()) {
			gifts.add(new TableEntityWrapper.TableGift(gift.getID(), gift.getType(), gift.getSubtype(), gift.getDescription(), gift.getInventory()));
		}

		final TreeItem<TableEntityWrapper.TableGift> root = new RecursiveTreeItem<>(gifts, RecursiveTreeObject::getChildren);
		typeCol.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());
		subtypeCol.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());
		descCol.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());
		invCol.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());
		ArrayList<JFXTreeTableColumn<TableEntityWrapper.TableGift, String>> giftCols = new ArrayList<>(Arrays.asList(idCol, typeCol, subtypeCol, descCol, invCol));
		giftsTable.getColumns().setAll(giftCols);

		giftsTable.getColumns().addListener(new ListChangeListener<>() {
			private boolean isColSuspended;
			@Override
			public void onChanged(ListChangeListener.Change change) {
				change.next();
				if (change.wasReplaced() && !isColSuspended) {
					this.isColSuspended = true;
					giftsTable.getColumns().setAll(giftCols);
					this.isColSuspended = false;
				}
			}
		});

		giftsTable.setRoot(root);
		giftsTable.setShowRoot(false);
	}

	private void loadUsersTable() {
		JFXTreeTableColumn<TableEntityWrapper.TableUser, String> idCol = new JFXTreeTableColumn<>("id");
		idCol.setCellValueFactory((TreeTableColumn.CellDataFeatures<TableEntityWrapper.TableUser, String> param) -> {
			if (idCol.validateValue(param)) {
				return param.getValue().getValue().getID();
			} else {
				return idCol.getComputedValue(param);
			}
		});

		JFXTreeTableColumn<TableEntityWrapper.TableUser, String> fNameCol = new JFXTreeTableColumn<>("f_name");
		fNameCol.setCellValueFactory((TreeTableColumn.CellDataFeatures<TableEntityWrapper.TableUser, String> param) -> {
			if (fNameCol.validateValue(param)) {
				return param.getValue().getValue().getFName();
			} else {
				return fNameCol.getComputedValue(param);
			}
		});

		JFXTreeTableColumn<TableEntityWrapper.TableUser, String> lNameCol = new JFXTreeTableColumn<>("l_name");
		lNameCol.setCellValueFactory((TreeTableColumn.CellDataFeatures<TableEntityWrapper.TableUser, String> param) -> {
			if (lNameCol.validateValue(param)) {
				return param.getValue().getValue().getLName();
			} else {
				return lNameCol.getComputedValue(param);
			}
		});

		JFXTreeTableColumn<TableEntityWrapper.TableUser, String> usernameCol = new JFXTreeTableColumn<>("username");
		usernameCol.setCellValueFactory((TreeTableColumn.CellDataFeatures<TableEntityWrapper.TableUser, String> param) -> {
			if (usernameCol.validateValue(param)) {
				return param.getValue().getValue().getUsername();
			} else {
				return usernameCol.getComputedValue(param);
			}
		});

		JFXTreeTableColumn<TableEntityWrapper.TableUser, String> acctTypeCol = new JFXTreeTableColumn<>("acct_type");
		acctTypeCol.setCellValueFactory((TreeTableColumn.CellDataFeatures<TableEntityWrapper.TableUser, String> param) -> {
			if (acctTypeCol.validateValue(param)) {
				return param.getValue().getValue().getAcctType();
			} else {
				return acctTypeCol.getComputedValue(param);
			}
		});

		JFXTreeTableColumn<TableEntityWrapper.TableUser, String> servicesCol = new JFXTreeTableColumn<>("services");
		servicesCol.setCellValueFactory((TreeTableColumn.CellDataFeatures<TableEntityWrapper.TableUser, String> param) -> {
			if (servicesCol.validateValue(param)) {
				return param.getValue().getValue().getServices();
			} else {
				return servicesCol.getComputedValue(param);
			}
		});

		JFXTreeTableColumn<TableEntityWrapper.TableUser, String> managerCol = new JFXTreeTableColumn<>("manager");
		managerCol.setCellValueFactory((TreeTableColumn.CellDataFeatures<TableEntityWrapper.TableUser, String> param) -> {
			if (managerCol.validateValue(param)) {
				return param.getValue().getValue().getManager();
			} else {
				return managerCol.getComputedValue(param);
			}
		});

		ObservableList<TableEntityWrapper.TableUser> users = FXCollections.observableArrayList();
		for (User user : cache.getUserCache()) {
			users.add(new TableEntityWrapper.TableUser(user.getID(), user.getFName(), user.getLName(), user.getUsername(), user.getAcctType(), user.getServices(), user.getDept()));
		}

		final TreeItem<TableEntityWrapper.TableUser> root = new RecursiveTreeItem<>(users, RecursiveTreeObject::getChildren);
		fNameCol.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());
		lNameCol.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());
		usernameCol.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());
		acctTypeCol.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());
		servicesCol.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());
		managerCol.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());
		ArrayList<JFXTreeTableColumn<TableEntityWrapper.TableUser, String>> userCols = new ArrayList<>(Arrays.asList(idCol, fNameCol, lNameCol, usernameCol, acctTypeCol, servicesCol, managerCol));
		usersTable.getColumns().setAll(userCols);

		usersTable.getColumns().addListener(new ListChangeListener<>() {
			private boolean isColSuspended;
			@Override
			public void onChanged(ListChangeListener.Change change) {
				change.next();
				if (change.wasReplaced() && !isColSuspended) {
					this.isColSuspended = true;
					usersTable.getColumns().setAll(userCols);
					this.isColSuspended = false;
				}
			}
		});

		usersTable.setRoot(root);
		usersTable.setShowRoot(false);
	}

	private void loadDoctorsTable() {
		JFXTreeTableColumn<TableEntityWrapper.TableDoctor, String> idCol = new JFXTreeTableColumn<>("id");
		idCol.setCellValueFactory((TreeTableColumn.CellDataFeatures<TableEntityWrapper.TableDoctor, String> param) -> {
			if (idCol.validateValue(param)) {
				return param.getValue().getValue().getID();
			} else {
				return idCol.getComputedValue(param);
			}
		});
		JFXTreeTableColumn<TableEntityWrapper.TableDoctor, String> fNameCol = new JFXTreeTableColumn<>("f_name");
		fNameCol.setCellValueFactory((TreeTableColumn.CellDataFeatures<TableEntityWrapper.TableDoctor, String> param) -> {
			if (fNameCol.validateValue(param)) {
				return param.getValue().getValue().getFName();
			} else {
				return fNameCol.getComputedValue(param);
			}
		});
		JFXTreeTableColumn<TableEntityWrapper.TableDoctor, String> lNameCol = new JFXTreeTableColumn<>("l_name");
		lNameCol.setCellValueFactory((TreeTableColumn.CellDataFeatures<TableEntityWrapper.TableDoctor, String> param) -> {
			if (lNameCol.validateValue(param)) {
				return param.getValue().getValue().getLName();
			} else {
				return lNameCol.getComputedValue(param);
			}
		});
		JFXTreeTableColumn<TableEntityWrapper.TableDoctor, String> usernameCol = new JFXTreeTableColumn<>("username");
		usernameCol.setCellValueFactory((TreeTableColumn.CellDataFeatures<TableEntityWrapper.TableDoctor, String> param) -> {
			if (usernameCol.validateValue(param)) {
				return param.getValue().getValue().getUsername();
			} else {
				return usernameCol.getComputedValue(param);
			}
		});
		JFXTreeTableColumn<TableEntityWrapper.TableDoctor, String> officeIDCol = new JFXTreeTableColumn<>("office_id");
		officeIDCol.setCellValueFactory((TreeTableColumn.CellDataFeatures<TableEntityWrapper.TableDoctor, String> param) -> {
			if (officeIDCol.validateValue(param)) {
				return param.getValue().getValue().getOfficeID();
			} else {
				return officeIDCol.getComputedValue(param);
			}
		});
		JFXTreeTableColumn<TableEntityWrapper.TableDoctor, String> addInfoCol = new JFXTreeTableColumn<>("addl_info");
		addInfoCol.setCellValueFactory((TreeTableColumn.CellDataFeatures<TableEntityWrapper.TableDoctor, String> param) -> {
			if (addInfoCol.validateValue(param)) {
				return param.getValue().getValue().getAddInfo();
			} else {
				return addInfoCol.getComputedValue(param);
			}
		});

		ObservableList<TableEntityWrapper.TableDoctor> doctors = FXCollections.observableArrayList();
		for (Doctor doctor : cache.getDoctorCache()) {
			doctors.add(new TableEntityWrapper.TableDoctor(doctor.getID(), doctor.getFName(), doctor.getLName(), doctor.getUsername(), doctor.getOfficeID(), doctor.getAddInfo()));
		}

		final TreeItem<TableEntityWrapper.TableDoctor> root = new RecursiveTreeItem<>(doctors, RecursiveTreeObject::getChildren);
		idCol.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());
		fNameCol.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());
		lNameCol.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());
		usernameCol.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());
		officeIDCol.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());
		addInfoCol.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());
		ArrayList<JFXTreeTableColumn<TableEntityWrapper.TableDoctor, String>> doctorCols = new ArrayList<>(Arrays.asList(idCol, fNameCol, lNameCol, usernameCol, officeIDCol, addInfoCol));
		doctorsTable.getColumns().setAll(doctorCols);

		doctorsTable.getColumns().addListener(new ListChangeListener<>() {
			private boolean isColSuspended;
			@Override
			public void onChanged(ListChangeListener.Change change) {
				change.next();
				if (change.wasReplaced() && !isColSuspended) {
					this.isColSuspended = true;
					doctorsTable.getColumns().setAll(doctorCols);
					this.isColSuspended = false;
				}
			}
		});

		doctorsTable.setRoot(root);
		doctorsTable.setShowRoot(false);
	}

	private void hideAllTablesExceptCurrent(String tableName) {
		switch (tableName) {
			case "Nodes": {
				setTableVisible(nodesTable, true);
				setTableVisible(edgesTable, false);
				setTableVisible(giftsTable, false);
				setTableVisible(usersTable, false);
				setTableVisible(doctorsTable, false);
			}
			break;
			case "Edges": {
				setTableVisible(nodesTable, false);
				setTableVisible(edgesTable, true);
				setTableVisible(giftsTable, false);
				setTableVisible(usersTable, false);
				setTableVisible(doctorsTable, false);
			}
			break;
			case "Gifts": {
				setTableVisible(nodesTable, false);
				setTableVisible(edgesTable, false);
				setTableVisible(giftsTable, true);
				setTableVisible(usersTable, false);
				setTableVisible(doctorsTable, false);
			}
			break;
			case "Users": {
				setTableVisible(nodesTable, false);
				setTableVisible(edgesTable, false);
				setTableVisible(giftsTable, false);
				setTableVisible(usersTable, true);
				setTableVisible(doctorsTable, false);
			}
			break;
			case "Doctors": {
				setTableVisible(nodesTable, false);
				setTableVisible(edgesTable, false);
				setTableVisible(giftsTable, false);
				setTableVisible(usersTable, false);
				setTableVisible(doctorsTable, true);
			}
		}
	}

	private void setTableVisible(JFXTreeTableView<?> table, boolean visible) {
		table.setVisible(visible);
		table.setMouseTransparent(!visible);
	}
}
