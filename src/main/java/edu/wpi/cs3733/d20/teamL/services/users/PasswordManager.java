package edu.wpi.cs3733.d20.teamL.services.users;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordManager {

	public static String hashPassword(String password) {
		return BCrypt.hashpw(password, generateUserSalt());
	}

	public static String hashPassword(String password, String salt) {
		return BCrypt.hashpw(password, salt);
	}

	public static boolean isPasswordCorrect(String enteredPassword, String hashedPassword) throws IllegalArgumentException {
		return BCrypt.checkpw(enteredPassword, hashedPassword);
	}

	public static String generateUserSalt() {
		return BCrypt.gensalt();
	}
}
