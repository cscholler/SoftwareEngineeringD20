package edu.wpi.cs3733.d20.teamL.services.users;

public interface IRequestHandlerService {
	String getCurrentRequestID();

	void setCurrentRequestID(String currentRequestID);

	String getCurrentRequestType();

	void setCurrentRequestType(String currentReqType);

	String getInterpreterReqLanguage();

	void setInterpreterReqLanguage(String interpreterReqLanguage);
}
