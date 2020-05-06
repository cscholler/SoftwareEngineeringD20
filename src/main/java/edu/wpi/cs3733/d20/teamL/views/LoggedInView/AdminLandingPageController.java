package edu.wpi.cs3733.d20.teamL.views.LoggedInView;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import edu.wpi.cs3733.d20.teamL.services.db.IDatabaseCache;
import edu.wpi.cs3733.d20.teamL.services.db.IDatabaseService;
import edu.wpi.cs3733.d20.teamL.util.FXMLLoaderFactory;
import edu.wpi.cs3733.d20.teamL.util.TableEntityWrapper;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.cell.TextFieldTreeTableCell;
import javafx.stage.Stage;
import javafx.util.Callback;

import com.google.inject.Inject;

import com.jfoenix.controls.JFXButton;

import lombok.extern.slf4j.Slf4j;

import edu.wpi.cs3733.d20.teamL.entities.Doctor;
import edu.wpi.cs3733.d20.teamL.entities.Edge;
import edu.wpi.cs3733.d20.teamL.entities.Gift;
import edu.wpi.cs3733.d20.teamL.entities.Node;
import edu.wpi.cs3733.d20.teamL.entities.User;
import edu.wpi.cs3733.d20.teamL.services.db.DBConstants;
import edu.wpi.cs3733.d20.teamL.services.db.SQLEntry;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

import edu.wpi.cs3733.d20.teamL.services.users.ILoginManager;

@Slf4j
public class AdminLandingPageController implements Initializable {
    private FXMLLoaderFactory loaderFactory = new FXMLLoaderFactory();
    private final ObservableList<String> tableOptions = FXCollections.observableArrayList("Map Nodes", "Map Edges", "Gift Inventory", "User Information", "Doctor Information");
	private ArrayList<TableEntityWrapper.TableNode> editedNodes = new ArrayList<>();
	private ArrayList<TableEntityWrapper.TableEdge> editedEdges = new ArrayList<>();
	private ArrayList<TableEntityWrapper.TableGift> editedGifts = new ArrayList<>();
    private ArrayList<TableEntityWrapper.TableUser> editedUsers = new ArrayList<>();
    private ArrayList<TableEntityWrapper.TableDoctor> editedDoctors = new ArrayList<>();
	private ArrayList<TableEntityWrapper.TableNode> deletedNodes = new ArrayList<>();
	private ArrayList<TableEntityWrapper.TableEdge> deletedEdges = new ArrayList<>();
	private ArrayList<TableEntityWrapper.TableGift> deletedGifts = new ArrayList<>();
	private ArrayList<TableEntityWrapper.TableUser> deletedUsers = new ArrayList<>();
	private ArrayList<TableEntityWrapper.TableDoctor> deletedDoctors = new ArrayList<>();
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
	@FXML
	private JFXComboBox<String> tableSelector;
	@FXML
	private Label timeLabel;
    @Inject
    private ILoginManager loginManager;
    @Inject
    private IDatabaseService db;
    @Inject
	private IDatabaseCache cache;

