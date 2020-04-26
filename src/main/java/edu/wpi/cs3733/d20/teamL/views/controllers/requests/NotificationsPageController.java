package edu.wpi.cs3733.d20.teamL.views.controllers.requests;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.ResourceBundle;

import edu.wpi.cs3733.d20.teamL.entities.GiftRequest;
import edu.wpi.cs3733.d20.teamL.entities.ServiceRequest;
import edu.wpi.cs3733.d20.teamL.services.users.ILoginManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;

import com.google.inject.Inject;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;

import lombok.extern.slf4j.Slf4j;

import edu.wpi.cs3733.d20.teamL.entities.MedicationRequest;
import edu.wpi.cs3733.d20.teamL.services.db.DBConstants;
import edu.wpi.cs3733.d20.teamL.services.db.IDatabaseService;
import edu.wpi.cs3733.d20.teamL.services.db.SQLEntry;
import edu.wpi.cs3733.d20.teamL.util.FXMLLoaderHelper;

@Slf4j
public class NotificationsPageController implements Initializable {
   	private ObservableList<MedicationRequest> list = FXCollections.observableArrayList();
	private FXMLLoaderHelper loaderHelper = new FXMLLoaderHelper();
	private MedicationRequest currentRequest;
	@Inject
	private IDatabaseService db;
	@Inject
	private ILoginManager loginManager;
	@FXML
    private JFXButton btnBack, btnCompleted, btnDecline;
    @FXML
    private JFXListView<ServiceRequest> notifications;
    @FXML
	private JFXListView<GiftRequest> giftReq;
	@FXML
    private JFXListView<MedicationRequest> medReq;
    @FXML
    private Label reqMessage, addInfo;

