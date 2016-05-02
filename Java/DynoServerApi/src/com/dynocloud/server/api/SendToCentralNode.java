package com.dynocloud.server.api;

import java.net.URISyntaxException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.fusesource.mqtt.client.BlockingConnection;
import org.fusesource.mqtt.client.MQTT;
import org.fusesource.mqtt.client.QoS;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;

//import javax.ws.rs.core.Response;

public class SendToCentralNode {
	
	Object object;
	int centralNodeID;
	String method;
	String path;
	int userID;
	
	private static Database_connection link = new Database_connection();
	private static PreparedStatement prep_sql;
	
	public SendToCentralNode(Object object, String method, String path){
		
		//System.out.println("SendToCentralNode constructor");
		
		this.object = object;
		this.method = method;
		this.path = path;
	}
	
	public void sendToUser(int userID){
		
		//System.out.println("SendToCentralNode sendToUser");
		
		 link.Open_link();
			
		  //ArrayList<Integer> nodes = new ArrayList<Integer>();
			
			try{
				String query_getNodes = "SELECT `CentralNodeID` FROM CentralNode where `UserID` = ?";
				prep_sql = link.linea.prepareStatement(query_getNodes);
				
				prep_sql.setInt(1, userID);
				
				ResultSet rs_query_getNodes= prep_sql.executeQuery();
				
					while(rs_query_getNodes.next()){
						
						int node = rs_query_getNodes.getInt("CentralNodeID");
						
						//System.out.println("SendToCentralNode node: "+node);

						sendToNode(userID, node);
						

					}
			}catch(Exception e){

				System.out.println("Error: " + e.getMessage());
				
				link.Close_link();
				
//				return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error loading profiles").build();
				
			}

		
	}
	
	public void sendToOtherNodes(int userID, int centralNodeID){
		
		//System.out.println("SendToCentralNode sendToUser");
		
		 link.Open_link();
			
		  //ArrayList<Integer> nodes = new ArrayList<Integer>();
			
			try{
				String query_getNodes = "SELECT `CentralNodeID` FROM CentralNode where `UserID` = ?";
				prep_sql = link.linea.prepareStatement(query_getNodes);
				
				prep_sql.setInt(1, userID);
				
				ResultSet rs_query_getNodes= prep_sql.executeQuery();
				
					while(rs_query_getNodes.next()){
						
						int node = rs_query_getNodes.getInt("CentralNodeID");
						
						//System.out.println("SendToCentralNode node: "+node);
						if(node != centralNodeID)
						sendToNode(userID, node);
						

					}
			}catch(Exception e){

				System.out.println("Error: " + e.getMessage());
				
				link.Close_link();
				
//				return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error loading profiles").build();
				
			}

		
	}
	
	public void sendToNode(int userID, int centralNodeID){
		
		//System.out.println("SendToCentralNode sendToNode");
		
		ObjectMapper mapper = new ObjectMapper();
		String jsonString = null;
		
		if(object != null){
			
			try {
				jsonString = mapper.writeValueAsString(object);
				
			} catch (JsonProcessingException e) {
				
				System.out.println("Error mapping to json: " + e.getMessage());
				//return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("JSON mapping error").build();
			}
			
		}

		//-------------------------------------------------------------------	
		
		
		//System.out.println(jsonString);
		
//		Header auth = new Header();
//		auth.setKey("Authorization");
//		auth.setValue("Bearer 56me538k6mevqf41tvjqe10nqj");
		
		Header mediaType = new Header();
		mediaType.setKey("Content-Type");
		mediaType.setValue("application/json");
		
		ArrayList<Header> headerList = new ArrayList<Header>();
		//headerList.add(auth);
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
			//main(args);
		}
		
		//System.out.println();
		//System.out.println(messageJsonString);
		
		
		//-------------------------------------------------------------------	
		

		//System.out.println("Relaying message to node");
		
		MQTT node = new MQTT();
		
		try {
			
			node.setHost("localhost", 1883);
			
			BlockingConnection node_connection = node.blockingConnection();
			
			try {
				
				node_connection.connect();
				
				String topic = "/DynoCloud/"+userID+"/"+centralNodeID;
				
				System.out.println("[MQTT] ["+topic+"] " + messageJsonString);
				
				node_connection.publish(topic, messageJsonString.getBytes(), QoS.AT_LEAST_ONCE, false);
				//System.out.println("Message relayed to node");
				
				node_connection.disconnect();
				
			} catch (Exception e) {
				System.out.println("[MQTT] Error relaying message");
				//TODO update local DB
			}
						
		} catch (URISyntaxException e) {
			System.out.println("[MQTT] Error connecting to node");
		}

	}
	
}
