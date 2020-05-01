package edu.wpi.cs3733.d20.teamL.views.controllers.requests;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.ResourceBundle;

import edu.wpi.cs3733.d20.teamL.entities.*;
import edu.wpi.cs3733.d20.teamL.services.users.ILoginManager;
import edu.wpi.cs3733.d20.teamL.services.users.IRequestHandlerService;
import edu.wpi.cs3733.d20.teamL.views.controllers.dialogues.AssignPopupController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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
import javafx.scene.layout.VBox;
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
	@FXML
	private JFXButton btnCompleted, btnDecline, btnApprove, btnAssign;
	@FXML
	private JFXListView<ServiceRequest> serviceReqs;
	@FXML
	private JFXListView<MedicationRequest> medReqs;
	@FXML
	private JFXListView<GiftDeliveryRequest> giftReqs;
	@FXML
	private Label reqMessage, addInfo, lblMed, lblService, lblGift;
	@Inject
	private IDatabaseService db;
	@Inject
	private ILoginManager loginManager;
	@Inject
	private IRequestHandlerService reqHandler;
	@FXML
	private HBox buttonBox;
	@FXML
	private VBox messageBox;
	private User user;

	/**
	 * Calls loadData and sets up the cellFactory
	 *
	 * @param url
	 * @param resourceBundle
	 */
	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		user = loginManager.getCurrentUser();
		resetButtons();

		setMessageLists(user);


		setCellFactories();
		loadRequests("medication");
		loadRequests("gift");
		loadRequests("service");

	}


	public void setCellFactories() {
		medReqs.setCellFactory(param -> new ListCell<>() {
			@Override
			protected void updateItem(MedicationRequest medReq, boolean empty) {
				super.updateItem(medReq, empty);
				if (medReq != null) {
					String status;
					switch (medReq.getStatus()) {
						default:
						case "0": {
							status = "Pending";
						}
						break;
						case "1": {
							status = "Approved";
						}
						break;
						case "2": {
							status = "Assigned";
						}
						break;
						case "3": {
							status = "Denied";
						}
						break;
						case "4": {
							status = "Completed";
						}
					}
					setText("[" + medReq.getDateAndTime() + "] " + medReq.getDose() + " of " + medReq.getMedType() + " for " + medReq.getPatientName() + " (" + status + ")");
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
						default:
						case "0": {
							status = "Pending";
						}
						break;
						case "1": {
							status = "Approved";
						}
						break;
						case "2": {
							status = "Assigned";
						}
						break;
						case "3": {
							status = "Denied";
						}
						break;
						case "4": {
							status = "Completed";
						}
					}
					setText("[" + giftReq.getDateAndTime() + "] " + giftReq.getGifts().size() + " gifts from " + giftReq.getSenderName() + " for " + giftReq.getPatientName() + " (" + status + ")");
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
						default:
						case "0": {
							status = "Pending";
						}
						break;
						case "1": {
							status = "Approved";
						}
						break;
						case "2": {
							status = "Assigned";
						}
						break;
						case "3": {
							status = "Denied";
						}
						break;
						case "4": {
							status = "Completed";
						}
					}
					String patientName = serviceReq.getPatientName();
					setText("[" + serviceReq.getDateAndTime() + "] " + serviceReq.getType() + " " + serviceReq.getService() + " service" + (patientName != null ? " for " + patientName : "") + " (" + status + ")");
				}
			}
		});
	}

	/**
	 * Loads data to the list view in the form of MedicineRequest Objects
	 */
	@FXML
	private void loadRequests(String type) {
		resetButtons();
		String username = user.getUsername();
		ArrayList<ArrayList<String>> requests = new ArrayList<>();
		switch (type) {
			case "medication": {
				medReqs.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
				medReqList.removeAll();
				switch (user.getAcctType()) {
					// Staff member
					default:
					case "1":
					case "3":
					case "0": {
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
				Collections.reverse(medReqList);
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
						ArrayList<ArrayList<String>> giftTable = db.getTableFromResultSet(db.executeQuery(new SQLEntry(DBConstants.GET_GIFT, new ArrayList<>(Collections.singletonList(row.get(i))))));
						if (giftTable.size() != 0) {
							giftEntries.add(giftTable.get(0));
						}
					}
					for (ArrayList<String> giftEntry : giftEntries) {
						gifts.add(new Gift(giftEntry.get(0), giftEntry.get(1), giftEntry.get(2), giftEntry.get(3), giftEntry.get(4)));
					}
					giftReqList.add(new GiftDeliveryRequest(row.get(0), row.get(1), patientName, roomID, row.get(2), row.get(3), row.get(4), gifts, row.get(8), row.get(9), row.get(10), row.get(11)));
				}
				Collections.reverse(giftReqList);
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
					String patientName = patientID != null ? getPatientFullName(patientID) : null;
					serviceReqList.add(new ServiceRequest(row.get(0), row.get(1), patientName, row.get(2), row.get(3), row.get(4), row.get(5), row.get(6), row.get(7), row.get(8), row.get(9)));
				}
				Collections.reverse(serviceReqList);
				serviceReqs.getItems().addAll(serviceReqList);
			}
		}
	}

	/**
	 * Checks for anyone clicking on the listView of medReq and opens them in the pane to the right
	 */
	@FXML
	private void displaySelectedMedReq() {
		resetButtons();
		MedicationRequest req = medReqs.getSelectionModel().getSelectedItem();
		giftReqs.getSelectionModel().getSelectedItems().removeAll();
		serviceReqs.getSelectionModel().getSelectedItems().removeAll();
		setCurrentMedicationRequest(req);
		try {
			if (req != null) {
				reqHandler.setCurrentRequestID(req.getID());
				reqHandler.setCurrentRequestType("medication");
				log.info(reqHandler.getCurrentRequestID() + ", " + reqHandler.getCurrentRequestType());
				reqMessage.setWrapText(true);
				addInfo.setWrapText(true);
				addInfo.setText("Notes: " + req.getNotes());
				String message;
				ArrayList<String> doctorNameRow = db.getTableFromResultSet(db.executeQuery(new SQLEntry(DBConstants.GET_DOCTOR_NAME, new ArrayList<>(Collections.singletonList(req.getDoctorID()))))).get(0);
				String doctorName = doctorNameRow.get(0) + " " + doctorNameRow.get(1);
				if (user.getAcctType().equals("2") && (user.getFName() + " " + user.getLName()).equals(doctorName)) {
					log.info("logged in as doctor");
					message = getUserFullName(req.getNurseUsername()) + " requests " + req.getDose() + " of " + req.getMedType() + " for " +
							req.getPatientName() + "(" + req.getPatientID() + ")" + (req.getRoomNum() != null ? " in room " + req.getRoomNum() : "");
					if (req.getStatus().equals("1") || req.getStatus().equals("2")) {
						buttonBox.getChildren().add(btnAssign);
						btnAssign.setText("Assign");
						if (req.getStatus().equals("2")) {
							btnAssign.setText("Re-Assign");
						}
					} else if (req.getStatus().equals("0")) {
						buttonBox.getChildren().add(btnDecline);
						buttonBox.getChildren().add(btnApprove);
					}
				} else {
					log.info("logged in as non doctor");
					message = doctorName + " has assigned you to " + req.getDose() + " of " + req.getMedType() + " to be delivered to " +
							req.getPatientName() + "(" + req.getPatientID() + ")" + (req.getRoomNum() != null ? " in room " + req.getRoomNum() : "");
					buttonBox.getChildren().add(btnCompleted);
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
	private void displaySelectedGiftReq() {
		resetButtons();
		GiftDeliveryRequest req = giftReqs.getSelectionModel().getSelectedItem();
		medReqs.getSelectionModel().getSelectedItems().removeAll();
		serviceReqs.getSelectionModel().getSelectedItems().removeAll();
		setCurrentGiftRequest(req);
		try {
			if (req != null) {
				reqHandler.setCurrentRequestID(req.getID());
				reqHandler.setCurrentRequestType("gift");
				log.info(reqHandler.getCurrentRequestID() + ", " + reqHandler.getCurrentRequestType());
				reqMessage.setWrapText(true);
				addInfo.setWrapText(true);
				addInfo.setText("Message: " + req.getMessage() + "\n" + "Notes: " + req.getNotes());
				String message;
				ArrayList<Gift> gifts = req.getGifts();
				Gift gift = gifts.get(0);
				String gift1Text = gift.getType() + ": " + gift.getSubtype() + "(" + gift.getId() + ")";
				String gift2Text = "";
				String gift3Text = "";
				if (gifts.size() >= 2) {
					if (gifts.get(1) != null) {
						gift = gifts.get(1);
						gift2Text = gift.getType() + ": " + gift.getSubtype() + "(" + gift.getId() + ")";
					}
				}
				if (gifts.size() == 3) {
					if (gifts.get(2) != null) {
						gift = gifts.get(2);
						gift3Text = gift.getType() + ": " + gift.getSubtype() + "(" + gift.getId() + ")";
					}
				}
				String allGiftsText = gift1Text + (!gift2Text.isEmpty() ? ", " + gift2Text : "") + (!gift3Text.isEmpty() ? ", " + gift3Text : "");
				if (user.isManager()) {
					log.info("logged in as manager");
					message = getUserFullName(req.getRequestUsername()) + " requests " + allGiftsText + " for " +
							req.getPatientName() + "(" + req.getPatientID() + ")" + (req.getRoomNum() != null ? " in room " + req.getRoomNum() : "");
					if (req.getStatus().equals("1") || req.getStatus().equals("2")) {
						buttonBox.getChildren().add(btnAssign);
						btnAssign.setText("Assign");
						if (req.getStatus().equals("2")) {
							btnAssign.setText("Re-Assign");
						}
					} else if (req.getStatus().equals("0")) {
						buttonBox.getChildren().add(btnDecline);
						buttonBox.getChildren().add(btnApprove);
					}
				} else {
					log.info("logged in as gift shop worker");
					message = "You have been assigned to deliver " + allGiftsText + " to " +
							req.getPatientName() + "(" + req.getPatientID() + ")" + (req.getRoomNum() != null ? " in room " + req.getRoomNum() : "");
					buttonBox.getChildren().add(btnCompleted);
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
		resetButtons();
		ServiceRequest req = serviceReqs.getSelectionModel().getSelectedItem();
		medReqs.getSelectionModel().getSelectedItems().removeAll();
		giftReqs.getSelectionModel().getSelectedItems().removeAll();
		try {
			setCurrentServiceRequest(req);
			if (req != null) {
				reqHandler.setCurrentRequestID(req.getID());
				reqHandler.setCurrentRequestType("service");
				log.info(reqHandler.getCurrentRequestID() + ", " + reqHandler.getCurrentRequestType());
				reqMessage.setWrapText(true);
				addInfo.setWrapText(true);
				addInfo.setText("Notes: " + req.getNotes());
				String message;
				if (user.isManager()) {
					log.info("logged in as manager");
					message = getUserFullName(req.getRequestUsername()) + " requests a " + (req.getType() != null ? req.getType() : "") + " " + req.getService() + " service " +
							((req.getPatientName() != null && req.getPatientID() != null) ? "for " + req.getPatientName() + "(" + req.getPatientID() + ")" : "") +
							(req.getLocation() != null ? " at location " + req.getLocation() : "");
					if (req.getStatus().equals("1") || req.getStatus().equals("2")) {
						buttonBox.getChildren().add(btnAssign);
						btnAssign.setText("Assign");
						if (req.getStatus().equals("2")) {
							btnAssign.setText("Re-Assign");
						}
					} else if (req.getStatus().equals("0")) {
						buttonBox.getChildren().add(btnDecline);
						buttonBox.getChildren().add(btnApprove);
					}
				} else {
					log.info("logged in as service worker");
					message = "You have been assigned to complete a " + (req.getType() != null ? req.getType() : "") + " " + req.getService() + " service " +
							((req.getPatientName() != null && req.getPatientID() != null) ? "for " + req.getPatientName() + "(" + req.getPatientID() + ")" : "") +
							(req.getLocation() != null ? " at location " + req.getLocation() : "");

					resetButtons();
					buttonBox.getChildren().add(btnCompleted);
					btnCompleted.setText("Completed");
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
		ArrayList<String> fullName = db.getTableFromResultSet(db.executeQuery(new SQLEntry(DBConstants.GET_NAME_BY_USERNAME, new ArrayList<>(Collections.singletonList(username))))).get(0);
		return fullName.get(0) + " " + fullName.get(1);
	}

	@FXML
	private void btnBackClicked() {
		loaderHelper.goBack();
	}

	@FXML
	private void btnAssignClicked() {
		try {
			FXMLLoader loader = loaderHelper.getFXMLLoader("AssignPopup");
			Parent root = loader.load();
			AssignPopupController assignPopupController = loader.getController();
			assignPopupController.setNotificationsPageController(this);
			loaderHelper.setupPopup(new Stage(), new Scene(root));
		} catch (IOException ex) {
			log.error("Encountered IOException", ex);
		}
	}

	@FXML
	private void btnApproveClicked() {
		if (user.isManager()) {
			resetButtons();
			buttonBox.getChildren().add(btnAssign);
		}
		switch (reqHandler.getCurrentRequestType()) {
			case "medication": {
				db.executeUpdate(new SQLEntry(DBConstants.UPDATE_MEDICATION_REQUEST_STATUS, new ArrayList<>(Arrays.asList("1", getCurrentMedicationRequest().getID()))));
				getCurrentMedicationRequest().setStatus("1");
			}
			break;
			case "gift": {
				db.executeUpdate(new SQLEntry(DBConstants.UPDATE_GIFT_DELIVERY_REQUEST_STATUS, new ArrayList<>(Arrays.asList("1", getCurrentGiftRequest().getID()))));
				getCurrentGiftRequest().setStatus("1");
			}
			break;
			case "service": {
				db.executeUpdate(new SQLEntry(DBConstants.UPDATE_SERVICE_REQUEST_STATUS, new ArrayList<>(Arrays.asList("1", getCurrentServiceRequest().getID()))));
				getCurrentServiceRequest().setStatus("1");
			}
		}
		setCellFactories();
	}

	@FXML
	private void btnCompletedClicked() {
		switch (reqHandler.getCurrentRequestType()) {
			case "medication": {
				db.executeUpdate(new SQLEntry(DBConstants.UPDATE_MEDICATION_REQUEST_STATUS, new ArrayList<>(Arrays.asList("4", getCurrentMedicationRequest().getID()))));
				getCurrentMedicationRequest().setStatus("4");
			}
			break;
			case "gift": {
				db.executeUpdate(new SQLEntry(DBConstants.UPDATE_GIFT_DELIVERY_REQUEST_STATUS, new ArrayList<>(Arrays.asList("4", getCurrentGiftRequest().getID()))));
				getCurrentGiftRequest().setStatus("4");
			}
			break;
			case "service": {
				db.executeUpdate(new SQLEntry(DBConstants.UPDATE_SERVICE_REQUEST_STATUS, new ArrayList<>(Arrays.asList("4", getCurrentServiceRequest().getID()))));
				getCurrentServiceRequest().setStatus("4");
			}
		}
		setCellFactories();
		switch (reqHandler.getCurrentRequestType()) {
			case "medication": {
				db.executeUpdate(new SQLEntry(DBConstants.REMOVE_MEDICATION_REQUEST, new ArrayList<>(Collections.singletonList(getCurrentMedicationRequest().getID()))));
				setCurrentMedicationRequest(null);
				medReqs.getSelectionModel().getSelectedItems().removeAll();
			}
			break;
			case "gift": {
				db.executeUpdate(new SQLEntry(DBConstants.REMOVE_GIFT_DELIVERY_REQUEST, new ArrayList<>(Collections.singletonList(getCurrentGiftRequest().getID()))));
				setCurrentMedicationRequest(null);
				giftReqs.getSelectionModel().getSelectedItems().removeAll();
			}
			break;
			case "service": {
				db.executeUpdate(new SQLEntry(DBConstants.REMOVE_SERVICE_REQUEST, new ArrayList<>(Collections.singletonList(getCurrentServiceRequest().getID()))));
				setCurrentMedicationRequest(null);
				serviceReqs.getSelectionModel().getSelectedItems().removeAll();
			}
		}
		setCellFactories();
	}

	@FXML
	private void btnDeclineClicked() {
		switch (reqHandler.getCurrentRequestType()) {
			case "medication": {
				db.executeUpdate(new SQLEntry(DBConstants.UPDATE_MEDICATION_REQUEST_STATUS, new ArrayList<>(Arrays.asList("3", getCurrentMedicationRequest().getID()))));
				getCurrentMedicationRequest().setStatus("3");
			}
			break;
			case "gift": {
				db.executeUpdate(new SQLEntry(DBConstants.UPDATE_GIFT_DELIVERY_REQUEST_STATUS, new ArrayList<>(Arrays.asList("3", getCurrentGiftRequest().getID()))));
				getCurrentGiftRequest().setStatus("3");
			}
			break;
			case "service": {
				db.executeUpdate(new SQLEntry(DBConstants.UPDATE_SERVICE_REQUEST_STATUS, new ArrayList<>(Arrays.asList("3", getCurrentServiceRequest().getID()))));
				getCurrentServiceRequest().setStatus("3");
			}
		}
		setCellFactories();
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

	public JFXButton getBtnAssign() {
		return btnAssign;
	}

	private void resetButtons() {
		buttonBox.getChildren().remove(btnCompleted);
		buttonBox.getChildren().remove(btnAssign);
		buttonBox.getChildren().remove(btnDecline);
		buttonBox.getChildren().remove(btnApprove);
		btnAssign.setText("Assign");
	}

	private void setMessageLists(User user) {
		if (user.isManager()) {

			if (user.getDept().equals("pharmacy")) {
				messageBox.getChildren().remove(giftReqs);
				messageBox.getChildren().remove(lblGift);
				messageBox.getChildren().remove(lblService);
				messageBox.getChildren().remove(serviceReqs);
			} else if (user.getDept().equals("gift_shop")) {
				messageBox.getChildren().remove(serviceReqs);
				messageBox.getChildren().remove(medReqs);
				messageBox.getChildren().remove(lblMed);
				messageBox.getChildren().remove(lblService);
			} else {
				messageBox.getChildren().remove(giftReqs);
				messageBox.getChildren().remove(medReqs);
				messageBox.getChildren().remove(lblGift);
				messageBox.getChildren().remove(lblMed);
			}
		} else {
			if (!(user.getServices().contains("pharmacy"))) {
				messageBox.getChildren().remove(medReqs);
				messageBox.getChildren().remove(lblMed);
			}
			if (!(user.getServices().contains("gift_shop"))) {
				messageBox.getChildren().remove(giftReqs);
				messageBox.getChildren().remove(lblGift);
			}
			if (user.getServices().equals("pharmacy") || user.getServices().equals("gift_shop") || user.getServices().equals("pharmacy;gift_shop") || user.getServices().equals("gift_shop;pharmacy")) {
				messageBox.getChildren().remove(serviceReqs);
				messageBox.getChildren().remove(lblService);
			}
		}

	}
}