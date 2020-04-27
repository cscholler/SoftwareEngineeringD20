package edu.wpi.cs3733.d20.teamL.views.controllers.requests;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.ResourceBundle;

import edu.wpi.cs3733.d20.teamL.entities.GiftDeliveryRequest;
import edu.wpi.cs3733.d20.teamL.entities.ServiceRequest;
import edu.wpi.cs3733.d20.teamL.entities.User;
import edu.wpi.cs3733.d20.teamL.services.users.ILoginManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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
   	private ObservableList<MedicationRequest> medReqList = FXCollections.observableArrayList();
   	private ObservableList<GiftDeliveryRequest> giftReqList = FXCollections.observableArrayList();
   	private ObservableList<ServiceRequest> serviceReqList = FXCollections.observableArrayList();
	private FXMLLoaderHelper loaderHelper = new FXMLLoaderHelper();
	private MedicationRequest currentRequest;
	@Inject
	private IDatabaseService db;
	@Inject
	private ILoginManager loginManager;
	@FXML
    private JFXButton btnBack, btnCompleted, btnDecline;
    @FXML
    private JFXListView<ServiceRequest> serviceReqs;
	@FXML
    private JFXListView<MedicationRequest> medReqs;
	@FXML
	private JFXListView<GiftDeliveryRequest> giftReqs;
    @FXML
    private Label reqMessage, addInfo;
    private User user;
    private String doctorUsername;

	/**
	 * Calls loadData and sets up the cellFactory
	 * @param url
	 * @param resourceBundle
	 */
	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		user = loginManager.getCurrentUser();
		loadRequests("medication");
		loadRequests("gift");
		loadRequests("service");
		medReqs.setCellFactory(param -> new ListCell<>() {
			@Override
			protected void updateItem(MedicationRequest medReq, boolean empty) {
				super.updateItem(medReq, empty);
				if (medReq != null) {
					String status;
					switch (medReq.getStatus()) {
						default :
						case "0" : {
							status = "Pending";
						}
						break;
						case "1" : {
							status = "Approved";
						}
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
		giftReqs.setCellFactory(param -> new ListCell<>() {
			@Override
			protected void updateItem(GiftDeliveryRequest giftReq, boolean empty) {
				super.updateItem(giftReq, empty);
				if (giftReq != null) {
					String status;
					switch (giftReq.getStatus()) {
						default :
						case "0" : {
							status = "Pending";
						}
						break;
						case "1" : {
							status = "Approved";
						}
						break;
						case "2" : {
							status = "Denied";
						}
					}
					//setText("[" + giftReq.getDateAndTime() + "] " +  giftReq.getDose() + " of " + giftReq.getMedType() + " for " + giftReq.getPatientName() + " (" + status + ")");
				}
			}
		});
		serviceReqs.setCellFactory(param -> new ListCell<>() {
			@Override
			protected void updateItem(ServiceRequest req, boolean empty) {
				super.updateItem(req, empty);
				if (req != null) {
					String status;
					switch (req.getStatus()) {
						default :
						case "0" : {
							status = "Pending";
						}
						break;
						case "1" : {
							status = "Approved";
						}
						break;
						case "2" : {
							status = "Denied";
						}
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
    private void loadRequests(String type) {
        medReqList.removeAll();
		String username = user.getUsername();
		ArrayList<ArrayList<String>> requests = new ArrayList<>();
		switch (type) {
			case "medication": {
				switch (user.getAcctType()) {
					// Staff member
					default:
					case "0":
					case "1": {
						log.info("Viewing notifications as staff member with username {}", username);
						requests = db.getTableFromResultSet(db.executeQuery(new SQLEntry(DBConstants.SELECT_ALL_MEDICATION_REQUESTS_FOR_DELIVERER, new ArrayList<>(Collections.singletonList(username)))));
					}
					break;
					// Doctor
					case "2": {
						log.info("Viewing notifications as doctor with username: {}", username);
						ArrayList<ArrayList<String>> doctorIDTable = db.getTableFromResultSet(db.executeQuery(new SQLEntry(DBConstants.GET_DOCTOR_ID_BY_USERNAME, new ArrayList<>(Collections.singletonList(username)))));
						if (doctorIDTable.size() != 0) {
							requests = db.getTableFromResultSet(db.executeQuery(new SQLEntry(DBConstants.SELECT_ALL_MEDICATION_REQUESTS_FOR_DOCTOR, new ArrayList<>(Collections.singletonList(doctorIDTable.get(0).get(0))))));
						}
					}
				}
				String patientID;
				String patientName;
				String roomID;
				for (ArrayList<String> row : requests) {
					patientID = row.get(2);
					ArrayList<String> name = db.getTableFromResultSet(db.executeQuery(new SQLEntry(DBConstants.GET_PATIENT_NAME, new ArrayList<>(Collections.singletonList(patientID))))).get(0);
					patientName = name.get(0) + " " + name.get(1);
					roomID = db.getTableFromResultSet(db.executeQuery(new SQLEntry(DBConstants.GET_PATIENT_ROOM, new ArrayList<>(Collections.singletonList(patientID))))).get(0).get(0);
					medReqList.add(new MedicationRequest(row.get(0), row.get(1), row.get(2), patientName, roomID, row.get(3), row.get(4), row.get(5), row.get(6), row.get(7), row.get(8), row.get(9)));
				}
				medReqs.getItems().addAll(medReqList);
			}
			break;
			case "gift": {
				requests = db.getTableFromResultSet(db.executeQuery(new SQLEntry(DBConstants.SELECT_ALL_GIFT_DELIVERY_REQUESTS_FOR_USER, new ArrayList<>(Collections.singletonList(username)))));
				for (ArrayList<String> row : requests) {

				}
			}
			break;
			default:
			case "service": {
			}
		}
    }

    /**
     * Checks for anyone clicking on the listView of medReq and opens them in the pane to the right
     */
    @FXML
    private void displaySelectedMedReq() {
        MedicationRequest req = medReqs.getSelectionModel().getSelectedItem();
        setCurrentRequest(req);
        try {
			//if (req.getPatientName() == null || req.getPatientName().isEmpty()) {
			if (req == null) {
				log.error("Invalid request");
			} else {
				reqMessage.setWrapText(true);
				addInfo.setWrapText(true);
				addInfo.setText(req.getNotes());
				String message;
				ArrayList<String> doctorNameRow = db.getTableFromResultSet(db.executeQuery(new SQLEntry(DBConstants.GET_DOCTOR_NAME, new ArrayList<>(Collections.singletonList(req.getDoctorID()))))).get(0);
				String doctorName = doctorNameRow.get(0) + " " + doctorNameRow.get(1);
				// TODO: Verify that it's the correct doctor
				if (user.getAcctType().equals("2") && (user.getFName() + " " + user.getLName()).equals(doctorName)) {
					log.info("logged in as doctor");
					message = req.getNurseUsername() + " requests " + req.getDose() + " of " + req.getMedType() + " for " + req.getPatientName() + "(" + req.getPatientID() +")" + " in room " + req.getRoomNum();
 				} else {
					log.info("logged in as non doctor");
					message = doctorName + " requests " + req.getDose() + " of " + req.getMedType() + " to be delivered to " + req.getPatientName() + "(" + req.getPatientID() +")" + " in room " + req.getRoomNum();
				}
				reqMessage.setText(message);
			}
		} catch (NullPointerException ex) {
        	log.info("No notification currently selected");
		}
    }

    @FXML
	private void displaySelectGiftReq() {

	}

	@FXML
	private void displaySelectServiceReq() {

	}

    @FXML
    private void btnBackClicked() {
		loaderHelper.goBack();
	}

	@FXML
	private void btnCompletedClicked() {
		String status = "1";
		db.executeUpdate(new SQLEntry(DBConstants.UPDATE_MEDICATION_REQUEST_STATUS, new ArrayList<>(Arrays.asList(status, getCurrentRequest().getID()))));
		getCurrentRequest().setStatus(status);
		System.out.println(getCurrentRequest().getStatus());
	}

	@FXML
	private void btnDeclineClicked() {
		String status = "3";
		db.executeUpdate(new SQLEntry(DBConstants.UPDATE_MEDICATION_REQUEST_STATUS, new ArrayList<>(Arrays.asList(status, getCurrentRequest().getID()))));
		getCurrentRequest().setStatus(status);
		System.out.println(getCurrentRequest().getStatus());
	}

	// TODO: add delivered button for med reqs

	public MedicationRequest getCurrentRequest() {
		return currentRequest;
	}

	public void setCurrentRequest(MedicationRequest currentRequest) {
		this.currentRequest = currentRequest;
	}
}
