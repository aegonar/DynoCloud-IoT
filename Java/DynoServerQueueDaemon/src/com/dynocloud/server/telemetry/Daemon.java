package com.dynocloud.server.telemetry;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import org.fusesource.mqtt.client.BlockingConnection;
import org.fusesource.mqtt.client.MQTT;
import org.fusesource.mqtt.client.Message;
import org.fusesource.mqtt.client.QoS;
import org.fusesource.mqtt.client.Topic;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Daemon {
	
	public static void main (String[] args) {
		
		MQTT mqtt = new MQTT();
		

			
		try {
			mqtt.setHost("dynocare.xyz", 1883);
		} catch (URISyntaxException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		mqtt.setKeepAlive((short) 5);
		mqtt.setWillTopic("will");
		mqtt.setWillMessage("Node disconnected");
				
		BlockingConnection connection = mqtt.blockingConnection();
		

			try {
				connection.connect();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		
		Topic[] topics = {new Topic("/DynoCloud/", QoS.AT_LEAST_ONCE)};

			
			try {
				byte[] qoses = connection.subscribe(topics);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}


			
			
		try{
			
		while(true){
			
			Message message=null;
			message = connection.receive();				

			String topic = message.getTopic();		
			byte[] payload = message.getPayload();			
			String payloadString = new String(payload, StandardCharsets.UTF_8);
			
			System.out.println(topic + " " + payloadString);
					
			ObjectMapper mapper = new ObjectMapper();
			MessageRequest mFromJSON = null;
				
			mFromJSON = mapper.readValue(payloadString, MessageRequest.class);	
			
			String url = "http://localhost/server_api/" +  mFromJSON.getPath();
			
			System.out.println(url);
			
			URL obj;
			HttpURLConnection con = null;
			
				
				obj = new URL(url);
				
				con = (HttpURLConnection) obj.openConnection();
				
				con.setRequestMethod(mFromJSON.getMethod());
				System.out.println("Method: " + mFromJSON.getMethod());
				
				for( Header header : mFromJSON.getHeaderList()){
					
					con.setRequestProperty(header.getKey(),header.getValue());
					System.out.println("Header: " + header.getKey() + ": " + header.getValue());
					
				}
				
//				con.setRequestProperty("Content-Type", "application/json");
//				con.setRequestProperty("Authorization", "Bearer 56me538k6mevqf41tvjqe10nqj");

				String urlParameters = mFromJSON.getPayload();
				
				con.setDoOutput(true);
				DataOutputStream wr = new DataOutputStream(con.getOutputStream());
				wr.writeBytes(urlParameters);
				wr.flush();
				wr.close();
	
				int responseCode = con.getResponseCode();
//				System.out.println("\nSending 'POST' request to URL : " + url);
//				System.out.println("Post parameters : " + urlParameters);
				System.out.println("Response Code : " + responseCode);
	
				BufferedReader in = new BufferedReader(
				        new InputStreamReader(con.getInputStream()));
				String inputLine;
				StringBuffer response = new StringBuffer();
	
				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				in.close();
				
				System.out.println(response.toString());
						
			message.ack();
			
			System.out.println("\n");
		}
		
		} catch (URISyntaxException e) {
			System.out.println("URISyntaxException");
			e.printStackTrace();
		} catch (JsonParseException e) {
			System.out.println("JsonParseException");
			e.printStackTrace();
		} catch (JsonMappingException e) {
			System.out.println("JsonMappingException");
			e.printStackTrace();
		} catch (MalformedURLException e) {
			System.out.println("MalformedURLException");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("IOException");
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
		
	}

}