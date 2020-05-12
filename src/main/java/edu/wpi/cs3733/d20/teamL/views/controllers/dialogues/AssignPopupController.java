package edu.wpi.cs3733.d20.teamL.views.controllers.dialogues;

import com.google.inject.Inject;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import edu.wpi.cs3733.d20.teamL.entities.GiftDeliveryRequest;
import edu.wpi.cs3733.d20.teamL.entities.MedicationRequest;
import edu.wpi.cs3733.d20.teamL.entities.ServiceRequest;
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
import javafx.scene.control.Label;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.ResourceBundle;

@Slf4j
public class AssignPopupController implements Initializable {
	private NotificationsPageController notificationsPageController;
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ArrayList<String> usersInDept = new ArrayList<>();
        ArrayList<ArrayList<String>> allUsers = db.getTableFromResultSet(db.executeQuery(new SQLEntry(DBConstants.SELECT_ALL_USERS)));
        String dept = loginManager.getCurrentUser().getDept();
        for (ArrayList<String> userInfo : allUsers) {
            User nextUser = new User(userInfo.get(0), userInfo.get(1), userInfo.get(2), userInfo.get(3), userInfo.get(4), userInfo.get(5), userInfo.get(6));
            if (nextUser.getServices() != null) {
				if (nextUser.getServices().toUpperCase().contains(dept.toUpperCase())) {
					if (dept.equals("interpreter")) {
						if (nextUser.getServices().toUpperCase().contains(reqHandler.getInterpreterReqLanguage().toUpperCase())) {
							usersInDept.add(nextUser.getFName() + " " + nextUser.getLName());
						}
					} else {
						usersInDept.add(nextUser.getFName() + " " + nextUser.getLName());
					}
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
		Label addInfo = getNotificationsPageController().getAddInfo();
		String dateAndTime = new SimpleDateFormat("M/dd/yy | h:mm aa").format(new Date());
		String updatedNotes = addInfo.getText();
		if (updatedNotes.contains("Assigned to ")) {
			updatedNotes = updatedNotes.substring(0, updatedNotes.indexOf("\nAssigned to "));
		}
		updatedNotes = updatedNotes.concat("\nAssigned to " + selectedName + " at " + dateAndTime);
		switch (reqHandler.getCurrentRequestType()) {
			case "medication": {
				MedicationRequest currentMedReq = getNotificationsPageController().getCurrentMedicationRequest();
				db.executeUpdate(new SQLEntry(DBConstants.UPDATE_MEDICATION_REQUEST_DELIVERER, new ArrayList<>(Arrays.asList(selectedUser, currentMedReq.getID()))));
				db.executeUpdate(new SQLEntry(DBConstants.UPDATE_MEDICATION_REQUEST_STATUS, new ArrayList<>(Arrays.asList("2", currentMedReq.getID()))));
				db.executeUpdate(new SQLEntry(DBConstants.UPDATE_MEDICATION_REQUEST_NOTES, new ArrayList<>(Arrays.asList(updatedNotes, currentMedReq.getID()))));
				currentMedReq.setStatus("2");
				currentMedReq.setNotes(updatedNotes);
			}
			break;
			case "gift": {
				GiftDeliveryRequest currentGiftReq = getNotificationsPageController().getCurrentGiftRequest();
				db.executeUpdate(new SQLEntry(DBConstants.UPDATE_GIFT_DELIVERY_REQUEST_ASSIGNEE, new ArrayList<>(Arrays.asList(selectedUser, currentGiftReq.getID()))));
				db.executeUpdate(new SQLEntry(DBConstants.UPDATE_GIFT_DELIVERY_REQUEST_STATUS, new ArrayList<>(Arrays.asList("2", currentGiftReq.getID()))));
				db.executeUpdate(new SQLEntry(DBConstants.UPDATE_GIFT_DELIVERY_REQUEST_NOTES, new ArrayList<>(Arrays.asList(updatedNotes, currentGiftReq.getID()))));
				currentGiftReq.setStatus("2");
				currentGiftReq.setNotes(updatedNotes);
			}
			break;
			case "service": {
				ServiceRequest currentServiceReq = getNotificationsPageController().getCurrentServiceRequest();
				db.executeUpdate(new SQLEntry(DBConstants.UPDATE_SERVICE_REQUEST_ASSIGNEE, new ArrayList<>(Arrays.asList(selectedUser, currentServiceReq.getID()))));
				db.executeUpdate(new SQLEntry(DBConstants.UPDATE_SERVICE_REQUEST_STATUS, new ArrayList<>(Arrays.asList("2", currentServiceReq.getID()))));
				db.executeUpdate(new SQLEntry(DBConstants.UPDATE_SERVICE_REQUEST_NOTES, new ArrayList<>(Arrays.asList(updatedNotes, currentServiceReq.getID()))));
				currentServiceReq.setStatus("2");
				currentServiceReq.setNotes(updatedNotes);
				log.info(currentServiceReq.getID());
			}
		}
		addInfo.setText(updatedNotes);
		getNotificationsPageController().resetCards();
		getNotificationsPageController().getBtnAssign().setText("Re-Assign");
		((Stage) btnSubmit.getScene().getWindow()).close();
	}

	public NotificationsPageController getNotificationsPageController() {
		return notificationsPageController;
	}

	public void setNotificationsPageController(NotificationsPageController notificationsPageController) {
		this.notificationsPageController = notificationsPageController;
	}
}
