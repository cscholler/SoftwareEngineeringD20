package edu.wpi.cs3733.d20.teamL.views.controllers.requests;

import java.io.IOException;
import java.net.URL;
import java.util.*;

import edu.wpi.cs3733.d20.teamL.entities.GiftDeliveryRequest;
import edu.wpi.cs3733.d20.teamL.entities.MedicationRequest;
import edu.wpi.cs3733.d20.teamL.entities.ServiceRequest;
import edu.wpi.cs3733.d20.teamL.entities.User;
import edu.wpi.cs3733.d20.teamL.services.users.ILoginManager;
import edu.wpi.cs3733.d20.teamL.services.users.IRequestHandlerService;
import edu.wpi.cs3733.d20.teamL.views.controllers.dialogues.AssignPopupController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import com.google.inject.Inject;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;

import lombok.extern.slf4j.Slf4j;

import edu.wpi.cs3733.d20.teamL.services.db.DBConstants;
import edu.wpi.cs3733.d20.teamL.services.db.IDatabaseService;
import edu.wpi.cs3733.d20.teamL.services.db.SQLEntry;
import edu.wpi.cs3733.d20.teamL.util.FXMLLoaderFactory;

@Slf4j
public class NotificationsPageController implements Initializable {
	private HashMap<GiftDeliveryRequest,HBox> giftMap = new HashMap<>();
	private HashMap<ServiceRequest,HBox> serviceMap = new HashMap<>();
	private HashMap<MedicationRequest,HBox> medicationMap = new HashMap<>();
	private HashMap<String, String> status = new HashMap<>() {{
		put("0","Pending");
		put("1","Approved");
		put("2","Assigned");
		put("3","Denied");
		put("4","Completed");
	}};

	private HashMap<String, ImageView> statusIcons = new HashMap<>() {{
		put("0", new ImageView(new Image("/edu/wpi/cs3733/d20/teamL/assets/notification_status/pending.png", 0, 15, true, false, true)));
		put("1", new ImageView(new Image("/edu/wpi/cs3733/d20/teamL/assets/notification_status/approved.png", 0, 15, true, false, true)));
		put("2", new ImageView(new Image("/edu/wpi/cs3733/d20/teamL/assets/notification_status/assigned.png", 0, 15, true, false, true)));
		put("3", new ImageView(new Image("/edu/wpi/cs3733/d20/teamL/assets/notification_status/denied.png", 0, 15, true, false, true)));
		put("4", new ImageView(new Image("/edu/wpi/cs3733/d20/teamL/assets/notification_status/completed.png", 0, 15, true, false, true)));
	}};

	private final FXMLLoaderFactory loaderHelper = new FXMLLoaderFactory();
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
	private Label startingText, reqMessage, addInfo, lblMed, lblService, lblGift;
	@FXML
	private VBox allNotifications;
	@Inject
	private IDatabaseService db;
	@Inject
	private ILoginManager loginManager;
	@Inject
	private IRequestHandlerService reqHandler;
	@FXML
	private HBox buttonBox;
	@FXML
	private VBox messageBox, centerVBox;
	private User user;

	/**
	 * Calls loadData and sets up the cellFactory
	 *
	 * @param url
	 * @param resourceBundle
	 */
	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		reqMessage = new Label();
		addInfo = new Label();

