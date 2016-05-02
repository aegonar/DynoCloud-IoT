package com.dynocloud.node.api;

public class Alert {
	
	int alertID;
	int enclosureNodeID;
	int centralNodeID;
	int userID;
	String dateTime;
	String message;
//	String destination;
	
	public int getAlertID() {
		return alertID;
	}
	public void setAlertID(int alertID) {
		this.alertID = alertID;
	}
	public int getEnclosureNodeID() {
		return enclosureNodeID;
	}
	public void setEnclosureNodeID(int enclosureNodeID) {
		this.enclosureNodeID = enclosureNodeID;
	}
	public int getCentralNodeID() {
		return centralNodeID;
	}
	public void setCentralNodeID(int centralNodeID) {
		this.centralNodeID = centralNodeID;
	}
	public int getUserID() {
		return userID;
	}
	public void setUserID(int userID) {
		this.userID = userID;
	}
	public String getDateTime() {
		return dateTime;
	}
	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	@Override
	public String toString() {
		return "Alert [alertID=" + alertID + ", enclosureNodeID=" + enclosureNodeID + ", centralNodeID=" + centralNodeID
				+ ", userID=" + userID + ", dateTime=" + dateTime + ", message=" + message + "]";
	}

	
}
