package com.dynocloud.node.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Overview {
	
	//int userID;
	int enclosureNodeID;
	//int centralNodeID;
	
	String dateTime;
	
	@JsonProperty("OPTIONAL_LOAD_TYPE")
	int OPTIONAL_LOAD_TYPE;
	
	@JsonProperty("TEMP")
	float TEMP;
	@JsonProperty("RH")
	float RH;
	@JsonProperty("OPTIONAL_LOAD")
	float OPTIONAL_LOAD;
	@JsonProperty("HEAT_LOAD")
	float HEAT_LOAD;
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
	
	String enclosureName;
	String petProfileID;
	//String profileName;
	
	float day_Temperature_SP;
	float day_Humidity_SP;
	float night_Temperature_SP;
	float night_Humidity_SP;
	float temperature_TH;
	float humidity_TH;
	
	
	boolean online;
	
	public boolean isOnline() {
		return online;
	}
	public void setOnline(boolean online) {
		this.online = online;
	}
	
	public float getDay_Temperature_SP() {
		return day_Temperature_SP;
	}
	public void setDay_Temperature_SP(float day_Temperature_SP) {
		this.day_Temperature_SP = day_Temperature_SP;
	}
	public float getDay_Humidity_SP() {
		return day_Humidity_SP;
	}
	public void setDay_Humidity_SP(float day_Humidity_SP) {
		this.day_Humidity_SP = day_Humidity_SP;
	}
	public float getNight_Temperature_SP() {
		return night_Temperature_SP;
	}
	public void setNight_Temperature_SP(float night_Temperature_SP) {
		this.night_Temperature_SP = night_Temperature_SP;
	}
	public float getNight_Humidity_SP() {
		return night_Humidity_SP;
	}
	public void setNight_Humidity_SP(float night_Humidity_SP) {
		this.night_Humidity_SP = night_Humidity_SP;
	}
	public float getTemperature_TH() {
		return temperature_TH;
	}
	public void setTemperature_TH(float temperature_TH) {
		this.temperature_TH = temperature_TH;
	}
	public float getHumidity_TH() {
		return humidity_TH;
	}
	public void setHumidity_TH(float humidity_TH) {
		this.humidity_TH = humidity_TH;
	}
	
	@JsonProperty("OPTIONAL_LOAD_TYPE")
	public int getOPTIONAL_LOAD_TYPE() {
		return OPTIONAL_LOAD_TYPE;
	}
	@JsonProperty("OPTIONAL_LOAD_TYPE")
	public void setOPTIONAL_LOAD_TYPE(int oPTIONAL_LOAD_TYPE) {
		OPTIONAL_LOAD_TYPE = oPTIONAL_LOAD_TYPE;
	}
	
//	public int getUserID() {
//		return userID;
//	}
//	public void setUserID(int userID) {
//		this.userID = userID;
//	}
	public int getEnclosureNodeID() {
		return enclosureNodeID;
	}
	public void setEnclosureNodeID(int enclosureNodeID) {
		this.enclosureNodeID = enclosureNodeID;
	}
//	public int getCentralNodeID() {
//		return centralNodeID;
//	}
//	public void setCentralNodeID(int centralNodeID) {
//		this.centralNodeID = centralNodeID;
//	}
	public String getDateTime() {
		return dateTime;
	}
	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}
	
	@JsonProperty("TEMP")
	public float getTEMP() {
		return TEMP;
	}
	@JsonProperty("TEMP")
	public void setTEMP(float tEMP) {
		TEMP = tEMP;
	}
	@JsonProperty("RH")
	public float getRH() {
		return RH;
	}
	@JsonProperty("RH")
	public void setRH(float rH) {
		RH = rH;
	}
	
	@JsonProperty("OPTIONAL_LOAD")
	public float getOPTIONAL_LOAD() {
		return OPTIONAL_LOAD;
	}
	@JsonProperty("OPTIONAL_LOAD")
	public void setOPTIONAL_LOAD(float oPTIONAL_LOAD) {
		OPTIONAL_LOAD = oPTIONAL_LOAD;
	}
	@JsonProperty("HEAT_LOAD")
	public float getHEAT_LOAD() {
		return HEAT_LOAD;
	}
	@JsonProperty("HEAT_LOAD")
	public void setHEAT_LOAD(float hEAT_LOAD) {
		HEAT_LOAD = hEAT_LOAD;
	}
	
	@JsonProperty("UV_STATUS")
	public int getUV_STATUS() {
		return UV_STATUS;
	}
	@JsonProperty("UV_STATUS")
	public void setUV_STATUS(int uV_STATUS) {
		UV_STATUS = uV_STATUS;
	}
	@JsonProperty("HUM_STATUS")
	public int getHUM_STATUS() {
		return HUM_STATUS;
	}
	@JsonProperty("HUM_STATUS")
	public void setHUM_STATUS(int hUMI_STATUS) {
		HUM_STATUS = hUMI_STATUS;
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
	
	@JsonProperty("HEAT_STATUS")
	public int getHEAT_STATUS() {
		return HEAT_STATUS;
	}
	@JsonProperty("HEAT_STATUS")
	public void setHEAT_STATUS(int hEAT_STATUS) {
		HEAT_STATUS = hEAT_STATUS;
	}
	@JsonProperty("OPTIONAL_STATUS")
	public int getOPTIONAL_STATUS() {
		return OPTIONAL_STATUS;
	}
	@JsonProperty("OPTIONAL_STATUS")
	public void setOPTIONAL_STATUS(int oPTIONAL_STATUS) {
		OPTIONAL_STATUS = oPTIONAL_STATUS;
	}
	
	public String getEnclosureName() {
		return enclosureName;
	}
	public void setEnclosureName(String enclosureName) {
		this.enclosureName = enclosureName;
	}
	public String getPetProfileID() {
		return petProfileID;
	}
	public void setPetProfileID(String petProfileID) {
		this.petProfileID = petProfileID;
	}
//	public String getProfileName() {
//		return profileName;
//	}
//	public void setProfileName(String profileName) {
//		this.profileName = profileName;
//	}
//	
//	@Override
//	public String toString() {
//		return "Overview [userID=" + userID + ", enclosureNodeID=" + enclosureNodeID + ", centralNodeID="
//				+ centralNodeID + ", dateTime=" + dateTime + ", OPTIONAL_LOAD_TYPE=" + OPTIONAL_LOAD_TYPE + ", TEMP="
//				+ TEMP + ", RH=" + RH + ", OPTIONAL_LOAD=" + OPTIONAL_LOAD + ", HEAT_LOAD=" + HEAT_LOAD + ", HUM_OR="
//				+ HUM_OR + ", HEAT_OR=" + HEAT_OR + ", UV_OR=" + UV_OR + ", OPTIONAL_OR=" + OPTIONAL_OR
//				+ ", HUM_STATUS=" + HUM_STATUS + ", HEAT_STATUS=" + HEAT_STATUS + ", UV_STATUS=" + UV_STATUS
//				+ ", OPTIONAL_STATUS=" + OPTIONAL_STATUS + ", enclosureName=" + enclosureName + ", petProfileID="
//				+ petProfileID + ", profileName=" + profileName + ", day_Temperature_SP=" + day_Temperature_SP
//				+ ", day_Humidity_SP=" + day_Humidity_SP + ", night_Temperature_SP=" + night_Temperature_SP
//				+ ", night_Humidity_SP=" + night_Humidity_SP + ", temperature_TH=" + temperature_TH + ", humidity_TH="
//				+ humidity_TH + "]";
//	}
		
}