	private final Timer timer = new Timer();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
		timer.scheduleAtFixedRate(timerWrapper(this::updateTime), 0, 1000);
		tableSelector.setItems(tableOptions);
		tableSelector.getSelectionModel().select(0);
		hideAllTablesExceptCurrent("Nodes");
		loadNodesTable();
    }

	@FXML
	public void tableSelected() {
		loadCurrentTable();
	}

    @FXML
    public void btnLogoutClicked() {
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
            Parent root = loaderFactory.getFXMLLoader("admin/AddPerson").load();
			loaderFactory.setupPopup(new Stage(), new Scene(root));
        } catch (IOException ex) {
            log.error("Encountered IOException", ex);
        }
    }

	@FXML
	private void btnAddDoctorClicked() {
		try {
			Parent root = loaderFactory.getFXMLLoader("AddDoctor").load();
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
	public void btnImportClicked() {
		try {
			Parent root = loaderFactory.getFXMLLoader("dialogues/ImportDialogue").load();
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
	public void btnExportClicked() {
		try {
			Parent root = loaderFactory.getFXMLLoader("dialogues/ExportDialogue").load();
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
    private void btnRebuildClicked() {
        try {
            Parent root = loaderFactory.getFXMLLoader("Admin/databaseDialogue").load();
            loaderFactory.setupPopup(new Stage(), new Scene(root));
        } catch (IOException ex) {
            log.error("Encountered IOException", ex);
        }
    }

    @FXML
	private void btnSaveClicked() {
		ArrayList<SQLEntry> updates = new ArrayList<>();
		for (TableEntityWrapper.TableDoctor doctor : deletedDoctors) {
			updates.add(new SQLEntry(DBConstants.REMOVE_DOCTOR, new ArrayList<>(Collections.singletonList(doctor.getID().get()))));
		}
		for (TableEntityWrapper.TableDoctor doctor : editedDoctors) {
			updates.add(new SQLEntry(DBConstants.UPDATE_DOCTOR, new ArrayList<>(Arrays.asList(doctor.getFirstName().get(), doctor.getLastName().get(), doctor.getOfficeID().get(),
					doctor.getAddInfo().get(), doctor.getID().get()))));
		}

		for (TableEntityWrapper.TableEdge edge : deletedEdges) {
			updates.add(new SQLEntry(DBConstants.REMOVE_EDGE, new ArrayList<>(Collections.singletonList(edge.getID().get()))));
		}
		for (TableEntityWrapper.TableEdge edge : editedEdges) {
			updates.add(new SQLEntry(DBConstants.UPDATE_EDGE, new ArrayList<>(Arrays.asList(edge.getSourceNode().get(), edge.getDestNode().get(), edge.getID().get()))));
		}

		for (TableEntityWrapper.TableNode node : deletedNodes) {
			updates.add(new SQLEntry(DBConstants.REMOVE_NODE, new ArrayList<>(Collections.singletonList(node.getID().get()))));
		}
		for (TableEntityWrapper.TableNode node : editedNodes) {
			updates.add(new SQLEntry(DBConstants.UPDATE_NODE, new ArrayList<>(Arrays.asList(node.getXPos().get(), node.getYPos().get(), node.getFloor().get(),
					node.getBuilding().get(), node.getType().get(), node.getLongName().get(), node.getShortName().get(), node.getID().get()))));
		}

		for (TableEntityWrapper.TableGift gift : deletedGifts) {
			updates.add(new SQLEntry(DBConstants.REMOVE_GIFT, new ArrayList<>(Collections.singletonList(gift.getID().get()))));
		}
		for (TableEntityWrapper.TableGift gift : editedGifts) {
			updates.add(new SQLEntry(DBConstants.UPDATE_GIFT, new ArrayList<>(Arrays.asList(gift.getType().get(), gift.getSubtype().get(), gift.getDescription().get(),
					gift.getInventory().get(), gift.getID().get()))));
		}

		for (TableEntityWrapper.TableUser user : deletedUsers) {
			updates.add(new SQLEntry(DBConstants.REMOVE_USER, new ArrayList<>(Collections.singletonList(user.getID().get()))));
		}
		for (TableEntityWrapper.TableUser user : editedUsers) {
			updates.add(new SQLEntry(DBConstants.UPDATE_USER, new ArrayList<>(Arrays.asList(user.getFName().get(), user.getLName().get(), user.getUsername().get(),
					user.getAcctType().get(), user.getServices().get(), user.getManager().get(), user.getID().get()))));
		}

		// TODO: run asynchronously
		db.executeUpdates(updates);
		cache.cacheAllFromDB();
		loadCurrentTable();
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

		JFXTreeTableColumn<TableEntityWrapper.TableNode, String> removeCol = new JFXTreeTableColumn<>("");
		removeCol.setPrefWidth(50);

		Callback<TreeTableColumn<TableEntityWrapper.TableNode, String>, TreeTableCell<TableEntityWrapper.TableNode, String>> removeColCellFactory = new Callback<>() {
			@Override
			public TreeTableCell<TableEntityWrapper.TableNode, String> call(final TreeTableColumn<TableEntityWrapper.TableNode, String> param) {
				return new TreeTableCell<>() {
					final JFXButton btn = new JFXButton("X");;
					{
						btn.setOnAction(event -> {
							deletedNodes.add(nodesTable.getTreeItem(getIndex()).getValue());
							nodesTable.getTreeItem(getIndex()).getParent().getChildren().remove(nodesTable.getTreeItem(getIndex()));
						});
					}

					@Override
					public void updateItem(String item, boolean empty) {
						super.updateItem(item, empty);
						if (empty) {
							setGraphic(null);
						} else {
							btn.setButtonType(JFXButton.ButtonType.RAISED);
							setGraphic(btn);
						}
						setText(null);
					}
				};
			}
		};

		ObservableList<TableEntityWrapper.TableNode> nodes = FXCollections.observableArrayList();
		for (Node node : cache.getNodeCache()) {
			nodes.add(new TableEntityWrapper.TableNode(node.getID(), String.valueOf(node.getPosition().getX()), String.valueOf(node.getPosition().getY()),
					String.valueOf(node.getFloor()), node.getBuilding(), node.getType(), node.getLongName(), node.getShortName()));
		}

		final TreeItem<TableEntityWrapper.TableNode> root = new RecursiveTreeItem<>(nodes, RecursiveTreeObject::getChildren);
		idCol.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());

		xPosCol.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());
		xPosCol.setOnEditCommit(event -> {
			TableEntityWrapper.TableNode editedNode = event.getRowValue().getValue();
			editedNode.setXPos(event.getNewValue());
			if (!editedNodes.contains(editedNode)) {
				editedNodes.add(editedNode);
			}
		});

		yPosCol.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());
		yPosCol.setOnEditCommit(event -> {
			TableEntityWrapper.TableNode editedNode = event.getRowValue().getValue();
			editedNode.setYPos(event.getNewValue());
			if (!editedNodes.contains(editedNode)) {
				editedNodes.add(editedNode);
			}
		});

		floorCol.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());
		floorCol.setOnEditCommit(event -> {
			TableEntityWrapper.TableNode editedNode = event.getRowValue().getValue();
			editedNode.setFloor(event.getNewValue());
			if (!editedNodes.contains(editedNode)) {
				editedNodes.add(editedNode);
			}
		});

		buildingCol.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());
		buildingCol.setOnEditCommit(event -> {
			TableEntityWrapper.TableNode editedNode = event.getRowValue().getValue();
			editedNode.setBuilding(event.getNewValue());
			if (!editedNodes.contains(editedNode)) {
				editedNodes.add(editedNode);
			}
		});

		typeCol.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());
		typeCol.setOnEditCommit(event -> {
			TableEntityWrapper.TableNode editedNode = event.getRowValue().getValue();
			editedNode.setType(event.getNewValue());
			if (!editedNodes.contains(editedNode)) {
				editedNodes.add(editedNode);
			}
		});

		lNameCol.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());
		lNameCol.setOnEditCommit(event -> {
			TableEntityWrapper.TableNode editedNode = event.getRowValue().getValue();
			editedNode.setLongName(event.getNewValue());
			if (!editedNodes.contains(editedNode)) {
				editedNodes.add(editedNode);
			}
		});

		sNameCol.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());
		sNameCol.setOnEditCommit(event -> {
			TableEntityWrapper.TableNode editedNode = event.getRowValue().getValue();
			editedNode.setShortName(event.getNewValue());
			if (!editedNodes.contains(editedNode)) {
				editedNodes.add(editedNode);
			}
		});

		removeCol.setCellFactory(removeColCellFactory);

		ArrayList<JFXTreeTableColumn<TableEntityWrapper.TableNode, String>> nodeCols = new ArrayList<>(Arrays.asList(idCol, xPosCol, yPosCol, floorCol, buildingCol, typeCol, lNameCol, sNameCol, removeCol));
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

		JFXTreeTableColumn<TableEntityWrapper.TableEdge, String> removeCol = new JFXTreeTableColumn<>("");
		removeCol.setPrefWidth(50);

		Callback<TreeTableColumn<TableEntityWrapper.TableEdge, String>, TreeTableCell<TableEntityWrapper.TableEdge, String>> removeColCellFactory = new Callback<>() {
			@Override
			public TreeTableCell<TableEntityWrapper.TableEdge, String> call(final TreeTableColumn<TableEntityWrapper.TableEdge, String> param) {
				return new TreeTableCell<>() {
					final JFXButton btn = new JFXButton("X");
					{
						btn.setOnAction(event -> {
							deletedEdges.add(edgesTable.getTreeItem(getIndex()).getValue());
							edgesTable.getTreeItem(getIndex()).getParent().getChildren().remove(edgesTable.getTreeItem(getIndex()));
						});
					}

					@Override
					public void updateItem(String item, boolean empty) {
						super.updateItem(item, empty);
						if (empty) {
							setGraphic(null);
						} else {
							btn.setButtonType(JFXButton.ButtonType.RAISED);
							setGraphic(btn);
						}
						setText(null);
					}
				};
			}
		};

		ObservableList<TableEntityWrapper.TableEdge> edges = FXCollections.observableArrayList();
		for (Edge edge : cache.getEdgeCache()) {
			edges.add(new TableEntityWrapper.TableEdge(edge.getID(), edge.getSource().getID(), edge.getDestination().getID()));
		}

		final TreeItem<TableEntityWrapper.TableEdge> root = new RecursiveTreeItem<>(edges, RecursiveTreeObject::getChildren);
		idCol.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());

		startNodeCol.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());
		startNodeCol.setOnEditCommit(event -> {
			TableEntityWrapper.TableEdge editedEdge = event.getRowValue().getValue();
			editedEdge.setSourceNode(event.getNewValue());
			if (!editedEdges.contains(editedEdge)) {
				editedEdges.add(editedEdge);
			}
		});

		endNodeCol.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());
		endNodeCol.setOnEditCommit(event -> {
			TableEntityWrapper.TableEdge editedEdge = event.getRowValue().getValue();
			editedEdge.setDestNode(event.getNewValue());
			if (!editedEdges.contains(editedEdge)) {
				editedEdges.add(editedEdge);
			}
		});

		removeCol.setCellFactory(removeColCellFactory);

		ArrayList<JFXTreeTableColumn<TableEntityWrapper.TableEdge, String>> edgeCols = new ArrayList<>(Arrays.asList(idCol, startNodeCol, endNodeCol, removeCol));
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
				return param.getValue().getValue().getDescription();
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

		JFXTreeTableColumn<TableEntityWrapper.TableGift, String> removeCol = new JFXTreeTableColumn<>("");
		removeCol.setPrefWidth(50);

		Callback<TreeTableColumn<TableEntityWrapper.TableGift, String>, TreeTableCell<TableEntityWrapper.TableGift, String>> removeColCellFactory = new Callback<>() {
			@Override
			public TreeTableCell<TableEntityWrapper.TableGift, String> call(final TreeTableColumn<TableEntityWrapper.TableGift, String> param) {
				return new TreeTableCell<>() {
					final JFXButton btn = new JFXButton("X");;
					{
						btn.setOnAction(event -> {
							deletedGifts.add(giftsTable.getTreeItem(getIndex()).getValue());
							giftsTable.getTreeItem(getIndex()).getParent().getChildren().remove(giftsTable.getTreeItem(getIndex()));
						});
					}

					@Override
					public void updateItem(String item, boolean empty) {
						super.updateItem(item, empty);
						if (empty) {
							setGraphic(null);
						} else {
							btn.setButtonType(JFXButton.ButtonType.RAISED);
							setGraphic(btn);
						}
						setText(null);
					}
				};
			}
		};

		ObservableList<TableEntityWrapper.TableGift> gifts = FXCollections.observableArrayList();
		for (Gift gift : cache.getGiftCache()) {
			gifts.add(new TableEntityWrapper.TableGift(gift.getID(), gift.getType(), gift.getSubtype(), gift.getDescription(), gift.getInventory()));
		}

		final TreeItem<TableEntityWrapper.TableGift> root = new RecursiveTreeItem<>(gifts, RecursiveTreeObject::getChildren);
		typeCol.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());
		typeCol.setOnEditCommit(event -> {
			TableEntityWrapper.TableGift editedGift = event.getRowValue().getValue();
			editedGift.setType(event.getNewValue());
			if (!editedGifts.contains(editedGift)) {
				editedGifts.add(editedGift);
			}
		});
		subtypeCol.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());
		subtypeCol.setOnEditCommit(event -> {
			TableEntityWrapper.TableGift editedGift = event.getRowValue().getValue();
			editedGift.setSubtype(event.getNewValue());
			if (!editedGifts.contains(editedGift)) {
				editedGifts.add(editedGift);
			}
		});
		descCol.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());
		descCol.setOnEditCommit(event -> {
			TableEntityWrapper.TableGift editedGift = event.getRowValue().getValue();
			editedGift.setDesc(event.getNewValue());
			if (!editedGifts.contains(editedGift)) {
				editedGifts.add(editedGift);
			}
		});
		invCol.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());
		invCol.setOnEditCommit(event -> {
			TableEntityWrapper.TableGift editedGift = event.getRowValue().getValue();
			editedGift.setInventory(event.getNewValue());
			if (!editedGifts.contains(editedGift)) {
				editedGifts.add(editedGift);
			}
		});

		removeCol.setCellFactory(removeColCellFactory);

		ArrayList<JFXTreeTableColumn<TableEntityWrapper.TableGift, String>> giftCols = new ArrayList<>(Arrays.asList(idCol, typeCol, subtypeCol, descCol, invCol, removeCol));
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

		JFXTreeTableColumn<TableEntityWrapper.TableUser, String> removeCol = new JFXTreeTableColumn<>("");
		removeCol.setPrefWidth(50);

		Callback<TreeTableColumn<TableEntityWrapper.TableUser, String>, TreeTableCell<TableEntityWrapper.TableUser, String>> removeColCellFactory = new Callback<>() {
			@Override
			public TreeTableCell<TableEntityWrapper.TableUser, String> call(final TreeTableColumn<TableEntityWrapper.TableUser, String> param) {
				return new TreeTableCell<>() {
					final JFXButton btn = new JFXButton("X");;
					{
						btn.setOnAction(event -> {
							deletedUsers.add(usersTable.getTreeItem(getIndex()).getValue());
							usersTable.getTreeItem(getIndex()).getParent().getChildren().remove(usersTable.getTreeItem(getIndex()));
						});
					}

					@Override
					public void updateItem(String item, boolean empty) {
						super.updateItem(item, empty);
						if (empty) {
							setGraphic(null);
						} else {
							btn.setButtonType(JFXButton.ButtonType.RAISED);
							setGraphic(btn);
						}
						setText(null);
					}
				};
			}
		};

		ObservableList<TableEntityWrapper.TableUser> users = FXCollections.observableArrayList();
		for (User user : cache.getUserCache()) {
			users.add(new TableEntityWrapper.TableUser(user.getID(), user.getFName(), user.getLName(), user.getUsername(), user.getAcctType(), user.getServices(), user.getDept()));
		}

		final TreeItem<TableEntityWrapper.TableUser> root = new RecursiveTreeItem<>(users, RecursiveTreeObject::getChildren);
		fNameCol.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());
		fNameCol.setOnEditCommit(event -> {
			TableEntityWrapper.TableUser editedUser = event.getRowValue().getValue();
			editedUser.setFName(event.getNewValue());
			if (!editedUsers.contains(editedUser)) {
				editedUsers.add(editedUser);
			}
		});

		lNameCol.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());
		lNameCol.setOnEditCommit(event -> {
			TableEntityWrapper.TableUser editedUser = event.getRowValue().getValue();
			editedUser.setLName(event.getNewValue());
			if (!editedUsers.contains(editedUser)) {
				editedUsers.add(editedUser);
			}
		});

		usernameCol.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());
		usernameCol.setOnEditCommit(event -> {
			TableEntityWrapper.TableUser editedUser = event.getRowValue().getValue();
			editedUser.setUsername(event.getNewValue());
			if (!editedUsers.contains(editedUser)) {
				editedUsers.add(editedUser);
			}
		});

		acctTypeCol.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());
		acctTypeCol.setOnEditCommit(event -> {
			TableEntityWrapper.TableUser editedUser = event.getRowValue().getValue();
			editedUser.setAcctType(event.getNewValue());
			if (!editedUsers.contains(editedUser)) {
				editedUsers.add(editedUser);
			}
		});

		servicesCol.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());
		servicesCol.setOnEditCommit(event -> {
			TableEntityWrapper.TableUser editedUser = event.getRowValue().getValue();
			editedUser.setServices(event.getNewValue());
			if (!editedUsers.contains(editedUser)) {
				editedUsers.add(editedUser);
			}
		});

		managerCol.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());
		managerCol.setOnEditCommit(event -> {
			TableEntityWrapper.TableUser editedUser = event.getRowValue().getValue();
			editedUser.setManager(event.getNewValue());
			if (!editedUsers.contains(editedUser)) {
				editedUsers.add(editedUser);
			}
		});

		removeCol.setCellFactory(removeColCellFactory);

		ArrayList<JFXTreeTableColumn<TableEntityWrapper.TableUser, String>> userCols = new ArrayList<>(Arrays.asList(idCol, fNameCol, lNameCol, usernameCol, acctTypeCol, servicesCol, managerCol, removeCol));
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
				return param.getValue().getValue().getFirstName();
			} else {
				return fNameCol.getComputedValue(param);
			}
		});

		JFXTreeTableColumn<TableEntityWrapper.TableDoctor, String> lNameCol = new JFXTreeTableColumn<>("l_name");
		lNameCol.setCellValueFactory((TreeTableColumn.CellDataFeatures<TableEntityWrapper.TableDoctor, String> param) -> {
			if (lNameCol.validateValue(param)) {
				return param.getValue().getValue().getLastName();
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

		JFXTreeTableColumn<TableEntityWrapper.TableDoctor, String> removeCol = new JFXTreeTableColumn<>("");
		removeCol.setPrefWidth(50);

		Callback<TreeTableColumn<TableEntityWrapper.TableDoctor, String>, TreeTableCell<TableEntityWrapper.TableDoctor, String>> removeColCellFactory = new Callback<>() {
			@Override
			public TreeTableCell<TableEntityWrapper.TableDoctor, String> call(final TreeTableColumn<TableEntityWrapper.TableDoctor, String> param) {
				return new TreeTableCell<>() {
					final JFXButton btn = new JFXButton("X");;
					{
						btn.setOnAction(event -> {
							deletedDoctors.add(doctorsTable.getTreeItem(getIndex()).getValue());
							doctorsTable.getTreeItem(getIndex()).getParent().getChildren().remove(doctorsTable.getTreeItem(getIndex()));
						});
					}

					@Override
					public void updateItem(String item, boolean empty) {
						super.updateItem(item, empty);
						if (empty) {
							setGraphic(null);
						} else {
							btn.setButtonType(JFXButton.ButtonType.RAISED);
							setGraphic(btn);
						}
						setText(null);
					}
				};
			}
		};

		ObservableList<TableEntityWrapper.TableDoctor> doctors = FXCollections.observableArrayList();
		for (Doctor doctor : cache.getDoctorCache()) {
			doctors.add(new TableEntityWrapper.TableDoctor(doctor.getID(), doctor.getFName(), doctor.getLName(), doctor.getUsername(), doctor.getOfficeID(), doctor.getAddInfo()));
		}

		final TreeItem<TableEntityWrapper.TableDoctor> root = new RecursiveTreeItem<>(doctors, RecursiveTreeObject::getChildren);
		idCol.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());

		fNameCol.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());
		fNameCol.setOnEditCommit(event -> {
			TableEntityWrapper.TableDoctor editedDoctor = event.getRowValue().getValue();
			editedDoctor.setFirstName(event.getNewValue());
			if (!editedDoctors.contains(editedDoctor)) {
				editedDoctors.add(editedDoctor);
			}
		});

		lNameCol.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());
		lNameCol.setOnEditCommit(event -> {
			TableEntityWrapper.TableDoctor editedDoctor = event.getRowValue().getValue();
			editedDoctor.setLastName(event.getNewValue());
			if (!editedDoctors.contains(editedDoctor)) {
				editedDoctors.add(editedDoctor);
			}
		});

		usernameCol.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());
		usernameCol.setOnEditCommit(event -> {
			TableEntityWrapper.TableDoctor editedDoctor = event.getRowValue().getValue();
			editedDoctor.setUsername(event.getNewValue());
			if (!editedDoctors.contains(editedDoctor)) {
				editedDoctors.add(editedDoctor);
			}
		});

		officeIDCol.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());
		officeIDCol.setOnEditCommit(event -> {
			TableEntityWrapper.TableDoctor editedDoctor = event.getRowValue().getValue();
			editedDoctor.setOfficeID(event.getNewValue());
			if (!editedDoctors.contains(editedDoctor)) {
				editedDoctors.add(editedDoctor);
			}
		});

		addInfoCol.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());
		addInfoCol.setOnEditCommit(event -> {
			TableEntityWrapper.TableDoctor editedDoctor = event.getRowValue().getValue();
			editedDoctor.setAddInfo(event.getNewValue());
			if (!editedDoctors.contains(editedDoctor)) {
				editedDoctors.add(editedDoctor);
			}
		});

		removeCol.setCellFactory(removeColCellFactory);

		ArrayList<JFXTreeTableColumn<TableEntityWrapper.TableDoctor, String>> doctorCols = new ArrayList<>(Arrays.asList(idCol, fNameCol, lNameCol, usernameCol, officeIDCol, addInfoCol, removeCol));
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

	private void loadCurrentTable() {
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

	@FXML
	private void changePasswordClicked() {
		try {
			Parent root = loaderFactory.getFXMLLoader("admin/ChangePassword").load();
			loaderFactory.setupPopup(new Stage(), new Scene(root));
		} catch (IOException ex) {
			log.error("Encountered IOException", ex);
		}
	}

	private TimerTask timerWrapper(Runnable r) {
		return new TimerTask() {
			@Override
			public void run() {
				r.run();
			}
		};
	}

	private void updateTime() {
		Platform.runLater(() -> timeLabel.setText(new SimpleDateFormat("E, MMM d | h:mm aa").format(new Date())));
	}
}
