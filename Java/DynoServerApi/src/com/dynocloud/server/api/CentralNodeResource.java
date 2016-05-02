package com.dynocloud.server.api;

//import javax.ws.rs.Consumes;
//import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
//import javax.ws.rs.POST;
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
import java.util.ArrayList;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Path("/central")
public class CentralNodeResource {
	
	private static Database_connection link = new Database_connection();
	private static PreparedStatement prep_sql;
	
	@Logged
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getModules(@Context HttpHeaders headers) {

	  Session session = new Session(headers);
      User currentUser = session.getUser();
        
      int userID=currentUser.getUserID();
      
      System.out.println("["+currentUser.getUserName()+"] [GET] /central");
	  
	  link.Open_link();
		
	  ArrayList<CentralNode> list = new ArrayList<CentralNode>();
		
		try{
			String query_getCentrals = "SELECT * FROM CentralNode where `UserID` = ?";
			prep_sql = link.linea.prepareStatement(query_getCentrals);
			
			prep_sql.setInt(1, userID);
			
			ResultSet rs_query_getCentrals= prep_sql.executeQuery();
			
				while(rs_query_getCentrals.next()){
					
					CentralNode centralNode = new CentralNode();
							
					centralNode.setCentralNodeID(rs_query_getCentrals.getInt("CentralNodeID"));

					list.add(centralNode);

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
  
	class CentralNode{
		
		int centralNodeID;

		public int getCentralNodeID() {
			return centralNodeID;
		}

		public void setCentralNodeID(int centralNodeID) {
			this.centralNodeID = centralNodeID;
		}
		
		
	}

} 