package com.dynocloud.node.telemetry;

import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.fusesource.mqtt.client.BlockingConnection;
import org.fusesource.mqtt.client.MQTT;
import org.fusesource.mqtt.client.Message;
import org.fusesource.mqtt.client.QoS;
import org.fusesource.mqtt.client.Topic;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Daemon {
	
	static Database_connection link = new Database_connection();
	static PreparedStatement prep_sql;
	
	public static void main (String[] args) {
		
		System.out.println("Telemetry Daemon");
		
		 link.Open_link();
			
		 boolean DynoCloud = false;
			
			try{
				String query_getOnline = "SELECT `DynoCloud` FROM Config;";
				prep_sql = link.linea.prepareStatement(query_getOnline);

				ResultSet rs_query_getOnline = prep_sql.executeQuery();
				
					while(rs_query_getOnline.next()){									
						DynoCloud = rs_query_getOnline.getBoolean("DynoCloud");
					}
					
			}catch(Exception e){
				System.out.println("Error: " + e.getMessage());
				//link.Close_link();
			}

		//link.Close_link();
		
		//-------------------------------------------
		
		String host=null;
		
		try {
			host = args[0];
		System.out.println("MQTT host: " + host);
		} catch (Exception e) {
			System.out.println("MQTT Broker host not set as arguments");
			System.exit(1);
		}
			
		MQTT node = new MQTT();
		
		try {		
			node.setHost(host, 1883);
					
		} catch (URISyntaxException e) {
			System.out.println("Error finding Broker");
			main(args);
		}
		
		node.setKeepAlive((short) 25);
		//node.setWillTopic("will");
		//node.setWillMessage("Node disconnected");		
		
		BlockingConnection connection = node.blockingConnection();
		System.out.println("Connecting to Broker");
		try {
			connection.connect();	
		} catch (Exception e) {
			System.out.println("Error connecting to Broker");
			main(args);
		}
		System.out.println("Broker online");
		
		String telemetryTopic = "/DynoCloud/Telemetry";
		String statusTopic = "/DynoCloud/Status";
		
		Topic[] topics = {new Topic(telemetryTopic, QoS.AT_LEAST_ONCE),
						new Topic(statusTopic, QoS.AT_LEAST_ONCE)};

		try {
			
			@SuppressWarnings("unused")
			byte[] qoses = connection.subscribe(topics);
			
		} catch (Exception e) {
			System.out.println("Error subscribing to topic");
			main(args);
		}
		
//-------------------------------------------------------------------
		//MQTT localServer = new MQTT();
		BlockingConnection queue_connection = new BlockingConnection(null);
		
		String token=null;
		int userID = 0;
		int centralNodeID = 0;
		
		if(DynoCloud){
			
			System.out.println("DynoCloud is enabled");
			System.out.println("Connecting to Queue");
			try {
				
				node.setHost(host, 1883);
				
				queue_connection = node.blockingConnection();
				
				try {
					
					queue_connection.connect();
					
				} catch (Exception e) {
					System.out.println("Error connecting to queue");
				}
							
			} catch (URISyntaxException e) {
				System.out.println("Error connecting to queue");
			}
			System.out.println("Queue online");
			
			
			// link.Open_link();
				
				
				try{
					String query_getConfig = "SELECT * FROM Config;";
					prep_sql = link.linea.prepareStatement(query_getConfig);

					ResultSet rs_query_getConfig = prep_sql.executeQuery();
					
						while(rs_query_getConfig.next()){	
							
							token = rs_query_getConfig.getString("Token");
							userID = rs_query_getConfig.getInt("UserID");
							centralNodeID = rs_query_getConfig.getInt("CentralNodeID");
						}
						
				}catch(Exception e){
					System.out.println("Error: " + e.getMessage());
					//link.Close_link();
				}

			//link.Close_link();
				
			
		} else {
			System.out.println("DynoCloud is disabled");
		}
//-------------------------------------------------------------------
		
		// link.Open_link();
			
			
			try{
				String query_getEnclosures = "SELECT * FROM EnclosureNode;";
				prep_sql = link.linea.prepareStatement(query_getEnclosures);

				ResultSet rs_query_getEnclosures = prep_sql.executeQuery();
				
					while(rs_query_getEnclosures.next()){	
						
						int EnclosureNodeID = rs_query_getEnclosures.getInt("EnclosureNodeID");
						InitialVariables initialVariables = new InitialVariables(EnclosureNodeID);
						initialVariables.sendToNode(host);

					}
					
			}catch(Exception e){
				System.out.println("Error: " + e.getMessage());
				//link.Close_link();
			}

			link.Close_link();
		//-------------------------------------------------------------------
		
			System.out.println("Ready");
			
		while(true){
			
			Message message=null;
			
			try {
				message = connection.receive();
			} catch (Exception e) {
				System.out.println("Error receiving message");
				main(args);
			}
			
			String topic = message.getTopic();		
			byte[] payload = message.getPayload();			
			String payloadString = new String(payload, StandardCharsets.UTF_8);
			
			System.out.println(topic + " " + payloadString);			
//-------------------------------------------------------------------
			if(topic.equals(telemetryTopic)){
				ObjectMapper mapper = new ObjectMapper();
				Telemetry telemetry = null;
					
				try {
					telemetry = mapper.readValue(payloadString, Telemetry.class);
				} catch (Exception e1) {
					System.out.println("Error mapping to json: " + e1.getMessage());
					main(args);
				}
				
				
				System.out.println(telemetry);
	//------------------------------------------------------------------- 			
		        link.Open_link();
				
				try{
					String query_telemetry = "INSERT INTO Telemetry (`DateTime`,`EnclosureNodeID`,`TEMP`,`RH`,`OPTIONAL_LOAD`,`HEAT_LOAD`,`UV_STATUS`,`HUM_STATUS`,`HEAT_STATUS`,`OPTIONAL_STATUS`,`HUM_OR`,`HEAT_OR`,`UV_OR`,`OPTIONAL_OR`)"
					+ "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?);";
	
					prep_sql = link.linea.prepareStatement(query_telemetry);
					
					prep_sql.setTimestamp(1, parseDate(telemetry.getDateTime()));
					prep_sql.setInt(2, telemetry.getCLIENTID());
					prep_sql.setFloat(3, telemetry.getTEMP());
					prep_sql.setFloat(4, telemetry.getRH());
					prep_sql.setFloat(5, telemetry.getOPTIONAL_LOAD());
					prep_sql.setFloat(6, telemetry.getHEAT_LOAD());
					prep_sql.setInt(7, telemetry.getUV_STATUS());
					prep_sql.setInt(8, telemetry.getHUM_STATUS());
					prep_sql.setInt(9, telemetry.getHEAT_STATUS());
					prep_sql.setInt(10, telemetry.getOPTIONAL_STATUS());
					prep_sql.setInt(11, telemetry.getHUM_OR());
					prep_sql.setInt(12, telemetry.getHEAT_OR());
					prep_sql.setInt(13, telemetry.getUV_OR());
					prep_sql.setInt(14, telemetry.getOPTIONAL_OR());
												
					prep_sql.executeUpdate();
					
					System.out.println("INSERTED\n");
					
				}catch(Exception e){
					System.out.println("Error: " + e.getMessage());
					//link.Close_link();							
				}
	
				//link.Close_link();
				
				
				
				message.ack();
	//-------------------------------------------------------------------			
				
		if(DynoCloud){
				
				telemetry.setUserID(userID);
				telemetry.setCentralNodeID(centralNodeID);
				
				String telemetryJsonString = null;
				
				try {
					telemetryJsonString = mapper.writeValueAsString(telemetry);
					
				} catch (JsonProcessingException e) {
					
					System.out.println("Error mapping to json: " + e.getMessage());
					main(args);
				}
				
	//-------------------------------------------------------------------
				
				System.out.println(telemetryJsonString);
				
				Header auth = new Header();
				auth.setKey("Authorization");
				auth.setValue("Bearer "+ token);
				
				Header mediaType = new Header();
				mediaType.setKey("Content-Type");
				mediaType.setValue("application/json");
				
				ArrayList<Header> headerList = new ArrayList<Header>();
				headerList.add(auth);
				headerList.add(mediaType);
				
				MessageRequest messageRequest = new MessageRequest();
				messageRequest.setHeaderList(headerList);
				messageRequest.setMethod("POST");
				messageRequest.setPath("publish");
				messageRequest.setPayload(telemetryJsonString);
				
				String messageJsonString = null;
				
				try {
					messageJsonString = mapper.writeValueAsString(messageRequest);
					
				} catch (JsonProcessingException e) {	
					System.out.println("Error mapping to json: " + e.getMessage());
					main(args);
				}
				
				System.out.println();
				System.out.println(messageJsonString);
//-------------------------------------------------------------------				
				System.out.println("Queueing message");
				
//				MQTT localServer = new MQTT();
//				
//				try {
//					
//					localServer.setHost(host, 1883);
//					
//					BlockingConnection server_connection = localServer.blockingConnection();
					
					try {
						
						//server_connection.connect();
						
						queue_connection.publish("/DynoCloud/Queue", messageJsonString.getBytes(), QoS.AT_LEAST_ONCE, false);
						System.out.println("Message relayed to queue");
						
					} catch (Exception e) {
						System.out.println("Error queueing message");
					}
								
//				} catch (URISyntaxException e) {
//					System.out.println("Error connecting to Server");
//				}
//				
			}
//-------------------------------------------------------------------				
			}else if(topic.equals(statusTopic)){
				System.out.println(payloadString);
				
				ObjectMapper mapper = new ObjectMapper();
				EnclosureNodeStatus ens = null;
					
				try {
					ens = mapper.readValue(payloadString, EnclosureNodeStatus.class);
				} catch (Exception e1) {
					System.out.println("Error mapping to json: " + e1.getMessage());
					//main(args);
				}
				
				if(ens.getStatus() == 1){
				
					InitialVariables initialVariables = new InitialVariables(ens.getCLIENTID());
					try {
						Thread.sleep(3500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					initialVariables.sendToNode(host);
				
				} else if(ens.getStatus() == 0){
					
					System.out.println("Enclosure Node "+ens.getCLIENTID()+" has gone offline");
					
				}
				
				message.ack();
			}			
//-------------------------------------------------------------------		
			System.out.println("---------------------------------------");				
		}
		
	}
	
	private static java.sql.Timestamp parseDate(String s) {
		
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = null;

		try {
			date = formatter.parse(s);	
		} catch (ParseException e) {
			e.printStackTrace();
		}
	
	return new java.sql.Timestamp(date.getTime());
	
	}
	
	static class EnclosureNodeStatus{
		
		@JsonProperty("ClientID")
		int CLIENTID;
		@JsonProperty("Status")
		int Status;
		
		@JsonProperty("ClientID")
		public int getCLIENTID() {
			return CLIENTID;
		}
		@JsonProperty("ClientID")
		public void setCLIENTID(int cLIENTID) {
			CLIENTID = cLIENTID;
		}
		@JsonProperty("Status")
		public int getStatus() {
			return Status;
		}
		@JsonProperty("Status")
		public void setStatus(int sTATUS) {
			Status = sTATUS;
		}
			
	}

}
