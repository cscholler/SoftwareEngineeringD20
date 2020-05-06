package edu.wpi.cs3733.d20.teamL.services.users;

public interface IRequestHandlerService {
	String getCurrentRequestType();

	void setCurrentRequestType(String currentRequestType);

	String getInterpreterReqLanguage();

	void setInterpreterReqLanguage(String interpreterReqLanguage);
}
