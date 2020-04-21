package edu.wpi.cs3733.d20.teamL.services.users;

import edu.wpi.cs3733.d20.teamL.entities.User;
import edu.wpi.cs3733.d20.teamL.services.Service;
import edu.wpi.cs3733.d20.teamL.services.db.DBConstants;
import edu.wpi.cs3733.d20.teamL.services.db.IDatabaseService;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

@Slf4j
public class LoginManager extends Service implements ILoginManager {
	private User currentUser;
	private boolean isAuthenticated;
	@Inject
	IDatabaseService db;

	public LoginManager() {
		super();
		this.serviceName = SERVICE_NAME;
	}

	@Override
	public void startService() {
		logOut();
	}

	@Override
	public void stopService() {
		logOut();
	}

	@Override
	public void logIn(String username, String password) {
		String hashedPassword = getHashedPassword(password);
		ArrayList<ArrayList<String>> results = db.getTableFromResultSet(db.executeQuery(DBConstants.getUser, new ArrayList<>(Arrays.asList(username, hashedPassword))));
		if (results.size() == 1) {
			ArrayList<String> userInfo = results.get(0);
			currentUser = new User(userInfo.get(0), userInfo.get(1), userInfo.get(2), userInfo.get(3), userInfo.get(4));
			String currentDateAndTime = new SimpleDateFormat("MM-dd-yyyy hh:mm:ss").format(new Date());
			db.executeUpdate(DBConstants.updateLastUserLogin, new ArrayList<>(Arrays.asList(currentDateAndTime, currentUser.getID())));
		} else {
			// No user found
			log.warn("No user found with the given username and password");
		}
	}

	@Override
	public void logOut() {
		currentUser = null;
		isAuthenticated = false;
	}

	@Override
	public String getHashedPassword(String password) {
		// TODO: SHA-256 implementation
		return password;
	}

	@Override
	public void updateUserInfo(ArrayList<String> fieldsToUpdate, ArrayList<String> newValues) {
		if (fieldsToUpdate.contains("name")) {
			updateName(newValues.get(fieldsToUpdate.indexOf("name")));
		} else if (fieldsToUpdate.contains("password")) {
			updatePassword(newValues.get(fieldsToUpdate.indexOf("password")));
		} else if (fieldsToUpdate.contains("acctType")) {
			updateAcctType(newValues.get(fieldsToUpdate.indexOf("acctType")));
		}
	}

	@Override
	public void updateName(String newName) {
		db.executeUpdate(DBConstants.updateUserName, new ArrayList<>(Arrays.asList(currentUser.getFName(), currentUser.getLName(), currentUser.getID())));
		currentUser.setFName(newName.substring(0, newName.indexOf(" ")));
		currentUser.setLName(newName.substring(newName.indexOf(" ") + 1));
	}

	@Override
	public void updatePassword(String newPassword) {
		db.executeUpdate(DBConstants.updateUserPassword, new ArrayList<>(Arrays.asList(newPassword, currentUser.getID())));
	}

	@Override
	public void updateAcctType(String newAcctType) {
		db.executeUpdate(DBConstants.updateUserAcctType, new ArrayList<>(Arrays.asList(newAcctType, currentUser.getID())));
		currentUser.setAcctType(newAcctType);
	}

	@Override
	public User getCurrentUser() {
		return currentUser;
	}

	@Override
	public boolean isAuthenticated() {
		return isAuthenticated;
	}
}
