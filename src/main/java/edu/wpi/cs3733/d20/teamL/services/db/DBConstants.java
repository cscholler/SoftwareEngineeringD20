package edu.wpi.cs3733.d20.teamL.services.db;

import java.util.ArrayList;
import java.util.Arrays;

public class DBConstants {
	public static final String DB_DRIVER = "org.apache.derby.jdbc.EmbeddedDriver";
	public static final String DB_URL = "jdbc:derby:myDB;create=true";
	public static final String SERVICE_NAME = "derby-db-embedded-01";

	public static ArrayList<String> GET_TABLE_NAMES() {
		return new ArrayList<>(Arrays.asList("Nodes", "Edges", "Users", "Doctors", "Patients", "Gifts", "Gift_Delivery_Requests", "Medication_Requests", "Service_Requests"));
	}

	public static final String CREATE_NODE_TABLE =
			"CREATE TABLE Nodes(" +
					"id VARCHAR(10) NOT NULL, " +
					"x_pos DOUBLE NOT NULL, " +
					"y_pos DOUBLE NOT NULL, " +
					"floor CHAR(1) NOT NULL, " +
					"building VARCHAR(64) NOT NULL, " +
					"node_type CHAR(4) NOT NULL, " +
					"l_name VARCHAR(32) NOT NULL, " +
					"s_name VARCHAR(32) NOT NULL, " +
					"PRIMARY KEY (id))";

	public static final String CREATE_EDGE_TABLE =
			"CREATE TABLE Edges(" +
					"id VARCHAR(21) NOT NULL, " +
					"node_start VARCHAR(10) NOT NULL REFERENCES Nodes(id), " +
					"node_end VARCHAR(10) NOT NULL REFERENCES Nodes(id), " +
					"PRIMARY KEY (id))";

	public static final String CREATE_USER_TABLE =
			"CREATE TABLE Users(" +
					"id INT NOT NULL GENERATED ALWAYS AS IDENTITY, " +
					"f_name VARCHAR(32) NOT NULL, " +
					"l_name VARCHAR(32) NOT NULL, " +
					"username VARCHAR(32) NOT NULL, " +
					"password VARCHAR(128) NOT NULL, " +
					"acct_type CHAR(1) NOT NULL, " +
					"services VARCHAR(512), " +
					"PRIMARY KEY (username))";

	public static final String CREATE_DOCTOR_TABLE =
			"CREATE TABLE Doctors(" +
					"id INT NOT NULL, " +
					"f_name VARCHAR(32) NOT NULL, " +
					"l_name VARCHAR(32) NOT NULL, " +
					"username VARCHAR(32) REFERENCES Users(username), " +
					"office_id VARCHAR(10) REFERENCES Nodes(id), " +
					"addl_info VARCHAR(256), " +
					"PRIMARY KEY (id))";

	public static final String CREATE_PATIENT_TABLE =
			"CREATE TABLE Patients(" +
					"id INT NOT NULL, " +
					"f_name VARCHAR(32) NOT NULL, " +
					"l_name VARCHAR(32) NOT NULL, " +
					"doctor_id INT REFERENCES Doctors(id), " +
					"room_id VARCHAR(10) REFERENCES Nodes(id), " +
					"addl_info VARCHAR(256), " +
					"PRIMARY KEY (id))";

	public static final String CREATE_GIFT_TABLE =
			"CREATE TABLE Gifts(" +
					"id INT NOT NULL GENERATED ALWAYS AS IDENTITY, " +
					"type VARCHAR(16) NOT NULL, " +
					"subtype VARCHAR(16) NOT NULL, " +
					"description VARCHAR(128) NOT NULL, " +
					"inventory INT NOT NULL, " +
					"PRIMARY KEY (id))";

	public static final String CREATE_GIFT_DELIVERY_REQUEST_TABLE =
			"CREATE TABLE Gift_Delivery_Requests(" +
					"id INT NOT NULL GENERATED ALWAYS AS IDENTITY, " +
					"patient_id INT NOT NULL REFERENCES Patients(id), " +
					"nurse_name VARCHAR(64) NOT NULL, " +
					"assignee VARCHAR(32) REFERENCES Users(username), " +
					"gift_id INT NOT NULL REFERENCES Gifts(id), " +
					"message VARCHAR(128), " +
					"notes VARCHAR(256), " +
					"status CHAR(1) NOT NULL, " +
					"date_and_time CHAR(19) NOT NULL, " +
					"PRIMARY KEY (id))";

	public static final String CREATE_MEDICATION_REQUEST_TABLE =
			"CREATE TABLE Medication_Requests(" +
					"id INT NOT NULL GENERATED ALWAYS AS IDENTITY, " +
					"doctor_id INT NOT NULL REFERENCES Doctors(id), " +
					"patient_id INT NOT NULL REFERENCES Patients(id), " +
					"nurse_name VARCHAR(64) NOT NULL, " +
					"dose VARCHAR(64) NOT NULL, " +
					"type VARCHAR(64) NOT NULL, " +
					"notes VARCHAR(256), " +
					"status CHAR(1) NOT NULL, " +
					"date_and_time CHAR(19) NOT NULL, " +
					"PRIMARY KEY (id))";

	public static final String CREATE_SERVICE_REQUEST_TABLE =
			"CREATE TABLE Service_Requests(" +
					"id INT NOT NULL GENERATED ALWAYS AS IDENTITY, " +
					"patient_id INT REFERENCES Patients(id), " +
					"request_user_id VARCHAR(32) REFERENCES Users(username), " +
					"location VARCHAR(10) NOT NULL REFERENCES Nodes(id), " +
					"service VARCHAR(64) NOT NULL, " +
					"type VARCHAR(64), " +
					"assignee VARCHAR(32) NOT NULL REFERENCES Users(username), " +
					"notes VARCHAR(256), " +
					"status CHAR(1) NOT NULL, " +
					"date_and_time CHAR(19) NOT NULL, " +
					"PRIMARY KEY (id))";

