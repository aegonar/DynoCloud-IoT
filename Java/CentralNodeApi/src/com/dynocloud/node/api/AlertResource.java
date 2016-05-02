package com.dynocloud.node.api;

import javax.ws.rs.Consumes;
//import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
//import javax.ws.rs.PUT;
//import javax.ws.rs.PUT;
import javax.ws.rs.Path;
//import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Path("/alerts")
public class AlertResource {
	
	private static Database_connection link = new Database_connection();
	private static PreparedStatement prep_sql;
	
	//@Logged
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAlerts(@Context HttpHeaders headers) {
	  	
//	  Session session = new Session(headers);
//      User currentUser = session.getUser();
//        
//      int userID=currentUser.getUserID();
      
      System.out.println("[GET] /alerts");
	  
	  link.Open_link();
		
	  ArrayList<Alert> list = new ArrayList<Alert>();
		
		try{
			String query_getAlerts = "SELECT * FROM Alerts;";
			prep_sql = link.linea.prepareStatement(query_getAlerts);
			
			//prep_sql.setInt(1, userID);
			
			ResultSet rs_query_getAlerts= prep_sql.executeQuery();
			
				while(rs_query_getAlerts.next()){
					
					Alert alert = new Alert();
							
					alert.setAlertID(rs_query_getAlerts.getInt("AlertID"));
					alert.setEnclosureNodeID(rs_query_getAlerts.getInt("EnclosureNodeID"));
//					alert.setCentralNodeID(rs_query_getAlerts.getInt("CentralNodeID"));
//					alert.setUserID(rs_query_getAlerts.getInt("UserID"));

					alert.setMessage(rs_query_getAlerts.getString("Message"));
//					alert.setDestination(rs_query_getAlerts.getString("Destination"));
					
					Timestamp myTimestamp = rs_query_getAlerts.getTimestamp("DateTime");
					String S = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(myTimestamp);			
					alert.setDateTime(S);

					list.add(alert);

				}
		}catch(Exception e){

			System.out.println("Error: " + e.getMessage());
			
			link.Close_link();
			
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error loading alerts").build();
			
		}

	link.Close_link();
	
	ObjectMapper mapper = new ObjectMapper();
	String jsonString = null;
	
	try {
		jsonString = mapper.writeValueAsString(list);
		
	} catch (JsonProcessingException e) {
		
		System.out.println("Error mapping to json: " + e.getMessage());
		return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("JSON mapping error").build();
	}

  return Response.ok(jsonString, MediaType.APPLICATION_JSON).build();
  
  }
  
	//@Logged
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response postAlert(Alert alert, @Context HttpHeaders headers) {
	
//	  Session session = new Session(headers);
//      User currentUser = session.getUser();
      
      System.out.println("[POST] /alerts");
      
      //System.out.println(alert);
      
	  link.Open_link();
			
		try{
			String query_postAlert = "INSERT INTO Alerts (`EnclosureNodeID`, `DateTime`, `Message`) VALUES (?,now(),?);";
			prep_sql = link.linea.prepareStatement(query_postAlert);
			
//			prep_sql.setInt(1, currentUser.getUserID());
//			prep_sql.setInt(2, alert.getCentralNodeID());
			prep_sql.setInt(1, alert.getEnclosureNodeID());
			
			//prep_sql.setString(4, alert.getDateTime());
			
			prep_sql.setString(2, alert.getMessage());
//			prep_sql.setString(5, alert.getDestination());
			
			prep_sql.executeUpdate();

		}catch(Exception e){

			System.out.println("Error: " + e.getMessage());
			
			link.Close_link();
			
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error creating alert").build();
			
		}

	link.Close_link();
	
	CloudSession cloudSession = new CloudSession();	
	if(cloudSession.isOnline()){
		alert.setUserID(cloudSession.getUserID());
		alert.setCentralNodeID(cloudSession.getCentralNodeID());
		
		SendToDynoServer sendToDynoServer = new SendToDynoServer(alert, "POST", "alerts");	
		sendToDynoServer.sendToServer();
	}
	
	return Response.status(Response.Status.OK).build();
  
  }
	/*
	@Logged
	@GET
	@Path("settings")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAlertSettings(@Context HttpHeaders headers) {
	  	
	  Session session = new Session(headers);
      User currentUser = session.getUser();
        
      int userID=currentUser.getUserID();
      
      System.out.println("["+currentUser.getUserName()+"] [GET] /alerts/settings");
	  
	  link.Open_link();
		
	  AlertSettings alertSettings = new AlertSettings();
		
		try{
			String query_getSettings = "SELECT * FROM AlertSettings where `UserID` = ?";
			prep_sql = link.linea.prepareStatement(query_getSettings);
			
			prep_sql.setInt(1, userID);
			
			ResultSet rs_query_getSettings= prep_sql.executeQuery();
			
				while(rs_query_getSettings.next()){
					
					alertSettings.setUserID(rs_query_getSettings.getInt("UserID"));
					alertSettings.setRetries(rs_query_getSettings.getInt("Retries"));
					alertSettings.setThreshold(rs_query_getSettings.getInt("Threshold"));
					
					alertSettings.setEmail(rs_query_getSettings.getBoolean("Email"));
					alertSettings.setPhone(rs_query_getSettings.getBoolean("Phone"));
					alertSettings.setOnScreen(rs_query_getSettings.getBoolean("OnScreen"));						

				}
		}catch(Exception e){

			System.out.println("Error: " + e.getMessage());
			
			link.Close_link();
			
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error loading alert settings").build();
			
		}

	link.Close_link();
	
	ObjectMapper mapper = new ObjectMapper();
	String jsonString = null;
	
	try {
		jsonString = mapper.writeValueAsString(alertSettings);
		
	} catch (JsonProcessingException e) {
		
		System.out.println("Error mapping to json: " + e.getMessage());
		return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("JSON mapping error").build();
	}

  return Response.ok(jsonString, MediaType.APPLICATION_JSON).build();
  
  }
	
	@Logged
	@PUT
	@Path("settings")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateUser(AlertSettings alertSettings, @Context HttpHeaders headers) {
		
	  Session session = new Session(headers);
	  User currentUser = session.getUser();
      	  
      System.out.println("["+currentUser.getUserName()+"] [PUT] /alerts/settings");
      
        link.Open_link();
      		
		try{
			String query_updateSettings = "UPDATE AlertSettings SET `Retries`=?,`Threshold`=?,`Email`=?,`Phone`=?,`OnScreen`=? WHERE `UserID`=?;";
			prep_sql = link.linea.prepareStatement(query_updateSettings);
							
			prep_sql.setInt(1, alertSettings.getRetries());
			prep_sql.setInt(2, alertSettings.getThreshold());
			
			prep_sql.setBoolean(3, alertSettings.isEmail());
			prep_sql.setBoolean(4, alertSettings.isPhone());
			prep_sql.setBoolean(5, alertSettings.isOnScreen());
			
			prep_sql.setInt(6, currentUser.getUserID());
			
			
			prep_sql.executeUpdate();
			int  rs_query_updateSettings = prep_sql.executeUpdate();
			
			if (rs_query_updateSettings == 0){
				return Response.status(Response.Status.CONFLICT).entity("Error updating settings").build();			
			}               
	        
		}catch(Exception e){

			System.out.println("Error: " + e.getMessage());
			
			link.Close_link();

			return Response.status(Response.Status.CONFLICT).entity("Error updating settings").build();
				
		}

	link.Close_link();
	
	return Response.status(Response.Status.OK).build();
	}
	*/
} 