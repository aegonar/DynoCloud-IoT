package com.dynocloud.telemetry;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.PreparedStatement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

//import javax.ws.rs.core.Response;

import org.fusesource.mqtt.client.BlockingConnection;
import org.fusesource.mqtt.client.MQTT;
import org.fusesource.mqtt.client.Message;
import org.fusesource.mqtt.client.QoS;
import org.fusesource.mqtt.client.Topic;

//import com.dynocloud.node.api.Header;
//import com.dynocloud.node.api.MessageRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
//import com.dynocloud.node.api.Database_connection;
//import com.dynocloud.server.telemetry.MessageRequest;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Daemon {
	
	public static void main (String[] args) {
		
		String host="192.168.0.199";
		
//		try {
//			host = args[0];
//		System.out.println("MQTT host: " + host);
//		} catch (Exception e) {
//			System.out.println("MQTT Broker host not set as arguments");
//			System.exit(1);
//		}
//		
		String path="localhost/server_api/";
		
//		try {
//			path = args[1];
//		System.out.println("Server host path: " + path);
//		} catch (Exception e) {
//			System.out.println("Server host path not set as arguments");
//			System.exit(1);
//		}
//		
		MQTT node = new MQTT();
		
		try {
			
			node.setHost(host, 1883);
					
		} catch (URISyntaxException e) {
			System.out.println("Error finding Broker");
			main(args);
		}

		node.setKeepAlive((short) 5);
		node.setWillTopic("will");
		node.setWillMessage("Node disconnected");
		
		
		BlockingConnection connection = node.blockingConnection();
		
		try {
			connection.connect();	
		} catch (Exception e) {
			System.out.println("Error connecting to Broker");
			main(args);
		}
		
		Topic[] topics = {new Topic("/DynoCloud/VariableSend", QoS.AT_LEAST_ONCE)};
		
		try {
			
			@SuppressWarnings("unused")
			byte[] qoses = connection.subscribe(topics);
			
		} catch (Exception e) {
			System.out.println("Error subscribing to topic");
			main(args);
		}
		//-------------------------------------------------------------------			
//		MQTT server = new MQTT();
//		BlockingConnection server_connection=null;
//		try {	
//			server.setHost("dynocare.xyz", 1883);			
//			server_connection = server.blockingConnection();
//			try {
//				server_connection.connect();
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//							
//		} catch (URISyntaxException e) {
//			System.out.println("Error connecting to Server");
//		}
		//-------------------------------------------------------------------		
		
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
///*			
			String url = "http://"+path+"publish";
			URL obj;
			HttpURLConnection con = null;
			
			try {
				
				obj = new URL(url);
				
			try {
				
				con = (HttpURLConnection) obj.openConnection();
				
				con.setRequestMethod("POST");
				con.setRequestProperty("Content-Type", "application/json");
				con.setRequestProperty("Authorization", "Bearer 56me538k6mevqf41tvjqe10nqj");

				String urlParameters = payloadString;
				
				con.setDoOutput(true);
				DataOutputStream wr = new DataOutputStream(con.getOutputStream());
				wr.writeBytes(urlParameters);
				wr.flush();
				wr.close();
	
				int responseCode = con.getResponseCode();
				System.out.println("\nSending 'POST' request to URL : " + url);
				System.out.println("Post parameters : " + urlParameters);
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
						
			} catch (MalformedURLException e) {
				System.out.println("Error connecting to Server");
			}
			} catch (IOException e) {
				System.out.println("Server Response: Malformed Message");
			}
	//*/		
	/*		
			ObjectMapper mapper = new ObjectMapper();
			Telemetry telemetry = null;
				
			try {
				telemetry = mapper.readValue(payloadString, Telemetry.class);
			} catch (Exception e1) {
				System.out.println("Error mapping to json: " + e1.getMessage());
				main(args);
			}
			
			
			System.out.println(telemetry);
	          
			Database_connection link = new Database_connection();
			PreparedStatement prep_sql;
			
	        link.Open_link();
			
			try{
				String query_telemetry = "INSERT INTO Telemetry (`EnclosureNodeID`,`TEMP`,`RH`,`OPTIONAL_LOAD`,`HEAT_LOAD`,`UV_STATUS`,`HUMI_STATUS`,`DateTime`) VALUES (?,?,?,?,?,?,?,?);";
				prep_sql = link.linea.prepareStatement(query_telemetry);
								
				prep_sql.setInt(1, telemetry.getCLIENTID());
				prep_sql.setFloat(2, telemetry.getTEMP());
				prep_sql.setFloat(3, telemetry.getRH());
				prep_sql.setFloat(4, telemetry.getOPTIONAL_LOAD());
				prep_sql.setFloat(5, telemetry.getHEAT_LOAD());
				prep_sql.setInt(6, telemetry.getUV_STATUS());
				prep_sql.setInt(7, telemetry.getHUMI_STATUS());
				
				prep_sql.setTimestamp(8, parseDate(telemetry.getDateTime()));
							
				prep_sql.executeUpdate();
				
			}catch(Exception e){
				System.out.println("Error: " + e.getMessage());
				link.Close_link();							
			}

			link.Close_link();
			
			System.out.println("INSERTED\n");
	*/		
			message.ack();
//-------------------------------------------------------------------			
			
	/*		
			telemetry.setUserID(2);
			telemetry.setCentralNodeID(1);
			
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
			auth.setValue("Bearer 56me538k6mevqf41tvjqe10nqj");
			
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
			System.out.println("Relaying message to server");
			
			MQTT localServer = new MQTT();
			
			try {
				
				localServer.setHost(host, 1883);
				
				BlockingConnection server_connection = localServer.blockingConnection();
				
				try {
					
					server_connection.connect();
					
					server_connection.publish("/DynoCloud/Queue", messageJsonString.getBytes(), QoS.AT_LEAST_ONCE, false);
					System.out.println("Message relayed to server");
					
				} catch (Exception e) {
					System.out.println("Error relaying message");
				}
							
			} catch (URISyntaxException e) {
				System.out.println("Error connecting to Server");
			}
			
//
//				try {
//					
//					
//					
//					server_connection.publish("/DynoCloud", messageJsonString.getBytes(), QoS.AT_LEAST_ONCE, false);
//					System.out.println("Message relayed to server");
//					
//				} catch (Exception e) {
//					System.out.println("Error: " + e.getMessage());
//					System.out.println("Error relaying message");
//				}

		*/	
			
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

}
