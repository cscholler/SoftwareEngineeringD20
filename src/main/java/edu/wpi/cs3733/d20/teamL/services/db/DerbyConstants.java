package edu.wpi.cs3733.d20.teamL.services.db;

public class DerbyConstants {
	static final String DB_DRIVER = "org.apache.derby.jdbc.EmbeddedDriver";
	static final String DB_PREFIX = "jdbc:derby:";
	static final String DB_URL = "bwh-embedded-db;create=true";
	public static final String SERVICE_NAME = "derby-db-embedded-01";

	public static final String CREATE_NODE_TABLE =
			"CREATE TABLE Nodes(" +
					"id VARCHAR(16) NOT NULL, " +
					"x_pos DOUBLE NOT NULL, " +
					"y_pos DOUBLE NOT NULL, " +
					"floor CHAR(2) NOT NULL, " +
					"building VARCHAR(64) NOT NULL, " +
					"node_type CHAR(4) NOT NULL, " +
					"l_name VARCHAR(64) NOT NULL, " +
					"s_name VARCHAR(64) NOT NULL, " +
					"PRIMARY KEY (id))";

	public static final String CREATE_EDGE_TABLE =
			"CREATE TABLE Edges(" +
					"id VARCHAR(21) NOT NULL, " +
					"node_start VARCHAR(16) NOT NULL REFERENCES Nodes(id), " +
					"node_end VARCHAR(16) NOT NULL REFERENCES Nodes(id), " +
					"PRIMARY KEY (id))";

	public static final String CREATE_USER_TABLE =
			"CREATE TABLE Users(" +
					"id INT NOT NULL GENERATED ALWAYS AS IDENTITY, " +
					"f_name VARCHAR(32) NOT NULL, " +
					"l_name VARCHAR(32) NOT NULL, " +
					"username VARCHAR(32) NOT NULL, " +
					"password VARCHAR(256) NOT NULL, " +
					// 0: staff member, 1: Nurse, 2: Doctor, 3: admin
					"acct_type CHAR(1) NOT NULL, " +
					"services VARCHAR(512), " +
					"manager VARCHAR(32), " +
					"PRIMARY KEY (username))";

	public static final String CREATE_DOCTOR_TABLE =
			"CREATE TABLE Doctors(" +
					"id INT NOT NULL, " +
					"f_name VARCHAR(32) NOT NULL, " +
					"l_name VARCHAR(32) NOT NULL, " +
					"username VARCHAR(32) REFERENCES Users(username), " +
					"office_id VARCHAR(16) REFERENCES Nodes(id), " +
					"addl_info VARCHAR(256), " +
					"PRIMARY KEY (id))";

	public static final String CREATE_PATIENT_TABLE =
			"CREATE TABLE Patients(" +
					"id INT NOT NULL, " +
					"f_name VARCHAR(32) NOT NULL, " +
					"l_name VARCHAR(32) NOT NULL, " +
					"doctor_id INT REFERENCES Doctors(id), " +
					"room_id VARCHAR(16) REFERENCES Nodes(id), " +
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
					"sender_name VARCHAR(32) NOT NULL, " +
					"request_username VARCHAR(32) NOT NULL REFERENCES Users(username), " +
					"assignee_username VARCHAR(32) REFERENCES Users(username), " +
					"gifts VARCHAR(256) NOT NULL, " +
					"message VARCHAR(128), " +
					"notes VARCHAR(256), " +
					// 0: Pending, 1: Approved, 2: Assigned, 3: Denied, 4: Completed
					"status CHAR(1) NOT NULL, " +
					"date_and_time CHAR(19) NOT NULL, " +
					"PRIMARY KEY (id))";

	public static final String CREATE_MEDICATION_REQUEST_TABLE =
			"CREATE TABLE Medication_Requests(" +
					"id INT NOT NULL GENERATED ALWAYS AS IDENTITY, " +
					"patient_id INT NOT NULL REFERENCES Patients(id), " +
					"doctor_id INT NOT NULL REFERENCES Doctors(id), " +
					"nurse_username VARCHAR(32) NOT NULL REFERENCES Users(username), " +
					"deliverer_username VARCHAR(32) REFERENCES Users(username), " +
					"dose VARCHAR(64) NOT NULL, " +
					"type VARCHAR(64) NOT NULL, " +
					"notes VARCHAR(256), " +
					// 0: Pending, 1: Approved, 2: Assigned, 3: Denied, 4: Completed
					"status CHAR(1) NOT NULL, " +
					"date_and_time CHAR(19) NOT NULL, " +
					"PRIMARY KEY (id))";

	public static final String CREATE_SERVICE_REQUEST_TABLE =
			"CREATE TABLE Service_Requests(" +
					"id INT NOT NULL GENERATED ALWAYS AS IDENTITY, " +
					"patient_id INT REFERENCES Patients(id), " +
					"request_username VARCHAR(32) REFERENCES Users(username), " +
					"assignee_username VARCHAR(32) REFERENCES Users(username), " +
					"location VARCHAR(16) REFERENCES Nodes(id), " +
					"service VARCHAR(64) NOT NULL, " +
					"type VARCHAR(64), " +
					"notes VARCHAR(256), " +
					// 0: Pending, 1: Approved, 2: Assigned, 3: Denied, 4: Completed
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

	public static final String DROP_GIFT_DELIVER_REQUEST_TABLE =
			"DROP TABLE Gift_Delivery_Requests";

	public static final String DROP_MEDICATION_REQUEST_TABLE =
			"DROP TABLE Medication_Requests";

	public static final String DROP_SERVICE_REQUEST_TABLE =
			"DROP TABLE Service_Requests";
}
