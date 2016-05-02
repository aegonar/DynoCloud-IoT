package com.dynocloud.node.request;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.fusesource.mqtt.client.BlockingConnection;
import org.fusesource.mqtt.client.MQTT;
import org.fusesource.mqtt.client.Message;
import org.fusesource.mqtt.client.QoS;
import org.fusesource.mqtt.client.Topic;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Daemon {
	
	static Database_connection link = new Database_connection();
	static PreparedStatement prep_sql;
	
	public static void main (String[] args) {

		int userID = 0;
		int centralNodeID = 0;
		
		System.out.println("Request Daemon");
		
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

			
			try{
				String query_getConfig = "SELECT * FROM Config;";
				prep_sql = link.linea.prepareStatement(query_getConfig);

				ResultSet rs_query_getConfig = prep_sql.executeQuery();
				
					while(rs_query_getConfig.next()){	
						
						userID = rs_query_getConfig.getInt("UserID");
						centralNodeID = rs_query_getConfig.getInt("CentralNodeID");
					}
					
			}catch(Exception e){
				System.out.println("Error: " + e.getMessage());
				link.Close_link();
			}
			
		link.Close_link();
		
		//------------------------------------------
		
		if(!DynoCloud){
			System.out.println("DynoCloud is disabled");
			System.out.println("Exiting...");
			System.exit(0);
		}
		
		System.out.println("DynoCloud is enabled");
		
		MQTT mqtt = new MQTT();
					
		try {
			mqtt.setHost("dynocare.xyz", 1883);
		} catch (URISyntaxException e1) {
			System.out.println("Error finding Broker");
			main(args);
		}
		mqtt.setKeepAlive((short) 5);
		mqtt.setWillTopic("will");
		mqtt.setWillMessage("Node disconnected");
				
		BlockingConnection connection = mqtt.blockingConnection();
		
		System.out.println("Connecting to Server");
			try {
				connection.connect();
			} catch (Exception e1) {
				System.out.println("Error connecting to Broker");
				main(args);
			}
			System.out.println("Server online");
		
		Topic[] topics = {new Topic("/DynoCloud/"+userID+"/"+centralNodeID, QoS.AT_LEAST_ONCE)};

			
			try {
				@SuppressWarnings("unused")
				byte[] qoses = connection.subscribe(topics);
			} catch (Exception e1) {
				System.out.println("Error subscribing to topic");
				main(args);
			}
			
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
			ObjectMapper mapper = new ObjectMapper();
			MessageRequest mFromJSON = null;
				
			try {
				mFromJSON = mapper.readValue(payloadString, MessageRequest.class);
			} catch (Exception e1) {
				System.out.println("Error mapping to json: " + e1.getMessage());
				main(args);
			}	
			
//------------------------------------------------------------------- 		
			
			String url = "http://localhost/node_api/" +  mFromJSON.getPath();
			
			System.out.println("Path: " + url);
			
			URL obj = null;
			HttpURLConnection con = null;
			
				
				try {
					obj = new URL(url);
				} catch (MalformedURLException e) {
					e.printStackTrace();
					main(args);
				}
				
				try {
					con = (HttpURLConnection) obj.openConnection();
				} catch (Exception e) {
					e.printStackTrace();
					main(args);
				}
				
				try {
					con.setRequestMethod(mFromJSON.getMethod());
				} catch (ProtocolException e) {
					e.printStackTrace();
					main(args);
				}
				System.out.println("Method: " + mFromJSON.getMethod());
				
				for( Header header : mFromJSON.getHeaderList()){
					
					con.setRequestProperty(header.getKey(),header.getValue());
					System.out.println("Header: " + header.getKey() + ": " + header.getValue());
					
				}
				

				String urlParameters = mFromJSON.getPayload();
					
					con.setDoOutput(true);
					DataOutputStream wr;
					try {
						wr = new DataOutputStream(con.getOutputStream());
						wr.writeBytes(urlParameters);
						wr.flush();
						wr.close();
					} catch (Exception e) {

						//null payload

					}
					
	
				int responseCode = 0;
				try {
					responseCode = con.getResponseCode();
				} catch (Exception e) {
					e.printStackTrace();
					main(args);
				}

				System.out.println("Response Code : " + responseCode);
	
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
					main(args);
				}

				
				System.out.println(response.toString());
						
			message.ack();
			
			System.out.println("---------------------------------------");	
		}	
	}
}