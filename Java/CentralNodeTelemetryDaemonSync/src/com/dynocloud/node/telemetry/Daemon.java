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

//import javax.ws.rs.core.Response;

import org.fusesource.mqtt.client.BlockingConnection;
//import org.fusesource.mqtt.client.Future;
//import org.fusesource.mqtt.client.FutureConnection;
import org.fusesource.mqtt.client.MQTT;
import org.fusesource.mqtt.client.Message;
import org.fusesource.mqtt.client.QoS;
import org.fusesource.mqtt.client.Topic;

//import com.dynocloud.node.api.Module;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Daemon {
	
	static Database_connection link = new Database_connection();
	static PreparedStatement prep_sql;
	
	public static void main (String[] args) {
		
		System.out.println("Telemetry Daemon Synchronous");
		
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
				link.Close_link();
			}

		link.Close_link();
		
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
		//String toMcuTopic = "/DynoCloud/MCU/1";
		
		ArrayList<String> topicList = new ArrayList<String>();
		
		topicList.add(telemetryTopic);
		topicList.add(statusTopic);
		//topicList.add(toMcuTopic);
		
//		Topic[] topics = {new Topic(telemetryTopic, QoS.AT_LEAST_ONCE),
//						new Topic(statusTopic, QoS.AT_LEAST_ONCE),
//						new Topic(toMcuTopic, QoS.AT_LEAST_ONCE)};
		
		
		//System.out.println("Error subscribing to topic");
		
		
		
		
		  link.Open_link();
			
		  ArrayList<Integer> enclosures = new ArrayList<Integer>();;
			
			try{
				String query_getModules = "SELECT `EnclosureNodeID` FROM EnclosureNode;";
				prep_sql = link.linea.prepareStatement(query_getModules);
				
				ResultSet rs_query_getModules= prep_sql.executeQuery();
				
					while(rs_query_getModules.next()){
						
					//Module module = new Module();
								
						//module.setEnclosureNodeID(rs_query_getModules.getInt("EnclosureNodeID"));
						topicList.add("/DynoCloud/MCU/"+rs_query_getModules.getInt("EnclosureNodeID"));
						enclosures.add(rs_query_getModules.getInt("EnclosureNodeID"));
						

					}
			}catch(Exception e){

				System.out.println("Error: " + e.getMessage());
				
				link.Close_link();
				
				//return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error loading profiles").build();
				
			}

		link.Close_link();
		
		
		
		
		Topic topics[] = new Topic[topicList.size()];
		System.out.println("Subscribing to topics:");
		int i=0;
		for(String topic : topicList){
			
			topics[i]=new Topic(topic, QoS.AT_LEAST_ONCE);
			i++;
			
			System.out.println("\t" + topic);
		}
		
		System.out.print("\n");
		
		try {
			
			@SuppressWarnings("unused")
			byte[] qoses = connection.subscribe(topics);
			
		} catch (Exception e) {
			System.out.println("Error subscribing to topic");
			main(args);
		}
//-------------------------------------------------------------------
			
//		MQTT toMcu = new MQTT();
//		
//		try {		
//			toMcu.setHost(host, 1883);
//					
//		} catch (URISyntaxException e) {
//			System.out.println("Error finding Broker");
//			main(args);
//		}
//		
//		toMcu.setKeepAlive((short) 25);
//		//node.setWillTopic("will");
//		//node.setWillMessage("Node disconnected");		
//		
//		BlockingConnection connectionToMcu = toMcu.blockingConnection();
//		System.out.println("Connecting to Broker");
//		try {
//			connectionToMcu.connect();	
//		} catch (Exception e) {
//			System.out.println("Error connecting to Broker");
//			main(args);
//		}
//		System.out.println("Broker online");
//		
////		String telemetryTopic = "/DynoCloud/Telemetry";
////		String statusTopic = "/DynoCloud/Status";
//		
//		Topic[] topicsMcu = {new Topic("/DynoCloud/Mcu/1", QoS.AT_LEAST_ONCE)};
//
//		try {
//			
//			@SuppressWarnings("unused")
//			byte[] qoses = connectionToMcu.subscribe(topicsMcu);
//			
//		} catch (Exception e) {
//			System.out.println("Error subscribing to topic");
//			main(args);
//		}
////------------------------------------------------------------------
//		
//		FutureConnection futureConnection = toMcu.futureConnection();
//		Future<Void> f1 = futureConnection.connect();
//		try {
//			f1.await();
//		} catch (Exception e2) {
//			// TODO Auto-generated catch block
//			e2.printStackTrace();
//		}
//
//		Future<byte[]> f2 = futureConnection.subscribe(new Topic[]{new Topic("/DynoCloud/Mcu/1", QoS.AT_LEAST_ONCE)});
//		try {
//			byte[] qoses = f2.await();
//		} catch (Exception e2) {
//			// TODO Auto-generated catch block
//			e2.printStackTrace();
//		}
//
//		// We can start future receive..
//		Future<Message> receive = futureConnection.receive();
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
			
			
			 link.Open_link();
				
				
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
					link.Close_link();
				}

			link.Close_link();
				
			
		} else {
			System.out.println("DynoCloud is disabled");
		}
