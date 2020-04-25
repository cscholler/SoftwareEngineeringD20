package edu.wpi.cs3733.d20.teamL.views.controllers.requests;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.ResourceBundle;

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

import edu.wpi.cs3733.d20.teamL.entities.MedicineRequest;
import edu.wpi.cs3733.d20.teamL.services.db.DBConstants;
import edu.wpi.cs3733.d20.teamL.services.db.IDatabaseService;
import edu.wpi.cs3733.d20.teamL.services.db.SQLEntry;
import edu.wpi.cs3733.d20.teamL.util.FXMLLoaderHelper;

@Slf4j
public class NotificationsPageController implements Initializable {
   	private ObservableList<MedicineRequest> list = FXCollections.observableArrayList();
	private FXMLLoaderHelper loaderHelper = new FXMLLoaderHelper();
	private MedicineRequest currentRequest;
	@Inject
	private IDatabaseService db;
	@FXML
    private JFXButton btnBack, btnCompleted, btnDecline;
    @FXML
    private JFXListView<MedicineRequest> notifications;
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
		notifications.setCellFactory(param -> new ListCell<>() {
			@Override
			protected void updateItem(MedicineRequest medReq, boolean empty) {
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
							status = "Denied";
						}
						break;
					}
					setText("[" + medReq.getDateAndTime() + "] " +  medReq.getDose() + " of " + medReq.getMedType() + " for " + medReq.getPatientName() + " (" + status + ")");
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
		ArrayList<ArrayList<String>> medRequests = db.getTableFromResultSet(db.executeQuery(new SQLEntry(DBConstants.selectAllMedicationRequests)));
		String patientID;
		String patientName;
		String doctorID;
		String doctorName;
		String roomID;
		for (ArrayList<String> row : medRequests) {
			doctorID = row.get(1);
			patientID = row.get(2);
			ArrayList<String> name = db.getTableFromResultSet(db.executeQuery(new SQLEntry(DBConstants.getDoctorName, new ArrayList<>(Collections.singletonList(doctorID))))).get(0);
			doctorName = name.get(0) + " " + name.get(1);
			name = db.getTableFromResultSet(db.executeQuery(new SQLEntry(DBConstants.getPatientName, new ArrayList<>(Collections.singletonList(patientID))))).get(0);
			patientName = name.get(0) + " " + name.get(1);
			roomID = db.getTableFromResultSet(db.executeQuery(new SQLEntry(DBConstants.getPatientRoom, new ArrayList<>(Collections.singletonList(patientID))))).get(0).get(0);
			System.out.println(row.get(0));
			list.add(new MedicineRequest(row.get(0), patientName, patientID, doctorName, row.get(3), row.get(4), row.get(5), roomID, row.get(6), row.get(7), row.get(8)));
		}
        notifications.getItems().addAll(list);
    }

    /**
     * Checks for anyone clicking on the listView of notifications and opens them in the pane to the right
     */
    @FXML
    private void displaySelected() {
        MedicineRequest req = notifications.getSelectionModel().getSelectedItem();
        setCurrentRequest(req);
        try {
			String message = notifications.getSelectionModel().getSelectedItem().getPatientName();
			if (message == null || message.isEmpty()) {
				System.out.println("Nothing");
			} else {
				reqMessage.setWrapText(true);
				reqMessage.setText(req.getNurseName() + " requests " + req.getDose() + " of " + req.getMedType() + " for " + req.getPatientName() + "(" + req.getPatientID() +")" + " in room " + req.getRoomNum());
				addInfo.setWrapText(true);
				addInfo.setText(req.getAddInfo());
				System.out.println(message);
			}
		} catch (NullPointerException ex) {
        	log.info("No notifcation currently selected");
		}
    }

    /**
     * Handles buttons pressed on the notification screen back, decline, approve
     * @param e tracks which button is pressed
     * @throws IOException
     */
    @FXML
    public void handleButtonAction(ActionEvent e) throws IOException {

        if (e.getSource() == btnBack) {
			Parent root = loaderHelper.getFXMLLoader("AdminView").load();
			loaderHelper.setupScene(new Scene(root));

        } else {
        	String status = "0";
        	if (e.getSource() == btnCompleted) {
        		status = "1";
			} else if (e.getSource() == btnDecline) {
        		status = "2";
			}
			db.executeUpdate(new SQLEntry(DBConstants.updateMedicationRequestStatus, new ArrayList<>(Arrays.asList(status, getCurrentRequest().getID()))));
			getCurrentRequest().setStatus(status);
			System.out.println(getCurrentRequest().getStatus());
		}
    }

	public MedicineRequest getCurrentRequest() {
		return currentRequest;
	}

	public void setCurrentRequest(MedicineRequest currentRequest) {
		this.currentRequest = currentRequest;
	}
}
