package com.dynocloud.server.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Module {

	int enclosureNodeID;
	int centralNodeID;
	int userID;
	String name;
	@JsonProperty("OPTIONAL_LOAD")
	int OPTIONAL_LOAD;
	String petProfileID;
	
//	@Override
//	public String toString() {
//		return "Module [enclosureNodeID=" + enclosureNodeID + ", centralNodeID=" + centralNodeID + ", userID=" + userID
//				+ ", name=" + name + ", OPTIONAL_LOAD=" + OPTIONAL_LOAD + ", petProfileID=" + petProfileID + "]";
//	}
	
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
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@JsonProperty("OPTIONAL_LOAD")
	public int getOPTIONAL_LOAD() {
		return OPTIONAL_LOAD;
	}
	@JsonProperty("OPTIONAL_LOAD")
	public void setOPTIONAL_LOAD(int oPTIONAL_LOAD) {
		OPTIONAL_LOAD = oPTIONAL_LOAD;
	}
	public String getPetProfileID() {
		return petProfileID;
	}
	public void setPetProfileID(String petProfileID) {
		this.petProfileID = petProfileID;
	}
	
	
	
}