//-------------------------------------------------------------------
		
		ArrayList<MessageToMcu> toMcu = new ArrayList<MessageToMcu>();
		
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
			
			System.out.println("Message received:");
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
					
				}catch(Exception e){
					System.out.println("Error: " + e.getMessage());
					link.Close_link();							
				}
	
				link.Close_link();
				
				System.out.println("Telemetry saved\n");
				
				message.ack();
	//-------------------------------------------------------------------
				
				//toMcu.add(payloadString);
				
				
					
				for(MessageToMcu queued : toMcu){
					
					System.out.println("[queue] "+queued);
					
						
					for(Integer enclosure : enclosures){
						
						int topicEnclosureNodeID = Integer.parseInt(topic.replaceAll("[\\D]", ""));
						
						String toMcuMessagePayload = null;
						String toMcuMessageTopic = null;
						MessageToMcu messageToMcu=null;
						
						messageToMcu = toMcu.get(0);
						toMcuMessagePayload = queued.getMessage();
						toMcuMessageTopic = queued.getTopic();
						
						int queueEnclosureNodeID = Integer.parseInt(toMcuMessageTopic.replaceAll("[\\D]", ""));
					}
					
				}
				
				
				String toMcuMessagePayload = null;
				String toMcuMessageTopic = null;
				MessageToMcu messageToMcu=null;
				
				try {
					
					int topicEnclosureNodeID = Integer.parseInt(topic.replaceAll("[\\D]", ""));
					
					messageToMcu = toMcu.get(0);
					toMcuMessagePayload = messageToMcu.getMessage();
					toMcuMessageTopic = messageToMcu.getTopic();
					
					int queueEnclosureNodeID = Integer.parseInt(toMcuMessageTopic.replaceAll("[\\D]", ""));
					
					if(topicEnclosureNodeID == queueEnclosureNodeID){
					
						messageToMcu = toMcu.remove(0);
						toMcuMessagePayload = messageToMcu.getMessage();
						toMcuMessageTopic = messageToMcu.getTopic();
						
						//System.out.println("Sending to MCU: "+toMcuMessageTopic+" "+toMcuMessagePayload+"\n");
						//System.out.println(toMcuPayload);
	
					
					//System.out.println(toMcuPayload);
					
						Thread.sleep(500);
					//-------------------------------------------------------------------	
	
						MQTT mcu = new MQTT();
						
						try {
							
							mcu.setHost(host, 1883);
							
							BlockingConnection mcu_connection = mcu.blockingConnection();
							
							try {
								
								mcu_connection.connect();
								
								//String mcuTopic = "/DynoCloud/MCU/"+enclosureNodeID;
								
								int enclosureNodeID = Integer.parseInt(toMcuMessageTopic.replaceAll("[\\D]", ""));
								
								String mcuTopic = "/DynoCloud/"+enclosureNodeID;
								
								System.out.println("[MQTT] ["+mcuTopic+"] " + toMcuMessagePayload);
								
								mcu_connection.publish(mcuTopic, toMcuMessagePayload.getBytes(), QoS.AT_LEAST_ONCE, false);
		
								mcu_connection.disconnect();
								
							} catch (Exception e) {
								System.out.println("[MQTT] Error relaying message");
							}
										
						} catch (URISyntaxException e) {
							System.out.println("[MQTT] Error connecting to node");
						}
					
					
						System.out.println();
						
					}
					System.out.println("Nothing queued to this MCU\n");
				
				} catch (Exception e1) {
					System.out.println("Nothing queued to MCUs\n");
				}
	//-------------------------------------------------------------------
				
				
				//Future<Message> receive = null;
				
				//Message messageToMcu=null;
				