	/**
	 * Calls loadData and sets up the cellFactory
	 * @param url
	 * @param resourceBundle
	 */
	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		loadData();
		medReq.setCellFactory(param -> new ListCell<>() {
			@Override
			protected void updateItem(MedicationRequest medReq, boolean empty) {
				super.updateItem(medReq, empty);
				if (medReq != null) {
					String status;
					switch (medReq.getStatus()) {
						default :
						case "0" : { status = "Pending"; }
						break;
						case "1" : { status = "Approved"; }
						break;
						case "2" : {
							status = "Delivered";
						}
						break;
						case "3" : {
							status = "Denied";
						}
					}
					setText("[" + medReq.getDateAndTime() + "] " +  medReq.getDose() + " of " + medReq.getMedType() + " for " + medReq.getPatientName() + " (" + status + ")");
				}
			}
		});
		giftReq.setCellFactory(param -> new ListCell<>() {
			@Override
			protected void updateItem(GiftRequest giftReq, boolean empty) {
				super.updateItem(giftReq, empty);
				if (giftReq != null) {
					String status;
					switch (giftReq.getStatus()) {
						default :
						case "0" : { status = "Pending"; }
						break;
						case "1" : { status = "Approved"; }
						break;
						case "2" : { status = "Denied"; }
						break;
					}
					//setText("[" + medReq.getDateAndTime() + "] " +  medReq.getDose() + " of " + medReq.getMedType() + " for " + medReq.getPatientName() + " (" + status + ")");
				}
			}
		});
		notifications.setCellFactory(param -> new ListCell<>() {
			@Override
			protected void updateItem(ServiceRequest req, boolean empty) {
				super.updateItem(req, empty);
				if (req != null) {
					String status;
					switch (req.getStatus()) {
						default :
						case "0" : { status = "Pending"; }
						break;
						case "1" : { status = "Approved"; }
						break;
						case "2" : { status = "Denied"; }
						break;
					}
					//setText("[" + medReq.getDateAndTime() + "] " +  medReq.getDose() + " of " + medReq.getMedType() + " for " + medReq.getPatientName() + " (" + status + ")");
				}
			}
		});
	}

    /**
     * Loads data to the list view in the form of MedicineRequest Objects
     */
    @FXML
    private void loadData() {
        list.removeAll();
		ArrayList<ArrayList<String>> medRequests;
		String query;
		switch (loginManager.getCurrentUser().getAcctType()) {
			// Staff member
			default:
			case "0": {
				query = DBConstants.SELECT_ALL_MEDICATION_REQUESTS_FOR_DELIVERER;
			}
			break;
			// Doctor
			case "2": {
				query = DBConstants.SELECT_ALL_MEDICATION_REQUESTS_FOR_DOCTOR;
			}
		}
		medRequests = db.getTableFromResultSet(db.executeQuery(new SQLEntry(query, new ArrayList<>(Collections.singletonList(loginManager.getCurrentUser().getUsername())))));

		String patientID;
		String patientName;
		String roomID;
		for (ArrayList<String> row : medRequests) {
			patientID = row.get(2);
			ArrayList<String> name = db.getTableFromResultSet(db.executeQuery(new SQLEntry(DBConstants.GET_PATIENT_NAME, new ArrayList<>(Collections.singletonList(patientID))))).get(0);
			patientName = name.get(0) + " " + name.get(1);
			roomID = db.getTableFromResultSet(db.executeQuery(new SQLEntry(DBConstants.GET_PATIENT_ROOM, new ArrayList<>(Collections.singletonList(patientID))))).get(0).get(0);
			list.add(new MedicationRequest(row.get(0), row.get(1), row.get(2), patientName, roomID, row.get(3), row.get(4), row.get(5), row.get(6), row.get(7), row.get(8), row.get(9)));
		}
        medReq.getItems().addAll(list);
    }

    /**
     * Checks for anyone clicking on the listView of medReq and opens them in the pane to the right
     */
    @FXML
    private void displaySelected() {
        MedicationRequest req = medReq.getSelectionModel().getSelectedItem();
        setCurrentRequest(req);
        try {
			String message = medReq.getSelectionModel().getSelectedItem().getPatientName();
			if (message == null || message.isEmpty()) {
				System.out.println("Nothing");
			} else {
				reqMessage.setWrapText(true);
				reqMessage.setText(req.getNurseUsername() + " requests " + req.getDose() + " of " + req.getMedType() + " for " + req.getPatientName() + "(" + req.getPatientID() +")" + " in room " + req.getRoomNum());
				addInfo.setWrapText(true);
				addInfo.setText(req.getNotes());
				System.out.println(message);
			}
		} catch (NullPointerException ex) {
        	log.info("No notification currently selected");
		}
    }

    /**
     * Handles buttons pressed on the notification screen back, decline, approve
     * @param e tracks which button is pressed
     * @throws IOException
     */


    @FXML
    private void btnBackClicked() throws IOException {
		Parent root = loaderHelper.getFXMLLoader("AdminView").load();
		loaderHelper.setupScene(new Scene(root));
	}

	@FXML
	private void btnCompletedClicked() throws IOException {
		String status = "1";
		db.executeUpdate(new SQLEntry(DBConstants.UPDATE_MEDICATION_REQUEST_STATUS, new ArrayList<>(Arrays.asList(status, getCurrentRequest().getID()))));
		getCurrentRequest().setStatus(status);
		System.out.println(getCurrentRequest().getStatus());
	}

	@FXML
	private void btnDeclineClicked() throws IOException {
		String status = "3";
		db.executeUpdate(new SQLEntry(DBConstants.UPDATE_MEDICATION_REQUEST_STATUS, new ArrayList<>(Arrays.asList(status, getCurrentRequest().getID()))));
		getCurrentRequest().setStatus(status);
		System.out.println(getCurrentRequest().getStatus());
	}

	public MedicationRequest getCurrentRequest() {
		return currentRequest;
	}

	public void setCurrentRequest(MedicationRequest currentRequest) {
		this.currentRequest = currentRequest;
	}
}
