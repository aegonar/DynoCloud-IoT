package com.dynocloud.server.api;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.PreparedStatement;

@Path("/IoT/override")
public class IoTControlOverrideResource {
	
	private static Database_connection link = new Database_connection();
	private static PreparedStatement prep_sql;
	
	@Logged
	@POST
	@Path("{CentralNodeID}/{EnclosureNodeID}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response postOverride(@PathParam("CentralNodeID") int CentralNodeID, @PathParam("EnclosureNodeID") int EnclosureNodeID,
									ControlOverride controlOverride, @Context HttpHeaders headers){
	  	  
	  Session session = new Session(headers);
      User currentUser = session.getUser();
      int userID = currentUser.getUserID();
        	  
      System.out.println("["+currentUser.getUserName()+"] [POST] /IoT/override/"+CentralNodeID+"/"+EnclosureNodeID);
      
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

	return Response.status(Response.Status.OK).build();
  
  }
} 