		user = loginManager.getCurrentUser();
		resetButtons();
		resetCards();
	}

	/**
	 * Loads all relevant notifications as cards into the ScrollPane
	 */
	private void loadRequests(String type) {
		resetButtons();
		String username = user.getUsername();
		ArrayList<ArrayList<String>> requests = new ArrayList<>();
		switch (type) {
			case "medication": {
				switch (user.getAcctType()) {
					// staff member
					default:
					case "1":
					case "3":
					case "0": {
						requests = db.getTableFromResultSet(db.executeQuery(new SQLEntry(DBConstants.SELECT_ALL_MEDICATION_REQUESTS_FOR_DELIVERER, new ArrayList<>(Collections.singletonList(username)))));
					}
					break;
					// Doctor
					case "2": {
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

					allNotifications.getChildren().add(createMedicationRequestCard(new MedicationRequest(row.get(0), row.get(1), patientName, row.get(2), roomID, row.get(3), row.get(4), row.get(5), row.get(6), row.get(7), row.get(8), row.get(9))));
				}
			}
			break;
			case "gift": {
				if (user.isManager() && user.getDept().equals("gift_shop")) {
					requests = db.getTableFromResultSet(db.executeQuery(new SQLEntry(DBConstants.SELECT_ALL_GIFT_DELIVERY_REQUESTS)));
				} else {
					requests = db.getTableFromResultSet(db.executeQuery(new SQLEntry(DBConstants.SELECT_ALL_GIFT_DELIVERY_REQUESTS_FOR_ASSIGNEE, new ArrayList<>(Collections.singletonList(username)))));
				}

				for (ArrayList<String> row : requests) {
					String patientID = row.get(1);
					String patientName = getPatientFullName(patientID);
					String roomID = db.getTableFromResultSet(db.executeQuery(new SQLEntry(DBConstants.GET_PATIENT_ROOM, new ArrayList<>(Collections.singletonList(patientID))))).get(0).get(0);

					allNotifications.getChildren().add(createGiftRequestCard(new GiftDeliveryRequest(row.get(0), row.get(1), patientName, roomID, row.get(2), row.get(3), row.get(4), row.get(5), row.get(6), row.get(7), row.get(8), row.get(9))));
				}
			}
			break;
			default:
			case "service": {
				if (user.isManager()) {
					requests = db.getTableFromResultSet(db.executeQuery(new SQLEntry(DBConstants.SELECT_ALL_SERVICE_REQUESTS_FOR_MANAGER, new ArrayList<>(Collections.singletonList(user.getDept())))));
				} else {
					requests = db.getTableFromResultSet(db.executeQuery(new SQLEntry(DBConstants.SELECT_ALL_SERVICE_REQUESTS_FOR_ASSIGNEE, new ArrayList<>(Collections.singletonList(username)))));
				}

				for (ArrayList<String> row : requests) {
					String patientID = row.get(1);
					String patientName = patientID != null ? getPatientFullName(patientID) : null;

					allNotifications.getChildren().add(createServiceRequestCard(new ServiceRequest(row.get(0), row.get(1), patientName, row.get(2), row.get(3), row.get(4), row.get(5), row.get(6), row.get(7), row.get(8), row.get(9))));
				}
			}
		}
		if (allNotifications.getChildren().isEmpty()) {
			Label empty = new Label("No notifications found");
			empty.setStyle("-fx-font-size: 20; -fx-font-weight: bold; -fx-padding: 50 0 0 0");
		}
	}

	/**
	 * Checks for anyone clicking on the listView of medReq and opens them in the pane to the right
	 */
	@FXML
	private void displaySelectedMedReq(MedicationRequest request) {
		resetButtons();
		try {
			if (request != null) {
				reqHandler.setCurrentRequestType("medication");
				reqMessage.setWrapText(true);
				reqMessage.setStyle("-fx-font-weight: bold; -fx-font-size: 40;");
				addInfo.setWrapText(true);
				addInfo.setText("Notes: " + request.getNotes());
				addInfo.setStyle("-fx-font-size: 30;");
				String message;
				ArrayList<String> doctorNameRow = db.getTableFromResultSet(db.executeQuery(new SQLEntry(DBConstants.GET_DOCTOR_NAME, new ArrayList<>(Collections.singletonList(request.getDoctorID()))))).get(0);
				String doctorName = doctorNameRow.get(0) + " " + doctorNameRow.get(1);
				if (user.getAcctType().equals("2") && (user.getFName() + " " + user.getLName()).equals(doctorName)) {
					message = getUserFullName(request.getNurseUsername()) + " requests " + request.getDose() + " of " + request.getMedType() + " for " +
							request.getPatientName() + "(" + request.getPatientID() + ")" + (request.getRoomNum() != null ? " in room " + request.getRoomNum() : "");
					if (request.getStatus().equals("1") || request.getStatus().equals("2")) {
						buttonBox.getChildren().add(btnAssign);
						btnAssign.setText("Assign");
						if (request.getStatus().equals("2")) {
							btnAssign.setText("Re-Assign");
						}
					} else if (request.getStatus().equals("0")) {
						buttonBox.getChildren().add(btnDecline);
						buttonBox.getChildren().add(btnApprove);
					}
				} else {
					message = doctorName + " has assigned you to " + request.getDose() + " of " + request.getMedType() + " to be delivered to " +
							request.getPatientName() + "(" + request.getPatientID() + ")" + (request.getRoomNum() != null ? " in room " + request.getRoomNum() : "");
					buttonBox.getChildren().add(btnCompleted);
				}
				reqMessage.setText(message);

				centerVBox.getChildren().clear();
				VBox notificationText = new VBox(reqMessage,addInfo);
				notificationText.setStyle("-fx-background-color: white; -fx-effect: dropshadow(three-pass-box, derive(BLACK, -20%), 10, 0, 2, 2);");
				centerVBox.getChildren().add(notificationText);
			} else {
				log.warn("Attempted to display an empty or invalid request.");
			}
		} catch (NullPointerException ex) {
			log.info("No notification currently selected");
		}
	}

	private void displaySelectedGiftReq(GiftDeliveryRequest request) {
		resetButtons();
		try {
			if (request != null) {
				reqHandler.setCurrentRequestType("gift");
				reqMessage.setWrapText(true);
				reqMessage.setStyle("-fx-font-weight: bold; -fx-font-size: 40;");
				addInfo.setWrapText(true);
				addInfo.setText("Message: " + request.getMessage() + "\n" + "Notes: " + request.getNotes());
				addInfo.setStyle("-fx-font-size: 30;");
				String message;
				String giftText = request.getGifts();

				if (user.isManager()) {
					message = getUserFullName(request.getRequestUsername()) + " requests " + giftText + " for " +
							request.getPatientName() + "(" + request.getPatientID() + ")" + (request.getRoomNum() != null ? " in room " + request.getRoomNum() : "");
					if (request.getStatus().equals("1") || request.getStatus().equals("2")) {
						buttonBox.getChildren().add(btnAssign);
						btnAssign.setText("Assign");
						if (request.getStatus().equals("2")) {
							btnAssign.setText("Re-Assign");
						}
					} else if (request.getStatus().equals("0")) {
						buttonBox.getChildren().add(btnDecline);
						buttonBox.getChildren().add(btnApprove);
					}
				} else {
					message = "You have been assigned to deliver " + giftText + " to " +
							request.getPatientName() + "(" + request.getPatientID() + ")" + (request.getRoomNum() != null ? " in room " + request.getRoomNum() : "");
					buttonBox.getChildren().add(btnCompleted);
				}
				reqMessage.setText(message);

				centerVBox.getChildren().clear();
				VBox notificationText = new VBox(reqMessage,addInfo);
				notificationText.setStyle("-fx-background-color: white; -fx-effect: dropshadow(three-pass-box, derive(BLACK, -20%), 10, 0, 2, 2);");
				centerVBox.getChildren().add(notificationText);
			} else {
				log.warn("Attempted to display an empty or invalid request.");
			}
		} catch (NullPointerException ex) {
			log.info("No notification currently selected");
		}
	}

	@FXML
	private void displaySelectedServiceReq(ServiceRequest request) {
		resetButtons();
		try {
			setCurrentServiceRequest(request);
			if (request != null) {
				reqHandler.setCurrentRequestType("service");
				reqMessage.setWrapText(true);
				reqMessage.setStyle("-fx-font-weight: bold; -fx-font-size: 40;");
				addInfo.setWrapText(true);
				addInfo.setText("Notes: " + request.getNotes());
				addInfo.setStyle("-fx-font-size: 30;");
				String message;
				Collection<String> vowels = new ArrayList<>(Arrays.asList("a", "e", "i", "o", "u"));
				boolean doesTypeStartWithVowel = false;
				boolean doesServiceStartWithVowel = false;
				if (request.getType() != null) {
					if (!(request.getType().isEmpty() && request.getType().isBlank())) {
						for (String vowel : vowels) {
							if (request.getType().toLowerCase().startsWith(vowel)) {
								doesTypeStartWithVowel = true;
								break;
							}
						}
					}
				} else {
					for (String vowel : vowels) {
						if (request.getService().toLowerCase().startsWith(vowel)) {
							doesServiceStartWithVowel = true;
							break;
						}
					}
				}
				if (user.isManager()) {
					if (user.getDept().equals("interpreter")) {
						reqHandler.setInterpreterReqLanguage(request.getType());
					}
					// TODO: Fix vowel checking
					message = getUserFullName(request.getRequestUsername()) + " requests " + (request.getType() != null ? ((doesTypeStartWithVowel ? "an " : "a ") +
							request.getType()) : (doesServiceStartWithVowel ? "an" : "a")) + " " + request.getService() + " service" +
							((request.getPatientName() != null && request.getPatientID() != null) ? " for " + request.getPatientName() + "(" + request.getPatientID() + ")" : "") +
							(request.getLocation() != null ? " at location " + request.getLocation() : "");
					if (request.getStatus().equals("1") || request.getStatus().equals("2")) {
						buttonBox.getChildren().add(btnAssign);
						btnAssign.setText("Assign");
						if (request.getStatus().equals("2")) {
							btnAssign.setText("Re-Assign");
						}
					} else if (request.getStatus().equals("0")) {
						buttonBox.getChildren().add(btnDecline);
						buttonBox.getChildren().add(btnApprove);
					}
				} else {
					message = "You have been assigned to complete " + (request.getType() != null ? ((doesTypeStartWithVowel ? "an " : "a ") +
							request.getType()) : (doesServiceStartWithVowel ? "an" : "a")) + " " + request.getService() + " service " +
							((request.getPatientName() != null && request.getPatientID() != null) ? " for " + request.getPatientName() + "(" + request.getPatientID() + ")" : "") +
							(request.getLocation() != null ? " at location " + request.getLocation() : "");
					buttonBox.getChildren().add(btnCompleted);
					btnCompleted.setText("Completed");
				}
				reqMessage.setText(message);

				centerVBox.getChildren().clear();
				VBox notificationText = new VBox(reqMessage,addInfo);
				notificationText.setStyle("-fx-background-color: white; -fx-effect: dropshadow(three-pass-box, derive(BLACK, -20%), 10, 0, 2, 2);");
				centerVBox.getChildren().add(notificationText);
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
			FXMLLoader loader = loaderHelper.getFXMLLoader("staff/AssignPopup");
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
		resetCards();
	}

	/**
	 * Marks a service request as completed and removes it from the database when the completed button is clicked
	 */
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

		switch (reqHandler.getCurrentRequestType()) {
			case "medication": {
				db.executeUpdate(new SQLEntry(DBConstants.REMOVE_MEDICATION_REQUEST, new ArrayList<>(Collections.singletonList(getCurrentMedicationRequest().getID()))));
				setCurrentMedicationRequest(null);
			}
			break;
			case "gift": {
				db.executeUpdate(new SQLEntry(DBConstants.REMOVE_GIFT_DELIVERY_REQUEST, new ArrayList<>(Collections.singletonList(getCurrentGiftRequest().getID()))));
				setCurrentMedicationRequest(null);
			}
			break;
			case "service": {
				db.executeUpdate(new SQLEntry(DBConstants.REMOVE_SERVICE_REQUEST, new ArrayList<>(Collections.singletonList(getCurrentServiceRequest().getID()))));
				setCurrentMedicationRequest(null);
			}
		}
		resetCards();
	}

	/**
	 * Denies a service request when the decline button is clicked
	 */
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
		resetCards();
	}

	/**
	 * Resets all buttons to their default positions, visibility, and text
	 */
	private void resetButtons() {
		buttonBox.getChildren().remove(btnCompleted);
		buttonBox.getChildren().remove(btnAssign);
		buttonBox.getChildren().remove(btnDecline);
		buttonBox.getChildren().remove(btnApprove);
		btnAssign.setText("Assign");
	}

	public void resetCards(){
		allNotifications.getChildren().clear();
		loadRequests("medication");
		loadRequests("gift");
		loadRequests("service");
	}

	private HBox createGiftRequestCard(GiftDeliveryRequest request) {
		HBox card = new HBox();

		ImageView statusIcon = new ImageView(new Image("/edu/wpi/cs3733/d20/teamL/assets/notification_status/" + status.get(request.getStatus()).toLowerCase() + ".png",0,15,true,false,true));
		statusIcon.setStyle("-fx-pref-height: 15; -fx-max-height: 15;");
		Label statusLabel = new Label(status.get(request.getStatus()));
		statusLabel.setStyle("-fx-font-size: 16;");
		HBox statusBox = new HBox(statusIcon,statusLabel);
		statusBox.setStyle("-fx-spacing: 5; -fx-alignment: center-left");

		Label cardHeader = new Label("Gift(s) for " + request.getPatientName()); //Set header text
		cardHeader.setStyle("-fx-font-size: 16; -fx-font-weight: bold");
		Label time = new Label("[" + request.getDateAndTime() + "]");
		time.setStyle("-fx-font-size: 16;");
		VBox titleNotes = new VBox(cardHeader,time,statusBox);
		titleNotes.setStyle("-fx-padding: 5 5 5 5; -fx-pref-width: 325");

		JFXButton btnDeleteCard = new JFXButton("", new ImageView(new Image("/edu/wpi/cs3733/d20/teamL/assets/staff_view/close.png", 0,15,true,false,true)));
		btnDeleteCard.setStyle("-fx-background-color: transparent; -fx-content-display: graphic-only;");
		btnDeleteCard.setOnAction(e -> {
			reqHandler.setCurrentRequestType("gift");
			setCurrentGiftRequest(request);
			btnDeclineClicked();
		});	//Set onAction

		card.getChildren().addAll(titleNotes,btnDeleteCard);
		card.setStyle("-fx-fill-width: true; -fx-background-color: white; ");
		card.setOnMouseClicked(e -> {
			displaySelectedGiftReq(request);
		});

		giftMap.put(request,card);
		return card;
	}

	private HBox createMedicationRequestCard(MedicationRequest request) {
		HBox card = new HBox();

		ImageView statusIcon = new ImageView(new Image("/edu/wpi/cs3733/d20/teamL/assets/notification_status/" + status.get(request.getStatus()).toLowerCase() + ".png",0,15,true,false,true));
		statusIcon.setStyle("-fx-pref-height: 15; -fx-max-height: 15;");
		Label statusLabel = new Label(status.get(request.getStatus()));
		statusLabel.setStyle("-fx-font-size: 16;");
		HBox statusBox = new HBox(statusIcon,statusLabel);
		statusBox.setStyle("-fx-spacing: 5; -fx-alignment: center-left");

		Label cardHeader = new Label(request.getPatientName() + " - " + request.getDose() + " " + request.getMedType()); //Set header text
		cardHeader.setStyle("-fx-font-size: 16; -fx-font-weight: bold");
		Label time = new Label("[" + request.getDateAndTime() + "]");
		time.setStyle("-fx-font-size: 16;");
		VBox titleNotes = new VBox(cardHeader,time,statusBox);
		titleNotes.setStyle("-fx-padding: 5 5 5 5; -fx-pref-width: 325");

		JFXButton btnDeleteCard = new JFXButton("", new ImageView(new Image("/edu/wpi/cs3733/d20/teamL/assets/staff_view/close.png", 0,15,true,false,true)));
		btnDeleteCard.setStyle("-fx-background-color: transparent; -fx-content-display: graphic-only;");
		btnDeleteCard.setOnAction(e -> {
			reqHandler.setCurrentRequestType("medication");
			setCurrentMedicationRequest(request);
			btnDeclineClicked();
		});	//Set onAction

		card.getChildren().addAll(titleNotes,btnDeleteCard);
		card.setStyle("-fx-max-width: 200; -fx-background-color: white; ");
		card.setOnMouseClicked(e -> {
			displaySelectedMedReq(request);
		});

		medicationMap.put(request,card);
		return card;
	}

	private HBox createServiceRequestCard(ServiceRequest request) {
		HBox card = new HBox();

		ImageView statusIcon = new ImageView(new Image("/edu/wpi/cs3733/d20/teamL/assets/notification_status/" + status.get(request.getStatus()).toLowerCase() + ".png",0,15,true,false,true));
		statusIcon.setStyle("-fx-pref-height: 15; -fx-max-height: 15;");
		Label statusLabel = new Label(status.get(request.getStatus()));
		statusLabel.setStyle("-fx-font-size: 16;");
		HBox statusBox = new HBox(statusIcon,statusLabel);
		statusBox.setStyle("-fx-spacing: 5; -fx-alignment: center-left");

		Label cardHeader = new Label(request.getService() + " Request"); //Set header text
		cardHeader.setStyle("-fx-font-size: 16; -fx-font-weight: bold");
		Label time = new Label("[" + request.getDateAndTime() + "]");
		time.setStyle("-fx-font-size: 16;");
		VBox titleNotes = new VBox(cardHeader,time,statusBox);
		titleNotes.setStyle("-fx-padding: 5 5 5 5; -fx-pref-width: 325");

		JFXButton btnDeleteCard = new JFXButton("", new ImageView(new Image("/edu/wpi/cs3733/d20/teamL/assets/staff_view/close.png", 0,15,true,false,true)));
		btnDeleteCard.setStyle("-fx-background-color: transparent; -fx-content-display: graphic-only;");
		btnDeleteCard.setOnAction(e -> {
			reqHandler.setCurrentRequestType("service");
			setCurrentServiceRequest(request);
			btnDeclineClicked();
		});

		card.getChildren().addAll(titleNotes,btnDeleteCard);
		card.setStyle("-fx-background-color: white; -fx-fill-width: true; -fx-pref-width: 350; -fx-effect: dropshadow(three-pass-box, derive(BLACK, -20%), 10, 0, 2, 2);");
		card.setOnMouseClicked(e -> {
			displaySelectedServiceReq(request);
		});

		serviceMap.put(request,card);
		return card;
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

	public Label getAddInfo() {
		return addInfo;
	}
}