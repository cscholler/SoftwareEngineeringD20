package edu.wpi.cs3733.d20.teamL.views.controllers.dialogues;

import com.google.inject.Inject;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import edu.wpi.cs3733.d20.teamL.entities.User;
import edu.wpi.cs3733.d20.teamL.services.db.DBConstants;
import edu.wpi.cs3733.d20.teamL.services.db.IDatabaseService;
import edu.wpi.cs3733.d20.teamL.services.db.SQLEntry;
import edu.wpi.cs3733.d20.teamL.services.users.ILoginManager;
import edu.wpi.cs3733.d20.teamL.services.users.IRequestHandlerService;
import edu.wpi.cs3733.d20.teamL.views.controllers.requests.NotificationsPageController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;

@Slf4j
public class AssignPopupController implements Initializable {
    private ObservableList<String> users = FXCollections.observableArrayList();
    @FXML
    private JFXComboBox<String> userSelector;
    @FXML
    private JFXButton btnCancel;
	@FXML
	private JFXButton btnSubmit;
    @Inject
    private IDatabaseService db;
    @Inject
    private ILoginManager loginManager;
    @Inject
    private IRequestHandlerService reqHandler;
    private NotificationsPageController notificationsPageController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ArrayList<String> usersInDept = new ArrayList<>();
        ArrayList<ArrayList<String>> allUsers = db.getTableFromResultSet(db.executeQuery(new SQLEntry(DBConstants.SELECT_ALL_USERS)));
        for (ArrayList<String> userInfo : allUsers) {
            User nextUser = new User(userInfo.get(0), userInfo.get(1), userInfo.get(2), userInfo.get(3), userInfo.get(4), userInfo.get(5), userInfo.get(6));
            if (nextUser.getServices() != null) {
				if (nextUser.getServices().contains(loginManager.getCurrentUser().getDept())) {
					usersInDept.add(nextUser.getFName() + " " + nextUser.getLName());
				}
			}
        }
        userSelector.setValue("Qualified Users");
        users.addAll(usersInDept);
        userSelector.setItems(users);
    }

    @FXML
    private void btnCancelClicked() {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }

	@FXML
	private void btnSubmitClicked() {
		String selectedName = userSelector.getValue();
		String fName = selectedName.substring(0, selectedName.indexOf(" "));
		String lName = selectedName.substring(selectedName.indexOf(" ") + 1);
		String selectedUser = db.getTableFromResultSet(db.executeQuery(new SQLEntry(DBConstants.GET_USERNAME_BY_NAME, new ArrayList<>(Arrays.asList(fName, lName))))).get(0).get(0);
		Stage stage = (Stage) btnSubmit.getScene().getWindow();
		stage.close();
		switch (reqHandler.getCurrentRequestType()) {
			case "medication": {
				db.executeUpdate(new SQLEntry(DBConstants.UPDATE_MEDICATION_REQUEST_DELIVERER, new ArrayList<>(Arrays.asList(selectedUser, reqHandler.getCurrentRequestID()))));
				db.executeUpdate(new SQLEntry(DBConstants.UPDATE_MEDICATION_REQUEST_STATUS, new ArrayList<>(Arrays.asList("2", getNotificationsPageController().getCurrentMedicationRequest().getID()))));
				getNotificationsPageController().getCurrentMedicationRequest().setStatus("1");
			}
			break;
			case "gift": {
				db.executeUpdate(new SQLEntry(DBConstants.UPDATE_GIFT_DELIVERY_REQUEST_ASSIGNEE, new ArrayList<>(Arrays.asList(selectedUser, reqHandler.getCurrentRequestID()))));
				db.executeUpdate(new SQLEntry(DBConstants.UPDATE_GIFT_DELIVERY_REQUEST_STATUS, new ArrayList<>(Arrays.asList("2", getNotificationsPageController().getCurrentGiftRequest().getID()))));
				getNotificationsPageController().getCurrentGiftRequest().setStatus("2");
			}
			break;
			case "service": {
				db.executeUpdate(new SQLEntry(DBConstants.UPDATE_SERVICE_REQUEST_ASSIGNEE, new ArrayList<>(Arrays.asList(selectedUser, reqHandler.getCurrentRequestID()))));
				db.executeUpdate(new SQLEntry(DBConstants.UPDATE_SERVICE_REQUEST_STATUS, new ArrayList<>(Arrays.asList("2", getNotificationsPageController().getCurrentServiceRequest().getID()))));
				getNotificationsPageController().getCurrentServiceRequest().setStatus("2");
			}
		}
		getNotificationsPageController().setCellFactories();
		getNotificationsPageController().getBtnAssign().setText("Re-Assign");
	}

	public NotificationsPageController getNotificationsPageController() {
		return notificationsPageController;
	}

	public void setNotificationsPageController(NotificationsPageController notificationsPageController) {
		this.notificationsPageController = notificationsPageController;
	}
}
