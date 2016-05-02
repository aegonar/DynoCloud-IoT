package com.dynocloud.node.api;

public class PetProfile {
	
	String petProfileID ;
	int userID;
	//String  name;
	float day_Temperature_SP;
	float day_Humidity_SP;
	float night_Temperature_SP;
	float night_Humidity_SP;
	float temperature_TH;
	float humidity_TH;
	
	String DayTime;
	String NightTime;	
	
	public String getDayTime() {
		return DayTime;
	}
	public void setDayTime(String dayTime) {
		DayTime = dayTime;
	}
	public String getNightTime() {
		return NightTime;
	}
	public void setNightTime(String nightTime) {
		NightTime = nightTime;
	}
	
	public String getPetProfileID() {
		return petProfileID;
	}
	public void setPetProfileID(String petProfileID) {
		this.petProfileID = petProfileID;
	}
	public int getUserID() {
		return userID;
	}
	public void setUserID(int userID) {
		this.userID = userID;
	}
//	public String getName() {
//		return name;
//	}
//	public void setName(String name) {
//		this.name = name;
//	}
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
	
	@Override
	public String toString() {
		return "PetProfile [petProfileID=" + petProfileID + ", userID=" + userID + ", day_Temperature_SP="
				+ day_Temperature_SP + ", day_Humidity_SP=" + day_Humidity_SP + ", night_Temperature_SP="
				+ night_Temperature_SP + ", night_Humidity_SP=" + night_Humidity_SP + ", temperature_TH="
				+ temperature_TH + ", humidity_TH=" + humidity_TH + ", DayTime=" + DayTime + ", NightTime=" + NightTime
				+ "]";
	}
	

	
}