	public static final String DROP_NODE_TABLE =
			"DROP TABLE Nodes";

	public static final String DROP_EDGE_TABLE =
			"DROP TABLE Edges";

	public static final String DROP_USER_TABLE =
			"DROP TABLE Users";

	public static final String DROP_DOCTOR_TABLE =
			"DROP TABLE Doctors";

	public static final String DROP_PATIENT_TABLE =
			"DROP TABLE Patients";

	public static final String DROP_GIFT_TABLE =
			"DROP TABLE Gifts";

	public static final String DROP_GIFT_REQUEST_TABLE =
			"DROP TABLE Gift_Delivery_Requests";

	public static final String DROP_MEDICATION_REQUEST_TABLE =
			"DROP TABLE Medication_Requests";

	public static final String DROP_SERVICE_REQUEST_TABLE =
			"DROP TABLE Service_Requests";

	public static final String ADD_NODE =
			"INSERT INTO Nodes(id, x_pos, y_pos, floor, building, node_type, l_name, s_name)" +
					"VALUES(?, ?, ?, ?, ?, ?, ?, ?)";

	public static final String ADD_EDGE =
			"INSERT INTO Edges(id, node_start, node_end)" +
					"VALUES(?, ?, ?)";

	public static final String ADD_USER =
			"INSERT INTO Users(f_name, l_name, username, password, acct_type, services)" +
					"VALUES(?, ?, ?, ?, ?, ?)";

	public static final String ADD_DOCTOR =
			"INSERT INTO Doctors(id, f_name, l_name, email, office_id)" +
					"VALUES(?, ?, ?, ?, ?)";

	public static final String ADD_PATIENT =
			"INSERT INTO Patients(id, f_name, l_name, doctor_id, room_id)" +
					"VALUES(?, ?, ?, ?, ?)";

	public static final String ADD_MEDICATION_REQUEST =
			"INSERT INTO Medication_Requests(doctor_id, patient_id, nurse_name, dose, type, notes, status, date_and_time)" +
					"VALUES(?, ?, ?, ?, ?, ?, ?, ?)";

	public static final String SELECT_ALL_NODES =
			"SELECT * " +
					"FROM Nodes";

	public static final String SELECT_ALL_EDGES =
			"SELECT * " +
					"FROM Edges";

	public static final String SELECT_ALL_DOCTORS =
			"SELECT * " +
					"FROM Doctors";

	public static final String SELECT_ALL_PATIENTS =
			"SELECT * " +
					"FROM Patients";

	public static final String SELECT_ALL_MEDICATION_REQUESTS =
			"SELECT * " +
					"FROM Medication_Requests";

	public static final String SELECT_ALL_USERS =
			"SELECT * " +
					"FROM Users";

	public static final String GET_USER =
			"SELECT id, f_name, l_name, username, acct_type, services " +
					"FROM Users " +
					"WHERE username = ? AND password = ?";

	public static final String GET_USER_BY_ID =
			"SELECT id, username, f_name, l_name, acct_type " +
					"FROM Users " +
					"WHERE id = ?";

	public static final String GET_DOCTOR_ID =
			"SELECT id " +
					"FROM Doctors " +
					"WHERE f_name = ? AND l_name = ?";

	public static final String GET_DOCTOR_NAME =
			"SELECT f_name, l_name " +
					"FROM Doctors " +
					"WHERE id = ?";

	public static final String GET_PATIENT_ID =
			"SELECT id " +
					"FROM Patients " +
					"WHERE f_name = ? AND l_name = ?";

	public static final String GET_PATIENT_NAME =
			"SELECT f_name, l_name " +
					"FROM Patients " +
					"WHERE id = ?";

	public static final String GET_PATIENT_ROOM =
			"SELECT room_id " +
					"FROM Patients " +
					"WHERE id = ?";

	public static final String UPDATE_NODE =
			"UPDATE Nodes " +
					"SET x_pos = ?, y_pos = ?, floor = ?, building = ?, node_type = ?, l_name = ?, s_name = ? " +
					"WHERE id = ?";

	public static final String UPDATE_EDGE =
			"UPDATE Edges " +
					"SET node_start = ?, node_end = ? " +
					"WHERE id = ?";

	public static final String UPDATE_MEDICATION_REQUEST =
			"UPDATE Medication_Requests " +
					"SET doctor_id = ?, patient_id = ?, nurse_name = ?, dose = ?, type = ?, notes = ?, status = ?, date_and_time = ? " +
					"WHERE id = ?";

	public static final String UPDATE_MEDICATION_REQUEST_STATUS =
			"UPDATE Medication_Requests " +
					"SET status = ? " +
					"WHERE id = ?";

	public static final String UPDATE_USER_NAME =
			"UPDATE Users " +
					"SET f_name = ?, l_name = ? " +
					"WHERE id = ?";

	public static final String UPDATE_USER_PASSWORD =
			"UPDATE Users " +
					"SET password = ? " +
					"WHERE id = ?";

	public static final String UPDATE_USER_ACCT_TYPE =
			"UPDATE Users " +
					"SET acct_type = ? " +
					"WHERE id = ?";

	public static final String UPDATE_LAST_USER_LOGIN =
			"UPDATE Users " +
					"SET last_login = ? " +
					"WHERE id = ?";

	public static final String REMOVE_NODE =
			"DELETE FROM Nodes " +
					"WHERE id = ?";

	public static final String REMOVE_EDGE =
			"DELETE FROM Edges " +
					"WHERE id = ?";
}
