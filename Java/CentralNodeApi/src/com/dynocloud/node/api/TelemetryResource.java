//package com.dynocloud.node.api;
//
//import javax.ws.rs.Consumes;
//import javax.ws.rs.GET;
////import javax.ws.rs.GET;
//import javax.ws.rs.POST;
//import javax.ws.rs.Path;
//import javax.ws.rs.Produces;
////import javax.ws.rs.core.Context;
////import javax.ws.rs.Produces;
////import javax.ws.rs.core.Context;
////import javax.ws.rs.core.HttpHeaders;
//import javax.ws.rs.core.MediaType;
//import javax.ws.rs.core.Response;
//
//import org.fusesource.mqtt.client.BlockingConnection;
//import org.fusesource.mqtt.client.MQTT;
//import org.fusesource.mqtt.client.QoS;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//
//import java.net.URISyntaxException;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.Timestamp;
////import java.sql.ResultSet;
//import java.text.DateFormat;
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
////import java.util.ArrayList;
//import java.util.Date;
//
//
//@Path("/publish")
//public class TelemetryResource {
//	
//	private static Database_connection link = new Database_connection();
//	private static PreparedStatement prep_sql;
//	
///*
//  @GET
//  @Produces(MediaType.APPLICATION_JSON)
//  public Response getTelemetry() {
//	  
//	  System.out.println("[GET] /publish");
//	      
//	  link.Open_link();
//		
//	  ArrayList<Telemetry> list = new ArrayList<Telemetry>();
//		
//		try{
//			String query_telemetry = "SELECT * FROM Telemetry;";
//			prep_sql = link.linea.prepareStatement(query_telemetry);
//			
//			ResultSet rs_query_telemetry = prep_sql.executeQuery();
//
//				while(rs_query_telemetry.next()){
//					
//					Telemetry telemetry = new Telemetry();
//
//					telemetry.setCLIENTID(rs_query_telemetry.getInt("EnclosureNodeID"));
//					telemetry.setTEMP(rs_query_telemetry.getInt("TEMP"));
//					telemetry.setRH(rs_query_telemetry.getInt("RH"));
//					telemetry.setOPTIONAL_LOAD(rs_query_telemetry.getInt("OPTIONAL_LOAD"));
//					telemetry.setHEAT_LOAD(rs_query_telemetry.getInt("HEAT_LOAD"));
//					telemetry.setUV_STATUS(rs_query_telemetry.getInt("UV_STATUS"));
//					telemetry.setHUM_STATUS(rs_query_telemetry.getInt("HUMI_STATUS"));
//					
//					Timestamp myTimestamp = rs_query_telemetry.getTimestamp("DateTime");
//					String S = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(myTimestamp);			
//					telemetry.setDateTime(S);
//							
//					list.add(telemetry);
//
//				}
//		}catch(Exception e){
//
//			System.out.println("Error: " + e.getMessage());
//			
//			link.Close_link();
//			
//			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error loading telemetry").build();
//			
//		}
//
//		link.Close_link();
//		
//		ObjectMapper mapper = new ObjectMapper();
//		String jsonString = null;
//		
//		try {
//			jsonString = mapper.writeValueAsString(list);
//			
//		} catch (JsonProcessingException e) {
//			
//			System.out.println("Error mapping to json: " + e.getMessage());
//			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("JSON mapping error").build();
//		}
//
//	  return Response.ok(jsonString, MediaType.APPLICATION_JSON).build();
//	  
//  }
//*/
//	  	@POST
//	    @Consumes({MediaType.APPLICATION_JSON})
//
//	    public Response postTelemetry(Telemetry telemetry) throws Exception{
//	  		
//	        System.out.println("[POST] /publish");
//	        System.out.println(telemetry);
//	          
//	        link.Open_link();
//			
//			try{				
//				String query_telemetry = "INSERT INTO Telemetry (`DateTime`,`EnclosureNodeID`,`TEMP`,`RH`,`OPTIONAL_LOAD`,`HEAT_LOAD`,`UV_STATUS`,`HUM_STATUS`,`HEAT_STATUS`,`OPTIONAL_STATUS`,`HUM_OR`,`HEAT_OR`,`UV_OR`,`OPTIONAL_OR`)"
//				+ "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?);";
//
//				prep_sql = link.linea.prepareStatement(query_telemetry);
//				
//				prep_sql.setTimestamp(1, parseDate(telemetry.getDateTime()));
//				prep_sql.setInt(2, telemetry.getCLIENTID());
//				prep_sql.setFloat(3, telemetry.getTEMP());
//				prep_sql.setFloat(4, telemetry.getRH());
//				prep_sql.setFloat(5, telemetry.getOPTIONAL_LOAD());
//				prep_sql.setFloat(6, telemetry.getHEAT_LOAD());
//				prep_sql.setInt(7, telemetry.getUV_STATUS());
//				prep_sql.setInt(8, telemetry.getHUM_STATUS());
//				prep_sql.setInt(9, telemetry.getHEAT_STATUS());
//				prep_sql.setInt(10, telemetry.getOPTIONAL_STATUS());
//				prep_sql.setInt(11, telemetry.getHUM_OR());
//				prep_sql.setInt(12, telemetry.getHEAT_OR());
//				prep_sql.setInt(13, telemetry.getUV_OR());
//				prep_sql.setInt(14, telemetry.getOPTIONAL_OR());
//										
//				prep_sql.executeUpdate();
//				
//			}catch(Exception e){
//
//				System.out.println("Error: " + e.getMessage());
//				
//				link.Close_link();
//				
//				return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error saving telemetry").build();
//								
//			}
//
//		link.Close_link();
//		
////---------------------------------------------------------		
//		telemetry.setUserID(2);
//		telemetry.setCentralNodeID(1);
//		
//		ObjectMapper mapper = new ObjectMapper();
//		String telemetryJsonString = null;
//		
//		try {
//			telemetryJsonString = mapper.writeValueAsString(telemetry);
//			
//		} catch (JsonProcessingException e) {
//			
//			System.out.println("Error mapping to json: " + e.getMessage());
////			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("JSON mapping error").build();
//		}
//		
//		System.out.println(telemetryJsonString);
//	
//	Header auth = new Header();
//	auth.setKey("Authorization");
//	auth.setValue("Bearer 56me538k6mevqf41tvjqe10nqj");
//	
//	Header mediaType = new Header();
//	mediaType.setKey("Content-Type");
//	mediaType.setValue("application/json");
//	
//	ArrayList<Header> headerList = new ArrayList<Header>();
//	headerList.add(auth);
//	headerList.add(mediaType);
//	
//	MessageRequest message = new MessageRequest();
//	message.setHeaderList(headerList);
//	message.setMethod("POST");
//	message.setPath("publish");
//	message.setPayload(telemetryJsonString);
//	
//
//	String messageJsonString = null;
//	
//	try {
//		messageJsonString = mapper.writeValueAsString(message);
//		
//	} catch (JsonProcessingException e) {
//		
//		System.out.println("Error mapping to json: " + e.getMessage());
////		return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("JSON mapping error").build();
//	}
//	
//	System.out.println();
//	System.out.println(messageJsonString);
//		
//			
////---------------------------------------------------------		
//	
//	
//System.out.println("Relaying message to node");
//	
//	MQTT server = new MQTT();
//	
//	try {
//		
//		server.setHost("dynocare.xyz", 1883);
//		
//		BlockingConnection server_connection = server.blockingConnection();
//		
//		try {
//			
//			server_connection.connect();
//			
//			server_connection.publish("/DynoCloud", messageJsonString.getBytes(), QoS.AT_LEAST_ONCE, false);
//			System.out.println("Message relayed to node");
//			
//			server_connection.disconnect();
//			
//		} catch (Exception e) {
//			System.out.println("Error relaying message");
//			//TODO update local DB
//		}
//					
//	} catch (URISyntaxException e) {
//		System.out.println("Error connecting to Server");
//	}
//		
//	//---------------------------------------------------------			
//		
//		return Response.status(Response.Status.OK).build();
//	    
//	    }
//
//	  	
//	  	
//	private static java.sql.Timestamp parseDate(String s) {
//		
//		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		Date date = null;
//
//		try {
//	
//			date = formatter.parse(s);
//			
//		} catch (ParseException e) {
//			e.printStackTrace();
//		}
//	
//	return new java.sql.Timestamp(date.getTime());
//	
//	}
//	
//	
//	  	
//} 