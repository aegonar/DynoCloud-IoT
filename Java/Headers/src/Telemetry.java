

import com.fasterxml.jackson.annotation.JsonProperty;

public class Telemetry {
	
	@JsonProperty("CLIENTID")
	int CLIENTID;
	@JsonProperty("TEMP")
	float TEMP;
	@JsonProperty("RH")
	float RH;
	@JsonProperty("OPTIONAL_LOAD")
	float OPTIONAL_LOAD;
	@JsonProperty("HEAT_LOAD")
	float HEAT_LOAD;
	@JsonProperty("UV_STATUS")
	int UV_STATUS;
	@JsonProperty("HUMI_STATUS")
	int HUMI_STATUS;
	
	String dateTime;
	int centralNodeID;
	int enclosureNodeID;
	int userID;
	
	@JsonProperty("CLIENTID")
	public int getCLIENTID() {
		return CLIENTID;
	}
	@JsonProperty("CLIENTID")
	public void setCLIENTID(int cLIENTID) {
		CLIENTID = cLIENTID;
		enclosureNodeID = cLIENTID;
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
	@JsonProperty("HUMI_STATUS")
	public int getHUMI_STATUS() {
		return HUMI_STATUS;
	}
	@JsonProperty("HUMI_STATUS")
	public void setHUMI_STATUS(int hUMI_STATUS) {
		HUMI_STATUS = hUMI_STATUS;
	}
	
	public String getDateTime() {
		return dateTime;
	}
	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
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
		
	public int getEnclosureNodeID() {
		return enclosureNodeID;
	}
	public void setEnclosureNodeID(int enclosureNodeID) {
		CLIENTID = enclosureNodeID;
		this.enclosureNodeID = enclosureNodeID;
	}
	
	@Override
	public String toString() {
		return "Telemetry [CLIENTID=" + CLIENTID + ", TEMP=" + TEMP + ", RH=" + RH + ", OPTIONAL_LOAD=" + OPTIONAL_LOAD
				+ ", HEAT_LOAD=" + HEAT_LOAD + ", UV_STATUS=" + UV_STATUS + ", HUMI_STATUS=" + HUMI_STATUS
				+ ", dateTime=" + dateTime + ", centralNodeID=" + centralNodeID + ", userID=" + userID + "]";
	}
	
}
