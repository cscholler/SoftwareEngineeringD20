package edu.wpi.cs3733.d20.teamL.services.users;

import edu.wpi.cs3733.d20.teamL.entities.User;

import java.util.ArrayList;

public interface ILoginManager {
	String SERVICE_NAME = "login-manager-01";

	void logIn(String username, String password);

	void logInFR(String username, double confidence);

	void logOut(boolean verbose);

	void updateUserInfo(ArrayList<String> fieldsToUpdate, ArrayList<String> newValues);

	void updateName(String newName);

	void updatePassword(String newPassword);

	void updateAcctType(String newAcctType);

	User getCurrentUser();

	boolean isAuthenticated();
}
