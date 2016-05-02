package com.dynocloud.node.alert;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Daemon {
	
	private static Database_connection link = new Database_connection();
	private static PreparedStatement prep_sql;	
	
	public static void main (String[] args) {

		
		System.out.println("Alert Daemon");
		
		 link.Open_link();
			
		 //boolean DynoCloud = false;
		 int minutes=0;
			
			try{
				String query_getConfig = "SELECT * FROM Config;";
				prep_sql = link.linea.prepareStatement(query_getConfig);

				ResultSet rs_query_getConfige = prep_sql.executeQuery();
				
					while(rs_query_getConfige.next()){									
						minutes = rs_query_getConfige.getInt("Minutes");
					}
					
			}catch(Exception e){
				System.out.println("Error: " + e.getMessage());
				link.Close_link();
			}

		link.Close_link();
		
	while(true){	
		
		
		link.Open_link();
					
			try{
				String query_getEnclosures = "SELECT * FROM EnclosureNode;";
				prep_sql = link.linea.prepareStatement(query_getEnclosures);			
			
				ResultSet rs_query_getEnclosures= prep_sql.executeQuery();
				
					while(rs_query_getEnclosures.next()){
		
						System.out.println("EnclosureNodeID: " + rs_query_getEnclosures.getInt("EnclosureNodeID"));		
						int EnclosureNodeID = rs_query_getEnclosures.getInt("EnclosureNodeID");
						
						try{
							String query_getProfile= "SELECT * FROM EnclosureNode, PetProfiles "
									+ "WHERE EnclosureNode.PetProfileID=PetProfiles.PetProfileID AND EnclosureNode.EnclosureNodeID=?;";
							
							prep_sql = link.linea.prepareStatement(query_getProfile);
														
							prep_sql.setInt(1, EnclosureNodeID);
				
							ResultSet rs_query_getProfile= prep_sql.executeQuery();
							
							if (!rs_query_getProfile.next() ) {
								System.out.println("rs_query_getProfile no data");
								link.Close_link();	
							} else {
								
								String PetProfileID = rs_query_getProfile.getString("PetProfileID");
//								float temp = rs_query_getProfile.getFloat("Temperature_TH");
//								float hum = rs_query_getProfile.getFloat("Humidity_TH");
								
								float dayTemp = rs_query_getProfile.getFloat("Day_Temperature_SP");
								float dayHum = rs_query_getProfile.getFloat("Day_Humidity_SP");
								
								float nightTemp = rs_query_getProfile.getFloat("Night_Temperature_SP");
								float nightHum = rs_query_getProfile.getFloat("Night_Humidity_SP");
			
								
								int tempTh = rs_query_getProfile.getInt("Temperature_TH");
								int humTh = rs_query_getProfile.getInt("Humidity_TH");
								
								
								
								SimpleDateFormat parser = new SimpleDateFormat("HH:mm");
								Date day = null;
								Date night = null;
								
								try {
									day = parser.parse(rs_query_getProfile.getString("DayTime"));
									night = parser.parse(rs_query_getProfile.getString("NightTime"));
								} catch (ParseException e1) {
							
									e1.printStackTrace();
								}
							
								//System.out.println("Day: "+parser.format(day));
								//System.out.println("Night: "+parser.format(night));

								//DateFormat dateFormat = new SimpleDateFormat("HH:mm");
								Date nowTime = new Date();
								
								String nowDate = parser.format(nowTime);
								//System.out.println("Now: "+parser.format(now)); //2014/08/06 15:59:48
								
								Date nowEpoch = null;
								try {
									nowEpoch = parser.parse(nowDate);
								} catch (ParseException e1) {
									e1.printStackTrace();
								}
								
								boolean dayLight = false;
							    if (nowEpoch.after(day) && nowEpoch.before(night)) {
							    	System.out.println("\tDay");
							    			    	
							    	dayLight=true;
							    	
							    } else {
							    	System.out.println("\tNight");
							    	

							    	
							    }
								
								
								
								
								
								System.out.println("\tPetProfileID: " + PetProfileID + " dayTemp: " + dayTemp + " dayHum: " + dayHum + " nightTemp " + nightTemp + " nightHum " + nightHum);
								
								try{
									  LocalDateTime now = LocalDateTime.now();;		
								      LocalDateTime past = now.minus(minutes, ChronoUnit.MINUTES);
								      LocalDateTime nowInc = now.plus(minutes, ChronoUnit.MINUTES);
									
									String query_metrics = "SELECT * FROM Telemetry where `EnclosureNodeID` = ? AND `DateTime`  >=  ?  AND `DateTime` < ?;";
									prep_sql = link.linea.prepareStatement(query_metrics);
									
									prep_sql.setInt(1, EnclosureNodeID);
									prep_sql.setString(2, past+"");
									prep_sql.setString(3, nowInc+"");
									
									ResultSet rs_query_metrics= prep_sql.executeQuery();
									
									float tempAvg=0;
									float humAvg=0;
									int count=0;
									
									while(rs_query_metrics.next()){
										tempAvg += rs_query_metrics.getFloat("TEMP");
										humAvg += rs_query_metrics.getFloat("RH");
										count++;										
									} 
									
									
									System.out.println("\tRecords: " + count);
									
									

									if(count>=(20*minutes)){
									//	if(count>0){
										
										
										System.out.println("\tminutes: " + minutes);
										
										float humPercentage = (float) (humTh/100.0);
										float tempPercentage = (float) (tempTh/100.0);
										
										System.out.println("\ttempTh: " + tempTh + "%");
										System.out.println("\thumTh: " + humTh + "%");
										
										System.out.println("\thumPercentage: " + humPercentage + "");
										System.out.println("\ttempPercentage: " + tempPercentage + "");
										
										System.out.println();
										
										tempAvg /= count;
										humAvg /= count;
	//									System.out.println("tempAvg: " + tempAvg);
	//									System.out.println("humAvg: " + humAvg);
										if(dayLight){
												if(tempAvg >= dayTemp*(1+tempPercentage) || tempAvg < dayTemp*(1-tempPercentage)){
													
													String name = rs_query_getEnclosures.getString("Name");
													String message = "Alert! Temperate out of range: " + tempAvg + " for enclosure node " +name+" should be "+dayTemp;
													
													Alert alert = new Alert();
													alert.setEnclosureNodeID(rs_query_getEnclosures.getInt("EnclosureNodeID"));
													alert.setMessage(message);
																							
													
													System.out.println("\t"+message);
													postAlert(alert);
												}else{
													System.out.println("\tTemperate ok: " + tempAvg);
												}
												
												if(humAvg >= dayHum*(1+humPercentage) || humAvg < dayHum*(1-humPercentage)){
													
													
													String name = rs_query_getEnclosures.getString("Name");
													String message = "Alert! Humidity out of range: " + humAvg + " for enclosure node " +name +" should be "+dayHum;
													
													Alert alert = new Alert();
													alert.setEnclosureNodeID(rs_query_getEnclosures.getInt("EnclosureNodeID"));
													alert.setMessage(message);
								
													
													System.out.println("\t"+message);
													postAlert(alert);
													
												}else{
													System.out.println("\tHumidity ok: " + humAvg);
												}
										
										}else{
											
											
											if(tempAvg >= nightTemp*(1+tempPercentage) || tempAvg < nightTemp*(1-tempPercentage)){
												String name = rs_query_getEnclosures.getString("Name");
												String message = "Alert! Temperate out of range: " + tempAvg + " for enclosure node " +name+" should be "+nightTemp;
												
												Alert alert = new Alert();
												alert.setEnclosureNodeID(rs_query_getEnclosures.getInt("EnclosureNodeID"));
												alert.setMessage(message);
												
												
												
												System.out.println("\t"+message);
												postAlert(alert);
											}else{
												System.out.println("\tTemperate ok: " + tempAvg);
											}
											
											if(humAvg >= nightHum*(1+humPercentage) || humAvg < nightHum*(1-humPercentage)){
												String name = rs_query_getEnclosures.getString("Name");
												String message = "Alert! Humidity out of range: " + humAvg + " for enclosure node " +name +" should be "+nightHum;
												
												Alert alert = new Alert();
												alert.setEnclosureNodeID(rs_query_getEnclosures.getInt("EnclosureNodeID"));
												alert.setMessage(message);
												
												
												
												System.out.println("\t"+message);
												postAlert(alert);
											}else{
												System.out.println("\tHumidity ok: " + humAvg);
											}
											
										}
										
									} else {
										System.out.println("\tNo recent data for "+minutes+" minute scan");
									}
									
								}catch(Exception e){

									System.out.println("Error: " + e.getMessage());
									
									link.Close_link();
				
								}

							}
							
						}catch(Exception e){

							System.out.println("Error: " + e.getMessage());
							
							link.Close_link();
		
						}

					}
			}catch(Exception e){

				System.out.println("Error: " + e.getMessage());
				
				link.Close_link();
	
			}

		link.Close_link();
		try {
			Thread.sleep(60000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
	  }

	
	
	public static void postAlert(Alert alert){
		
		
		//------------------------------------------------------------------- 		
		
		String url = "http://localhost/node_api/alerts";
		
		//System.out.println("Path: " + url);
		
		URL obj = null;
		HttpURLConnection con = null;
		
			
			try {
				obj = new URL(url);
			} catch (MalformedURLException e) {
				e.printStackTrace();
				//main(args);
			}
			
			try {
				con = (HttpURLConnection) obj.openConnection();
			} catch (Exception e) {
				e.printStackTrace();
				//main(args);
			}
			
			try {
				con.setRequestMethod("POST");
			} catch (ProtocolException e) {
				e.printStackTrace();
				//main(args);
			}
			
			con.setRequestProperty("Content-Type", "application/json");
			//System.out.println("Method: " + mFromJSON.getMethod());
			
//			for( Header header : mFromJSON.getHeaderList()){
//				
//				con.setRequestProperty(header.getKey(),header.getValue());
//				System.out.println("Header: " + header.getKey() + ": " + header.getValue());
//				
//			}
			
			
			ObjectMapper mapper = new ObjectMapper();
			String jsonString = null;
			
			try {
				jsonString = mapper.writeValueAsString(alert);
				//System.out.println(jsonString);
				
			} catch (JsonProcessingException e) {
				
				System.out.println("Error mapping to json: " + e.getMessage());
				//return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("JSON mapping error").build();
			}
			

			//String urlParameters = mFromJSON.getPayload();
				
				con.setDoOutput(true);
				DataOutputStream wr;
				try {
					wr = new DataOutputStream(con.getOutputStream());
					wr.writeBytes(jsonString);
					wr.flush();
					wr.close();
				} catch (Exception e) {

					//null payload

				}
				

			@SuppressWarnings("unused")
			int responseCode = 0;
			try {
				responseCode = con.getResponseCode();
			} catch (Exception e) {
				e.printStackTrace();
				//main(args);
			}

			//System.out.println("Response Code : " + responseCode);

			BufferedReader in;
			StringBuffer response = null;
			
			try {
				in = new BufferedReader(new InputStreamReader(con.getInputStream()));
				String inputLine;
				response = new StringBuffer();
	
				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				in.close();
			} catch (Exception e) {
				e.printStackTrace();
				//main(args);
			}

			
			System.out.println(response.toString());
		
		
	}
	
	
	
}
