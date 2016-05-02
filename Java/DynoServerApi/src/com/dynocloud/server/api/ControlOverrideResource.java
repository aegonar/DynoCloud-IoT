package com.dynocloud.server.api;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
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

@Path("/override")
public class ControlOverrideResource {
	
	private static Database_connection link = new Database_connection();
	private static PreparedStatement prep_sql;
	
	@Logged
	@GET
	@Path("{CentralNodeID}/{EnclosureNodeID}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getOverrides(@PathParam("CentralNodeID") int CentralNodeID, @PathParam("EnclosureNodeID") int EnclosureNodeID,
								@Context HttpHeaders headers) {

	  Session session = new Session(headers);
      User currentUser = session.getUser();     
      int userID=currentUser.getUserID();
      
      System.out.println("["+currentUser.getUserName()+"] [GET] /override/"+CentralNodeID+"/"+EnclosureNodeID);
	  
	  link.Open_link();
		
	  ArrayList<ControlOverride> list = new ArrayList<ControlOverride>();
		
		try{
			String query_getOverrides = "SELECT * FROM OverrideHistory where `UserID` = ? AND `CentralNodeID`=? AND `EnclosureNodeID`=?";
			prep_sql = link.linea.prepareStatement(query_getOverrides);
			
			prep_sql.setInt(1, userID);
			prep_sql.setInt(2, CentralNodeID);
			prep_sql.setInt(3, EnclosureNodeID);
			
			ResultSet rs_query_getOverrides= prep_sql.executeQuery();
			
				while(rs_query_getOverrides.next()){
					
					ControlOverride controlOverride = new ControlOverride();
							
					controlOverride.setEnclosureNodeID(rs_query_getOverrides.getInt("EnclosureNodeID"));
					controlOverride.setCentralNodeID(rs_query_getOverrides.getInt("CentralNodeID"));
					controlOverride.setUserID(rs_query_getOverrides.getInt("UserID"));
					controlOverride.setHUM_OR(rs_query_getOverrides.getInt("HUM_OR"));
					controlOverride.setHEAT_OR(rs_query_getOverrides.getInt("HEAT_OR"));
					controlOverride.setUV_OR(rs_query_getOverrides.getInt("UV_OR"));
					controlOverride.setOPTIONAL_OR(rs_query_getOverrides.getInt("OPTIONAL_OR"));
					controlOverride.setHUM_STATUS(rs_query_getOverrides.getInt("HUM_STATUS"));
					controlOverride.setHEAT_STATUS(rs_query_getOverrides.getInt("HEAT_STATUS"));
					controlOverride.setUV_STATUS(rs_query_getOverrides.getInt("UV_STATUS"));
					controlOverride.setOPTIONAL_STATUS(rs_query_getOverrides.getInt("OPTIONAL_STATUS"));
					
					Timestamp myTimestamp = rs_query_getOverrides.getTimestamp("DateTime");
					String S = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(myTimestamp);			
					controlOverride.setDateTime(S);
					
					list.add(controlOverride);

				}
		}catch(Exception e){

			System.out.println("Error: " + e.getMessage());
			
			link.Close_link();
			
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error loading overrides").build();
			
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
  
	@Logged
	@POST
	@Path("{CentralNodeID}/{EnclosureNodeID}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response postOverride(@PathParam("CentralNodeID") int CentralNodeID, @PathParam("EnclosureNodeID") int EnclosureNodeID,
									ControlOverride controlOverride, @Context HttpHeaders headers){
	  	  
	  Session session = new Session(headers);
      User currentUser = session.getUser();
      int userID = currentUser.getUserID();
        	  
      System.out.println("["+currentUser.getUserName()+"] [POST] /override/"+CentralNodeID+"/"+EnclosureNodeID);
      
	  link.Open_link();

		try{
			String query_postOverride = "INSERT INTO OverrideHistory (`UserID`,`CentralNodeID`,`EnclosureNodeID`,`DateTime`,`HUM_OR`,`HEAT_OR`,`UV_OR`,`OPTIONAL_OR`,`HUM_STATUS`,`HEAT_STATUS`,`UV_STATUS`,`OPTIONAL_STATUS`) VALUES (?,?,?,now(),?,?,?,?,?,?,?,?);";
			prep_sql = link.linea.prepareStatement(query_postOverride);

			prep_sql.setInt(1, userID);
			prep_sql.setInt(2, CentralNodeID);
			prep_sql.setInt(3, EnclosureNodeID);
			prep_sql.setInt(4, controlOverride.getHUM_OR());
			prep_sql.setInt(5, controlOverride.getHEAT_OR());
			prep_sql.setInt(6, controlOverride.getUV_OR());
			prep_sql.setInt(7, controlOverride.getOPTIONAL_OR());
			prep_sql.setInt(8, controlOverride.getHUM_STATUS());
			prep_sql.setInt(9, controlOverride.getHEAT_STATUS());
			prep_sql.setInt(10, controlOverride.getUV_STATUS());
			prep_sql.setInt(11, controlOverride.getOPTIONAL_STATUS());
			
			prep_sql.executeUpdate();

		}catch(Exception e){

			System.out.println("Error: " + e.getMessage());
			
			link.Close_link();
			
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error creating override").build();
			
		}

	link.Close_link();
	
	SendToCentralNode sendToCentralNode = new SendToCentralNode(controlOverride, "POST", "IoT/override/"+EnclosureNodeID);
	sendToCentralNode.sendToNode(currentUser.getUserID(), CentralNodeID);
	
	return Response.status(Response.Status.OK).build();
  
  }
} 