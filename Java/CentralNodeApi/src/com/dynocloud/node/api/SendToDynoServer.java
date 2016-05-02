package com.dynocloud.node.api;

import java.net.URISyntaxException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.fusesource.mqtt.client.BlockingConnection;
import org.fusesource.mqtt.client.MQTT;
import org.fusesource.mqtt.client.QoS;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;

public class SendToDynoServer {
	
	Object object;
	String method;
	String path;
	
	private static Database_connection link = new Database_connection();
	private static PreparedStatement prep_sql;
	
	public SendToDynoServer(Object object, String method, String path){		
		this.object = object;
		this.method = method;
		this.path = path;
	}
	
	public void sendToServer(){

		String host = "localhost";
		//System.out.println("MQTT host: " + host);
	
		MQTT node = new MQTT();

		BlockingConnection queue_connection = new BlockingConnection(null);
		
		String token=null;

		//System.out.println("Connecting to Queue");
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
		//System.out.println("Queue online");
		
		
		 link.Open_link();
			
			
			try{
				String query_getConfig = "SELECT * FROM Config;";
				prep_sql = link.linea.prepareStatement(query_getConfig);

				ResultSet rs_query_getConfig = prep_sql.executeQuery();
				
					while(rs_query_getConfig.next()){	
						token = rs_query_getConfig.getString("Token");
					}
					
			}catch(Exception e){
				System.out.println("Error: " + e.getMessage());
				link.Close_link();
			}

		link.Close_link();

//-------------------------------------------------------------------
		
		ObjectMapper mapper = new ObjectMapper();
		String jsonString = null;
		
		try {
			jsonString = mapper.writeValueAsString(object);
		} catch (JsonProcessingException e) {
			System.out.println("Error mapping to json: " + e.getMessage());
		}
//-------------------------------------------------------------------
		
		//System.out.println(jsonString);
		
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
		messageRequest.setMethod(method);
		messageRequest.setPath(path);
		messageRequest.setPayload(jsonString);
		
		String messageJsonString = null;
		
		try {
			messageJsonString = mapper.writeValueAsString(messageRequest);
			
		} catch (JsonProcessingException e) {	
			System.out.println("Error mapping to json: " + e.getMessage());
		}
		
		//System.out.println(messageJsonString);
//-------------------------------------------------------------------				
		//System.out.println("Queueing message");
		
			try {;
				
				queue_connection.publish("/DynoCloud/Queue", messageJsonString.getBytes(), QoS.AT_LEAST_ONCE, false);
				//System.out.println("Message relayed to queue");
				System.out.println("[MQTT] [/DynoCloud/Queue] "+messageJsonString);
				queue_connection.disconnect();
				
			} catch (Exception e) {
				System.out.println("Error queueing message");
			}
						
	}
	
}
