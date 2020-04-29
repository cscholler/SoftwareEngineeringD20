package edu.wpi.cs3733.d20.teamL.services.users;

public class RequestHandlerService implements IRequestHandlerService {
	public String currentRequestID;
	public String currentRequestType;

	@Override
	public String getCurrentRequestID() {
		return currentRequestID;
	}

	@Override
	public void setCurrentRequestID(String currentRequestID) {
		this.currentRequestID = currentRequestID;
	}

	@Override
	public String getCurrentRequestType() {
		return null;
	}

	@Override
	public void setCurrentRequestType(String currentRequestType) {
		this.currentRequestType = currentRequestType;
	}
}
