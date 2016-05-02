package com.dynocloud.node.api;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Path("/module")
public class ModuleResource {
	
	private static Database_connection link = new Database_connection();
	private static PreparedStatement prep_sql;	

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getModules() {
      
      System.out.println("[GET] /module");
	  
	  link.Open_link();
		
	  ArrayList<Module> list = new ArrayList<Module>();
		
		try{
			String query_getModules = "SELECT * FROM EnclosureNode;";
			prep_sql = link.linea.prepareStatement(query_getModules);
			
			ResultSet rs_query_getModules= prep_sql.executeQuery();
			
				while(rs_query_getModules.next()){
					
					Module module = new Module();
							
					module.setEnclosureNodeID(rs_query_getModules.getInt("EnclosureNodeID"));
					module.setName(rs_query_getModules.getString("Name"));
					module.setOPTIONAL_LOAD(rs_query_getModules.getInt("OPTIONAL_LOAD"));
					module.setPetProfileID(rs_query_getModules.getString("PetProfileID"));

					list.add(module);

				}
		}catch(Exception e){

			System.out.println("Error: " + e.getMessage());
			
			link.Close_link();
			
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error loading profiles").build();
			
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
  

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response postModule(Module module) {
        	  
      System.out.println("[POST] /module");
      
	  link.Open_link();
			
		try{
			String query_postModule = "INSERT INTO EnclosureNode (`Name`,`OPTIONAL_LOAD`,`PetProfileID`, `Online`) VALUES (?,?,?,?);";
			prep_sql = link.linea.prepareStatement(query_postModule);


			prep_sql.setString(1, module.getName());
			prep_sql.setInt(2, module.getOPTIONAL_LOAD());
			prep_sql.setString(3, module.getPetProfileID());
			prep_sql.setBoolean(4, false);
			
			prep_sql.executeUpdate();

		}catch(Exception e){

			System.out.println("Error: " + e.getMessage());
			
			link.Close_link();
			
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error creating module").build();
			
		};
	
		Timestamp maxAdded=null;
	    
	    try{
			String query_getLatestAdded = "SELECT MAX(Added) AS MaxAdded FROM `EnclosureNode`;";
			prep_sql = link.linea.prepareStatement(query_getLatestAdded);
															
			ResultSet rs_query_getLatestAdded = prep_sql.executeQuery();
			
			if (!rs_query_getLatestAdded.next() ) {
				System.out.println("rs_query_getLatestAdded no data");
				link.Close_link();
				return Response.status(Response.Status.FORBIDDEN).entity("Module not found").build();	
			} else {
				maxAdded = rs_query_getLatestAdded.getTimestamp("MaxAdded");
			}
	
		}catch(Exception e){
			System.out.println("Error: " + e.getMessage());
			link.Close_link();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error loading enclosures").build();	
		}
	    
	    int EnclosureNodeID;
	    
	    try{
			String query_getLatest = "SELECT * FROM EnclosureNode where `Added`=?;";
			prep_sql = link.linea.prepareStatement(query_getLatest);
;
			prep_sql.setTimestamp(1, maxAdded);
															
			ResultSet rs_query_getLatest= prep_sql.executeQuery();
			
			if (!rs_query_getLatest.next() ) {
				System.out.println("rs_query_getLatest no data");
				link.Close_link();
				return Response.status(Response.Status.FORBIDDEN).entity("Module not found").build();	
			} else {
				EnclosureNodeID = rs_query_getLatest.getInt("EnclosureNodeID");
			}
	
		}catch(Exception e){
			System.out.println("Error: " + e.getMessage());
			link.Close_link();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error loading enclosures").build();	
		}      
	
	link.Close_link();
	    
	PetProfileSchedule schedule = new PetProfileSchedule();
	schedule.rebuildShedule();
	
	InitialVariables initialVariables = new InitialVariables(EnclosureNodeID);
	initialVariables.sendToNode("localhost");
	
    EnclosureNode enclosureNode = new EnclosureNode();
    enclosureNode.setEnclosureNodeID(EnclosureNodeID);
    
	CloudSession cloudSession = new CloudSession();	
	if(cloudSession.isOnline()){
		module.setUserID(cloudSession.getUserID());
		module.setCentralNodeID(cloudSession.getCentralNodeID());
		module.setEnclosureNodeID(EnclosureNodeID);
		
		SendToDynoServer sendToDynoServer = new SendToDynoServer(module, "POST", "IoT/module");	
		sendToDynoServer.sendToServer();
	}
	

	ObjectMapper mapper = new ObjectMapper();
	String jsonString = null;
	
	try {
		jsonString = mapper.writeValueAsString(enclosureNode);
		
	} catch (JsonProcessingException e) {
		
		System.out.println("Error mapping to json: " + e.getMessage());
		return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("JSON mapping error").build();
	}
	
	  return Response.ok(jsonString, MediaType.APPLICATION_JSON).build();
  
  }
	

	@GET
	@Path("{EnclosureNodeID}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getModule(@PathParam("EnclosureNodeID") int EnclosureNodeID) {
	  	
      System.out.println("[GET] /module/"+EnclosureNodeID);
      
	  link.Open_link();
			  
		Module module = new Module();
		
		try{
			String query_getModules = "SELECT * FROM EnclosureNode where`EnclosureNodeID` = ?;";
			prep_sql = link.linea.prepareStatement(query_getModules);
			
			prep_sql.setInt(1, EnclosureNodeID);
			
			ResultSet rs_query_getModules = prep_sql.executeQuery();
			
			if (!rs_query_getModules.next() ) {
				System.out.println("rs_query_getModules no data");
				link.Close_link();
				return Response.status(Response.Status.FORBIDDEN).entity("Module not found").build();	
			} else {
				module.setEnclosureNodeID(rs_query_getModules.getInt("EnclosureNodeID"));
				module.setName(rs_query_getModules.getString("Name"));
				module.setOPTIONAL_LOAD(rs_query_getModules.getInt("OPTIONAL_LOAD"));
				module.setPetProfileID(rs_query_getModules.getString("PetProfileID"));
			}
		}catch(Exception e){

			System.out.println("Error: " + e.getMessage());
			
			link.Close_link();
			
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error loading module").build();
				
		}

	link.Close_link();
	
	ObjectMapper mapper = new ObjectMapper();
	String jsonString = null;
	
	try {
		jsonString = mapper.writeValueAsString(module);
		
	} catch (JsonProcessingException e) {
		
		System.out.println("Error mapping to json: " + e.getMessage());
		return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("JSON mapping error").build();
	}

  return Response.ok(jsonString, MediaType.APPLICATION_JSON).build();
  
  }
	
	@DELETE
	@Path("{EnclosureNodeID}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteModule( @PathParam("EnclosureNodeID") int EnclosureNodeID) {
	  
      System.out.println("[DELETE] /module/"+EnclosureNodeID);
      
	  link.Open_link();
		
		try{
			String query_deleteModule = "DELETE FROM EnclosureNode where `EnclosureNodeID` = ?;";
			prep_sql = link.linea.prepareStatement(query_deleteModule);
			
			prep_sql.setInt(1, EnclosureNodeID);
			
			int rs_query_deleteModule=prep_sql.executeUpdate();

			if (rs_query_deleteModule == 0){
				System.out.println("rs_query_deleteModule no data");
				link.Close_link();
				return Response.status(Response.Status.NOT_FOUND).entity("Module not found").build();
			}	

		}catch(Exception e){

			System.out.println("Error: " + e.getMessage());
			
			link.Close_link();
			
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error deleting module").build();
				
		}

	link.Close_link();
	
	PetProfileSchedule schedule = new PetProfileSchedule();
	schedule.rebuildShedule();
	
	CloudSession cloudSession = new CloudSession();	
	if(cloudSession.isOnline()){
		//module.setUserID(cloudSession.getUserID());
		//module.setCentralNodeID(cloudSession.getCentralNodeID());
		
		SendToDynoServer sendToDynoServer = new SendToDynoServer(null, "DELETE", "IoT/module/"+cloudSession.getCentralNodeID()+"/"+EnclosureNodeID);	
		sendToDynoServer.sendToServer();
	}

	return Response.status(Response.Status.OK).build();
  
  }
	
	@PUT
	@Path("{EnclosureNodeID}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response putModule( @PathParam("EnclosureNodeID") int EnclosureNodeID, Module module) {
		
      System.out.println("[PUT] /module/"+EnclosureNodeID);
     	  
	  link.Open_link();
			
		try{
			String query_putModule = "UPDATE EnclosureNode SET `Name`=?,`OPTIONAL_LOAD`=?,`PetProfileID`=? WHERE `EnclosureNodeID`=?;";
			prep_sql = link.linea.prepareStatement(query_putModule);
			
			prep_sql.setString(1, module.getName());
			prep_sql.setInt(2, module.getOPTIONAL_LOAD());
			prep_sql.setString(3, module.getPetProfileID());
			prep_sql.setInt(4, EnclosureNodeID);
					
			int rs_query_putModule=prep_sql.executeUpdate();

			if (rs_query_putModule == 0){
				System.out.println("rs_query_putModule no data");
				link.Close_link();
				return Response.status(Response.Status.FORBIDDEN).entity("Module not found").build();
			}

		}catch(Exception e){

			System.out.println("Error: " + e.getMessage());
			
			link.Close_link();
			
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error updating module").build();
			
		}
		
		
		PetProfileSchedule schedule = new PetProfileSchedule();
		schedule.rebuildShedule();
		
		InitialVariables initialVariables = new InitialVariables(EnclosureNodeID);
		initialVariables.sendToNode("localhost");
		
		CloudSession cloudSession = new CloudSession();	
		if(cloudSession.isOnline()){
			//module.setUserID(cloudSession.getUserID());
			//module.setCentralNodeID(cloudSession.getCentralNodeID());
			
			SendToDynoServer sendToDynoServer = new SendToDynoServer(module, "PUT", "IoT/module/"+cloudSession.getCentralNodeID()+"/"+EnclosureNodeID);	
			sendToDynoServer.sendToServer();
		}
	
	return Response.status(Response.Status.OK).build();
  
  }	
	
	class EnclosureNode{
		int EnclosureNodeID;

		public int getEnclosureNodeID() {
			return EnclosureNodeID;
		}

		public void setEnclosureNodeID(int enclosureNodeID) {
			EnclosureNodeID = enclosureNodeID;
		}
		
		
	}

} 