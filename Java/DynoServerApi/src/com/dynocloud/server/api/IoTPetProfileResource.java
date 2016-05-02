package com.dynocloud.server.api;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.PreparedStatement;


@Path("/IoT/profiles")
public class IoTPetProfileResource {
	
	private static Database_connection link = new Database_connection();
	private static PreparedStatement prep_sql;
	
 
	@Logged
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response postProfile(PetProfile profile, @Context HttpHeaders headers) {
	  	  
	  Session session = new Session(headers);
      User currentUser = session.getUser();
      int userID = currentUser.getUserID(); 
        	  
      System.out.println("["+currentUser.getUserName()+"] [POST] /IoT/profiles");
      
	  link.Open_link();
			
		try{
			String query_postProfile = "INSERT INTO PetProfiles (`UserID`,`PetProfileID`,`Day_Temperature_SP`,`Day_Humidity_SP`,`Night_Temperature_SP`,`Night_Humidity_SP`,`Temperature_TH`,`Humidity_TH`,`DayTime`,`NightTime`) VALUES (?,?,?,?,?,?,?,?,?,?);";
			
			prep_sql = link.linea.prepareStatement(query_postProfile);
			
			prep_sql.setInt(1, userID);
			prep_sql.setString(2, profile.getPetProfileID());
			prep_sql.setFloat(3, profile.getDay_Temperature_SP());
			prep_sql.setFloat(4, profile.getDay_Humidity_SP());
			prep_sql.setFloat(5, profile.getNight_Temperature_SP());
			prep_sql.setFloat(6, profile.getNight_Humidity_SP());
			prep_sql.setFloat(7, profile.getTemperature_TH());
			prep_sql.setFloat(8, profile.getHumidity_TH());
			prep_sql.setString(9, profile.getDayTime());
			prep_sql.setString(10, profile.getNightTime());
			
			prep_sql.executeUpdate();

		}catch(Exception e){

			System.out.println("Error: " + e.getMessage());
			
			link.Close_link();
			
			return Response.status(Response.Status.CONFLICT).entity("Profile exists").build();
			
		}

	link.Close_link();
	
	if(currentUser.isDynoCloud()){
		SendToCentralNode sendToCentralNode = new SendToCentralNode(profile, "POST", "IoT/profiles");
		sendToCentralNode.sendToOtherNodes(userID, currentUser.getCentralNodeID());
	}
	
	return Response.status(Response.Status.OK).build();
  
  }
	
	
	@Logged
	@DELETE
	@Path("{PetProfileID}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteProfile(@PathParam("PetProfileID") String PetProfileID, @Context HttpHeaders headers) {

	  Session session = new Session(headers);
      User currentUser = session.getUser();       
      int userID=currentUser.getUserID();
	  
      System.out.println("["+currentUser.getUserName()+"] [DELETE] /IoT/profiles/"+PetProfileID);
      
	  link.Open_link();
		
		try{
			String query_getProfiles = "DELETE FROM PetProfiles where `UserID` = ? AND `PetProfileID` = ?";
			prep_sql = link.linea.prepareStatement(query_getProfiles);
			
			prep_sql.setInt(1, userID);
			prep_sql.setString(2, PetProfileID);
			
			int rs_query_getProfiles=prep_sql.executeUpdate();

			if (rs_query_getProfiles == 0){
				System.out.println("rs_query_getProfiles no data");
				link.Close_link();
				return Response.status(Response.Status.NOT_FOUND).entity("Profile not found").build();
			}	

		}catch(Exception e){

			System.out.println("Error: " + e.getMessage());
			
			link.Close_link();
			
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error deleting profile").build();
				
		}

	link.Close_link();
	
	if(currentUser.isDynoCloud()){
		SendToCentralNode sendToCentralNode = new SendToCentralNode(null, "DELETE", "IoT/profiles");
		sendToCentralNode.sendToOtherNodes(userID, currentUser.getCentralNodeID());
	}
	
	return Response.status(Response.Status.OK).build();
  
  }
	
	@Logged
	@PUT
	@Path("{PetProfileID}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateProfile(@PathParam("PetProfileID") String PetProfileID, PetProfile profile, @Context HttpHeaders headers) {
	
	  Session session = new Session(headers);
	  User currentUser = session.getUser();
	  int userID=currentUser.getUserID();
      	  
      System.out.println("["+currentUser.getUserName()+"] [PUT] /IoT/profiles/"+PetProfileID);
	  
	  link.Open_link();
			
		try{
			String query_putProfile = "UPDATE PetProfiles SET `PetProfileID`=?,`Day_Temperature_SP`=?,`Day_Humidity_SP`=?,`Night_Temperature_SP`=?,`Night_Humidity_SP`=?,`Temperature_TH`=?,`Humidity_TH`=?,`DayTime`=?,`NightTime`=? WHERE `PetProfileID`=? AND `UserID`=?;";
			prep_sql = link.linea.prepareStatement(query_putProfile);
			
			prep_sql.setString(1, profile.getPetProfileID());
			prep_sql.setFloat(2, profile.getDay_Temperature_SP());
			prep_sql.setFloat(3, profile.getDay_Humidity_SP());
			prep_sql.setFloat(4, profile.getNight_Temperature_SP());
			prep_sql.setFloat(5, profile.getNight_Humidity_SP());
			prep_sql.setFloat(6, profile.getTemperature_TH());
			prep_sql.setFloat(7, profile.getHumidity_TH());
			prep_sql.setString(8, profile.getDayTime());
			prep_sql.setString(9, profile.getNightTime());
			prep_sql.setString(10, PetProfileID);
			prep_sql.setInt(11, currentUser.getUserID());
					
			int rs_query_putProfile=prep_sql.executeUpdate();

			if (rs_query_putProfile == 0){
				System.out.println("rs_query_putProfile no data");
				link.Close_link();
				return Response.status(Response.Status.NOT_FOUND).entity("Profile not found").build();
			}

		}catch(Exception e){

			System.out.println("Error: " + e.getMessage());
			
			link.Close_link();
			
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Profile exists").build();
			
		}

	link.Close_link();
	
	if(currentUser.isDynoCloud()){
		SendToCentralNode sendToCentralNode = new SendToCentralNode(profile, "PUT", "IoT/profiles");
		sendToCentralNode.sendToOtherNodes(userID, currentUser.getCentralNodeID());
	}
	
	return Response.status(Response.Status.OK).build();
  
  }	

} 