package com.dynocloud.node.api;

import java.net.URISyntaxException;

import org.fusesource.mqtt.client.BlockingConnection;
import org.fusesource.mqtt.client.MQTT;
import org.fusesource.mqtt.client.QoS;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


public class SendToEnclosureNode {
	
	Object object;
	int enclosureNodeID;
	
	public SendToEnclosureNode(Object object, int enclosureNodeID){
				
		this.object = object;
		this.enclosureNodeID = enclosureNodeID;
	}
	
	public void sendToNode(){
				
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

		MQTT node = new MQTT();
		
		try {
			
			node.setHost("localhost", 1883);
			
			BlockingConnection node_connection = node.blockingConnection();
			
			try {
				
				node_connection.connect();
				
				//String topic = "/DynoCloud/MCU/"+enclosureNodeID;
				String topic = "/DynoCloud/"+enclosureNodeID;
				
				System.out.println("[MQTT] ["+topic+"] " + jsonString);
				
				node_connection.publish(topic, jsonString.getBytes(), QoS.AT_LEAST_ONCE, false);
				//System.out.println("[MQTT] Sent");
				node_connection.disconnect();
				
			} catch (Exception e) {
				System.out.println("[MQTT] Error relaying message");
			}
						
		} catch (URISyntaxException e) {
			System.out.println("[MQTT] Error connecting to node");
		}

	}
	
}
