package com.dynocloud.node.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ControlOverride {
	
	String dateTime;
	int	enclosureNodeID;
	int	centralNodeID;
	int	userID;
	
	@JsonProperty("HUM_OR")
	int HUM_OR;
	@JsonProperty("HEAT_OR")
	int HEAT_OR;
	@JsonProperty("UV_OR")
	int UV_OR;
	@JsonProperty("OPTIONAL_OR")
	int OPTIONAL_OR;
	
	@JsonProperty("HUM_STATUS")
	int HUM_STATUS;
	@JsonProperty("HEAT_STATUS")
	int HEAT_STATUS;
	@JsonProperty("UV_STATUS")
	int UV_STATUS;
	@JsonProperty("OPTIONAL_STATUS")
	int OPTIONAL_STATUS;
	
	public String getDateTime() {
		return dateTime;
	}
	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
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
	@JsonProperty("HUM_OR")
	public int getHUM_OR() {
		return HUM_OR;
	}
	@JsonProperty("HUM_OR")
	public void setHUM_OR(int hUM_OR) {
		HUM_OR = hUM_OR;
	}
	@JsonProperty("HEAT_OR")
	public int getHEAT_OR() {
		return HEAT_OR;
	}
	@JsonProperty("HEAT_OR")
	public void setHEAT_OR(int hEAT_OR) {
		HEAT_OR = hEAT_OR;
	}
	@JsonProperty("UV_OR")
	public int getUV_OR() {
		return UV_OR;
	}
	@JsonProperty("UV_OR")
	public void setUV_OR(int uV_OR) {
		UV_OR = uV_OR;
	}
	@JsonProperty("OPTIONAL_OR")
	public int getOPTIONAL_OR() {
		return OPTIONAL_OR;
	}
	@JsonProperty("OPTIONAL_OR")
	public void setOPTIONAL_OR(int oPTIONAL_OR) {
		OPTIONAL_OR = oPTIONAL_OR;
	}
	@JsonProperty("HUM_STATUS")
	public int getHUM_STATUS() {
		return HUM_STATUS;
	}
	@JsonProperty("HUM_STATUS")
	public void setHUM_STATUS(int hUM_STATUS) {
		HUM_STATUS = hUM_STATUS;
	}
	@JsonProperty("HEAT_STATUS")
	public int getHEAT_STATUS() {
		return HEAT_STATUS;
	}
	@JsonProperty("HEAT_STATUS")
	public void setHEAT_STATUS(int hEAT_STATUS) {
		HEAT_STATUS = hEAT_STATUS;
	}
	@JsonProperty("UV_STATUS")
	public int getUV_STATUS() {
		return UV_STATUS;
	}
	@JsonProperty("UV_STATUS")
	public void setUV_STATUS(int uV_STATUS) {
		UV_STATUS = uV_STATUS;
	}
	@JsonProperty("OPTIONAL_STATUS")
	public int getOPTIONAL_STATUS() {
		return OPTIONAL_STATUS;
	}
	@JsonProperty("OPTIONAL_STATUS")
	public void setOPTIONAL_STATUS(int oPTIONAL_STATUS) {
		OPTIONAL_STATUS = oPTIONAL_STATUS;
	}
	
	@Override
	public String toString() {
		return "ControlOverride [dateTime=" + dateTime + ", enclosureNodeID=" + enclosureNodeID + ", centralNodeID="
				+ centralNodeID + ", userID=" + userID + ", HUM_OR=" + HUM_OR + ", HEAT_OR=" + HEAT_OR + ", UV_OR="
				+ UV_OR + ", OPTIONAL_OR=" + OPTIONAL_OR + ", HUM_STATUS=" + HUM_STATUS + ", HEAT_STATUS=" + HEAT_STATUS
				+ ", UV_STATUS=" + UV_STATUS + ", OPTIONAL_STATUS=" + OPTIONAL_STATUS + "]";
	}
	

}
