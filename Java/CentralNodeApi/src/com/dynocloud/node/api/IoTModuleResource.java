package com.dynocloud.node.api;

import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.PreparedStatement;


@Path("/IoT/module")
public class IoTModuleResource {
	
	private static Database_connection link = new Database_connection();
	private static PreparedStatement prep_sql;	
	
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
		
	return Response.status(Response.Status.OK).build();
  
  }	

} 