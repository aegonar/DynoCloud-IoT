package com.dynocloud.node.api;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PetProfileSchedule {
	
	private static Database_connection link = new Database_connection();
	private static PreparedStatement prep_sql;
	private static String fileName = "/tmp/dynoCron";
	private static String userName = "pi";
	
	public void rebuildShedule(){
		
		System.out.println("[CRON] Rebuilding Shedule");
		
		link.Open_link();
		
		  ArrayList<Crontab> enclosures = new ArrayList<Crontab>();
			
			try{
				String query_getEnclosures = "SELECT * FROM EnclosureNode;";
				prep_sql = link.linea.prepareStatement(query_getEnclosures);
				
				ResultSet rs_query_getEnclosures= prep_sql.executeQuery();
				
					while(rs_query_getEnclosures.next()){
						
						Crontab module = new Crontab();
						
						module.setEnclosureNodeID(rs_query_getEnclosures.getInt("EnclosureNodeID"));
						
					
					String query_getProfiles = "SELECT * FROM PetProfiles where PetProfileID=?;";
					prep_sql = link.linea.prepareStatement(query_getProfiles);
					
					prep_sql.setString(1, rs_query_getEnclosures.getString("PetProfileID"));
					
					ResultSet rs_query_getProfiles= prep_sql.executeQuery();
					
						while(rs_query_getProfiles.next()){
							
							ProtoProfile dayProfile = new ProtoProfile();
							ProtoProfile nightProfile = new ProtoProfile();
							
							SimpleDateFormat parser = new SimpleDateFormat("HH:mm");
							
							//---------------------------
							
							dayProfile.setTemp_SP(rs_query_getProfiles.getFloat("Day_Temperature_SP"));
							dayProfile.setRH_SP(rs_query_getProfiles.getFloat("Day_Humidity_SP"));
							
							String dayTimeString = rs_query_getProfiles.getString("DayTime");
														
							Date dayTime=null;
							
							try {
								dayTime = parser.parse(dayTimeString);
							} catch (ParseException e1) {
								e1.printStackTrace();
							}
							
							Calendar dayCalendar = GregorianCalendar.getInstance(); // creates a new calendar instance
							dayCalendar.setTime(dayTime); 
							
							TimeSchedule dayTimeSchedule = new TimeSchedule();

							
							dayTimeSchedule.setHour(dayCalendar.get(Calendar.HOUR_OF_DAY));
							dayTimeSchedule.setMinute(dayCalendar.get(Calendar.MINUTE));
							dayProfile.setDayTime(1);
							
							//---------------------------
							
							nightProfile.setTemp_SP(rs_query_getProfiles.getFloat("Night_Temperature_SP"));
							nightProfile.setRH_SP(rs_query_getProfiles.getFloat("Night_Humidity_SP"));
							
							String nightTimeString = rs_query_getProfiles.getString("NightTime");
							
							Date nightTime=null;
							
							try {
								nightTime = parser.parse(nightTimeString);
							} catch (ParseException e1) {
								e1.printStackTrace();
							}
							
							Calendar nightCalendar = GregorianCalendar.getInstance(); // creates a new calendar instance
							nightCalendar.setTime(nightTime); 
							
							TimeSchedule nightTimeSchedule = new TimeSchedule();
							
							nightTimeSchedule.setHour(nightCalendar.get(Calendar.HOUR_OF_DAY));
							nightTimeSchedule.setMinute(nightCalendar.get(Calendar.MINUTE));
							nightProfile.setDayTime(0);
							
							//---------------------------
							
							module.setDayProfile(dayProfile);
							module.setNightProfile(nightProfile);
							
							module.setDayTimeSchedule(dayTimeSchedule);
							module.setNightTimeSchedule(nightTimeSchedule);
						}
					
						//System.out.println("module: " + module);
						enclosures.add(module);
					}
				
					
					
			}catch(Exception e){

				System.out.println("Error: " + e.getMessage());
				
				link.Close_link();
				
			}
			
			link.Close_link();
		
		
		
			ArrayList<String> cronStrings = new ArrayList<String>();
		
			for(Crontab cron : enclosures){
				
				
				ProtoProfile dayProfile = cron.getDayProfile();
				ProtoProfile nightProfile = cron.getNightProfile();
				
				ObjectMapper mapper = new ObjectMapper();
				
				String dayProfileJsonString = null;
				String nightProfileJsonString = null;
				
				//ProtoProfile dayTimeSchedule = cron.getDayTimeSchedule();
				
				try {
					dayProfileJsonString = mapper.writeValueAsString(dayProfile);
					nightProfileJsonString = mapper.writeValueAsString(nightProfile);
					
					dayProfileJsonString = mapper.writeValueAsString(dayProfileJsonString);
					nightProfileJsonString = mapper.writeValueAsString(nightProfileJsonString);
					
					//System.out.println("dayProfile json: " + dayProfileJsonString);
					//System.out.println("nightProfile json: " + nightProfileJsonString);
					
				} catch (JsonProcessingException e) {
					
					System.out.println("Error mapping to json: " + e.getMessage());
					//return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("JSON mapping error").build();
				}
								
				TimeSchedule dayTimeSchedule = cron.getDayTimeSchedule();
				TimeSchedule nightTimeSchedule = cron.getNightTimeSchedule();
				
				
				//String cmd = "mosquitto_pub -h localhost -t /DynoCloud/MCU/" + cron.getEnclosureNodeID();
				String cmd = "mosquitto_pub -h localhost -t /DynoCloud/" + cron.getEnclosureNodeID();
				
				String day = dayTimeSchedule.getMinute() + " " + dayTimeSchedule.getHour() + " * * * " + cmd + " -m " + dayProfileJsonString;
				String night= nightTimeSchedule.getMinute() + " " + nightTimeSchedule.getHour() + " * * * " + cmd + " -m " + nightProfileJsonString;
				
				cronStrings.add(day);
				cronStrings.add(night);
				
			}
			
					
		try {
			
			Writer writer = new BufferedWriter(
			new OutputStreamWriter(
	        new FileOutputStream(fileName), "utf-8"));
			
			for(String cmd : cronStrings){
	        
				writer.write(cmd);
				writer.write("\n");
				
			}
			
			writer.close();
			
		} catch (Exception e){
			System.out.println("Error: " + e.getMessage());
		}
		

		String cmd = "cat "+fileName+" | crontab -u "+userName+" -";
        ProcessBuilder pb = new ProcessBuilder("/bin/bash", "-c", cmd);
        pb.redirectErrorStream(true);
        try {
            Process p = pb.start();
            String s;
            BufferedReader stdout = new BufferedReader (
                new InputStreamReader(p.getInputStream()));
            while ((s = stdout.readLine()) != null) {
                System.out.println(s);
  
                
            }
            p.getInputStream().close();
            p.getOutputStream().close();
            p.getErrorStream().close();
         } catch (Exception ex) {
            ex.printStackTrace();
        }		
		
	}
	
	class Crontab {
		int enclosureNodeID;
		ProtoProfile dayProfile;
		ProtoProfile nightProfile;
		TimeSchedule dayTimeSchedule;
		TimeSchedule nightTimeSchedule;
		
		public TimeSchedule getDayTimeSchedule() {
			return dayTimeSchedule;
		}
		public void setDayTimeSchedule(TimeSchedule dayTimeSchedule) {
			this.dayTimeSchedule = dayTimeSchedule;
		}
		public TimeSchedule getNightTimeSchedule() {
			return nightTimeSchedule;
		}
		public void setNightTimeSchedule(TimeSchedule nightTimeSchedule) {
			this.nightTimeSchedule = nightTimeSchedule;
		}
		public int getEnclosureNodeID() {
			return enclosureNodeID;
		}
		public void setEnclosureNodeID(int enclosureNodeID) {
			this.enclosureNodeID = enclosureNodeID;
		}
		public ProtoProfile getDayProfile() {
			return dayProfile;
		}
		public void setDayProfile(ProtoProfile dayProfile) {
			this.dayProfile = dayProfile;
		}
		public ProtoProfile getNightProfile() {
			return nightProfile;
		}
		public void setNightProfile(ProtoProfile nightProfile) {
			this.nightProfile = nightProfile;
		}
		@Override
		public String toString() {
			return "Crontab [enclosureNodeID=" + enclosureNodeID + ", dayProfile=" + dayProfile + ", nightProfile="
					+ nightProfile + ", dayTimeSchedule=" + dayTimeSchedule + ", nightTimeSchedule=" + nightTimeSchedule
					+ "]";
		}
	}
	
	class TimeSchedule{
		int hour;
		int minute;
		
		public int getHour() {
			return hour;
		}
		public void setHour(int hour) {
			this.hour = hour;
		}
		public int getMinute() {
			return minute;
		}
		public void setMinute(int minute) {
			this.minute = minute;
		}
		@Override
		public String toString() {
			return "TimeSchedule [hour=" + hour + ", minute=" + minute + "]";
		}
	}
	
	class ProtoProfile{

		int dayTime;
			
		@JsonProperty("temp_SP")
		float temp_SP;
		@JsonProperty("RH_SP")
		float RH_SP;
		
		public int getDayTime() {
			return dayTime;
		}
		public void setDayTime(int dayTime) {
			this.dayTime = dayTime;
		}
		@JsonProperty("temp_SP")
		public float getTemp_SP() {
			return temp_SP;
		}
		@JsonProperty("temp_SP")
		public void setTemp_SP(float temp_SP) {
			this.temp_SP = temp_SP;
		}
		@JsonProperty("RH_SP")
		public float getRH_SP() {
			return RH_SP;
		}
		@JsonProperty("RH_SP")
		public void setRH_SP(float rH_SP) {
			RH_SP = rH_SP;
		}
		@Override
		public String toString() {
			return "ProtoProfile [dayTime=" + dayTime + ", temp_SP=" + temp_SP + ", RH_SP=" + RH_SP + "]";
		}
	}
}
