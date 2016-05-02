package com.dynocloud.server.api;

//import javax.ws.rs.Consumes;
//import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
//import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Path("/module")
public class ModuleResource {
	
	private static Database_connection link = new Database_connection();
	private static PreparedStatement prep_sql;
	
	@Logged
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getModules(@Context HttpHeaders headers) {

	  Session session = new Session(headers);
      User currentUser = session.getUser();
        
      int userID=currentUser.getUserID();
      
      System.out.println("["+currentUser.getUserName()+"] [GET] /module");
	  
	  link.Open_link();
		
	  ArrayList<Module> list = new ArrayList<Module>();
		
		try{
			String query_getModules = "SELECT * FROM EnclosureNode where `UserID` = ?";
			prep_sql = link.linea.prepareStatement(query_getModules);
			
			prep_sql.setInt(1, userID);
			
			ResultSet rs_query_getModules= prep_sql.executeQuery();
			
				while(rs_query_getModules.next()){
					
					Module module = new Module();
							
					module.setEnclosureNodeID(rs_query_getModules.getInt("EnclosureNodeID"));
					module.setCentralNodeID(rs_query_getModules.getInt("CentralNodeID"));
					module.setUserID(rs_query_getModules.getInt("UserID"));
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
  
//	@Logged
//	@POST
//	@Consumes(MediaType.APPLICATION_JSON)
//	public Response postModule(Module module, @Context HttpHeaders headers) {
//	  	  
//	  Session session = new Session(headers);
//      User currentUser = session.getUser();
//        	  
//      System.out.println("["+currentUser.getUserName()+"] [POST] /module");
//      
//	  link.Open_link();
//			
//		try{
//			String query_postModule = "INSERT INTO EnclosureNode (`CentralNodeID`,`UserID`,`Name`,`OPTIONAL_LOAD`,`PetProfileID`) VALUES (?,?,?,?,?);";
//			prep_sql = link.linea.prepareStatement(query_postModule);
//
//			prep_sql.setInt(1, module.getCentralNodeID());
//			prep_sql.setInt(2, currentUser.getUserID());
//			prep_sql.setString(3, module.getName());
//			prep_sql.setInt(4, module.getOPTIONAL_LOAD());
//			prep_sql.setString(5, module.getPetProfileID());
//			
//			prep_sql.executeUpdate();
//
//		}catch(Exception e){
//
//			System.out.println("Error: " + e.getMessage());
//			
//			link.Close_link();
//			
//			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error creating module").build();
//			
//		}
//
//	link.Close_link();
//	
//	return Response.status(Response.Status.OK).build();
//  
//  }
	
	@Logged
	@GET
	@Path("{CentralNodeID}/{EnclosureNodeID}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getModule(@PathParam("CentralNodeID") int CentralNodeID, @PathParam("EnclosureNodeID") int EnclosureNodeID, 
								@Context HttpHeaders headers) {
	  	
	  Session session = new Session(headers);
      User currentUser = session.getUser();       
      int userID=currentUser.getUserID();

      System.out.println("["+currentUser.getUserName()+"] [GET] /module/"+CentralNodeID+"/"+EnclosureNodeID);
      
	  link.Open_link();
			  
		Module module = new Module();
		
		try{
			String query_getModules = "SELECT * FROM EnclosureNode where `UserID` = ? AND `CentralNodeID` = ? AND `EnclosureNodeID` = ?";
			prep_sql = link.linea.prepareStatement(query_getModules);
			
			prep_sql.setInt(1, userID);
			prep_sql.setInt(2, CentralNodeID);
			prep_sql.setInt(3, EnclosureNodeID);
			
			ResultSet rs_query_getModules = prep_sql.executeQuery();
			
			if (!rs_query_getModules.next() ) {
				System.out.println("rs_query_getModules no data");
				link.Close_link();
				return Response.status(Response.Status.FORBIDDEN).entity("Module not found").build();	
			} else {
				module.setEnclosureNodeID(rs_query_getModules.getInt("EnclosureNodeID"));
				module.setCentralNodeID(rs_query_getModules.getInt("CentralNodeID"));
				module.setUserID(rs_query_getModules.getInt("UserID"));
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
	
	
//	@Logged
//	@DELETE
//	@Path("{CentralNodeID}/{EnclosureNodeID}")
//	@Produces(MediaType.APPLICATION_JSON)
//	public Response deleteModule(@PathParam("CentralNodeID") int CentralNodeID, @PathParam("EnclosureNodeID") int EnclosureNodeID, 
//								@Context HttpHeaders headers) {
//
//	  Session session = new Session(headers);
//      User currentUser = session.getUser();       
//      int userID=currentUser.getUserID();
//	  
//      System.out.println("["+currentUser.getUserName()+"] [DELETE] /module/"+CentralNodeID+"/"+EnclosureNodeID);
//      
//	  link.Open_link();
//		
//		try{
//			String query_deleteModule = "DELETE FROM EnclosureNode where `UserID` = ? AND `EnclosureNodeID` = ? AND `CentralNodeID` = ?";
//			prep_sql = link.linea.prepareStatement(query_deleteModule);
//			
//			prep_sql.setInt(1, userID);
//			prep_sql.setInt(2, EnclosureNodeID);
//			prep_sql.setInt(3, CentralNodeID);
//			
//			int rs_query_deleteModule=prep_sql.executeUpdate();
//
//			if (rs_query_deleteModule == 0){
//				System.out.println("rs_query_deleteModule no data");
//				link.Close_link();
//				return Response.status(Response.Status.FORBIDDEN).entity("Cannot delete module").build();
//			}	
//
//		}catch(Exception e){
//
//			System.out.println("Error: " + e.getMessage());
//			
//			link.Close_link();
//			
//			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error deleting module").build();
//				
//		}
//
//	link.Close_link();
//	
//	SendToCentralNode sendToCentralNode = new SendToCentralNode(null, "DELETE", "module/"+EnclosureNodeID);
//	sendToCentralNode.sendToNode(currentUser.getUserID(), CentralNodeID);
//
//	return Response.status(Response.Status.OK).build();
//  
//  }
	
	@Logged
	@PUT
	@Path("{CentralNodeID}/{EnclosureNodeID}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response putModule(@PathParam("CentralNodeID") int CentralNodeID, @PathParam("EnclosureNodeID") int EnclosureNodeID, 
						Module module, @Context HttpHeaders headers) {
		
	  Session session = new Session(headers);
	  User currentUser = session.getUser();
      	  
      System.out.println("["+currentUser.getUserName()+"] [PUT] /module/"+CentralNodeID+"/"+EnclosureNodeID);
      
      //System.out.println(module);
	  
	  link.Open_link();
			
		try{
			String query_putModule = "UPDATE EnclosureNode SET `Name`=?,`OPTIONAL_LOAD`=?,`PetProfileID`=? WHERE `EnclosureNodeID`=? AND `CentralNodeID` = ? AND `UserID`=?;";
			prep_sql = link.linea.prepareStatement(query_putModule);
			
			prep_sql.setString(1, module.getName());
			prep_sql.setInt(2, module.getOPTIONAL_LOAD());
			prep_sql.setString(3, module.getPetProfileID());
			prep_sql.setInt(4, EnclosureNodeID);
			prep_sql.setInt(5, CentralNodeID);
			prep_sql.setInt(6, currentUser.getUserID());
					
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

	link.Close_link();
	
	SendToCentralNode sendToCentralNode = new SendToCentralNode(module, "PUT", "IoT/module/"+EnclosureNodeID);
	sendToCentralNode.sendToNode(currentUser.getUserID(), CentralNodeID);
		
	return Response.status(Response.Status.OK).build();
  
  }	

} 