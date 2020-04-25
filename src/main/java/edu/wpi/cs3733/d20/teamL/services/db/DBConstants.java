package edu.wpi.cs3733.d20.teamL.services.db;

import java.util.ArrayList;
import java.util.Arrays;

public class DBConstants {
	public static final String DB_DRIVER = "org.apache.derby.jdbc.EmbeddedDriver";
	public static final String DB_URL = "jdbc:derby:myDB;create=true";
	public static final String SERVICE_NAME = "derby-db-embedded-01";

	public static ArrayList<String> GET_TABLE_NAMES() {
		return new ArrayList<>(Arrays.asList("Nodes", "Edges", "Doctors", "Patients", "Medication_Requests", "Users"));
	}

	public static final String createNodeTable =
			"CREATE TABLE Nodes(" +
					"id VARCHAR(10), " +
					"x_pos VARCHAR(32), " +
					"y_pos VARCHAR(32), " +
					"floor CHAR(1), " +
					"building VARCHAR(64), " +
					"node_type CHAR(4), " +
					"l_name VARCHAR(32), " +
					"s_name VARCHAR(32), " +
					"PRIMARY KEY (id))";

	public static final String updateNode =
			"UPDATE Nodes " +
					"SET x_pos = ?, y_pos = ?, floor = ?, building = ?, node_type = ?, l_name = ?, s_name = ? " +
					"WHERE id = ?";

	public static final String removeNode =
			"DELETE FROM Nodes " +
					"WHERE id = ?";

	public static final String createEdgeTable =
			"CREATE TABLE Edges(" +
					"id VARCHAR(21), " +
					"node_start VARCHAR(10) REFERENCES Nodes(id), " +
					"node_end VARCHAR(10) REFERENCES Nodes(id), " +
					"PRIMARY KEY (id))";

	public static final String updateEdge =
			"UPDATE Edges " +
					"SET node_start = ?, node_end = ? " +
					"WHERE id = ?";

	public static final String removeEdge =
			"DELETE FROM Edges " +
					"WHERE id = ?";

	public static final String createDoctorTable =
			"CREATE TABLE Doctors(" +
					"id INT, " +
					"f_name VARCHAR(32), " +
					"l_name VARCHAR(32), " +
					"email VARCHAR(32), " +
					"office_id VARCHAR(10) REFERENCES Nodes(id), " +
					"PRIMARY KEY (id))";

	public static final String createPatientTable =
			"CREATE TABLE Patients(" +
					"id INT, " +
					"f_name VARCHAR(32), " +
					"l_name VARCHAR(32), " +
					"doctor_id INT REFERENCES Doctors(id), " +
					"room_id VARCHAR(10) REFERENCES Nodes(id), " +
					"PRIMARY KEY (id))";

	public static final String createMedicationRequestTable =
			"CREATE TABLE Medication_Requests(" +
					"id INT NOT NULL GENERATED ALWAYS AS IDENTITY, " +
					"doctor_id INT REFERENCES Doctors(id), " +
					"patient_id INT REFERENCES Patients(id), " +
					"nurse_name VARCHAR(64), " +
					"dose VARCHAR(64), " +
					"type VARCHAR(64), " +
					"notes VARCHAR(512), " +
					"status CHAR(1), " +
					"date_and_time CHAR(19), " +
					"PRIMARY KEY (id))";

	public static final String updateMedicationRequest =
			"UPDATE Medication_Requests " +
					"SET doctor_id = ?, patient_id = ?, nurse_name = ?, dose = ?, type = ?, notes = ?, status = ?, date_and_time = ? " +
					"WHERE id = ?";

	public static final String updateMedicationRequestStatus =
			"UPDATE Medication_Requests " +
					"SET status = ? " +
					"WHERE id = ?";

	public static final String createUserTable =
			"CREATE TABLE Users(" +
					"id INT, " +
					"username VARCHAR(32), " +
					"password VARCHAR(32), " +
					"acct_type CHAR(1), " +
					"last_login CHAR(19), " +
					"PRIMARY KEY (id))";

	public static final String dropNodeTable =
			"DROP TABLE Nodes";

	public static final String dropEdgeTable =
			"DROP TABLE Edges";

	public static final String dropDoctorTable =
			"DROP TABLE Doctors";

	public static final String dropPatientTable =
			"DROP TABLE Patients";

	public static final String dropMedicationRequestTable =
			"DROP TABLE Medication_Requests";

	public static final String dropUserTable =
			"DROP TABLE Users";

	public static final String addNode =
			"INSERT INTO Nodes(id, x_pos, y_pos, floor, building, node_type, l_name, s_name)" +
					"VALUES(?, ?, ?, ?, ?, ?, ?, ?)";

	public static final String addEdge =
			"INSERT INTO Edges(id, node_start, node_end)" +
					"VALUES(?, ?, ?)";

	public static final String addDoctor =
			"INSERT INTO Doctors(id, f_name, l_name, email, office_id)" +
					"VALUES(?, ?, ?, ?, ?)";

	public static final String addPatient =
			"INSERT INTO Patients(id, f_name, l_name, doctor_id, room_id)" +
					"VALUES(?, ?, ?, ?, ?)";

	public static final String addMedicationRequest =
			"INSERT INTO Medication_Requests(doctor_id, patient_id, nurse_name, dose, type, notes, status, date_and_time)" +
					"VALUES(?, ?, ?, ?, ?, ?, ?, ?)";

	public static final String addUser =
			"INSERT INTO Users(id, username, password, acct_type, last_login)" +
					"VALUES(?, ?, ?, ?, ?)";

	public static final String selectAllNodes =
			"SELECT * " +
					"FROM Nodes";

	public static final String selectAllEdges =
			"SELECT * " +
					"FROM Edges";

	public static final String selectAllDoctors =
			"SELECT * " +
					"FROM Doctors";

	public static final String selectAllPatients =
			"SELECT * " +
					"FROM Patients";

	public static final String selectAllMedicationRequests =
			"SELECT * " +
					"FROM Medication_Requests";

	public static final String selectAllUsers =
			"SELECT * " +
					"FROM Users";

	public static final String getDoctorID =
			"SELECT id " +
					"FROM Doctors " +
					"WHERE f_name = ? AND l_name = ?";

	public static final String getDoctorName =
			"SELECT f_name, l_name " +
					"FROM Doctors " +
					"WHERE id = ?";

	public static final String getPatientID =
			"SELECT id " +
					"FROM Patients " +
					"WHERE f_name = ? AND l_name = ?";

	public static final String getPatientName =
			"SELECT f_name, l_name " +
					"FROM Patients " +
					"WHERE id = ?";

	public static final String getPatientRoom =
			"SELECT room_id " +
					"FROM Patients " +
					"WHERE id = ?";

}
