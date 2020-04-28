package edu.wpi.cs3733.d20.teamL.views.controllers.requests;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.ResourceBundle;

import edu.wpi.cs3733.d20.teamL.entities.*;
import edu.wpi.cs3733.d20.teamL.services.users.ILoginManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;

import com.google.inject.Inject;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;

import javafx.scene.control.SelectionMode;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

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
	private MedicationRequest currentMedicationRequest;
	private GiftDeliveryRequest currentGiftRequest;
	private ServiceRequest currentServiceRequest;
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
    @FXML
    HBox buttonBox;

    private User user;
    private String doctorUsername;
    private boolean meds;
    JFXButton approve = new JFXButton();
    JFXButton assign = new JFXButton();

	/**
	 * Calls loadData and sets up the cellFactory
	 * @param url
	 * @param resourceBundle
	 */
	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
        approve = btnCompleted;
        assign = btnCompleted;
		user = loginManager.getCurrentUser();

		if(user.isManager()){
			buttonBox.getChildren().remove(btnCompleted);
			buttonBox.getChildren().add(approve);
			approve.setText("Approve");
			approve.setOnAction(markedApproved);
		} else {
			buttonBox.getChildren().remove(btnDecline);
		}



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
					setText("[" + giftReq.getDateAndTime() + "] " +  giftReq.getGifts().size() + " gifts from " + giftReq.getSenderName() + " for " + giftReq.getPatientName() + " (" + status + ")");
				}
			}
		});
		serviceReqs.setCellFactory(param -> new ListCell<>() {
			@Override
			protected void updateItem(ServiceRequest serviceReq, boolean empty) {
				super.updateItem(serviceReq, empty);
				if (serviceReq != null) {
					String status;
					switch (serviceReq.getStatus()) {
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
					String patientName = serviceReq.getPatientName();
					setText("[" + serviceReq.getDateAndTime() + "] " +  serviceReq.getType() + " " + serviceReq.getService() + " service" + (patientName != null ? " for " + patientName : "") + " (" + status + ")");
				}
			}
		});
	}

    /**
     * Loads data to the list view in the form of MedicineRequest Objects
     */
    @FXML
    private void loadRequests(String type) {
		String username = user.getUsername();
		ArrayList<ArrayList<String>> requests = new ArrayList<>();
		switch (type) {
			case "medication": {
				medReqs.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
				medReqList.removeAll();
				switch (user.getAcctType()) {
					// Staff member
					default:
					case "0":
					case "3":
					case "1": {
						log.info("Viewing notifications as staff member with username {}", username);
						requests = db.getTableFromResultSet(db.executeQuery(new SQLEntry(DBConstants.SELECT_ALL_MEDICATION_REQUESTS_FOR_DELIVERER, new ArrayList<>(Collections.singletonList(username)))));
					}
					break;
					// Doctor
					case "2": {
						log.info("Viewing notifications as doctor with username: {}", username);
						ArrayList<ArrayList<String>> doctorIDTable = db.getTableFromResultSet(db.executeQuery(new SQLEntry(DBConstants.GET_DOCTOR_ID_BY_USERNAME, new ArrayList<>(Collections.singletonList(username)))));
						if (doctorIDTable.size() == 1) {
							requests = db.getTableFromResultSet(db.executeQuery(new SQLEntry(DBConstants.SELECT_ALL_MEDICATION_REQUESTS_FOR_DOCTOR, new ArrayList<>(Collections.singletonList(doctorIDTable.get(0).get(0))))));
						}
					}
				}

				for (ArrayList<String> row : requests) {
					String patientID = row.get(1);
					String patientName = getPatientFullName(patientID);
					String roomID = db.getTableFromResultSet(db.executeQuery(new SQLEntry(DBConstants.GET_PATIENT_ROOM, new ArrayList<>(Collections.singletonList(patientID))))).get(0).get(0);
					medReqList.add(new MedicationRequest(row.get(0), row.get(1), patientName, row.get(2), roomID, row.get(3), row.get(4), row.get(5), row.get(6), row.get(7), row.get(8), row.get(9)));
				}
				medReqs.getItems().addAll(medReqList);
			}
			break;
			case "gift": {
				giftReqs.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
				giftReqList.removeAll();
				if (user.isManager() && user.getDept().equals("gift_shop")) {
					requests = db.getTableFromResultSet(db.executeQuery(new SQLEntry(DBConstants.SELECT_ALL_GIFT_DELIVERY_REQUESTS)));
				} else {
					requests = db.getTableFromResultSet(db.executeQuery(new SQLEntry(DBConstants.SELECT_ALL_GIFT_DELIVERY_REQUESTS_FOR_ASSIGNEE, new ArrayList<>(Collections.singletonList(username)))));
				}

				for (ArrayList<String> row : requests) {
					String patientID = row.get(1);
					String patientName = getPatientFullName(patientID);
					String roomID = db.getTableFromResultSet(db.executeQuery(new SQLEntry(DBConstants.GET_PATIENT_ROOM, new ArrayList<>(Collections.singletonList(patientID))))).get(0).get(0);
					ArrayList<Gift> gifts = new ArrayList<>();
					ArrayList<ArrayList<String>> giftEntries = new ArrayList<>();
					for (int i = 5; i < 8; i++) {
						giftEntries.add(db.getTableFromResultSet(db.executeQuery(new SQLEntry(DBConstants.GET_GIFT, new ArrayList<>(Collections.singletonList(row.get(i)))))).get(0));
					}
					for (ArrayList<String> giftEntry : giftEntries) {
						gifts.add(new Gift(giftEntry.get(0), giftEntry.get(1), giftEntry.get(2), giftEntry.get(3), giftEntry.get(4)));
					}
					giftReqList.add(new GiftDeliveryRequest(row.get(0), row.get(1), patientName, roomID, row.get(2), row.get(3), row.get(4), gifts, row.get(8), row.get(9), row.get(10), row.get(11)));
				}
				giftReqs.getItems().addAll(giftReqList);
			}
			break;
			default:
			case "service": {
				serviceReqs.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
				serviceReqList.removeAll();
				if (user.isManager()) {
					requests = db.getTableFromResultSet(db.executeQuery(new SQLEntry(DBConstants.SELECT_ALL_SERVICE_REQUESTS_FOR_MANAGER, new ArrayList<>(Collections.singletonList(user.getDept())))));
				} else {
					requests = db.getTableFromResultSet(db.executeQuery(new SQLEntry(DBConstants.SELECT_ALL_SERVICE_REQUESTS_FOR_ASSIGNEE, new ArrayList<>(Collections.singletonList(username)))));
				}

				for (ArrayList<String> row : requests) {
					String patientID = row.get(1);
					String patientName = getPatientFullName(patientID);
					serviceReqList.add(new ServiceRequest(row.get(0), row.get(1), patientName, row.get(2), row.get(3), row.get(4), row.get(5), row.get(6), row.get(7), row.get(8), row.get(9)));
				}
				serviceReqs.getItems().addAll(serviceReqList);
			}
		}
    }

    /**
     * Checks for anyone clicking on the listView of medReq and opens them in the pane to the right
     */
    @FXML
    private void displaySelectedMedReq() {
        MedicationRequest req = medReqs.getSelectionModel().getSelectedItem();
        giftReqs.getSelectionModel().getSelectedItems().removeAll();
		serviceReqs.getSelectionModel().getSelectedItems().removeAll();
        setCurrentMedicationRequest(req);
        try {
			if (req != null) {
				reqMessage.setWrapText(true);
				addInfo.setWrapText(true);
				addInfo.setText("Notes: " + req.getNotes());
				String message;
				ArrayList<String> doctorNameRow = db.getTableFromResultSet(db.executeQuery(new SQLEntry(DBConstants.GET_DOCTOR_NAME, new ArrayList<>(Collections.singletonList(req.getDoctorID()))))).get(0);
				String doctorName = doctorNameRow.get(0) + " " + doctorNameRow.get(1);
				if (user.getAcctType().equals("2") && (user.getFName() + " " + user.getLName()).equals(doctorName)) {
					log.info("logged in as doctor");
					message = getUserFullName(req.getNurseUsername()) + " requests " + req.getDose() + " of " + req.getMedType() + " for " +
							req.getPatientName() + "(" + req.getPatientID() +")" + " in room " + req.getRoomNum();
 				} else {
					log.info("logged in as non doctor");
					message = doctorName + " has assigned you to " + req.getDose() + " of " + req.getMedType() + " to be delivered to " +
							req.getPatientName() + "(" + req.getPatientID() +")" + " in room " + req.getRoomNum();
				}
				reqMessage.setText(message);
			} else {
				log.warn("Attempted to display an empty or invalid request.");
			}
		} catch (NullPointerException ex) {
        	log.info("No notification currently selected");
		}
        meds = true;
    }

    @FXML
	private void displaySelectedGiftReq() {
		GiftDeliveryRequest req = giftReqs.getSelectionModel().getSelectedItem();
		medReqs.getSelectionModel().getSelectedItems().removeAll();
		serviceReqs.getSelectionModel().getSelectedItems().removeAll();
		setCurrentGiftRequest(req);
		try {
			if (req != null) {
				reqMessage.setWrapText(true);
				addInfo.setWrapText(true);
				addInfo.setText("Message: " + req.getMessage() + "\r\n" + "Notes: " + req.getNotes());
				String message;
				ArrayList<Gift> gifts = req.getGifts();
				Gift gift = gifts.get(0);
				String gift1Text = gift.getType() + ": " + gift.getSubtype() + "(" + gift.getId() + ")";
				String gift2Text = "";
				String gift3Text = "";
				if (gifts.get(1) != null) {
					gift = gifts.get(1);
					gift2Text = gift.getType() + ": " + gift.getSubtype() + "(" + gift.getId() + ")";
				}
				if (gifts.get(2) != null) {
					gift = gifts.get(2);
					gift3Text = gift.getType() + ": " + gift.getSubtype() + "(" + gift.getId() + ")";
				}
				String allGiftsText = gift1Text + (!gift2Text.isEmpty() ? ", " + gift2Text : "") + (!gift3Text.isEmpty() ? ", " + gift3Text : "");
				if (user.isManager()) {
					log.info("logged in as manager");
					message = getUserFullName(req.getRequestUsername()) + " requests " + allGiftsText + "for " +
							req.getPatientName() + "(" + req.getPatientID() +")" + " in room " + req.getRoomNum();
				} else {
					log.info("logged in as gift shop worker");
					message = "You have been assigned to deliver " + allGiftsText + "to " +
							req.getPatientName() + "(" + req.getPatientID() +")" + " in room " + req.getRoomNum();
				}
				reqMessage.setText(message);
			} else {
				log.warn("Attempted to display an empty or invalid request.");
			}
		} catch (NullPointerException ex) {
			log.info("No notification currently selected");
		}
	}

	@FXML
	private void displaySelectedServiceReq() {
		ServiceRequest req = serviceReqs.getSelectionModel().getSelectedItem();
		medReqs.getSelectionModel().getSelectedItems().removeAll();
		giftReqs.getSelectionModel().getSelectedItems().removeAll();
		setCurrentServiceRequest(req);
		try {
			if (req != null) {
				reqMessage.setWrapText(true);
				addInfo.setWrapText(true);
				addInfo.setText("Notes: " + req.getNotes());
				String message;
				if (user.isManager()) {
					log.info("logged in as manager");
					message = getUserFullName(req.getRequestUsername()) + " requests " + (req.getType() != null ? req.getType() : "") + " " + req.getService() + "service " +
							((req.getPatientName() != null && req.getPatientID() != null) ? "for " + req.getPatientName() + "(" + req.getPatientID() + ")" : "") +
							(req.getLocation() != null ? " at location " + req.getLocation() : "");
				} else {
					log.info("logged in as service worker");
					message = "You have been assigned to complete a " + (req.getType() != null ? req.getType() : "") + " " + req.getService() + " service " +
							((req.getPatientName() != null && req.getPatientID() != null) ? "for " + req.getPatientName() + "(" + req.getPatientID() + ")" : "") +
							(req.getLocation() != null ? " at location " + req.getLocation() : "");
				}
				reqMessage.setText(message);
			} else {
				log.warn("Attempted to display an empty or invalid request.");
			}
		} catch (NullPointerException ex) {
			log.info("No notification currently selected");
		}
	}

	private String getPatientFullName(String patientID) {
		ArrayList<String> fullName = db.getTableFromResultSet(db.executeQuery(new SQLEntry(DBConstants.GET_PATIENT_NAME, new ArrayList<>(Collections.singletonList(patientID))))).get(0);
		return fullName.get(0) + " " + fullName.get(1);
	}

	private String getUserFullName(String username) {
    	ArrayList<String> fullName =  db.getTableFromResultSet(db.executeQuery(new SQLEntry(DBConstants.GET_NAME_BY_USERNAME, new ArrayList<>(Collections.singletonList(username))))).get(0);
    	return fullName.get(0) + " " + fullName.get(1);
    }

	@FXML
	private void btnBackClicked() {
		loaderHelper.goBack();
	}

    EventHandler<ActionEvent> assignTask = event -> {
		try {
			Parent root = loaderHelper.getFXMLLoader("AssignPopup").load();
			loaderHelper.setupPopup(new Stage(), new Scene(root));
		} catch (IOException ex) {
			log.error("Encountered IOException", ex);
		}
	};

    EventHandler<ActionEvent> markedApproved = event ->  {
		if (user.isManager()) {
			buttonBox.getChildren().remove(approve);
			buttonBox.getChildren().add(assign);
			assign.setText("Assign");
			assign.setOnAction(assignTask);
			String status = "1";
			db.executeUpdate(new SQLEntry(DBConstants.UPDATE_MEDICATION_REQUEST_STATUS, new ArrayList<>(Arrays.asList(status, getCurrentMedicationRequest().getID()))));
			getCurrentMedicationRequest().setStatus(status);
		}
	};

    @FXML
	private void btnCompletedClicked(){

	}

    @FXML
    private void btnDeclineClicked() {
        String status = "3";
        db.executeUpdate(new SQLEntry(DBConstants.UPDATE_MEDICATION_REQUEST_STATUS, new ArrayList<>(Arrays.asList(status, getCurrentMedicationRequest().getID()))));
        getCurrentMedicationRequest().setStatus(status);
    }

    public MedicationRequest getCurrentMedicationRequest() {
        return currentMedicationRequest;
    }

    public void setCurrentMedicationRequest(MedicationRequest currentMedicationRequest) {
        this.currentMedicationRequest = currentMedicationRequest;
    }

	public GiftDeliveryRequest getCurrentGiftRequest() {
		return currentGiftRequest;
	}

	public void setCurrentGiftRequest(GiftDeliveryRequest currentGiftRequest) {
		this.currentGiftRequest = currentGiftRequest;
	}

	public ServiceRequest getCurrentServiceRequest() {
		return currentServiceRequest;
	}

	public void setCurrentServiceRequest(ServiceRequest currentServiceRequest) {
		this.currentServiceRequest = currentServiceRequest;
	}
}
