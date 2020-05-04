package edu.wpi.cs3733.d20.teamL.views.LoggedInView;

import com.jfoenix.controls.*;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import edu.wpi.cs3733.d20.teamL.entities.User;
import edu.wpi.cs3733.d20.teamL.services.db.IDatabaseCache;
import edu.wpi.cs3733.d20.teamL.services.db.IDatabaseService;
import edu.wpi.cs3733.d20.teamL.util.FXMLLoaderFactory;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import edu.wpi.cs3733.d20.teamL.services.users.ILoginManager;

import javax.inject.Inject;

@Slf4j
public class AdminLandingPageController implements Initializable {
    FXMLLoaderFactory loaderHelper = new FXMLLoaderFactory();
    @FXML
    private JFXComboBox<String> selectDatabase;
    private ObservableList<String> databaseOptions = FXCollections.observableArrayList("Map Nodes", "Map Edges", "Gift Inventory", "Users", "Doctors");

    @FXML
    private JFXTreeTableView<TableUser> usersTable;

    @FXML
    private JFXButton btnView;

	@FXML
    private ScrollPane scrollPane;

    @Inject
    ILoginManager loginManager;
    @Inject
    IDatabaseService db;
    @Inject
	IDatabaseCache cache;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    	//scrollPane.prefWidthProperty().bind(anchorPane.widthProperty());
		//scrollPane.prefHeightProperty().bind(anchorPane.heightProperty());

        selectDatabase.setItems(databaseOptions);
		JFXTreeTableColumn<TableUser, String> idCol = new JFXTreeTableColumn<>("id");
		idCol.setPrefWidth(150);
		idCol.setCellValueFactory((TreeTableColumn.CellDataFeatures<TableUser, String> param) -> {
			if (idCol.validateValue(param)) {
				return param.getValue().getValue().id;
			} else {
				return idCol.getComputedValue(param);
			}
		});
		JFXTreeTableColumn<TableUser, String> fNameCol = new JFXTreeTableColumn<>("f_name");
		fNameCol.setPrefWidth(150);
		fNameCol.setCellValueFactory((TreeTableColumn.CellDataFeatures<TableUser, String> param) -> {
			if (fNameCol.validateValue(param)) {
				return param.getValue().getValue().fName;
			} else {
				return fNameCol.getComputedValue(param);
			}
		});
		JFXTreeTableColumn<TableUser, String> lNameCol = new JFXTreeTableColumn<>("l_name");
		lNameCol.setCellValueFactory((TreeTableColumn.CellDataFeatures<TableUser, String> param) -> {
			if (lNameCol.validateValue(param)) {
				return param.getValue().getValue().lName;
			} else {
				return lNameCol.getComputedValue(param);
			}
		});
		JFXTreeTableColumn<TableUser, String> usernameCol = new JFXTreeTableColumn<>("username");
		usernameCol.setCellValueFactory((TreeTableColumn.CellDataFeatures<TableUser, String> param) -> {
			if (usernameCol.validateValue(param)) {
				return param.getValue().getValue().username;
			} else {
				return usernameCol.getComputedValue(param);
			}
		});
		JFXTreeTableColumn<TableUser, String> acctTypeCol = new JFXTreeTableColumn<>("acct_type");
		acctTypeCol.setCellValueFactory((TreeTableColumn.CellDataFeatures<TableUser, String> param) -> {
			if (acctTypeCol.validateValue(param)) {
				return param.getValue().getValue().acctType;
			} else {
				return acctTypeCol.getComputedValue(param);
			}
		});
		JFXTreeTableColumn<TableUser, String> servicesCol = new JFXTreeTableColumn<>("services");
		servicesCol.setCellValueFactory((TreeTableColumn.CellDataFeatures<TableUser, String> param) -> {
			if (servicesCol.validateValue(param)) {
				return param.getValue().getValue().services;
			} else {
				return servicesCol.getComputedValue(param);
			}
		});
		JFXTreeTableColumn<TableUser, String> managerCol = new JFXTreeTableColumn<>("manager");
		managerCol.setCellValueFactory((TreeTableColumn.CellDataFeatures<TableUser, String> param) -> {
			if (managerCol.validateValue(param)) {
				return param.getValue().getValue().manager;
			} else {
				return managerCol.getComputedValue(param);
			}
		});

