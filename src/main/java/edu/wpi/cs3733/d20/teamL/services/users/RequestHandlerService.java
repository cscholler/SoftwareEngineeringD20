package edu.wpi.cs3733.d20.teamL.services.users;

public class RequestHandlerService implements IRequestHandlerService {
	public String currentRequestType;
	public String interpreterReqLanguage;

	@Override
	public String getCurrentRequestType() {
		return currentRequestType;
	}

	@Override
	public void setCurrentRequestType(String currentRequestType) {
		this.currentRequestType = currentRequestType;
	}

	@Override
	public String getInterpreterReqLanguage() {
		return interpreterReqLanguage;
	}

	@Override
	public void setInterpreterReqLanguage(String interpreterReqLanguage) {
		this.interpreterReqLanguage = interpreterReqLanguage;
	}
}
