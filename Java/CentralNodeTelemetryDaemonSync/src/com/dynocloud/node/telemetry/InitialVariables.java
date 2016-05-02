package com.dynocloud.node.telemetry;

import java.net.URISyntaxException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.fusesource.mqtt.client.BlockingConnection;
import org.fusesource.mqtt.client.MQTT;
import org.fusesource.mqtt.client.QoS;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


public class InitialVariables {
	
	int enclosureNodeID;
	private static Database_connection link = new Database_connection();
	private static PreparedStatement prep_sql;
	
	public InitialVariables(int enclosureNodeID){
				
		this.enclosureNodeID = enclosureNodeID;
	}
	
	public void sendToNode(String host){
						
		int optional=0;
		String petProfileID=null;
		
		link.Open_link();
		
			try{
				String query_getModules = "SELECT * FROM EnclosureNode where`EnclosureNodeID` = ?;";
				prep_sql = link.linea.prepareStatement(query_getModules);
				
				prep_sql.setInt(1, enclosureNodeID);
				
				ResultSet rs_query_getModules = prep_sql.executeQuery();
				
				if (!rs_query_getModules.next() ) {
					System.out.println("rs_query_getModules no data");
					link.Close_link();
	
				} else {

					optional = rs_query_getModules.getInt("OPTIONAL_LOAD");
					petProfileID = rs_query_getModules.getString("PetProfileID");

				}
			}catch(Exception e){

				System.out.println("Error: " + e.getMessage());
				
				link.Close_link();
		
			}

		 PetProfile profile = new PetProfile();
			
			try{
				String query_getProfiles = "SELECT * FROM PetProfiles where `PetProfileID` = ?";
				prep_sql = link.linea.prepareStatement(query_getProfiles);
				
				prep_sql.setString(1, petProfileID);
				
				ResultSet rs_query_getProfiles= prep_sql.executeQuery();
				
				if (!rs_query_getProfiles.next() ) {
					System.out.println("rs_query_getProfiles no data");
					link.Close_link();;
					
				} else {
						profile.setPetProfileID(rs_query_getProfiles.getString("PetProfileID"));
						
						profile.setDay_Temperature_SP(rs_query_getProfiles.getFloat("Day_Temperature_SP"));
						profile.setDay_Humidity_SP(rs_query_getProfiles.getFloat("Day_Humidity_SP"));
						
						profile.setNight_Temperature_SP(rs_query_getProfiles.getFloat("Night_Temperature_SP"));
						profile.setNight_Humidity_SP(rs_query_getProfiles.getFloat("Night_Humidity_SP"));
						
						profile.setTemperature_TH(rs_query_getProfiles.getFloat("Temperature_TH"));
						profile.setHumidity_TH(rs_query_getProfiles.getFloat("Humidity_TH"));
						
						profile.setDayTime(rs_query_getProfiles.getString("DayTime"));
						profile.setNightTime(rs_query_getProfiles.getString("NightTime"));
					}
			}catch(Exception e){
				System.out.println("Error: " + e.getMessage());				
				link.Close_link();	
			}

		link.Close_link();
		
		
		EnclosureInitialVariables eiv = new EnclosureInitialVariables();
		eiv.setOPTIONAL(optional);
		//-------------------------------------------------------------------
			
		
			SimpleDateFormat parser = new SimpleDateFormat("HH:mm");
			Date day = null;
			Date night = null;
			
			try {
				day = parser.parse(profile.getDayTime());
				night = parser.parse(profile.getNightTime());
			} catch (ParseException e1) {
		
				e1.printStackTrace();
			}
		
			//System.out.println("Day: "+parser.format(day));
			//System.out.println("Night: "+parser.format(night));

			//DateFormat dateFormat = new SimpleDateFormat("HH:mm");
			Date now = new Date();
			
			String nowDate = parser.format(now);
			//System.out.println("Now: "+parser.format(now)); //2014/08/06 15:59:48
			
			Date nowEpoch = null;
			try {
				nowEpoch = parser.parse(nowDate);
			} catch (ParseException e1) {
				e1.printStackTrace();
			}
			

		    if (nowEpoch.after(day) && nowEpoch.before(night)) {
		    	//System.out.println("Day");
		    			    	
		    	eiv.setRH(profile.getDay_Humidity_SP());
		    	eiv.setTEMP(profile.getDay_Temperature_SP());	
		    	eiv.setDayTime(1);
		    	
		    } else {
		    	//System.out.println("Night");
		    	
		    	eiv.setRH(profile.getNight_Humidity_SP());
		    	eiv.setTEMP(profile.getNight_Temperature_SP());
		    	eiv.setDayTime(0);
		    	
		    }
	
		 //-------------------------------------------------------------------	

		ObjectMapper mapper = new ObjectMapper();
		String jsonString = null;
		
		try {
			jsonString = mapper.writeValueAsString(eiv);
			
		} catch (JsonProcessingException e) {
			
			System.out.println("Error mapping to json: " + e.getMessage());
		}
		
		//-------------------------------------------------------------------	

		MQTT node = new MQTT();
		
		try {
			
			node.setHost(host, 1883);
			
			BlockingConnection node_connection = node.blockingConnection();
			
			try {
				
				node_connection.connect();
				
				String topic = "/DynoCloud/MCU/"+enclosureNodeID;
				
				System.out.println("[MQTT] ["+topic+"] " + jsonString);
				
				node_connection.publish(topic, jsonString.getBytes(), QoS.AT_LEAST_ONCE, false);

				node_connection.disconnect();
				
			} catch (Exception e) {
				System.out.println("[MQTT] Error relaying message");
			}
						
		} catch (URISyntaxException e) {
			System.out.println("[MQTT] Error connecting to node");
		}

	}
	
	
	
	class EnclosureInitialVariables{
		
		@JsonProperty("temp_SP")
		float TEMP;
		@JsonProperty("RH_SP")
		float RH;
		@JsonProperty("OPTIONAL")
		int OPTIONAL;
		//@JsonProperty("dayLight")
		int dayTime;
		
		@JsonProperty("temp_SP")
		public float getTEMP() {
			return TEMP;
		}
		@JsonProperty("temp_SP")
		public void setTEMP(float tEMP) {
			TEMP = tEMP;
		}
		
		@JsonProperty("RH_SP")
		public float getRH() {
			return RH;
		}
		@JsonProperty("RH_SP")
		public void setRH(float rH) {
			RH = rH;
		}
		
		@JsonProperty("OPTIONAL")
		public int getOPTIONAL() {
			return OPTIONAL;
		}
		@JsonProperty("OPTIONAL")
		public void setOPTIONAL(int oPTIONAL) {
			OPTIONAL = oPTIONAL;
		}
		
		public int getDayTime() {
			return dayTime;
		}
		public void setDayTime(int dayTime) {
			this.dayTime = dayTime;
		}

	}
	
}