		ObservableList<TableUser> users = FXCollections.observableArrayList();
		for (User user : cache.getUserCache()) {
			log.info(user.getID() + ", " + user.getFName() + ", " + user.getLName() + ", " + user.getUsername() + ", " + user.getAcctType() + ", " + user.getServices() + ", " + user.getDept());
			users.add(new TableUser(user.getID(), user.getFName(), user.getLName(), user.getUsername(), user.getAcctType(), user.getServices(), user.getDept()));
		}
		final TreeItem<TableUser> root = new RecursiveTreeItem<>(users, RecursiveTreeObject::getChildren);
		usersTable.getColumns().setAll(idCol, fNameCol, lNameCol, usernameCol, acctTypeCol, servicesCol, managerCol);
		usersTable.setRoot(root);
		usersTable.setShowRoot(false);
    }

    /*private TreeItem<TableUser> populateUsers() {

    	for (User user : cache.getUserCache()) {

    		users.add(new TableUser(user.getID(), user.getFName(), user.getLName(), user.getUsername(), user.getAcctType(), user.getServices(), user.getDept()));
		}
		return new RecursiveTreeItem<>(users, RecursiveTreeObject::getChildren);
	}*/

    @FXML
    public void logoutBtn() {
        loginManager.logOut(true);
        try {
            Parent root = loaderHelper.getFXMLLoader("map_viewer/MapViewer").load();
            loaderHelper.setupScene(new Scene(root));
        } catch (IOException ex) {
            log.error("Encountered IOException", ex);
        }
    }

    @FXML
    private void btnMapClicked() {
        try {
            Parent root = loaderHelper.getFXMLLoader("Admin/MapEditor").load();
            loaderHelper.setupPopup(new Stage(), new Scene(root));
        } catch (IOException ex) {
            log.error("Encountered IOException", ex);
        }
    }

    @FXML
    private void rebuildDatabaseClicked() {
        try {
            Parent root = loaderHelper.getFXMLLoader("Admin/databaseDialogue").load();
            loaderHelper.setupPopup(new Stage(), new Scene(root));
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
            Parent root = loaderHelper.getFXMLLoader("AddUser").load();
            loaderHelper.setupScene(new Scene(root));
        } catch (IOException e) {
            log.error("Encountered IOException", e);
        }
    }

    @FXML
    private void addDoctorClicked() {
        try {
            Parent root = loaderHelper.getFXMLLoader("AddDoctor").load();
            loaderHelper.setupScene(new Scene(root));
        } catch (IOException e) {
            log.error("Encountered IOException", e);
        }
    }

    @FXML
    private void addPatientClicked() {
        try {
            Parent root = loaderHelper.getFXMLLoader("AddPatient").load();
            loaderHelper.setupScene(new Scene(root));
        } catch (IOException e) {
            log.error("Encountered IOException", e);
        }
    }

    @FXML
    public void importClicked() {
        try {
            Parent root = loaderHelper.getFXMLLoader("dialogues/ImportDialogue").load();
            loaderHelper.setupPopup(new Stage(), new Scene(root));
        } catch (IOException ex) {
            log.error("Encountered IOException", ex);
        }
    }

    @FXML
    public void exportClicked() {
        try {
            Parent root = loaderHelper.getFXMLLoader("dialogues/ExportDialogue").load();
            loaderHelper.setupPopup(new Stage(), new Scene(root));
        } catch (IOException ex) {
            log.error("Encountered IOException", ex);
        }
    }

    static class TableUser extends RecursiveTreeObject<TableUser> {
    	private final StringProperty id;
    	private final StringProperty fName;
		private final StringProperty lName;
		private final StringProperty username;
		private final StringProperty acctType;
		private final StringProperty services;
		private final StringProperty manager;

    	public TableUser(String id, String fName, String lName, String username, String acctType, String services, String manager) {
    		this.id = new SimpleStringProperty(id);
    		this.fName = new SimpleStringProperty(fName);
    		this.lName = new SimpleStringProperty(lName);
    		this.username = new SimpleStringProperty(username);
    		this.acctType = new SimpleStringProperty(acctType);
    		this.services = new SimpleStringProperty(services);
    		this.manager = new SimpleStringProperty(manager);
		}
	}
}
