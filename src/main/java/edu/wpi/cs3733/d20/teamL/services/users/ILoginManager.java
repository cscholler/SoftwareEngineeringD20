package edu.wpi.cs3733.d20.teamL.services.users;

import edu.wpi.cs3733.d20.teamL.entities.User;

import java.util.ArrayList;

public interface ILoginManager {
	String SERVICE_NAME = "login-manager";

	void startService();

	void stopService();

	void logIn(String username, String password);

	void logOut();

	String getHashedPassword(String password);

	void updateUserInfo(ArrayList<String> fieldsToUpdate, ArrayList<String> newValues);

	void updateName(String newName);

	void updatePassword(String newPassword);

	void updateAcctType(String newAcctType);

	User getCurrentUser();

	boolean isAuthenticated();
}
