package com.dynocloud.node.api;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;


@Path("IoT/profiles")
public class IoTPetProfileResource {
	
	private static Database_connection link = new Database_connection();
	private static PreparedStatement prep_sql;
	

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response postProfile(PetProfile profile) {
	  	        	  
      System.out.println("[POST] /IoT/profiles");
      
	  link.Open_link();
			
		try{
			String query_postProfile = "INSERT INTO PetProfiles (`PetProfileID`,`Day_Temperature_SP`,`Day_Humidity_SP`,`Night_Temperature_SP`,`Night_Humidity_SP`,`Temperature_TH`,`Humidity_TH`,`DayTime`,`NightTime`) VALUES (?,?,?,?,?,?,?,?,?);";
			prep_sql = link.linea.prepareStatement(query_postProfile);
			
			prep_sql.setString(1, profile.getPetProfileID());
			prep_sql.setFloat(2, profile.getDay_Temperature_SP());
			prep_sql.setFloat(3, profile.getDay_Humidity_SP());
			prep_sql.setFloat(4, profile.getNight_Temperature_SP());
			prep_sql.setFloat(5, profile.getNight_Humidity_SP());
			prep_sql.setFloat(6, profile.getTemperature_TH());
			prep_sql.setFloat(7, profile.getHumidity_TH());
			prep_sql.setString(8, profile.getDayTime());
			prep_sql.setString(9, profile.getNightTime());
			
			prep_sql.executeUpdate();

		}catch(Exception e){

			System.out.println("Error: " + e.getMessage());
			
			link.Close_link();
			
			return Response.status(Response.Status.CONFLICT).entity("Profile exists").build();
			
		}

	link.Close_link();
	
	return Response.status(Response.Status.OK).build();
  
  }
	

	@DELETE
	@Path("{PetProfileID}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteProfile(@PathParam("PetProfileID") String PetProfileID) {

      System.out.println("[DELETE] /IoT/profiles/"+PetProfileID);
      
	  link.Open_link();
		
		try{
			String query_getProfiles = "DELETE FROM PetProfiles where `PetProfileID` = ?";
			prep_sql = link.linea.prepareStatement(query_getProfiles);
			
			//prep_sql.setInt(1, userID);
			prep_sql.setString(1, PetProfileID);
			
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

	return Response.status(Response.Status.OK).build();
  
  }
	
	@PUT
	@Path("{PetProfileID}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateProfile(@PathParam("PetProfileID") String PetProfileID, PetProfile profile) {
	
  	  
      System.out.println("[PUT] /IoT/profiles/"+PetProfileID);
	  
	  link.Open_link();
			
		try{
			String query_putProfile = "UPDATE PetProfiles SET `PetProfileID`=?,`Day_Temperature_SP`=?,`Day_Humidity_SP`=?,`Night_Temperature_SP`=?,`Night_Humidity_SP`=?,`Temperature_TH`=?,`Humidity_TH`=?,`DayTime`=?,`NightTime`=? WHERE `PetProfileID`=?;";
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
			//prep_sql.setInt(9, currentUser.getUserID());
					
			int rs_query_putProfile=prep_sql.executeUpdate();

			if (rs_query_putProfile == 0){
				System.out.println("rs_query_putProfile no data");
				link.Close_link();
				return Response.status(Response.Status.NOT_FOUND).entity("Profile not found").build();
			}

		}catch(Exception e){

			System.out.println("Error: " + e.getMessage());
			
			link.Close_link();
			
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error deleting profile").build();
			
		}

	link.Close_link();	
	
	PetProfileSchedule schedule = new PetProfileSchedule();
	schedule.rebuildShedule();
	
	ArrayList<Integer> enclosures = getEnclosureWithProfile(PetProfileID);
	for(Integer enclosure : enclosures){
		InitialVariables initialVariables = new InitialVariables(enclosure);
		initialVariables.sendToNode("localhost");
	};
	
	return Response.status(Response.Status.OK).build();
  
  }
	
	
	private ArrayList<Integer> getEnclosureWithProfile(String PetProfileID){
		
		ArrayList<Integer> nodes = new ArrayList<Integer>();

		link.Open_link();
		try{
			String query_getEnclosures = "SELECT * FROM EnclosureNode where `PetProfileID` = ?";
			prep_sql = link.linea.prepareStatement(query_getEnclosures);
			
			prep_sql.setString(1, PetProfileID);
			
			ResultSet rs_query_getEnclosures= prep_sql.executeQuery();
			
			if (!rs_query_getEnclosures.next() ) {
				System.out.println("rs_query_getEnclosures no data");
				link.Close_link();
				
			} else {
				nodes.add(rs_query_getEnclosures.getInt("EnclosureNodeID"));
			}
		}catch(Exception e){
	
			System.out.println("Error: " + e.getMessage());
			link.Close_link();	
		}
	
		link.Close_link();
		return nodes;
	}
	
	
} 