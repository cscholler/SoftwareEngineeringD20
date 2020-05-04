package edu.wpi.cs3733.d20.teamL.views.LoggedInView;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import edu.wpi.cs3733.d20.teamL.entities.User;
import edu.wpi.cs3733.d20.teamL.services.db.IDatabaseCache;
import edu.wpi.cs3733.d20.teamL.services.db.IDatabaseService;
import edu.wpi.cs3733.d20.teamL.util.FXMLLoaderFactory;
import edu.wpi.cs3733.d20.teamL.util.TableEntityWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import edu.wpi.cs3733.d20.teamL.services.users.ILoginManager;

import javax.inject.Inject;

@Slf4j
public class AdminLandingPageController implements Initializable {
    private FXMLLoaderFactory loaderFactory = new FXMLLoaderFactory();
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
    private void confirmRebuildDBClicked() {
        log.warn("Rebuilding database");
        db.rebuildDatabase();
    }


    @FXML
    private void addUserClicked() {
        try {
            System.out.println("Got here");
            Parent root = loaderFactory.getFXMLLoader("AddUser").load();
            loaderFactory.setupScene(new Scene(root));
        } catch (IOException e) {
            log.error("Encountered IOException", e);
        }
    }

    @FXML
    private void addDoctorClicked() {
        try {
            Parent root = loaderFactory.getFXMLLoader("AddDoctor").load();
            loaderFactory.setupScene(new Scene(root));
        } catch (IOException e) {
            log.error("Encountered IOException", e);
        }
    }

    @FXML
    private void addPatientClicked() {
        try {
            Parent root = loaderFactory.getFXMLLoader("AddPatient").load();
            loaderFactory.setupScene(new Scene(root));
        } catch (IOException e) {
            log.error("Encountered IOException", e);
        }
    }

    @FXML
    public void importClicked() {
        try {
            Parent root = loaderFactory.getFXMLLoader("dialogues/ImportDialogue").load();
            loaderFactory.setupPopup(new Stage(), new Scene(root));
        } catch (IOException ex) {
            log.error("Encountered IOException", ex);
        }
    }

    @FXML
    public void exportClicked() {
        try {
            Parent root = loaderFactory.getFXMLLoader("dialogues/ExportDialogue").load();
            loaderFactory.setupPopup(new Stage(), new Scene(root));
        } catch (IOException ex) {
            log.error("Encountered IOException", ex);
        }
    }

	public void btnViewClicked() {
    	if (tableSelector != null) {
    		if (tableSelector.getValue() != null) {
				switch (tableSelector.getValue()) {
					default:
					case "Map Nodes": {
						hideAllTablesExceptCurrent("Nodes");
					}
					break;
					case "Map Edges": {
						hideAllTablesExceptCurrent("Edges");
					}
					break;
					case "Gift Inventory": {
						hideAllTablesExceptCurrent("Gifts");
					}
					break;
					case "User Information": {
						loadUserTable();
						hideAllTablesExceptCurrent("Users");
					}
					break;
					case "Doctor Information": {
						hideAllTablesExceptCurrent("Doctors");
					}
				}
			}
		}
	}

	private void hideAllTablesExceptCurrent(String tableName) {
    	switch (tableName) {
			case "Nodes": {
				nodesTable.setVisible(true);
				nodesTable.setMouseTransparent(false);
				edgesTable.setVisible(false);
				edgesTable.setMouseTransparent(true);
				giftsTable.setVisible(false);
				giftsTable.setMouseTransparent(true);
				usersTable.setVisible(false);
				usersTable.setMouseTransparent(true);
				doctorsTable.setVisible(false);
				doctorsTable.setMouseTransparent(true);
			}
			break;
			case "Edges": {
				nodesTable.setVisible(false);
				nodesTable.setMouseTransparent(true);
				edgesTable.setVisible(true);
				edgesTable.setMouseTransparent(false);
				giftsTable.setVisible(false);
				giftsTable.setMouseTransparent(true);
				usersTable.setVisible(false);
				usersTable.setMouseTransparent(true);
				doctorsTable.setVisible(false);
				doctorsTable.setMouseTransparent(true);
			}
			break;
			case "Gifts": {
				nodesTable.setVisible(false);
				nodesTable.setMouseTransparent(true);
				edgesTable.setVisible(false);
				edgesTable.setMouseTransparent(true);
				giftsTable.setVisible(true);
				giftsTable.setMouseTransparent(false);
				usersTable.setVisible(false);
				usersTable.setMouseTransparent(true);
				doctorsTable.setVisible(false);
				doctorsTable.setMouseTransparent(true);
			}
			break;
			case "Users": {
				nodesTable.setVisible(false);
				nodesTable.setMouseTransparent(true);
				edgesTable.setVisible(true);
				edgesTable.setMouseTransparent(false);
				giftsTable.setVisible(false);
				giftsTable.setMouseTransparent(true);
				usersTable.setVisible(true);
				usersTable.setMouseTransparent(false);
				doctorsTable.setVisible(false);
				doctorsTable.setMouseTransparent(true);
			}
			break;
			case "Doctors": {
				nodesTable.setVisible(false);
				nodesTable.setMouseTransparent(true);
				edgesTable.setVisible(true);
				edgesTable.setMouseTransparent(false);
				giftsTable.setVisible(false);
				giftsTable.setMouseTransparent(true);
				usersTable.setVisible(false);
				usersTable.setMouseTransparent(true);
				doctorsTable.setVisible(true);
				doctorsTable.setMouseTransparent(false);
			}
		}
	}

	private void loadUserTable() {
    	log.info("here");
		JFXTreeTableColumn<TableEntityWrapper.TableUser, String> idCol = new JFXTreeTableColumn<>("id");
		idCol.setPrefWidth(150);
		idCol.setCellValueFactory((TreeTableColumn.CellDataFeatures<TableEntityWrapper.TableUser, String> param) -> {
			if (idCol.validateValue(param)) {
				return param.getValue().getValue().getID();
			} else {
				return idCol.getComputedValue(param);
			}
		});
		JFXTreeTableColumn<TableEntityWrapper.TableUser, String> fNameCol = new JFXTreeTableColumn<>("f_name");
		fNameCol.setPrefWidth(150);
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
		usersTable.getColumns().setAll(idCol, fNameCol, lNameCol, usernameCol, acctTypeCol, servicesCol, managerCol);
		usersTable.setRoot(root);
		usersTable.setShowRoot(false);
	}
}