//				Message messageToMcu = null;
//				try {
//					messageToMcu = receive.await();
//				} catch (Exception e1) {
//					// TODO Auto-generated catch block
//					e1.printStackTrace();
//				}
//				//message.ack();
//				
////				try {
////					receive = futureConnection.receive();
////				} catch (Exception e) {
////					System.out.println("Error receiving message");
////					//main(args);
////				}
//				
//				String topicToMcu = messageToMcu.getTopic();		
//				byte[] payloadToMcu = messageToMcu.getPayload();			
//				String payloadToMcuString = new String(payloadToMcu, StandardCharsets.UTF_8);
//				
//				messageToMcu.ack();
//
//				
//				System.out.println(topicToMcu + " " + payloadToMcuString);	
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
				
				//System.out.println(telemetryJsonString);
				System.out.println("Queueing message to server queue");
				
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
				
				//System.out.println();
				System.out.println(messageJsonString);
//-------------------------------------------------------------------				
				
				
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
						System.out.println("Message relayed to server queue");
						
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
//					try {
//						Thread.sleep(3500);
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					}
					initialVariables.sendToNode(host);
				
				} else if(ens.getStatus() == 0){
					
					System.out.println("Enclosure Node "+ens.getCLIENTID()+" has gone offline");
					
				}
				
				message.ack();
			//}			
//-------------------------------------------------------------------
		}else {//if(topic.equals(toMcuTopic)){
			
//			boolean topicFound=false;
//			
//			for(String currentTopic : topicList){
//				
//				//System.out.println("Search: " + currentTopic);
//				
//				if(topic.equals(currentTopic)){
				
					MessageToMcu messageToMcu= new MessageToMcu();
					
					messageToMcu.setTopic(topic);
					messageToMcu.setMessage(payloadString);
					
					toMcu.add(messageToMcu);
					
					System.out.println("Message to MCU queued");
					
//					topics[i]=new Topic(topic, QoS.AT_LEAST_ONCE);
//					i++;
//					
//					System.out.println("topic: " + topic);
//					topicFound=true;
//					break;
//				}
//			}
//			
			message.ack();
//			
//			if(!topicFound)
//			System.out.println("Unknown topic");
//			else 
//				topicFound=false;

			//System.out.println(payloadString);
			
//			MessageToMcu messageToMcu= new MessageToMcu();
//			
//			messageToMcu.setTopic(topic);
//			messageToMcu.setMessage(payloadString);
//			
//			toMcu.add(messageToMcu);
//			
//			System.out.println("Message to MCU queued");
			
//			ObjectMapper mapper = new ObjectMapper();
//			EnclosureNodeStatus ens = null;
//				
//			try {
//				ens = mapper.readValue(payloadString, EnclosureNodeStatus.class);
//			} catch (Exception e1) {
//				System.out.println("Error mapping to json: " + e1.getMessage());
//				//main(args);
//			}
//			
//			if(ens.getStatus() == 1){
//			
//				InitialVariables initialVariables = new InitialVariables(ens.getCLIENTID());
//				try {
//					Thread.sleep(3500);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//				initialVariables.sendToNode(host);
//			
//			} else if(ens.getStatus() == 0){
//				
//				System.out.println("Enclosure Node "+ens.getCLIENTID()+" has gone offline");
//				
//			}
			
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
	
	static class MessageToMcu{
		
		String topic;
		String message;
		
		public String getTopic() {
			return topic;
		}
		public void setTopic(String topic) {
			this.topic = topic;
		}
		public String getMessage() {
			return message;
		}
		public void setMessage(String message) {
			this.message = message;
		}
		@Override
		public String toString() {
			return "MessageToMcu [topic=" + topic + ", message=" + message + "]";
		}
		
		
			
	}

}
